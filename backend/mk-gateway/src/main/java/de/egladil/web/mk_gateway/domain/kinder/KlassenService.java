// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;
import de.egladil.web.mk_gateway.domain.kinder.events.KlasseChanged;
import de.egladil.web.mk_gateway.domain.kinder.events.KlasseCreated;
import de.egladil.web.mk_gateway.domain.kinder.events.KlasseDeleted;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
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
 * KlassenService
 */
@ApplicationScoped
public class KlassenService {

	private static final Logger LOG = LoggerFactory.getLogger(KlassenService.class);

	private final KlasseDuplettenpruefer duplettenpruefer = new KlasseDuplettenpruefer();

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	KlassenRepository klassenRepository;

	@Inject
	AuthorizationService authService;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	KinderService kinderService;

	@Inject
	LoesungszettelService loesungszettelService;

	@Inject
	WettbewerbService wettbewerbService;

	private KlasseCreated klasseCreated;

	@Inject
	Event<KlasseCreated> klasseCreatedEvent;

	private KlasseChanged klasseChanged;

	@Inject
	Event<KlasseChanged> klasseChangedEvent;

	private KlasseDeleted klasseDeleted;

	@Inject
	Event<KlasseDeleted> klasseDeletedEvent;

	private WettbewerbID wettbewerbID;

	public static KlassenService createForTest(final AuthorizationService authService, final KinderService kinderRepository, final TeilnahmenRepository teilnahmenRepository, final VeranstalterRepository veranstalterRepository, final WettbewerbService wettbewerbService, final LoesungszettelService loesungszettelService, final KlassenRepository klassenRepository) {

		KlassenService result = new KlassenService();
		result.authService = authService;
		result.kinderService = kinderRepository;
		result.teilnahmenRepository = teilnahmenRepository;
		result.veranstalterRepository = veranstalterRepository;
		result.wettbewerbService = wettbewerbService;
		result.loesungszettelService = loesungszettelService;
		result.klassenRepository = klassenRepository;
		return result;
	}

	public static KlassenService createForIntegrationTest(final EntityManager em) {

		KlassenService result = new KlassenService();

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

		KinderService kinderService = KinderService.createForTest(authService, kinderRepository, teilnahmenRepository,
			veranstalterRepository, wettbewerbService, loesungszettelService, klassenRepository);

		result.authService = authService;
		result.kinderService = kinderService;
		result.klassenRepository = klassenRepository;
		result.loesungszettelService = loesungszettelService;
		result.teilnahmenRepository = teilnahmenRepository;
		result.veranstalterRepository = veranstalterRepository;
		result.wettbewerbService = wettbewerbService;

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

		this.authorizeAction(schulkuerzel, lehrerUuid, "pruefeDuplikat");

		return pruefeDuplikatWithoutAuthorization(data);
	}

