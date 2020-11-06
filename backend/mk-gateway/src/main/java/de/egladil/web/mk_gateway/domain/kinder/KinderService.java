// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.kinder.events.KindChanged;
import de.egladil.web.mk_gateway.domain.kinder.events.KindCreated;
import de.egladil.web.mk_gateway.domain.kinder.events.KindDeleted;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KlassenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UserHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VeranstalterHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.WettbewerbHibernateRepository;

/**
 * KinderService
 */
@ApplicationScoped
public class KinderService {

	private static final Logger LOG = LoggerFactory.getLogger(KinderService.class);

	private final KindDublettenpruefer dublettenpruefer = new KindDublettenpruefer();

	@Inject
	AuthorizationService authService;

	@Inject
	KinderRepository kinderRepository;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	KlassenRepository klassenRepository;

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

	public static KinderService createForTest(final AuthorizationService authService, final KinderRepository kinderRepository, final TeilnahmenRepository teilnahmenRepository, final VeranstalterRepository veranstalterRepository, final WettbewerbService wettbewerbService, final LoesungszettelService loesungszettelService, final KlassenRepository klassenRepository) {

		KinderService result = new KinderService();
		result.authService = authService;
		result.kinderRepository = kinderRepository;
		result.teilnahmenRepository = teilnahmenRepository;
		result.veranstalterRepository = veranstalterRepository;
		result.wettbewerbService = wettbewerbService;
		result.loesungszettelService = loesungszettelService;
		result.klassenRepository = klassenRepository;
		return result;
	}

	public static KinderService createForIntegrationTest(final EntityManager em) {

		VeranstalterRepository veranstalterRepository = VeranstalterHibernateRepository.createForIntegrationTest(em);

		AuthorizationService authService = AuthorizationService.createForTest(
			veranstalterRepository, UserHibernateRepository.createForIntegrationTest(em));

		KinderRepository kinderRepository = KinderHibernateRepository.createForIntegrationTest(em);
		TeilnahmenRepository teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(em);
		WettbewerbService wettbewerbService = WettbewerbService
			.createForTest(WettbewerbHibernateRepository.createForIntegrationTest(em));

		LoesungszettelService loesungszettelService = LoesungszettelService.createForTest(authService,
			LoesungszettelHibernateRepository.createForIntegrationTest(em));

		KlassenRepository klassenRepository = KlassenHibernateRepository.createForIntegrationTest(em);

		KinderService result = new KinderService();
		result.authService = authService;
		result.kinderRepository = kinderRepository;
		result.klassenRepository = klassenRepository;
		result.loesungszettelService = loesungszettelService;
		result.teilnahmenRepository = teilnahmenRepository;
		result.veranstalterRepository = veranstalterRepository;
		result.wettbewerbService = wettbewerbService;
		return result;

	}

