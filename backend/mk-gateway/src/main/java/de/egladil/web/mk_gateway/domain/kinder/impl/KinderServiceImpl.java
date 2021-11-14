// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kinder.Dublettenpruefer;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KindAdaptable;
import de.egladil.web.mk_gateway.domain.kinder.KindAdapter;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.KinderService;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.kinder.events.KindChanged;
import de.egladil.web.mk_gateway.domain.kinder.events.KindCreated;
import de.egladil.web.mk_gateway.domain.kinder.events.KindDeleted;
import de.egladil.web.mk_gateway.domain.klassenlisten.KindImportVO;
import de.egladil.web.mk_gateway.domain.klassenlisten.impl.KindImportDaten;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.OnlineLoesungszettelService;
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
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.TeilnahmenHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.VeranstalterHibernateRepository;

/**
 * KinderServiceImpl
 */
@ApplicationScoped
public class KinderServiceImpl implements KinderService {

	private static final Logger LOG = LoggerFactory.getLogger(KinderServiceImpl.class);

	private final Dublettenpruefer dublettenpruefer = new Dublettenpruefer();

	private final KindAdapter kindAdapter = new KindAdapter();

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

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
	OnlineLoesungszettelService loesungszettelService;

	@Inject
	DomainEventHandler domainEventHandler;

	private WettbewerbID wettbewerbID;

	private KindCreated kindCreated;

	private KindChanged kindChanged;

	private KindDeleted kindDeleted;

	public static KinderServiceImpl createForTest(final AuthorizationService authService, final KinderRepository kinderRepository, final TeilnahmenRepository teilnahmenRepository, final VeranstalterRepository veranstalterRepository, final WettbewerbService wettbewerbService, final OnlineLoesungszettelService loesungszettelService, final KlassenRepository klassenRepository) {

		KinderServiceImpl result = new KinderServiceImpl();
		result.authService = authService;
		result.kinderRepository = kinderRepository;
		result.teilnahmenRepository = teilnahmenRepository;
		result.veranstalterRepository = veranstalterRepository;
		result.wettbewerbService = wettbewerbService;
		result.loesungszettelService = loesungszettelService;
		result.klassenRepository = klassenRepository;
		return result;
	}

	public static KinderServiceImpl createForIntegrationTest(final EntityManager em) {

		KinderServiceImpl result = new KinderServiceImpl();
		result.authService = AuthorizationService.createForIntegrationTest(em);
		result.kinderRepository = KinderHibernateRepository.createForIntegrationTest(em);
		result.klassenRepository = KlassenHibernateRepository.createForIntegrationTest(em);
		result.loesungszettelService = OnlineLoesungszettelService.createForIntegrationTest(em);
		result.teilnahmenRepository = TeilnahmenHibernateRepository.createForIntegrationTest(em);
		result.veranstalterRepository = VeranstalterHibernateRepository.createForIntegrationTest(em);
		result.wettbewerbService = WettbewerbService.createForIntegrationTest(em);
		result.domainEventHandler = DomainEventHandler.createForIntegrationTest(em);
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
	@Override
	public boolean pruefeDublette(final KindRequestData daten, final String veranstalterUuid) {

		final KindAdapter kindAdapter = new KindAdapter();

		Teilnahme teilnahme = getAktuelleTeilnahme(daten, veranstalterUuid, "pruefeDublette");

		authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(veranstalterUuid), teilnahme.teilnahmenummer(),
			"[kindAnlegen - teilnahmenummer=" + teilnahme.teilnahmenummer().identifier() + "]");

		List<Kind> kinder = findWithTeilnahme(TeilnahmeIdentifierAktuellerWettbewerb.createFromTeilnahme(teilnahme));
		List<KindAdaptable> adaptedKinder = new ArrayList<>();

		if (kinder.isEmpty()) {

			return false;
		}

		if (daten.klasseUuid() != null) {

			final Identifier klasseID = new Identifier(daten.klasseUuid());

			adaptedKinder = kinder.stream().filter(k -> klasseID.equals(k.klasseID())).map(k -> kindAdapter.adaptKind(k))
				.collect(Collectors.toList());

		} else {

			adaptedKinder = kinder.stream().map(k -> kindAdapter.adaptKind(k))
				.collect(Collectors.toList());
		}

		return this.koennteDubletteSein(kindAdapter.adaptKindRequestData(daten), adaptedKinder);
	}

	@Override
	public List<Kind> findWithSchulteilname(final String schulkuerzel) {

		TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier = TeilnahmeIdentifierAktuellerWettbewerb
			.createForSchulteilnahme(schulkuerzel);

		return this.findWithTeilnahme(teilnahmeIdentifier);

	}