	boolean pruefeDuplikatWithoutAuthorization(final KlasseRequestData data) {

		Identifier schuleID = new Identifier(data.schulkuerzel());

		List<Klasse> klassen = klassenRepository.findKlassenWithSchule(schuleID);

		if (klassen.isEmpty()) {

			return false;
		}

		Klasse klasse = data.getUuid() == null ? new Klasse() : new Klasse(new Identifier(data.getUuid()));
		klasse = klasse.withName(data.klasse().name()).withSchuleID(schuleID);

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

		this.authorizeAction(schulkuerzel, lehrerUuid, "klasseAnlegen");

		boolean duplikat = this.pruefeDuplikatWithoutAuthorization(data);

		if (duplikat) {

			String msg = "klasseAnlegen: " + MessageFormat.format(applicationMessages.getString("checkKlasseDuplikat"),
				new Object[] { data.klasse().name() });
			LOG.error(msg);
			throw new BadRequestException(msg);
		}

		Klasse klasse = new Klasse().withName(data.klasse().name()).withSchuleID(new Identifier(schulkuerzel));

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

	/**
	 * Vorhandene Klasse wird umbenannt.
	 *
	 * @param  data
	 * @param  lehrerUuid
	 * @return
	 */
	public KlasseAPIModel klasseUmbenennen(final KlasseRequestData data, final String lehrerUuid) {

		String schulkuerzel = data.schulkuerzel();

		this.authorizeAction(schulkuerzel, lehrerUuid, "klasseUmbenennen");

		Identifier klasseID = new Identifier(data.getUuid());

		Optional<Klasse> optKlasse = klassenRepository.ofIdentifier(klasseID);

		if (optKlasse.isEmpty()) {

			LOG.warn("{} versucht Klasse zu aendern, die es nicht gibt. klasseID={}", lehrerUuid, klasseID);
			throw new NotFoundException();
		}

		boolean duplikat = this.pruefeDuplikatWithoutAuthorization(data);

		if (duplikat) {

			String msg = "klasseUmbenennen: " + MessageFormat.format(applicationMessages.getString("checkKlasseDuplikat"),
				new Object[] { data.klasse().name() });
			LOG.error(msg);
			throw new BadRequestException(msg);
		}

		Klasse klasse = optKlasse.get();

		String nameAlt = klasse.name();

		klasse = klasse.withName(data.klasse().name());
		Klasse geaenderte = klassenRepository.changeKlasse(klasse);

		klasseChanged = (KlasseChanged) new KlasseChanged(lehrerUuid)
			.withNameAlt(nameAlt)
			.withName(klasse.name())
			.withKlasseID(klasseID.identifier())
			.withSchulkuerzel(schulkuerzel);

		if (klasseChangedEvent != null) {

			klasseChangedEvent.fire(klasseChanged);
		} else {

			System.out.println(klasseChanged.serializeQuietly());
		}

		return KlasseAPIModel.createFromKlasse(geaenderte);
	}

	/**
	 * Löcht die gegebene Klasse und gibt das API-Model der gelöschten Klasse zurück.
	 *
	 * @param  klasseUuid
	 * @param  lehrerUuid
	 * @return            KlasseAPIModel
	 */
	@Transactional
	public final KlasseAPIModel klasseLoeschen(final String klasseUuid, final String lehrerUuid) {

		Identifier klasseID = new Identifier(klasseUuid);

		Optional<Klasse> optKlasse = klassenRepository.ofIdentifier(klasseID);

		if (optKlasse.isEmpty()) {

			LOG.warn("{} versucht Klasse zu loeschen, die es nicht gibt: klasseID={}", lehrerUuid, klasseID);
			throw new NotFoundException();
		}

		Klasse klasse = optKlasse.get();
		String schulkuerzel = klasse.schuleID().identifier();

		this.authorizeAction(schulkuerzel, lehrerUuid, "klasseLoeschen");

		List<Kind> kinder = kinderService.findKinderMitKlasseWithoutAuthorization(klasseID,
			new TeilnahmeIdentifierAktuellerWettbewerb(schulkuerzel, Teilnahmeart.SCHULE));

		if (!kinder.isEmpty()) {

			for (Kind kind : kinder) {

				kinderService.kindLoeschenWithoutAuthorizationCheck(kind, lehrerUuid);
			}

		}

		klassenRepository.removeKlasse(klasse);

		klasseDeleted = (KlasseDeleted) new KlasseDeleted(lehrerUuid)
			.withKlasseID(klasse.identifier().identifier())
			.withName(klasse.name())
			.withSchulkuerzel(schulkuerzel);

		if (klasseDeletedEvent != null) {

			klasseDeletedEvent.fire(klasseDeleted);
		} else {

			System.out.println(klasseDeleted.serializeQuietly());
		}
		return KlasseAPIModel.createFromKlasse(klasse);
	}

	void authorizeAction(final String schulkuerzel, final String lehrerUuid, final String callingMethodForLog) {

		this.authService.checkPermissionForTeilnahmenummer(new Identifier(lehrerUuid), new Identifier(schulkuerzel),
			"[" + callingMethodForLog + " - schulkuerzel=" + schulkuerzel + "]");

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer(schulkuerzel).withWettbewerbID(getWettbewerbID());

		Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier);

		if (optTeilnahme.isEmpty()) {

			String msg = this.getClass().getSimpleName() + "." + callingMethodForLog + "(...): " + "Schule mit UUID="
				+ schulkuerzel + " ist nicht zum aktuellen Wettbewerb (" + wettbewerbID + ") angemeldet";

			LOG.warn(msg);
			throw new NotFoundException(
				msg);
		}
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

	KlasseChanged getKlasseChanged() {

		return klasseChanged;
	}

	KlasseDeleted getKlasseDeleted() {

		return klasseDeleted;
	}

}
