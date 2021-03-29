// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.events.KindChanged;
import de.egladil.web.mk_gateway.domain.kinder.events.LoesungszettelChanged;
import de.egladil.web.mk_gateway.domain.kinder.events.LoesungszettelCreated;
import de.egladil.web.mk_gateway.domain.kinder.events.LoesungszettelDeleted;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;

/**
 * LoesungszettelService
 */
@ApplicationScoped
public class LoesungszettelService {

	private static final Logger LOG = LoggerFactory.getLogger(LoesungszettelService.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	Event<LoesungszettelCreated> loesungszettelCreatedEvent;

	@Inject
	Event<LoesungszettelChanged> loesungszettelChangedEvent;

	@Inject
	Event<LoesungszettelDeleted> loesungszettelDeletedEvent;

	@Inject
	Event<KindChanged> kindChangedEvent;

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	KinderRepository kinderRepository;

	@Inject
	AuthorizationService authService;

	@Inject
	WettbewerbService wettbewerbService;

	private LoesungszettelCreated loesungszettelCreated;

	private LoesungszettelChanged loesungszettelChanged;

	private LoesungszettelDeleted loesungszettelDeleted;

	private KindChanged kindChanged;

	public static LoesungszettelService createForIntegrationTest(final EntityManager entityManager) {

		LoesungszettelService result = new LoesungszettelService();
		result.loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);
		result.kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		result.authService = AuthorizationService.createForIntegrationTests(entityManager);
		result.wettbewerbService = WettbewerbService.createForIntegrationTest(entityManager);
		return result;

	}

	@Deprecated
	public static LoesungszettelService createForTest(final AuthorizationService authService, final WettbewerbService wettbewerbService, final KinderRepository kinderRepository, final LoesungszettelRepository loesungszettelRepository) {

		LoesungszettelService result = new LoesungszettelService();
		result.authService = authService;
		result.wettbewerbService = wettbewerbService;
		result.kinderRepository = kinderRepository;
		result.loesungszettelRepository = loesungszettelRepository;
		return result;
	}

	/**
	 * Sucht den Loesingszettel mit der gegeben ID und gibt ihn als APIModel zurück, sofern der Veranstalter berechtigt ist.
	 *
	 * @param  loesungszettelID
	 * @param  veranstalterID
	 * @return                  LoesungszettelAPIModel
	 */
	public LoesungszettelAPIModel findLoesungszettelWithID(final Identifier loesungszettelID, final Identifier veranstalterID) {

		Optional<Loesungszettel> opt = loesungszettelRepository
			.ofID(loesungszettelID);

		if (opt.isEmpty()) {

			LOG.warn("Kein Loesungszettel mit UUID {} bekannt", loesungszettelID);

			throw new NotFoundException();
		}

		Loesungszettel loesungszettel = opt.get();

		authService.checkPermissionForTeilnahmenummer(veranstalterID,
			loesungszettel.getTheTeilnahmenummer(),
			"[findLoesungszettelWithID - " + loesungszettelID.toString() + "]");

		if (loesungszettel.auswertungsquelle() != Auswertungsquelle.ONLINE) {

			throw new BadRequestException("Diese Methode darf nur bei Onlineauswertungen aufgerufen werden");
		}

		LoesungszettelAPIModel result = new LoesungszettelToAPIModelMapper().apply(loesungszettel);

		return result;
	}

	@Transactional
	public void loesungszettelLoeschenWithAuthorizationCheck(final Identifier identifier, final Identifier veranstalterID) {

		Optional<Loesungszettel> opt = loesungszettelRepository.ofID(identifier);

		if (opt.isEmpty()) {

			return;
		}

		authService.checkPermissionForTeilnahmenummer(veranstalterID,
			opt.get().getTheTeilnahmenummer(),
			"[loesungszettelLoeschenWithAuthorizationCheck - " + identifier.toString() + "]");

		this.loesungszettelLoeschenWithoutAuthorizationCheck(identifier,
			veranstalterID.identifier());
	}

