// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassen;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auswertungen.LoesungszettelService;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.klassen.api.KlasseRequestData;
import de.egladil.web.mk_gateway.domain.klassen.events.KlasseCreated;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * KlassenService
 */
@ApplicationScoped
public class KlassenService {

	private static final Logger LOG = LoggerFactory.getLogger(KlassenService.class);

	private final KlasseDuplettenpruefer duplettenpruefer = new KlasseDuplettenpruefer();

	@Inject
	KlassenRepository klassenRepository;

	@Inject
	AuthorizationService authService;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	KinderRepository kinderRepository;

	@Inject
	LoesungszettelService loesungszettelService;

	@Inject
	WettbewerbService wettbewerbService;

	private KlasseCreated klasseCreated;

	@Inject
	Event<KlasseCreated> klasseCreatedEvent;

	private WettbewerbID wettbewerbID;

	public static KlassenService createForTest(final AuthorizationService authService, final KinderRepository kinderRepository, final TeilnahmenRepository teilnahmenRepository, final VeranstalterRepository veranstalterRepository, final WettbewerbService wettbewerbService, final LoesungszettelService loesungszettelService, final KlassenRepository klassenRepository) {

		KlassenService result = new KlassenService();
		result.authService = authService;
		result.kinderRepository = kinderRepository;
		result.teilnahmenRepository = teilnahmenRepository;
		result.veranstalterRepository = veranstalterRepository;
		result.wettbewerbService = wettbewerbService;
		result.loesungszettelService = loesungszettelService;
		result.klassenRepository = klassenRepository;
		return result;
	}

	/**
	 * Läd die Klassen einer angemeldeten Schule.
	 *
	 * @param  schulkuerzel
	 *                      String Kürzel der Schule
	 * @param  lehrerUuid
	 *                      UUID eines Lehrers der Schule.
	 * @return              List
	 */
	public List<KlasseAPIModel> klassenZuSchuleLaden(final String schulkuerzel, final String lehrerUuid) {

		this.authService.checkPermissionForTeilnahmenummer(new Identifier(lehrerUuid), new Identifier(schulkuerzel),
			"[klassenZuSchuleLaden - schulkuerzel=" + schulkuerzel + "]");

		Veranstalter veranstalter = veranstalterRepository.ofId(new Identifier(lehrerUuid)).get();

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(veranstalter.teilnahmeart())
			.withTeilnahmenummer(schulkuerzel).withWettbewerbID(getWettbewerbID());

		Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier);

		if (optTeilnahme.isEmpty()) {

			LOG.warn("Schule {} ist nicht zum aktuellen Wettbewerb angemeldet. Anfragender Veranstalter: {}",
				schulkuerzel, veranstalter.person().toString());
			throw new NotFoundException();
		}

		List<Klasse> klassen = klassenRepository.findKlassenWithSchule(new Identifier(schulkuerzel));

		if (klassen.isEmpty()) {

			return new ArrayList<>();
		}

		return klassen.stream().map(kl -> KlasseAPIModel.createFromKlasse(kl)).collect(Collectors.toList());
	}

	/**
	 * Prüft, ob es sich bei den gegebenen Klassendaten um ein mögliches Duplitakt handeln könnte.
	 *
	 * @param  data
	 * @param  lehrerUuid
	 * @return
	 */
	public boolean pruefeDuplikat(final KlasseRequestData data, final String lehrerUuid) {

		String schulkuerzel = data.schulkuerzel();

		this.authService.checkPermissionForTeilnahmenummer(new Identifier(lehrerUuid), new Identifier(schulkuerzel),
			"[pruefeDuplikat - schulkuerzel=" + schulkuerzel + "]");

		Identifier schuleID = new Identifier(schulkuerzel);

		List<Klasse> klassen = klassenRepository.findKlassenWithSchule(schuleID);

		if (klassen.isEmpty()) {

			return false;
		}

		Klasse klasse = new Klasse().withName(data.klasse().name()).withSchuleID(schuleID);

		return koennteDuplikatSein(klasse, klassen);
	}

	boolean koennteDuplikatSein(final Klasse klasse, final List<Klasse> alleKlassenDerSchule) {

		for (Klasse kl : alleKlassenDerSchule) {

			if (duplettenpruefer.apply(klasse, kl)) {

				return true;
			}
		}

		return false;

	}

	public KlasseAPIModel klasseAnlegen(final KlasseRequestData data, final String lehrerUuid) {

		String schulkuerzel = data.schulkuerzel();

		this.authService.checkPermissionForTeilnahmenummer(new Identifier(lehrerUuid), new Identifier(schulkuerzel),
			"[klasseAnlegen - schulkuerzel=" + schulkuerzel + "]");

		Identifier schuleID = new Identifier(schulkuerzel);

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer(schulkuerzel).withWettbewerbID(getWettbewerbID());

		Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier);

		if (optTeilnahme.isEmpty()) {

			String msg = this.getClass().getSimpleName() + ".klasseAnlegen(...): " + "Schule mit UUID="
				+ schulkuerzel + " ist nicht zum aktuellen Wettbewerb (" + wettbewerbID + ") angemeldet";

			LOG.warn(msg);
			throw new NotFoundException(
				msg);
		}

		Klasse klasse = new Klasse().withName(data.klasse().name()).withSchuleID(schuleID);

		Klasse neueKlasse = klassenRepository.addKlasse(klasse);

		klasseCreated = (KlasseCreated) new KlasseCreated(lehrerUuid)
			.withKlasseID(neueKlasse.identifier().identifier())
			.withName(neueKlasse.name())
			.withSchulkuerzel(schulkuerzel);

		if (klasseCreatedEvent != null) {

			klasseCreatedEvent.fire(klasseCreated);
		} else {

			System.out.println(klasseCreated.serializeQuietly());
		}

		return KlasseAPIModel.createFromKlasse(neueKlasse);
	}

	public WettbewerbID getWettbewerbID() {

		if (wettbewerbID == null) {

			this.wettbewerbID = this.wettbewerbService.aktuellerWettbewerb().get().id();
		}

		return wettbewerbID;
	}

	KlasseCreated getKlasseCreated() {

		return klasseCreated;
	}

}
