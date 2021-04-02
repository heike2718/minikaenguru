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

import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
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
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	@Inject
	Event<SecurityIncidentRegistered> securityIncidentEvent;

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

			LOG.warn("Kein Loesungszettel mit UUID " + loesungszettelID + " bekannt");

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
	public boolean loesungszettelLoeschenWithAuthorizationCheck(final Identifier identifier, final Identifier veranstalterID) {

		Optional<Loesungszettel> opt = loesungszettelRepository.ofID(identifier);

		if (opt.isEmpty()) {

			return false;
		}

		authService.checkPermissionForTeilnahmenummer(veranstalterID,
			opt.get().getTheTeilnahmenummer(),
			"[loesungszettelLoeschenWithAuthorizationCheck - " + identifier.toString() + "]");

		Loesungszettel lz = opt.get();
		String lzWettbewerbID = lz.teilnahmeIdentifier().wettbewerbID();
		Integer lzJahr = Integer.valueOf(lzWettbewerbID);

		Wettbewerb wettbewerb = getWettbewerb();

		if (lzJahr < wettbewerb.id().jahr()) {

			String msg = "Veranstalter " + StringUtils.abbreviate(veranstalterID.identifier(), 11)
				+ ": versucht Lösungszettel aus Jahr " + lzJahr + " zu löschen. UUID=" + identifier.identifier();
			new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);

			String message = MessageFormat.format(applicationMessages.getString("loesungszettel.delete.gesperrt"),
				new Object[] { lzWettbewerbID });
			ResponsePayload responsePayload = ResponsePayload.messageOnly(
				MessagePayload.warn(message));

			throw new InvalidInputException(responsePayload);

		}

		boolean deleted = this.loesungszettelLoeschenWithoutAuthorizationCheck(identifier, veranstalterID.identifier());

		return deleted;

	}

	/**
	 * Löscht den gegebenen Lösungszettel. AuthorizationCheck muss hierfür bereits stattgefunden haben.
	 *
	 * @param  identifier
	 * @param  veranstalterUuid
	 * @return                  boolean
	 */
	@Transactional(TxType.REQUIRED)
	public boolean loesungszettelLoeschenWithoutAuthorizationCheck(final Identifier identifier, final String veranstalterUuid) {

		Optional<PersistenterLoesungszettel> optPersistenter = this.loesungszettelRepository.removeLoesungszettel(identifier,
			veranstalterUuid);

		if (optPersistenter.isPresent()) {

			PersistenterLoesungszettel geloeschter = optPersistenter.get();

			LoesungszettelRohdaten rohdatenAlt = new LoesungszettelRohdaten().withAntwortcode(geloeschter.getAntwortcode())
				.withNutzereingabe(geloeschter.getNutzereingabe()).withTypo(geloeschter.isTypo())
				.withWertungscode(geloeschter.getWertungscode());

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(geloeschter.getTeilnahmeart())
				.withTeilnahmenummer(geloeschter.getTeilnahmenummer())
				.withWettbewerbID(new WettbewerbID(geloeschter.getWettbewerbUuid()));

			loesungszettelDeleted = (LoesungszettelDeleted) new LoesungszettelDeleted(veranstalterUuid)
				.withUuid(identifier.identifier())
				.withKindID(geloeschter.getKindID())
				.withRohdatenAlt(rohdatenAlt)
				.withSpracheAlt(geloeschter.getSprache())
				.withTeilnahmeIdentifier(teilnahmeIdentifier);

		} else {

			LOG.warn(
				"Seltsame Konstellation: loesungszettel mit UUID=" + identifier
					+ " vorhanden, aber loesungszettelRepository.removeLoesungszettel returned Optional.empty");

			Optional<Kind> optKind = kinderRepository.findKindWithLoesungszettelId(identifier);

			if (optKind.isPresent()) {

				loesungszettelDeleted = (LoesungszettelDeleted) new LoesungszettelDeleted(veranstalterUuid)
					.withKindID(optKind.get().identifier().identifier())
					.withUuid(identifier.identifier());
			}

		}

		if (this.loesungszettelDeleted != null) {

			propagateLoesungszettelDeleted();
		}

		return optPersistenter.isPresent();

	}

	/**
	 * Sendet das loesungszettelDeleted-Objekt. Einer der EventHandler ist KinderService, der dafür sorgt, dass die
	 * Loesungszettel-Referenz genullt wird.
	 */
	private void propagateLoesungszettelDeleted() {

		if (loesungszettelDeletedEvent != null) {

			loesungszettelDeletedEvent.fire(loesungszettelDeleted);
		} else {

			System.out.println(loesungszettelDeleted.serializeQuietly());
		}
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

			LOG.warn("kein Lösungszettel mit UUID=" + identifier + " vorhanden.");
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

		if (loesungszetteldaten.kindID() == null) {

			String msg = MessageFormat.format(applicationMessages.getString("loesungszettel.addOrChange.invalidArguments"),
				new Object[] { "kindID null" });

			ResponsePayload result = ResponsePayload.messageOnly(MessagePayload.error(msg));

			throw new InvalidInputException(result);
		}

		Identifier kindID = new Identifier(loesungszetteldaten.kindID());

		Optional<Kind> optKind = kinderRepository.ofId(kindID);

		if (optKind.isEmpty()) {

			LOG.warn("== ins 1 == Kein Kind  mit UUID=" + kindID + " (requestDaten.kindID) bekannt");

			// TODO hier Telegram-Message! mit veranstalterID

			throw new NotFoundException();
		}

		Kind kind = optKind.get();

		Wettbewerb wettbewerb = getWettbewerb();

		TeilnahmeIdentifier teilnahmeIdentifier = getTeilnahmeIdentifierFromKind(kind, wettbewerb);

		authService.checkPermissionForTeilnahmenummer(veranstalterID,
			new Identifier(teilnahmeIdentifier.teilnahmenummer()),
			"[loesungszettelAnlegen - kindID=" + kindID + "]");

		Loesungszettel loesungszettel = null;
		boolean concurrent = false;

		Identifier kindLoesungszettelID = kind.loesungszettelID();

		Identifier responseLoesungszettelID = null;

		Optional<Loesungszettel> optKindLoesungszettel = kindLoesungszettelID == null ? Optional.empty()
			: loesungszettelRepository.ofID(kind.loesungszettelID());

		if (optKindLoesungszettel.isPresent()) {

			// #291: seltener Fall eines inserts, das auf ein Kind mit vorhandener Lösungszettelreferenz trifft.
			LOG.warn(
				"== ins 2 == veranstalter {}: kindID={} (requestDaten.kindID) referenziert vorhandenen Loesungszettel {} - unvollstaendig geloescht?",
				StringUtils.abbreviate(veranstalterID.identifier(), 11), kindID, kindLoesungszettelID);

			// TODO hier Telegram-Message! mit allen IDs

			loesungszettel = optKindLoesungszettel.get();

			if (!kindID.equals(loesungszettel.kindID())) {

				LOG.warn("== ins 2.1 == Loesungszettel referenziert anderes Kind: Abbruch inkonsistente Daten!!!");

				String msg = "Veranstalter " + StringUtils.abbreviate(veranstalterID.identifier(), 11)
					+ " - Lösungszettel anlegen: Daten sind inkonsistent: loesungszettelID="
					+ loesungszettel.identifier()
					+ ", requestDaten.kindID=" + kindID + ", loesungszettel.kindID=" + loesungszettel.kindID();

				doFireInconsistentDataAndExitMethodWithInvalidInputException(msg);

				// das ist nur pro Forma
				return null;

			} else {

				LOG.warn("== ins 2.1 == Loesungszettel konkurrierend erfasst");
				responseLoesungszettelID = kindLoesungszettelID;
				concurrent = true;
			}
		} else {

			loesungszettel = this.doCreateAndSaveNewLoesungszettel(loesungszetteldaten, wettbewerb, kind,
				veranstalterID.identifier());

			responseLoesungszettelID = loesungszettel.identifier();

		}

		LoesungszettelpunkteAPIModel result = this.mapLoesungszettel(responseLoesungszettelID, loesungszettel, wettbewerb);

		String messageFormatKey = concurrent ? "loesungszettel.addOrChange.concurrent" : "loesungszettel.addOrChange.success";

		String msg = MessageFormat.format(applicationMessages.getString(messageFormatKey),
			new Object[] { result.punkte(), Integer.valueOf(result.laengeKaengurusprung()) });

		MessagePayload messagePayload = concurrent ? MessagePayload.warn(msg) : MessagePayload.info(msg);
		ResponsePayload responsePayload = new ResponsePayload(messagePayload, result);
		return responsePayload;
	}

	private Loesungszettel doCreateAndSaveNewLoesungszettel(final LoesungszettelAPIModel loesungszetteldaten, final Wettbewerb wettbewerb, final Kind kind, final String veranstalterID) {

		Loesungszettel loesungszettel = new LoesungszettelCreator().createLoesungszettel(loesungszetteldaten, wettbewerb,
			kind);

		Identifier kindLoesungszettelID = loesungszettelRepository.addLoesungszettel(loesungszettel);
		kind.withLoesungszettelID(kindLoesungszettelID);

		kinderRepository.changeKind(kind);

		loesungszettelCreated = (LoesungszettelCreated) new LoesungszettelCreated(veranstalterID)
			.withKindID(kind.identifier().identifier())
			.withRohdatenNeu(loesungszettel.rohdaten())
			.withSpracheNeu(loesungszettel.sprache())
			.withTeilnahmeIdentifier(loesungszettel.teilnahmeIdentifier())
			.withUuid(kindLoesungszettelID.identifier());

		if (loesungszettelCreatedEvent != null) {

			loesungszettelCreatedEvent.fire(loesungszettelCreated);
		} else {

			System.out.println(loesungszettelCreated.serializeQuietly());
		}

		return loesungszettel.withIdentifier(kindLoesungszettelID);
	}

	/**
	 * @param msg
	 *            String
	 */
	private void doFireInconsistentDataAndExitMethodWithInvalidInputException(final String msg) {

		new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);

		String message = MessageFormat.format(applicationMessages.getString("loesungszettel.addOrChange.invalidArguments"),
			"es gibt inkonsistente Daten in der Datenbank");
		ResponsePayload responsePayload = ResponsePayload.messageOnly(MessagePayload.error(message));

		throw new InvalidInputException(responsePayload);
	}

	/**
	 * @param  kind
	 * @param  wettbewerb
	 * @return
	 */
	private TeilnahmeIdentifier getTeilnahmeIdentifierFromKind(final Kind kind, final Wettbewerb wettbewerb) {

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
			.withTeilnahmeart(kind.teilnahmeIdentifier().teilnahmeart())
			.withTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer())
			.withWettbewerbID(wettbewerb.id());
		return teilnahmeIdentifier;
	}

	/**
	 * Ändert die Daten eines vorhandenen Lösungszettels.
	 *
	 * @param  loesungszetteldaten
	 * @param  veranstalterID
	 * @return                     LoesungszettelpunkteAPIModel
	 */
	@Transactional
	public ResponsePayload loesungszettelAendern(final LoesungszettelAPIModel loesungszetteldaten, final Identifier veranstalterID) {

		if (loesungszetteldaten.uuid() == null) {

			String msg = MessageFormat.format(applicationMessages.getString("loesungszettel.addOrChange.invalidArguments"),
				new Object[] { "uuid null" });

			ResponsePayload result = ResponsePayload.messageOnly(MessagePayload.error(msg));

			throw new InvalidInputException(result);
		}

		if (loesungszetteldaten.kindID() == null) {

			String msg = MessageFormat.format(applicationMessages.getString("loesungszettel.addOrChange.invalidArguments"),
				new Object[] { "kindID null" });

			ResponsePayload result = ResponsePayload.messageOnly(MessagePayload.error(msg));

			throw new InvalidInputException(result);
		}

		Identifier loesungszettelID = new Identifier(loesungszetteldaten.uuid());
		Identifier kindID = new Identifier(loesungszetteldaten.kindID());
		Wettbewerb wettbewerb = getWettbewerb();

		Optional<Loesungszettel> optLoesungszettel = loesungszettelRepository
			.ofID(loesungszettelID);

		Optional<Kind> optKind = kinderRepository.ofId(kindID);

		if (optKind.isEmpty()) {

			LOG.warn(" == upd 1 == Kein Kind mit UUID=" + kindID + " (requestDaten.kindID) vorhanden (konkurrierend geloescht?)");

			if (optLoesungszettel.isPresent()) {

				Loesungszettel lz = optLoesungszettel.get();

				Integer lzJahr = Integer.valueOf(lz.teilnahmeIdentifier().wettbewerbID());

				if (lzJahr < wettbewerb.id().jahr()) {

					String msg = "Veranstalter " + StringUtils.abbreviate(veranstalterID.identifier(), 11)
						+ ": versucht Lösungszettel aus Jahr " + lzJahr + " zu ändern. UUID=" + lz.identifier();
					new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);

					String message = MessageFormat.format(applicationMessages.getString("loesungszettel.change.gesperrt"),
						new Object[] { lz.teilnahmeIdentifier().wettbewerbID() });
					ResponsePayload responsePayload = ResponsePayload.messageOnly(
						MessagePayload.warn(message));

					throw new InvalidInputException(responsePayload);

				}

				LOG.warn(" == upd 1.1 == Loesungszettel mit UUID=" + loesungszetteldaten.uuid() + " vorhanden, wird geloescht");

				// TODO: Telegram-Message!
				this.loesungszettelLoeschenWithoutAuthorizationCheck(loesungszettelID, veranstalterID.identifier());
			}

			throw new NotFoundException();
		}

		Kind kind = optKind.get();
		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
			.withTeilnahmeart(kind.teilnahmeIdentifier().teilnahmeart())
			.withTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer())
			.withWettbewerbID(wettbewerb.id());

		authService.checkPermissionForTeilnahmenummer(veranstalterID,
			new Identifier(teilnahmeIdentifier.teilnahmenummer()),
			"[loesungszettelAnlegen - kindID=" + kindID + "]");

		Identifier kindLoesungszettelID = kind.loesungszettelID();

		if (optLoesungszettel.isEmpty()) {

			LOG.warn("== upd 3 == Kein Loesungszettel mit UUID=" + loesungszettelID
				+ " (requestDaten.uuid) vorhanden (konkurrierend geloescht?)");

			Optional<Loesungszettel> optReferencedLoesungszettel = loesungszettelRepository.ofID(kind.loesungszettelID());

			if (optReferencedLoesungszettel.isEmpty()) {

				if (kind.loesungszettelID() != null) {

					LOG.warn("== upd 3.1 == kind referenziert nicht mehr vorhandenen Loesungszettel="
						+ kind.loesungszettelID().identifier() + " (loesungszettel unvollstaendig geloescht?)");

					// TODO Telegram-Message!

					Optional<Loesungszettel> optLoesungszettelMitKind = loesungszettelRepository
						.findLoesungszettelWithKindID(kindID);

					if (optLoesungszettelMitKind.isPresent()) {

						LOG.warn("== upd 3.2 == loesungszettelMitKind wird geaendert Kind="
							+ kindID + ", geaenderter Loesungszettel=" + kindLoesungszettelID);

						ResponsePayload result = doChangeLoesungszettel(optLoesungszettelMitKind.get().identifier(),
							loesungszetteldaten, wettbewerb,
							optLoesungszettelMitKind.get(), kind, veranstalterID.identifier());

						// TODO Telegram-Message!

						return result;

					} else {

						LOG.warn("== upd 3.2 == anderen loesun Kind="
							+ kindID + " (loesungszettel konkurrierend geloescht?)");

						Loesungszettel neuerLoesungszettel = this.doCreateAndSaveNewLoesungszettel(loesungszetteldaten, wettbewerb,
							kind,
							veranstalterID.identifier());

						LoesungszettelpunkteAPIModel result = this.mapLoesungszettel(neuerLoesungszettel.identifier(),
							neuerLoesungszettel,
							wettbewerb);
						String msg = MessageFormat.format(applicationMessages.getString("loesungszettel.addOrChange.success"),
							new Object[] { result.punkte(), Integer.valueOf(result.laengeKaengurusprung()) });

						ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), result);
						return responsePayload;
					}

				}

				LOG.warn("== upd 3.4 == update wird zu insert Kind="
					+ kindID + " (loesungszettel konkurrierend geloescht?)");

				Loesungszettel neuerLoesungszettel = this.doCreateAndSaveNewLoesungszettel(loesungszetteldaten, wettbewerb, kind,
					veranstalterID.identifier());

				LoesungszettelpunkteAPIModel result = this.mapLoesungszettel(neuerLoesungszettel.identifier(), neuerLoesungszettel,
					wettbewerb);
				String msg = MessageFormat.format(applicationMessages.getString("loesungszettel.addOrChange.success"),
					new Object[] { result.punkte(), Integer.valueOf(result.laengeKaengurusprung()) });

				ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), result);
				return responsePayload;

			} else {

				// optReferencedLoesungszettel.isPresent()

				Loesungszettel referencedLoesungszettel = optReferencedLoesungszettel.get();

				if (!kindID.equals(referencedLoesungszettel.kindID())) {

					LOG.warn(
						"== upd 3.5 == vom Kind {} referenzierter Loesungszettel zeigt auf anderes Kind {} - Abbruch inkonsistente Daten!!!",
						kindID, referencedLoesungszettel.kindID());

					// TODO: Telegram Message

					String msg = "Veranstalter " + StringUtils.abbreviate(veranstalterID.identifier(), 11)
						+ " - Lösungszettel ändern: Daten sind inkonsistent: requestDaten.UUID="
						+ loesungszettelID
						+ ", requestDaten.kindID=" + kindID + ", kind.loesungszettelID=" + kindLoesungszettelID
						+ ", referencedLoesungszettel.kindID="
						+ referencedLoesungszettel.kindID();

					doFireInconsistentDataAndExitMethodWithInvalidInputException(msg);

					return null;

				} else {

					// !kindID.equals(referencedLoesungszettel.kindID())

					LOG.warn("== upd 3.6 == Unerwartete Konstellation: zu aendernder loesungszettel mit UUID=" + loesungszettelID
						+ " existiert nicht, aber Kind hat einen Loesungszettel mit anderer UUID: kind.UUID=" + kindID
						+ ", kind.loesungszettelUUID=" + referencedLoesungszettel.identifier()
						+ ". referenzierter Loesungszettel wird geaendert.");

					// TODO: Telegram-Message!

					Loesungszettel loesungszettel = new LoesungszettelCreator().createLoesungszettel(loesungszetteldaten,
						wettbewerb,
						kind);

					return this.doChangeLoesungszettel(referencedLoesungszettel.identifier(), loesungszetteldaten, wettbewerb,
						loesungszettel,
						kind, veranstalterID.identifier());
				}
			}

		} else {

			Loesungszettel persistenter = optLoesungszettel.get();

			// if (!loesungszettelID.equals(kind.loesungszettelID())) {
			if (!kindID.equals(persistenter.kindID())) {

				LOG.warn(
					"== upd 4 == Kind {} referenziert keinen oder einen anderen Loesungszettel {} - requestDaten.loesungszettel={}",
					kind.identifier(), kind.loesungszettelID(), loesungszettelID);

				if (kind.loesungszettelID() != null) {

					LOG.warn(
						"== upd 4.1 == Abbruch inkonsistente Daten!!!");

					// TODO: Telegram Message

					String msg = "Veranstalter " + StringUtils.abbreviate(veranstalterID.identifier(), 11)
						+ " - Kind "
						+ kind.identifier()
						+ " referenziert keinen oder einen anderen als den zu ändernden Lösungszettel: kind.loesungszettelID="
						+ kindLoesungszettelID
						+ ", requestDaten.loesungszettel="
						+ loesungszettelID;

					doFireInconsistentDataAndExitMethodWithInvalidInputException(msg);

					return null;
				} else {

					Identifier gespeicherteLoesungszettelKindReferenz = persistenter.kindID();

					if (!kindID.equals(gespeicherteLoesungszettelKindReferenz)) {

						LOG.warn(
							"== upd 4.2 == persistenter Loesungszettel {} zeigt auf anderes Kind {}, requestData.KindID={} - Abbruch inkonsistente Daten!!!",
							loesungszettelID,
							gespeicherteLoesungszettelKindReferenz == null ? "null" : gespeicherteLoesungszettelKindReferenz,
							kindID);

						// TODO: Telegram Message

						String lzKindID = gespeicherteLoesungszettelKindReferenz == null ? "null"
							: gespeicherteLoesungszettelKindReferenz.identifier();

						String msg = "Veranstalter " + StringUtils.abbreviate(veranstalterID.identifier(), 11)
							+ " - Kind "
							+ kind.identifier()
							+ " referenziert keinen Lösungszettel, zu ändernder Lösungszettel referenziert nicht das Kind aus dem Request: loesungszettel.kindID="
							+ lzKindID
							+ ", requestDaten.kindID="
							+ kindID;

						doFireInconsistentDataAndExitMethodWithInvalidInputException(msg);
						return null;
					} else {

						LOG.debug(
							"== upd 4.3 == loesungszettel wird geaendert und kind.loesungszettelID wird gesetzt");

						// !kindID.equals(gespeicherteLoesungszettelKindReferenz

						return this.doChangeLoesungszettel(loesungszettelID, loesungszetteldaten, wettbewerb, persistenter, kind,
							veranstalterID.identifier());
					}
				}
			}

			return this.doChangeLoesungszettel(loesungszettelID, loesungszetteldaten, wettbewerb, persistenter, kind,
				veranstalterID.identifier());

		}
	}

	private ResponsePayload doChangeLoesungszettel(final Identifier loesungszettelID, final LoesungszettelAPIModel loesungszetteldaten, final Wettbewerb wettbewerb, final Loesungszettel persistenter, final Kind kind, final String veranstalterID) {

		Loesungszettel loesungszettel = new LoesungszettelCreator().createLoesungszettel(loesungszetteldaten, wettbewerb,
			kind);

		loesungszettel = loesungszettel.withIdentifier(loesungszettelID);
		loesungszettelRepository.updateLoesungszettel(loesungszettel);

		kind.withLoesungszettelID(loesungszettelID);
		kinderRepository.changeKind(kind);

		loesungszettelChanged = (LoesungszettelChanged) new LoesungszettelChanged(veranstalterID)
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
		String msg = MessageFormat.format(applicationMessages.getString("loesungszettel.addOrChange.success"),
			new Object[] { result.punkte(), Integer.valueOf(result.laengeKaengurusprung()) });

		ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info(msg), result);
		return responsePayload;

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
