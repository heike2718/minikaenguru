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
import javax.ws.rs.NotFoundException;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.PrivatkindRequestData;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.veranstalter.Privatveranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.PrivatveranstalterService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * PrivatkinderService
 */
@ApplicationScoped
public class PrivatkinderService {

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
	Event<KindCreated> kindCreatedEvent;

	private WettbewerbID wettbewerbID;

	private KindCreated kindCreated;

	public static PrivatkinderService createForTest(final KinderRepository kinderRepository, final TeilnahmenRepository teilnahmenRepository, final AuthorizationService authService) {

		PrivatkinderService result = new PrivatkinderService();
		result.authService = authService;
		result.kinderRepository = kinderRepository;
		result.teilnahmenRepository = teilnahmenRepository;
		result.wettbewerbID = new WettbewerbID(2018);
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

		if (this.wettbewerbID == null) {

			this.wettbewerbID = this.wettbewerbService.aktuellerWettbewerb().get().id();
		}

		TeilnahmeIdentifier teilnahmeIdentifier = daten.teilnahmeIdentifier();

		this.authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid),
			new Identifier(teilnahmeIdentifier.teilnahmenummer()));

		List<Kind> kinder = kinderRepository.findKinderWithTeilnahme(teilnahmeIdentifier, this.wettbewerbID);

		if (kinder.isEmpty()) {

			return false;
		}

		Kind kind = Kind.createFromAPIModelWithoutKlasseID(daten.kind());

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

		TeilnahmeIdentifier teilnahmeIdentifier = daten.teilnahmeIdentifier();

		this.authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid),
			new Identifier(teilnahmeIdentifier.teilnahmenummer()));

		Kind kind = Kind.createFromAPIModelWithoutKlasseID(daten.kind());

		Kind gespeichertesKind = kinderRepository.addKind(kind);

		KindAPIModel result = KindAPIModel.createFromKind(gespeichertesKind);

		kindCreated = (KindCreated) new KindCreated(veranstalterUuid).withKindID(result.uuid())
			.withKlassenstufe(gespeichertesKind.klassenstufe())
			.withSprache(gespeichertesKind.sprache())
			.withTeilnahmenummer(teilnahmeIdentifier.teilnahmenummer());

		if (this.kindCreatedEvent != null) {

			this.kindCreatedEvent.fire(kindCreated);
		}

		return result;
	}

	public List<KindAPIModel> loadAllKinder(final String veranstalterUuid) {

		if (this.wettbewerbID == null) {

			this.wettbewerbID = this.wettbewerbService.aktuellerWettbewerb().get().id();
		}

		Privatveranstalter veranstalter = this.privatveranstalterService.findPrivatveranstalter(veranstalterUuid);

		List<Identifier> teilnahmeIdentifiers = veranstalter.teilnahmeIdentifier();

		Optional<Teilnahme> optTeilnahme = this.teilnahmenRepository
			.ofTeilnahmenummerArtWettbewerb(teilnahmeIdentifiers.get(0).identifier(), Teilnahmeart.PRIVAT, wettbewerbID);

		if (optTeilnahme.isEmpty()) {

			throw new NotFoundException(
				"Privatveranstalter mit UUID=" + veranstalterUuid + " ist nicht zum aktuellen Wettbewerb angemeldet");
		}

		Teilnahme teilnahme = optTeilnahme.get();

		List<Kind> kinder = this.kinderRepository.findKinderWithTeilnahme(teilnahme.teilnahmeIdentifier(), wettbewerbID);
		final List<KindAPIModel> result = new ArrayList<>();

		kinder.forEach(kind -> result.add(KindAPIModel.createFromKind(kind)));

		return result;
	}

	KindCreated getKindCreated() {

		return kindCreated;
	}

}