	List<Kind> findWithTeilnahme(final TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier) {

		return kinderRepository.withTeilnahme(teilnahmeIdentifier);
	}

	private boolean koennteDubletteSein(final KindAdaptable kind, final List<KindAdaptable> kinder) {

		for (KindAdaptable k : kinder) {

			if (Boolean.TRUE == dublettenpruefer.apply(k, kind)) {

				return true;
			}
		}

		return false;
	}

	@Override
	public String getWarnungstext(final KindEditorModel kind) {

		if (StringUtils.isBlank(kind.klasseUuid())) {

			return getWarnungPrivat(kind);
		}

		return getWarnungSchulkind(kind);

	}

	String getWarnungSchulkind(final KindEditorModel kind) {

		String klassenstufeSuffix = kind.klassenstufe().klassenstufe().getLabelSuffix();

		if (StringUtils.isNotBlank(kind.nachname()) && StringUtils.isNotBlank(kind.zusatz())) {

			return MessageFormat.format(applicationMessages.getString("checkKindDuplikat.klasse.vornameNachnameZusatz"),
				new Object[] { kind.vorname(), kind.nachname(), kind.zusatz(), klassenstufeSuffix });

		}

		if (StringUtils.isNotBlank(kind.nachname())) {

			return MessageFormat.format(applicationMessages.getString("checkKindDuplikat.klasse.vornameNachname"),
				new Object[] { kind.vorname(), kind.nachname(), klassenstufeSuffix });
		}

		return MessageFormat.format(applicationMessages.getString("checkKindDuplikat.klasse.nurVorname"),
			new Object[] { kind.vorname(), klassenstufeSuffix });
	}

	String getWarnungPrivat(final KindEditorModel kind) {

		if (StringUtils.isNotBlank(kind.nachname()) && StringUtils.isNotBlank(kind.zusatz())) {

			return MessageFormat.format(applicationMessages.getString("checkKindDuplikat.privat.vornameNachnameZusatz"),
				new Object[] { kind.klassenstufe().label(), kind.vorname(), kind.nachname(), kind.zusatz() });

		}

		if (StringUtils.isNotBlank(kind.nachname())) {

			return MessageFormat.format(applicationMessages.getString("checkKindDuplikat.privat.vornameNachname"),
				new Object[] { kind.klassenstufe().label(), kind.vorname(), kind.nachname() });
		}

		return MessageFormat.format(applicationMessages.getString("checkKindDuplikat.privat.nurVorname"),
			new Object[] { kind.klassenstufe().label(), kind.vorname() });
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
	@Override
	@Transactional
	public KindAPIModel kindAnlegen(final KindRequestData daten, final String veranstalterUuid) {

		Teilnahme teilnahme = getAktuelleTeilnahme(daten, veranstalterUuid, "kindAnlegen");

		authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(veranstalterUuid), teilnahme.teilnahmenummer(),
			"[kindAnlegen - teilnahmenummer=" + teilnahme.teilnahmenummer().identifier() + "]");

		Identifier klasseID = StringUtils.isBlank(daten.klasseUuid()) ? null : new Identifier(daten.klasseUuid());

		Kind kind = new Kind().withDaten(daten.kind())
			.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createFromTeilnahme(teilnahme))
			.withLandkuerzel(daten.kuerzelLand())
			.withKlasseID(klasseID);

		if (klasseID != null && kind.landkuerzel() == null) {

			String msg = "Schulkind wird ohne landkuerzel angelegt: " + daten.logData();
			LOG.warn(msg);

			new LoggableEventDelegate().fireDataInconsistencyEvent(msg, domainEventHandler);
		}

		Kind gespeichertesKind = kinderRepository.addKind(kind);

		KindAPIModel result = KindAPIModel.createFromKind(gespeichertesKind, Optional.empty());

		kindCreated = (KindCreated) new KindCreated(veranstalterUuid)
			.withKindID(result.uuid())
			.withKlassenstufe(gespeichertesKind.klassenstufe())
			.withSprache(gespeichertesKind.sprache())
			.withTeilnahmenummer(teilnahme.teilnahmenummer().identifier())
			.withKlasseID(gespeichertesKind.klasseID() == null ? null : gespeichertesKind.klasseID().identifier());

		if (this.domainEventHandler != null) {

			this.domainEventHandler.handleEvent(kindCreated);
		} else {

			System.out.println(kindCreated.typeName() + ": " + kindCreated.serializeQuietly());
		}

