// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlasseDuplettenpruefer;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.domain.kinder.KlassenService;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KlasseRequestData;
import de.egladil.web.mk_gateway.domain.kinder.events.KlasseChanged;
import de.egladil.web.mk_gateway.domain.kinder.events.KlasseCreated;
import de.egladil.web.mk_gateway.domain.kinder.events.KlasseDeleted;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.OnlineLoesungszettelService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KlassenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VeranstalterHibernateRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;

/**
 * KlassenServiceImpl
 */
@ApplicationScoped
public class KlassenServiceImpl implements KlassenService {

	private static final Logger LOG = LoggerFactory.getLogger(KlassenServiceImpl.class);

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
	KinderServiceImpl kinderService;

	@Inject
	OnlineLoesungszettelService loesungszettelService;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	UploadRepository uploadRepository;

	@Inject
	DomainEventHandler domainEventHandler;

	private WettbewerbID wettbewerbID;

	public static KlassenServiceImpl createForTest(final AuthorizationService authService, final KinderServiceImpl kinderRepository, final TeilnahmenRepository teilnahmenRepository, final VeranstalterRepository veranstalterRepository, final WettbewerbService wettbewerbService, final OnlineLoesungszettelService loesungszettelService, final KlassenRepository klassenRepository) {

		KlassenServiceImpl result = new KlassenServiceImpl();
		result.authService = authService;
		result.kinderService = kinderRepository;
		result.teilnahmenRepository = teilnahmenRepository;
		result.veranstalterRepository = veranstalterRepository;
		result.wettbewerbService = wettbewerbService;
		result.loesungszettelService = loesungszettelService;
		result.klassenRepository = klassenRepository;
		return result;
	}

	public static KlassenServiceImpl createForIntegrationTest(final EntityManager em) {

		KlassenServiceImpl result = new KlassenServiceImpl();
		result.authService = AuthorizationService.createForIntegrationTest(em);
		result.kinderService = KinderServiceImpl.createForIntegrationTest(em);
		result.klassenRepository = KlassenHibernateRepository.createForIntegrationTest(em);
		result.loesungszettelService = OnlineLoesungszettelService.createForIntegrationTest(em);
		result.teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(em);
		result.veranstalterRepository = VeranstalterHibernateRepository.createForIntegrationTest(em);
		result.wettbewerbService = WettbewerbService.createForIntegrationTest(em);
		result.domainEventHandler = DomainEventHandler.createForIntegrationTest(em);

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
	@Override
	public List<KlasseAPIModel> klassenZuSchuleLaden(final String schulkuerzel, final String lehrerUuid) {

		this.authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(lehrerUuid), new Identifier(schulkuerzel),
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

		final Map<Identifier, Pair<Long, Long>> anzahlenKinder = this.kinderService.countKinder(klassen);
		final Map<Identifier, Long> anzahlenLoesungszettel = this.kinderService.countLoesungszettel(klassen);

		List<KlasseAPIModel> result = klassen.stream()
			.map(kl -> KlasseAPIModel.createFromKlasse(kl).withAnzahlKinder(anzahlenKinder.get(kl.identifier()).getLeft())
				.withAnzahlKinderZuPruefen(anzahlenKinder.get(kl.identifier()).getRight())
				.withAnzahlLoesungszettel(anzahlenLoesungszettel.get(kl.identifier())))
			.collect(Collectors.toList());

		return result;
	}

	/**
	 * Prüft, ob es sich bei den gegebenen Klassendaten um ein mögliches Duplitakt handeln könnte.
	 *
	 * @param  data
	 * @param  lehrerUuid
	 * @return
	 */
	@Override
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

	@Override
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

		Klasse neueKlasse = createKlasseAndFireEvent(data, lehrerUuid, schulkuerzel);
		return KlasseAPIModel.createFromKlasse(neueKlasse);
	}

	Klasse createKlasseAndFireEvent(final KlasseRequestData data, final String lehrerUuid, final String schulkuerzel) {

		Klasse klasse = new Klasse().withName(data.klasse().name()).withSchuleID(new Identifier(schulkuerzel));

		Klasse neueKlasse = klassenRepository.addKlasse(klasse);

		KlasseCreated klasseCreated = (KlasseCreated) new KlasseCreated(lehrerUuid)
			.withKlasseID(neueKlasse.identifier().identifier())
			.withName(neueKlasse.name())
			.withSchulkuerzel(schulkuerzel);

		if (domainEventHandler != null) {

			domainEventHandler.handleEvent(klasseCreated);
		} else {

			System.out.println(klasseCreated.serializeQuietly());
		}
		return neueKlasse;
	}