	/**
	 * Löscht den gegebenen Lösungszettel. AuthorizationCheck muss hierfür bereits stattgefunden haben.
	 *
	 * @param  identifier
	 * @param  veranstalterUuid
	 * @return                  LoesungszettelDeleted - kann null sein
	 */
	@Transactional(TxType.REQUIRED)
	public Optional<PersistenterLoesungszettel> loesungszettelLoeschenWithoutAuthorizationCheck(final Identifier identifier, final String veranstalterUuid) {

		Optional<PersistenterLoesungszettel> optPersistenter = this.loesungszettelRepository.removeLoesungszettel(identifier,
			veranstalterUuid);

		if (optPersistenter.isPresent()) {

			PersistenterLoesungszettel persistenterLoesungszettel = optPersistenter.get();

			Sprache sprache = persistenterLoesungszettel.getSprache();

			LoesungszettelRohdaten rohdaten = new LoesungszettelRohdaten()
				.withAntwortcode(persistenterLoesungszettel.getAntwortcode())
				.withNutzereingabe(persistenterLoesungszettel.getNutzereingabe())
				.withTypo(persistenterLoesungszettel.isTypo())
				.withWertungscode(persistenterLoesungszettel.getWertungscode());

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
				.withTeilnahmeart(persistenterLoesungszettel.getTeilnahmeart())
				.withTeilnahmenummer(persistenterLoesungszettel.getTeilnahmenummer())
				.withWettbewerbID(new WettbewerbID(persistenterLoesungszettel.getWettbewerbUuid()));

			this.loesungszettelDeleted = (LoesungszettelDeleted) new LoesungszettelDeleted(veranstalterUuid)
				.withKindID(persistenterLoesungszettel.getKindID())
				.withRohdatenAlt(rohdaten)
				.withRohdatenNeu(null)
				.withSpracheAlt(sprache)
				.withSpracheNeu(null)
				.withTeilnahmeIdentifier(teilnahmeIdentifier)
				.withUuid(persistenterLoesungszettel.getUuid());

			if (this.loesungszettelDeleted != null) {

				if (loesungszettelDeletedEvent != null) {

					loesungszettelDeletedEvent.fire(loesungszettelDeleted);
				} else {

					System.out.println(loesungszettelDeleted.serializeQuietly());
				}
			}

		}

		return optPersistenter;
	}

