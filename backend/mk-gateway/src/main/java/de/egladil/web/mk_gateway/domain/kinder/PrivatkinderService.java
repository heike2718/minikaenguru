// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auswertungen.LoesungszettelService;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.PrivatkindRequestData;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.Privatveranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.PrivatveranstalterService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * PrivatkinderService
 */
@ApplicationScoped
public class PrivatkinderService {

	private static final Logger LOG = LoggerFactory.getLogger(PrivatkinderService.class);

	private final KindDublettenpruefer dublettenpruefer = new KindDublettenpruefer();

	@Inject
	AuthorizationService authService;

	@Inject
	KinderRepository kinderRepository;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	PrivatveranstalterService privatveranstalterService;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	LoesungszettelService loesungszettelService;

	@Inject
	Event<KindCreated> kindCreatedEvent;

	@Inject
	Event<KindChanged> kindChangedEvent;

	@Inject
	Event<KindDeleted> kindDeletedEvent;

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	private WettbewerbID wettbewerbID;

	private KindCreated kindCreated;

	private KindChanged kindChanged;

	private KindDeleted kindDeleted;

	public static PrivatkinderService createForTest(final AuthorizationService authService, final KinderRepository kinderRepository, final TeilnahmenRepository teilnahmenRepository, final PrivatveranstalterService privatveranstalterService, final WettbewerbService wettbewerbService, final LoesungszettelService loesungszettelService) {

		PrivatkinderService result = new PrivatkinderService();
		result.authService = authService;
		result.kinderRepository = kinderRepository;
		result.teilnahmenRepository = teilnahmenRepository;
		result.privatveranstalterService = privatveranstalterService;
		result.wettbewerbService = wettbewerbService;
		result.loesungszettelService = loesungszettelService;
		return result;
	}

	/**
	 * Prüft, ob das gegebene Kind evtl. eine Dublette wäre.
	 *
	 * @param  daten
	 *                          PrivatkindRequestData
	 * @param  veranstalterUuid
	 *                          String
	 * @return                  boolean
	 */
	public boolean pruefeDublettePrivat(final PrivatkindRequestData daten, final String veranstalterUuid) {

		Teilnahme teilnahme = getAktuelleTeilnahme(veranstalterUuid, "pruefeDublettePrivat");

		List<Kind> kinder = kinderRepository.findKinderWithTeilnahme(teilnahme);

		if (kinder.isEmpty()) {

			return false;
		}

		Kind kind = new Kind().withDaten(daten.kind());

		return this.koennteDubletteSein(kind, kinder);
	}

	boolean koennteDubletteSein(final Kind kind, final List<Kind> kinder) {

		for (Kind k : kinder) {

			if (Boolean.TRUE == dublettenpruefer.apply(k, kind)) {

				return true;
			}
		}

		return false;
	}

	/**
	 * Legt ein neues Kind an.
	 *
	 * @param  daten
	 *                          PrivatkindRequestData
	 * @param  veranstalterUuid
	 *                          String
	 * @return                  KindAPIModel
	 */
	public KindAPIModel privatkindAnlegen(final PrivatkindRequestData daten, final String veranstalterUuid) {

		Teilnahme teilnahme = getAktuelleTeilnahme(veranstalterUuid, "privatkindAnlegen");

		Kind kind = new Kind().withDaten(daten.kind()).withTeilnahmeIdentifier(teilnahme.teilnahmeIdentifier());

		Kind gespeichertesKind = kinderRepository.addKind(kind);

		KindAPIModel result = KindAPIModel.createFromKind(gespeichertesKind);

		kindCreated = (KindCreated) new KindCreated(veranstalterUuid).withKindID(result.uuid())
			.withKlassenstufe(gespeichertesKind.klassenstufe())
			.withSprache(gespeichertesKind.sprache())
			.withTeilnahmenummer(teilnahme.teilnahmenummer().identifier());

		if (this.kindCreatedEvent != null) {

			this.kindCreatedEvent.fire(kindCreated);
		} else {

			System.out.println("kindCreated: " + kindCreated.serializeQuietly());
		}

		return result;
	}