		return result;
	}

	@Override
	public List<Kind> importiereKinder(final Identifier veranstalterID, final String schulkuerzel, final List<KindImportVO> importDaten) {

		List<Kind> result = new ArrayList<>();

		for (KindImportVO item : importDaten) {

			KindImportDaten kindImportDaten = item.getKindImportDaten();

			if (kindImportDaten.isNichtImportiert()) {

				continue;
			}

			if (kindImportDaten.getKindRequestData() != null) {

				KindRequestData kindRequestData = kindImportDaten.getKindRequestData();
				Kind kind = new Kind().withDaten(kindRequestData.kind())
					.withTeilnahmeIdentifier(TeilnahmeIdentifierAktuellerWettbewerb.createForSchulteilnahme(schulkuerzel))
					.withLandkuerzel(kindRequestData.kuerzelLand())
					.withKlasseID(new Identifier(kindRequestData.klasseUuid()));
				kind.setDublettePruefen(item.isDublettePruefen());
				kind.setKlassenstufePruefen(kindImportDaten.isKlassenstufePruefen());
				kind.setImportiert(true);

				Kind gespeichertesKind = kinderRepository.addKind(kind);
				result.add(gespeichertesKind);

				kindCreated = (KindCreated) new KindCreated().withKindID(gespeichertesKind.identifier().identifier())
					.withKlasseID(gespeichertesKind.klasseID().identifier())
					.withKlassenstufe(gespeichertesKind.klassenstufe())
					.withSprache(gespeichertesKind.sprache())
					.withTeilnahmenummer(schulkuerzel);

				domainEventHandler.handleEvent(kindCreated);
			}
		}
		return result;
	}

	@Override
	@Transactional
	public KindAPIModel kindAendern(final KindRequestData daten, final String veranstalterUuid) {

		Optional<Kind> optKind = kinderRepository.ofId(new Identifier(daten.uuid()));

		if (optKind.isEmpty()) {

			LOG.warn("{} versucht Kind zu ändern, das es nicht gibt", veranstalterUuid);
			throw new NotFoundException();
		}

		Kind kind = optKind.get();

		authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(veranstalterUuid),
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

		// das KindEditorModel hat die Attribute vorname, nachname, zusatz, klassenstufe, sprache, klasseUuid
		Kind geaendertesKind = new Kind(new Identifier(daten.uuid())).withDaten(daten.kind())
			.withLandkuerzel(kind.landkuerzel())
			.withTeilnahmeIdentifier(kind.teilnahmeIdentifier())
			.withLoesungszettelID(kind.loesungszettelID());

		if (geaendertesKind.klasseID() != null && geaendertesKind.landkuerzel() == null) {

			String msg = "Schulkind verliert beim Ändern sein landkuerzel: " + daten.logData() + ", altes landkuerzel="
				+ kind.landkuerzel();
			LOG.warn(msg);

			new LoggableEventDelegate().fireDataInconsistencyEvent(msg, domainEventHandler);
		}

		boolean changed = this.kinderRepository.changeKind(geaendertesKind);

		Optional<LoesungszettelpunkteAPIModel> optPunkte = this.loesungszettelService
			.findPunkteWithID(geaendertesKind.loesungszettelID());

		KindAPIModel result = KindAPIModel.createFromKind(geaendertesKind, optPunkte);

		if (changed) {

			String klasseID = geaendertesKind.klasseID() == null ? null : geaendertesKind.klasseID().identifier();
			String loesungszettelID = geaendertesKind.loesungszettelID() == null ? null
				: geaendertesKind.loesungszettelID().identifier();

			kindChanged = (KindChanged) new KindChanged(veranstalterUuid)
				.withKlassenstufe(geaendertesKind.klassenstufe())
				.withSprache(geaendertesKind.sprache())
				.withTeilnahmenummer(geaendertesKind.teilnahmeIdentifier().teilnahmenummer())
				.withKindID(geaendertesKind.identifier().identifier())
				.withKlasseID(klasseID)
				.withLoesungszettelID(loesungszettelID);

			if (domainEventHandler != null) {

				domainEventHandler.handleEvent(kindChanged);
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
	@Override
	@Transactional
	public KindAPIModel kindLoeschen(final String uuid, final String veranstalterUuid) {

		Optional<Kind> optKind = kinderRepository.ofId(new Identifier(uuid));

		if (optKind.isEmpty()) {

			LOG.warn("{} versucht Kind zu löschen, das es nicht gibt", veranstalterUuid);
			throw new NotFoundException();
		}

		Kind kind = optKind.get();

		authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(veranstalterUuid),
			new Identifier(kind.teilnahmeIdentifier().teilnahmenummer()), "[kindLoeschen - kindUUID=" + uuid + "]");

		kindLoeschenWithoutAuthorizationCheck(kind, veranstalterUuid);

		return KindAPIModel.createFromKind(kind, Optional.empty());
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
				.withTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer())
				.withKlasseID(kind.klasseID() == null ? null : kind.klasseID().identifier())
				.withLoesungszettelID(kind.loesungszettelID() == null ? null : kind.loesungszettelID().identifier());

			if (this.domainEventHandler != null) {

				this.domainEventHandler.handleEvent(kindDeleted);
			} else {

				System.out.println(kindDeleted.typeName() + ": " + kindDeleted.serializeQuietly());
			}
		}

		return removed;

	}

	@Override
	public List<KindAPIModel> kinderZuTeilnahmeLaden(final String teilnahmenummer, final String veranstalterUuid) {

		this.authService.checkPermissionForTeilnahmenummerAndReturnRolle(new Identifier(veranstalterUuid),
			new Identifier(teilnahmenummer),
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

		kinder.forEach(kind -> {

			Optional<LoesungszettelpunkteAPIModel> optPunkte = loesungszettelService.findPunkteWithID(kind.loesungszettelID());

			result.add(KindAPIModel.createFromKind(kind, optPunkte));

		});

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

			new LoggableEventDelegate().fireDataInconsistencyEvent(msg, domainEventHandler);
			throw new NotFoundException(
				msg);
		}

		return optTeilnahme.get();
	}

	/**
	 * Gibt für die gegebenen Klassen die Anzahl der Kinder zurück und die Anzahl der Kinder mit möglichen Fehlern.
	 *
	 * @param  klassen
	 * @return         Map mit klasse.identifier als key und Pair als value. L ist Anzahl der Kinder, R ist Anzahl der zu prüfenden
	 *                 Kinder
	 */
	Map<Identifier, Pair<Long, Long>> countKinder(final List<Klasse> klassen) {

		Map<Identifier, Pair<Long, Long>> result = new HashMap<>();

		for (Klasse klasse : klassen) {

			long anzahlKinder = this.kinderRepository.countKinderInKlasse(klasse);
			long anzahlZuPruefen = this.kinderRepository.countKinderZuPruefen(klasse);
			result.put(klasse.identifier(), Pair.of(anzahlKinder, anzahlZuPruefen));

		}

		return result;

	}

	/**
	 * Gibt für die gegebenen Klassen die Anzahl der KinderDatenTeilnahmeurkundenMapper zurück.
	 *
	 * @param  klassen
	 * @return         Map mit klasse.identifier als key
	 */
	Map<Identifier, Long> countLoesungszettel(final List<Klasse> klassen) {

		Map<Identifier, Long> result = new HashMap<>();

		for (Klasse klasse : klassen) {

			long anzahlKinder = this.kinderRepository.countLoesungszettelInKlasse(klasse);
			result.put(klasse.identifier(), Long.valueOf(anzahlKinder));

		}

		return result;

	}

	// #291: Erkenntnis - Dies war ein Experiment zum DDD, aber es hat sich gezeigt, dass die Wahrscheinlichkeit von inkonsistenten
	// Daten nicht 0 ist
	// und inkonsistente Daten durch kapseln in eine Transaktion hier besser vermieden werden sollteb, weil das Erreichen
	// konsistenter Daten anderenfalls zu vollkommen unnötig komplexem Code mit kaum zu beherrschender Testbasis führt.

	// @Transactional
	// public void handleLoesungszettelDeleted(@Observes final LoesungszettelDeleted loesungszettelDeletedEvent) {
	//
	// if (loesungszettelDeletedEvent.kindID() != null) {
	//
	// Optional<Kind> optKind = kinderRepository.ofId(new Identifier(loesungszettelDeletedEvent.kindID()));
	//
	// if (optKind.isPresent()) {
	//
	// Kind kind = optKind.get();
	// kind.deleteLoesungszettel();
	//
	// this.kinderRepository.changeKind(kind);
	// }
	// }
	// }

	KindCreated getKindCreated() {

		return kindCreated;
	}

	@Override
	public WettbewerbID getWettbewerbID() {

		if (wettbewerbID == null) {

			this.wettbewerbID = this.wettbewerbService.aktuellerWettbewerb().get().id();
		}

		return wettbewerbID;
	}

}