	/**
	 * Ändert nur die Sprache eines Lösungszettels als Teil einer Transaktion.
	 *
	 * @param identifier
	 * @param sprache
	 *                   Sprache
	 */
	public void spracheLoesungszettelAendern(final Identifier identifier, final Sprache sprache, final String veranstalterUuid) {

		Optional<PersistenterLoesungszettel> optionalPersistenter = loesungszettelRepository
			.findPersistentenLoesungszettel(identifier);

		if (optionalPersistenter.isEmpty()) {

			LOG.warn("kein Lösungszettel mit UUID {} vorhanden.", identifier);
			return;
		}

		PersistenterLoesungszettel persistenterLoesungszettel = optionalPersistenter.get();

		authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid),
			new Identifier(persistenterLoesungszettel.getTeilnahmenummer()),
			"[spracheLoesungszettelAendern - " + identifier.toString() + "]");

		Sprache spracheAlt = persistenterLoesungszettel.getSprache();
		LoesungszettelRohdaten rohdaten = new LoesungszettelRohdaten()
			.withAntwortcode(persistenterLoesungszettel.getAntwortcode())
			.withNutzereingabe(persistenterLoesungszettel.getNutzereingabe())
			.withTypo(persistenterLoesungszettel.isTypo())
			.withWertungscode(persistenterLoesungszettel.getWertungscode());

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
			.withTeilnahmeart(persistenterLoesungszettel.getTeilnahmeart())
			.withTeilnahmenummer(persistenterLoesungszettel.getTeilnahmenummer())
			.withWettbewerbID(new WettbewerbID(persistenterLoesungszettel.getWettbewerbUuid()));

		persistenterLoesungszettel.setSprache(sprache);

		this.loesungszettelRepository.updateLoesungszettelInTransaction(persistenterLoesungszettel);

		loesungszettelChanged = (LoesungszettelChanged) new LoesungszettelChanged(veranstalterUuid)
			.withKindID(persistenterLoesungszettel.getKindID())
			.withRohdatenAlt(rohdaten)
			.withRohdatenNeu(rohdaten)
			.withSpracheAlt(spracheAlt)
			.withSpracheNeu(sprache)
			.withTeilnahmeIdentifier(teilnahmeIdentifier)
			.withUuid(persistenterLoesungszettel.getUuid());

		if (loesungszettelChangedEvent != null) {

			loesungszettelChangedEvent.fire(loesungszettelChanged);
		}
	}

	public Optional<LoesungszettelpunkteAPIModel> findPunkteWithID(final Identifier loesungszettelID) {

		if (loesungszettelID == null) {

			return Optional.empty();
		}

		Optional<Loesungszettel> optLoesungszettel = this.loesungszettelRepository.ofID(loesungszettelID);

		if (optLoesungszettel.isEmpty()) {

			return Optional.empty();
		}

		Loesungszettel loesungszettel = optLoesungszettel.get();

		LoesungszettelpunkteAPIModel result = new LoesungszettelpunkteAPIModel()
			.withLaengeKaengurusprung(loesungszettel.laengeKaengurusprung())
			.withLoesungszettelId(loesungszettel.identifier().identifier())
			.withPunkte(loesungszettel.punkteAsString());

		return Optional.of(result);

	}

	/**
	 * Legt einen neuen Lösungszettel an.
	 *
	 * @param  loesungszetteldaten
	 *                             LoesungszettelAPIModel
	 * @param  veranstalterUuid
	 *                             String
	 * @return
	 */
	@Transactional
	public ResponsePayload loesungszettelAnlegen(final LoesungszettelAPIModel loesungszetteldaten, final Identifier veranstalterID) {

		Identifier kindID = new Identifier(loesungszetteldaten.kindID());

		Optional<Kind> optKind = kinderRepository.ofId(kindID);

		if (optKind.isEmpty()) {

			LOG.warn("Kein Kind  mit UUID {} bekannt", kindID);

			throw new NotFoundException();
		}

		Kind kind = optKind.get();

		Wettbewerb wettbewerb = getWettbewerb();

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
			.withTeilnahmeart(kind.teilnahmeIdentifier().teilnahmeart())
			.withTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer())
			.withWettbewerbID(wettbewerb.id());

		authService.checkPermissionForTeilnahmenummer(veranstalterID,
			new Identifier(teilnahmeIdentifier.teilnahmenummer()),
			"[loesungszettelAnlegen - kindID=" + kindID + "]");

		Loesungszettel loesungszettel = null;
		boolean concurrent = false;

		Identifier loesungszettelID = kind.loesungszettelID();

		if (loesungszettelID != null) {

			Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository.ofID(kind.loesungszettelID());

			if (optLoesungszettel.isPresent()) {

				loesungszettel = optLoesungszettel.get();
				concurrent = true;
			} else {

				LOG.error(
					"Dateninkonsistenz: kind.uuid={}, kind.loesungszettelID={}: kein Lösungszettel mit dieser UUID vorhanden. Kind wird aktualisiert",
					kind.identifier(), kind.loesungszettelID());

				kind.withLoesungszettelID(null);
				kinderRepository.changeKind(kind);

				kindChanged = (KindChanged) new KindChanged(veranstalterID.identifier())
					.withKlassenstufe(kind.klassenstufe())
					.withSprache(kind.sprache())
					.withTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer())
					.withKindID(kind.identifier().identifier())
					.withKlasseID(kind.klasseID().identifier())
					.withLoesungszettelID(null);

				if (kindChangedEvent != null) {

					kindChangedEvent.fire(kindChanged);
				} else {

					System.out.println(kindChanged.typeName() + ": " + kindChanged.serializeQuietly());
				}

				String msg = MessageFormat.format(applicationMessages.getString("loesungszettel.add.datenInkonsistent"),
					new Object[0]);

				ResponsePayload responsePayload = new ResponsePayload(MessagePayload.warn(msg), null);
				return responsePayload;
			}
		} else {

			loesungszettel = new LoesungszettelCreator().createLoesungszettel(loesungszetteldaten, wettbewerb,
				kind);

			loesungszettelID = loesungszettelRepository.addLoesungszettel(loesungszettel);
			kind.withLoesungszettelID(loesungszettelID);

			kinderRepository.changeKind(kind);

			loesungszettelCreated = (LoesungszettelCreated) new LoesungszettelCreated(veranstalterID.identifier())
				.withKindID(kind.identifier().identifier())
				.withRohdatenNeu(loesungszettel.rohdaten())
				.withSpracheNeu(loesungszettel.sprache())
				.withTeilnahmeIdentifier(loesungszettel.teilnahmeIdentifier())
				.withUuid(loesungszettelID.identifier());

			if (loesungszettelCreatedEvent != null) {

				loesungszettelCreatedEvent.fire(loesungszettelCreated);
			} else {

				System.out.println(loesungszettelCreated.serializeQuietly());
			}

			kindChanged = (KindChanged) new KindChanged(veranstalterID.identifier())
				.withKlassenstufe(kind.klassenstufe())
				.withSprache(kind.sprache())
				.withTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer())
				.withKindID(kind.identifier().identifier())
				.withKlasseID(kind.klasseID().identifier())
				.withLoesungszettelID(loesungszettelID.identifier());

			if (kindChangedEvent != null) {

				kindChangedEvent.fire(kindChanged);
			} else {

				System.out.println(kindChanged.typeName() + ": " + kindChanged.serializeQuietly());
			}

		}

		LoesungszettelpunkteAPIModel result = this.mapLoesungszettel(loesungszettelID, loesungszettel, wettbewerb);

		String messageFormatKey = concurrent ? "loesungszettel.addOrChange.concurrent" : "loesungszettel.addOrChange.success";

		String msg = MessageFormat.format(applicationMessages.getString(messageFormatKey),
			new Object[] { result.punkte(), Integer.valueOf(result.laengeKaengurusprung()) });

		MessagePayload messagePayload = concurrent ? MessagePayload.warn(msg) : MessagePayload.info(msg);
		ResponsePayload responsePayload = new ResponsePayload(messagePayload, result);
		return responsePayload;
	}

	/**
	 * Ändert die Daten eines vorhandenen Lösungszettels.
	 *
	 * @param  loesungszetteldaten
	 * @param  veranstalterID
	 * @return                     LoesungszettelpunkteAPIModel
	 */
	@Transactional
	public LoesungszettelpunkteAPIModel loesungszettelAendern(final LoesungszettelAPIModel loesungszetteldaten, final Identifier veranstalterID) {

		Identifier loesungszettelID = new Identifier(loesungszetteldaten.uuid());

		Optional<Loesungszettel> opt = loesungszettelRepository
			.ofID(loesungszettelID);

		if (opt.isEmpty()) {

			LOG.warn("Kein Loesungszettel mit UUID {} bekannt", loesungszettelID);

			throw new NotFoundException();
		}

		Loesungszettel persistenter = opt.get();

		authService.checkPermissionForTeilnahmenummer(veranstalterID,
			persistenter.getTheTeilnahmenummer(),
			"[loesungszettelAendern - " + loesungszettelID.toString() + "]");

		Optional<Kind> optKind = kinderRepository.ofId(persistenter.kindID());

		if (optKind.isEmpty()) {

			LOG.warn("Kein Kind mit UUID {} bekannt", persistenter.kindID());

			throw new NotFoundException();
		}

		Kind kind = optKind.get();

		if (!loesungszettelID.equals(kind.loesungszettelID())) {

			String msg = "Für Kind mit UUID=" + kind.identifier() + " passen kind.loesungszettelID=" + kind.loesungszettelID()
				+ " und loesungszettel.uuid=" + loesungszettelID + " nicht zueinander. Triggering user="
				+ StringUtils.abbreviate(veranstalterID.identifier(), 11);
			LOG.error("Fehler bei Aenderung Loesungszettel: " + msg);

			new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);

			throw new MkGatewayRuntimeException(msg);
		}

		Wettbewerb wettbewerb = getWettbewerb();
		Loesungszettel loesungszettel = new LoesungszettelCreator().createLoesungszettel(loesungszetteldaten, wettbewerb,
			kind);

		loesungszettel = loesungszettel.withIdentifier(loesungszettelID);
		loesungszettelRepository.updateLoesungszettel(loesungszettel);

		loesungszettelChanged = (LoesungszettelChanged) new LoesungszettelChanged(veranstalterID.identifier())
			.withKindID(kind.identifier().identifier())
			.withRohdatenAlt(persistenter.rohdaten())
			.withRohdatenNeu(loesungszettel.rohdaten())
			.withSpracheAlt(persistenter.sprache())
			.withSpracheNeu(loesungszettel.sprache())
			.withTeilnahmeIdentifier(loesungszettel.teilnahmeIdentifier())
			.withUuid(loesungszettelID.identifier());

		if (loesungszettelChangedEvent != null) {

			loesungszettelChangedEvent.fire(loesungszettelChanged);
		} else {

			System.out.println(loesungszettelChanged.serializeQuietly());
		}

		LoesungszettelpunkteAPIModel result = this.mapLoesungszettel(loesungszettelID, loesungszettel, wettbewerb);

		return result;
	}

	LoesungszettelpunkteAPIModel mapLoesungszettel(final Identifier loesungszettelID, final Loesungszettel loesungszettel, final Wettbewerb wettbewerb) {

		List<LoesungszettelZeileAPIModel> zeilen = new ArrayList<>();
		char[] eingabebuchstaben = loesungszettel.rohdaten().nutzereingabe().toCharArray();
		int anzahlSpalten = loesungszettel.klassenstufe().getAnzahlSpalten();

		List<String> aufgabennummern = loesungszettel.klassenstufe().getAufgabennummern(wettbewerb.id().jahr());

		for (int i = 0; i < eingabebuchstaben.length; i++) {

			LoesungszettelZeileAPIModel zeile = new LoesungszettelZeileAPIModel().withAnzahlSpalten(anzahlSpalten)
				.withEingabe(ZulaessigeLoesungszetteleingabe.valueOfChar(eingabebuchstaben[i])).withIndex(i)
				.withName(aufgabennummern.get(i));
			zeilen.add(zeile);
		}

		LoesungszettelpunkteAPIModel result = new LoesungszettelpunkteAPIModel()
			.withLaengeKaengurusprung(loesungszettel.laengeKaengurusprung())
			.withPunkte(loesungszettel.punkteAsString())
			.withLoesungszettelId(loesungszettelID.identifier())
			.withZeilen(zeilen);

		return result;
	}

	private Wettbewerb getWettbewerb() {

		return this.wettbewerbService.aktuellerWettbewerb().get();
	}

	KindChanged getKindChanged() {

		return kindChanged;
	}

	LoesungszettelCreated getLoesungszettelCreated() {

		return loesungszettelCreated;
	}

	LoesungszettelChanged getLoesungszettelChanged() {

		return loesungszettelChanged;
	}

	LoesungszettelDeleted getLoesungszettelDeleted() {

		return loesungszettelDeleted;
	}
}