	/**
	 * Vorhandene Klasse wird umbenannt.
	 *
	 * @param  data
	 * @param  lehrerUuid
	 * @return
	 */
	@Override
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

		KlasseChanged klasseChanged = (KlasseChanged) new KlasseChanged(lehrerUuid)
			.withNameAlt(nameAlt)
			.withName(klasse.name())
			.withKlasseID(klasseID.identifier())
			.withSchulkuerzel(schulkuerzel);

		if (domainEventHandler != null) {

			domainEventHandler.handleEvent(klasseChanged);
		} else {

			System.out.println(klasseChanged.serializeQuietly());
		}

		final Map<Identifier, Pair<Long, Long>> anzahlenKinder = this.kinderService
			.countKinder(Arrays.asList(new Klasse[] { geaenderte }));

		return KlasseAPIModel.createFromKlasse(geaenderte).withAnzahlKinder(anzahlenKinder.get(geaenderte.identifier()).getLeft())
			.withAnzahlKinderZuPruefen(anzahlenKinder.get(geaenderte.identifier()).getRight());
	}

	/**
	 * Löcht die gegebene Klasse und gibt das API-Model der gelöschten Klasse zurück.
	 *
	 * @param  klasseUuid
	 * @param  lehrerUuid
	 * @return            KlasseAPIModel
	 */
	@Override
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

		this.deleteKlasseWithoutAuthorizationCheck(schulkuerzel, klasse, lehrerUuid);

		return KlasseAPIModel.createFromKlasse(klasse);
	}

	boolean deleteKlasseWithoutAuthorizationCheck(final String schulkuerzel, final Klasse klasse, final String lehrerUuid) {

		List<Kind> kinder = kinderService.findKinderMitKlasseWithoutAuthorization(klasse.identifier(),
			new TeilnahmeIdentifierAktuellerWettbewerb(schulkuerzel, Teilnahmeart.SCHULE));

		if (!kinder.isEmpty()) {

			for (Kind kind : kinder) {

				kinderService.kindLoeschenWithoutAuthorizationCheck(kind, lehrerUuid);
			}
		}

		boolean result = klassenRepository.removeKlasse(klasse);

		KlasseDeleted klasseDeleted = (KlasseDeleted) new KlasseDeleted(lehrerUuid)
			.withKlasseID(klasse.identifier().identifier())
			.withName(klasse.name())
			.withSchulkuerzel(schulkuerzel);

		if (domainEventHandler != null) {

			domainEventHandler.handleEvent(klasseDeleted);
		} else {

			System.out.println(klasseDeleted.serializeQuietly());
		}

		return result;

	}

	@Override
	@Transactional
	public ResponsePayload alleKlassenLoeschen(final Identifier schuleId, final String lehrerUuid) {

		this.authorizeAction(schuleId.identifier(), lehrerUuid, "alleKlassenLoeschen");

		List<Klasse> klassen = klassenRepository.findKlassenWithSchule(schuleId);

		for (Klasse klasse : klassen) {

			this.deleteKlasseWithoutAuthorizationCheck(schuleId.identifier(), klasse, lehrerUuid);
		}

		// I0436: auch die klassenlistenuploads alle löschen.
		uploadRepository.deleteUploadsKlassenlisten(schuleId.identifier());

		return ResponsePayload.messageOnly(MessagePayload.info(applicationMessages.getString("deleteAllKlassen.success")));
	}

	@Override
	@Transactional
	public List<Klasse> importiereKlassen(final Identifier lehrerID, final Identifier schuleID, final List<KlasseRequestData> klassendaten) {

		List<Klasse> klassen = klassenRepository.findKlassenWithSchule(schuleID);
		List<Klasse> result = new ArrayList<>();

		for (KlasseRequestData data : klassendaten) {

			Optional<Klasse> optKlasse = klassen.stream()
				.filter(kl -> kl.name().equalsIgnoreCase(data.klasse().name().toLowerCase())).findFirst();

			if (optKlasse.isPresent()) {

				result.add(optKlasse.get());
			} else {

				Klasse neueKlasse = this.createKlasseAndFireEvent(data, lehrerID.identifier(), schuleID.identifier());
				result.add(neueKlasse);
			}
		}

		return result;
	}

	void authorizeAction(final String schulkuerzel, final String lehrerUuid, final String callingMethodForLog) {

		this.authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(lehrerUuid), new Identifier(schulkuerzel),
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

	@Override
	public WettbewerbID getWettbewerbID() {

		if (wettbewerbID == null) {

			this.wettbewerbID = this.wettbewerbService.aktuellerWettbewerb().get().id();
		}

		return wettbewerbID;
	}

	void resetWettbewerbIDForTest() {

		this.wettbewerbID = null;
	}
}