	/**
	 * Prüft, ob das gegebene Kind evtl. eine Dublette wäre.
	 *
	 * @param  daten
	 *                          KindRequestData
	 * @param  veranstalterUuid
	 *                          String
	 * @return                  boolean
	 */
	public boolean pruefeDublette(final KindRequestData daten, final String veranstalterUuid) {

		Teilnahme teilnahme = getAktuelleTeilnahme(daten, veranstalterUuid, "pruefeDublette");

		authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid), teilnahme.teilnahmenummer(),
			"[kindAnlegen - teilnahmenummer=" + teilnahme.teilnahmenummer().identifier() + "]");

		List<Kind> kinder = kinderRepository.withTeilnahme(TeilnahmeIdentifierAktuellerWettbewerb.createFromTeilnahme(teilnahme));

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
	 *                          KindRequestData
	 * @param  veranstalterUuid
	 *                          String
	 * @return                  KindAPIModel
	 */
	public KindAPIModel kindAnlegen(final KindRequestData daten, final String veranstalterUuid) {

		Teilnahme teilnahme = getAktuelleTeilnahme(daten, veranstalterUuid, "kindAnlegen");

		authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid), teilnahme.teilnahmenummer(),
			"[kindAnlegen - teilnahmenummer=" + teilnahme.teilnahmenummer().identifier() + "]");

		Kind kind = new Kind().withDaten(daten.kind())
			.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createFromTeilnahme(teilnahme));

		Kind gespeichertesKind = kinderRepository.addKind(kind);

		KindAPIModel result = KindAPIModel.createFromKind(gespeichertesKind);

		kindCreated = (KindCreated) new KindCreated(veranstalterUuid)
			.withKindID(result.uuid())
			.withKlassenstufe(gespeichertesKind.klassenstufe())
			.withSprache(gespeichertesKind.sprache())
			.withTeilnahmenummer(teilnahme.teilnahmenummer().identifier());

		if (this.kindCreatedEvent != null) {

			this.kindCreatedEvent.fire(kindCreated);
		} else {

			System.out.println(kindCreated.typeName() + ": " + kindCreated.serializeQuietly());
		}

		return result;
	}

	@Transactional
	public KindAPIModel kindAendern(final KindRequestData daten, final String veranstalterUuid) {

		Optional<Kind> optKind = kinderRepository.withIdentifier(new Identifier(daten.uuid()));

		if (optKind.isEmpty()) {

			LOG.warn("{} versucht Kind zu ändern, das es nicht gibt", veranstalterUuid);
			throw new NotFoundException();
		}

		Kind kind = optKind.get();

		authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid),
			new Identifier(kind.teilnahmeIdentifier().teilnahmenummer()), "[kindAendern - kindUUID=" + daten.uuid() + "]");

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

				System.out.println(kindChanged.typeName() + ": " + kindChanged.serializeQuietly());
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
	public KindAPIModel kindLoeschen(final String uuid, final String veranstalterUuid) {

		Optional<Kind> optKind = kinderRepository.withIdentifier(new Identifier(uuid));

		if (optKind.isEmpty()) {

			LOG.warn("{} versucht Kind zu löschen, das es nicht gibt", veranstalterUuid);
			throw new NotFoundException();
		}

		Kind kind = optKind.get();

		authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid),
			new Identifier(kind.teilnahmeIdentifier().teilnahmenummer()), "[kindLoeschen - kindUUID=" + uuid + "]");

		kindLoeschenWithoutAuthorizationCheck(kind, veranstalterUuid);

		return KindAPIModel.createFromKind(kind);
	}

	/**
	 * Löscht das gegebene Kind ohne erneute Autorisierung.
	 *
	 * @param  kind
	 * @param  veranstalterUuid
	 * @return
	 */
	boolean kindLoeschenWithoutAuthorizationCheck(final Kind kind, final String veranstalterUuid) {

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

				System.out.println(kindDeleted.typeName() + ": " + kindDeleted.serializeQuietly());
			}
		}

		return removed;

	}

	public List<KindAPIModel> kinderZuTeilnahmeLaden(final String teilnahmenummer, final String veranstalterUuid) {

		this.authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid), new Identifier(teilnahmenummer),
			"[kinderZuTeilnahmeLaden - " + teilnahmenummer + "]");

		Veranstalter veranstalter = veranstalterRepository.ofId(new Identifier(veranstalterUuid)).get();
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(getWettbewerbID());

		Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier);

		if (optTeilnahme.isEmpty()) {

			LOG.warn("Veranstalter mit uuid={} ist nicht zum aktuellen Wettebwerb angemeldet.", veranstalterUuid);

			throw new NotFoundException();
		}

		Teilnahme aktuelleTeilnahme = optTeilnahme.get();

		List<Kind> kinder = this.kinderRepository
			.withTeilnahme(TeilnahmeIdentifierAktuellerWettbewerb.createFromTeilnahme(aktuelleTeilnahme));
		final List<KindAPIModel> result = new ArrayList<>();

		kinder.forEach(kind -> result.add(KindAPIModel.createFromKind(kind)));

		return result;
	}

	List<Kind> findKinderMitKlasseWithoutAuthorization(final Identifier klasseID, final TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier) {

		List<Kind> kinder = kinderRepository.withTeilnahme(teilnahmeIdentifier).stream().filter(k -> klasseID.equals(k.klasseID()))
			.collect(Collectors.toList());

		return kinder;
	}

	private String getTeilnahmenummerFromKlasse(final String klasseUuid) {

		if (klasseUuid == null) {

			String msg = "klasseUuid darf an dieser Stelle nicht null sein";

			LOG.error(msg);
			throw new MkGatewayRuntimeException(msg);
		}

		Optional<Klasse> optKlasse = klassenRepository.ofIdentifier(new Identifier(klasseUuid));

		if (optKlasse.isEmpty()) {

			String msg = "Klasse mit UUID=" + klasseUuid + " nicht gefunden";

			LOG.error(msg);
			throw new MkGatewayRuntimeException(msg);
		}

		return optKlasse.get().schuleID().identifier();

	}

	private Teilnahme getAktuelleTeilnahme(final KindRequestData daten, final String veranstalterUuid, final String callingMethod) {

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(new Identifier(veranstalterUuid));

		if (optVeranstalter.isEmpty()) {

			String msg = "Veranstalter mit UUID=" + veranstalterUuid + " nicht gefunden";

			LOG.error(msg);
			throw new MkGatewayRuntimeException(msg);
		}

		Veranstalter veranstalter = optVeranstalter.get();

		String teilnahmenummer = null;

		List<Identifier> teilnahmeIdentifiers = veranstalter.teilnahmeIdentifier();

		if (veranstalter.rolle() == Rolle.PRIVAT) {

			teilnahmenummer = teilnahmeIdentifiers.get(0).identifier();
		} else {

			teilnahmenummer = getTeilnahmenummerFromKlasse(daten.klasseUuid());
		}

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(getWettbewerbID());

		Optional<Teilnahme> optTeilnahme = this.teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier);

		if (optTeilnahme.isEmpty()) {

			String msg = this.getClass().getSimpleName() + "." + callingMethod + "(...): " + "Veranstalter mit UUID="
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