	@Transactional
	public KindAPIModel privatkindAendern(final PrivatkindRequestData daten, final String veranstalterUuid) {

		Optional<Kind> optKind = kinderRepository.findKindWithIdentifier(new Identifier(daten.uuid()), getWettbewerbID());

		if (optKind.isEmpty()) {

			LOG.warn("{} versucht Kind zu ändern, das es nicht gibt", veranstalterUuid);
			throw new NotFoundException();
		}

		Kind kind = optKind.get();

		authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid),
			new Identifier(kind.teilnahmeIdentifier().teilnahmenummer()), "[privatkindAendern - " + daten.uuid() + "]");

		KindEditorModel kindDaten = daten.kind();

		if (kind.loesungszettelID() != null) {

			if (kind.klassenstufe() != kindDaten.klassenstufe().klassenstufe()) {

				this.loesungszettelService.loesungszettelLoeschenWithoutAuthorizationCheck(kind.loesungszettelID(),
					veranstalterUuid);
			} else {

				if (kind.sprache() != kindDaten.sprache().sprache()) {

					this.loesungszettelService.spracheLoesungszettelAendern(kind.loesungszettelID(), kindDaten.sprache().sprache(),
						veranstalterUuid);
				}
			}
		}

		Kind geaendertesKind = new Kind(new Identifier(daten.uuid())).withDaten(daten.kind())
			.withTeilnahmeIdentifier(kind.teilnahmeIdentifier())
			.withLoesungszettelID(kind.loesungszettelID());

		boolean changed = this.kinderRepository.changeKind(geaendertesKind);

		KindAPIModel result = KindAPIModel.createFromKind(geaendertesKind);

		if (changed) {

			kindChanged = (KindChanged) new KindChanged(veranstalterUuid)
				.withKlassenstufe(geaendertesKind.klassenstufe())
				.withSprache(geaendertesKind.sprache())
				.withTeilnahmenummer(geaendertesKind.teilnahmeIdentifier().teilnahmenummer())
				.withKindID(geaendertesKind.identifier().identifier());

			if (kindChangedEvent != null) {

				kindChangedEvent.fire(kindChanged);
			} else {

				System.out.println("kindChanged: " + kindChanged.serializeQuietly());
			}
		}

		return result;
	}

	/**
	 * Löscht das gegebene Kind.
	 *
	 * @param  uuid
	 *                          String UUID des Kindes
	 * @param  veranstalterUuid
	 *                          UUID des Veranstalters
	 * @return                  boolean true, falls gelöscht, false sonst.
	 */
	@Transactional
	public KindAPIModel privatkindLoeschen(final String uuid, final String veranstalterUuid) {

		Optional<Kind> optKind = kinderRepository.findKindWithIdentifier(new Identifier(uuid), getWettbewerbID());

		if (optKind.isEmpty()) {

			LOG.warn("{} versucht Kind zu löschen, das es nicht gibt", veranstalterUuid);
			throw new NotFoundException();
		}

		Kind kind = optKind.get();

		authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid),
			new Identifier(kind.teilnahmeIdentifier().teilnahmenummer()), "[privatkindLoeschen - " + uuid + "]");

		if (kind.loesungszettelID() != null) {

			this.loesungszettelService.loesungszettelLoeschenWithoutAuthorizationCheck(kind.loesungszettelID(), veranstalterUuid);
		}

		boolean removed = this.kinderRepository.removeKind(kind);

		if (removed) {

			kindDeleted = (KindDeleted) new KindDeleted(veranstalterUuid)
				.withKindID(kind.identifier().identifier())
				.withKlassenstufe(kind.klassenstufe())
				.withSprache(kind.sprache())
				.withTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer());

			if (this.kindDeletedEvent != null) {

				this.kindDeletedEvent.fire(kindDeleted);
			} else {

				System.out.println("kindDeleted: " + kindDeleted.serializeQuietly());
			}
		}

		return KindAPIModel.createFromKind(kind);
	}

	public List<KindAPIModel> loadAllKinder(final String veranstalterUuid) {

		Teilnahme teilnahme = getAktuelleTeilnahme(veranstalterUuid, "loadAllKinder");

		List<Kind> kinder = this.kinderRepository.findKinderWithTeilnahme(teilnahme);
		final List<KindAPIModel> result = new ArrayList<>();

		kinder.forEach(kind -> result.add(KindAPIModel.createFromKind(kind)));

		return result;
	}

	private Teilnahme getAktuelleTeilnahme(final String veranstalterUuid, final String callingMethod) {

		Privatveranstalter veranstalter = this.privatveranstalterService.findPrivatveranstalter(veranstalterUuid);

		List<Identifier> teilnahmeIdentifiers = veranstalter.teilnahmeIdentifier();

		Optional<Teilnahme> optTeilnahme = this.teilnahmenRepository
			.ofTeilnahmenummerArtWettbewerb(teilnahmeIdentifiers.get(0).identifier(), Teilnahmeart.PRIVAT, getWettbewerbID());

		if (optTeilnahme.isEmpty()) {

			String msg = this.getClass().getSimpleName() + "." + callingMethod + "(...): " + "Privatveranstalter mit UUID="
				+ veranstalterUuid + " ist nicht zum aktuellen Wettbewerb (" + wettbewerbID + ") angemeldet";

			new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);
			throw new NotFoundException(
				msg);
		}

		return optTeilnahme.get();
	}

	KindCreated getKindCreated() {

		return kindCreated;
	}

	public WettbewerbID getWettbewerbID() {

		if (wettbewerbID == null) {

			this.wettbewerbID = this.wettbewerbService.aktuellerWettbewerb().get().id();
		}

		return wettbewerbID;
	}

}
