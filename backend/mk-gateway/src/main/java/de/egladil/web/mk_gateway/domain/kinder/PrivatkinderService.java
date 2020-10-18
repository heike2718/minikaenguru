// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.api.PrivatkindRequestData;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

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
	Event<KindCreated> kindCreatedEvent;

	private KindCreated kindCreated;

	public static PrivatkinderService createForTest(final KinderRepository kinderRepository, final TeilnahmenRepository teilnahmenRepository, final AuthorizationService authService) {

		PrivatkinderService result = new PrivatkinderService();
		result.authService = authService;
		result.kinderRepository = kinderRepository;
		result.teilnahmenRepository = teilnahmenRepository;
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

		TeilnahmeIdentifier teilnahmeIdentifier = daten.teilnahmeIdentifier();

		this.authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid),
			new Identifier(teilnahmeIdentifier.teilnahmenummer()));

		List<Kind> kinder = kinderRepository.findKinderWithTeilnahme(teilnahmeIdentifier);

		if (kinder.isEmpty()) {

			return false;
		}

		Kind kind = Kind.createFromAPIModelWithoutKlasseID(daten.kind());

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

		KindAPIModel result = KindAPIModel.create(gespeichertesKind.klassenstufe(), gespeichertesKind.sprache())
			.withNachname(kind.nachname())
			.withUuid(gespeichertesKind.identifier().identifier())
			.withVorname(gespeichertesKind.vorname())
			.withZusatz(gespeichertesKind.zusatz());

		kindCreated = (KindCreated) new KindCreated(veranstalterUuid).withKindID(result.uuid())
			.withKlassenstufe(gespeichertesKind.klassenstufe())
			.withSprache(gespeichertesKind.sprache())
			.withTeilnahmenummer(teilnahmeIdentifier.teilnahmenummer());

		if (this.kindCreatedEvent != null) {

			this.kindCreatedEvent.fire(kindCreated);
		}

		return result;
	}

	KindCreated getKindCreated() {

		return kindCreated;
	}

}
