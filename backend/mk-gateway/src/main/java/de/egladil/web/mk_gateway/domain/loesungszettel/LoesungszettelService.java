// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.events.LoesungszettelChanged;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.statistik.functions.PunkteStringMapper;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * LoesungszettelService
 */
@ApplicationScoped
public class LoesungszettelService {

	private static final Logger LOG = LoggerFactory.getLogger(LoesungszettelService.class);

	@Inject
	Event<LoesungszettelChanged> loesungszettelChangedEvent;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	KinderRepository kinderRepository;

	@Inject
	AuthorizationService authService;

	@Inject
	WettbewerbService wettbewerbService;

	private Wettbewerb aktuellerWettbewerb;

	private LoesungszettelChanged loesungszettelChanged;

	public static LoesungszettelService createForTest(final AuthorizationService authService, final WettbewerbService wettbewerbService, final KinderRepository kinderRepository, final LoesungszettelRepository loesungszettelRepository) {

		LoesungszettelService result = new LoesungszettelService();
		result.authService = authService;
		result.wettbewerbService = wettbewerbService;
		result.kinderRepository = kinderRepository;
		result.loesungszettelRepository = loesungszettelRepository;
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

		return this.loesungszettelLoeschenWithoutAuthorizationCheck(identifier, veranstalterID.identifier());

	}

	/**
	 * Löscht den gegebenen Lösungszettel. AuthorizationCheck muss hierfür bereits stattgefunden haben.
	 *
	 * @param  identifier
	 * @param  veranstalterUuid
	 * @return                  boolean
	 */
	public boolean loesungszettelLoeschenWithoutAuthorizationCheck(final Identifier identifier, final String veranstalterUuid) {

		return this.loesungszettelRepository.removeLoesungszettel(identifier, veranstalterUuid);
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

		persistenterLoesungszettel.withSprache(sprache);

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

		String punkte = new PunkteStringMapper().apply(loesungszettel.punkte());

		LoesungszettelpunkteAPIModel result = new LoesungszettelpunkteAPIModel()
			.withLaengeKaengurusprung(loesungszettel.laengeKaengurusprung())
			.withLoesungszettelId(loesungszettel.identifier().identifier())
			.withPunkte(punkte);

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
	public LoesungszettelpunkteAPIModel loesungszettelAnlegen(final LoesungszettelAPIModel loesungszetteldaten, final Identifier veranstalterID) {

		Identifier kindID = new Identifier(loesungszetteldaten.kindID());

		Optional<Kind> optKind = kinderRepository.ofId(kindID);

		if (optKind.isEmpty()) {

			LOG.warn("Kein Kind  mit UUID {} bekannt", kindID);

			throw new NotFoundException();
		}

		Kind kind = optKind.get();

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
			.withTeilnahmeart(kind.teilnahmeIdentifier().teilnahmeart())
			.withTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer())
			.withWettbewerbID(getWettbewerb().id());

		authService.checkPermissionForTeilnahmenummer(veranstalterID,
			new Identifier(teilnahmeIdentifier.teilnahmenummer()),
			"[loesungszettelAnlegen - kindID=" + kindID + "]");

		Loesungszettel loesungszettel = new LoesungszettelCreator().createLoesungszettel(loesungszetteldaten, getWettbewerb(),
			kind);

		Identifier loesungszettelID = loesungszettelRepository.addLoesungszettel(loesungszettel);
		kind.withLoesungszettelID(loesungszettelID);

		kinderRepository.changeKind(kind);

		String punkte = new PunkteStringMapper().apply(loesungszettel.punkte());

		return new LoesungszettelpunkteAPIModel().withLaengeKaengurusprung(loesungszettel.laengeKaengurusprung()).withPunkte(punkte)
			.withLoesungszettelId(loesungszettelID.identifier());
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

		authService.checkPermissionForTeilnahmenummer(veranstalterID,
			opt.get().getTheTeilnahmenummer(),
			"[findLoesungszettelWithID - " + loesungszettelID.toString() + "]");

		Loesungszettel loesungszettel = opt.get();

		if (loesungszettel.auswertungsquelle() != Auswertungsquelle.ONLINE) {

			throw new BadRequestException("Diese Methode darf nur bei Onlineauswertungen aufgerufen werden");
		}

		LoesungszettelAPIModel result = new LoesungszettelToAPIModelMapper().apply(loesungszettel);

		return result;
	}

	public LoesungszettelpunkteAPIModel loesungszettelAendern(final LoesungszettelAPIModel loesungszetteldaten, final Identifier veranstalterID) {

		Identifier loesungszettelID = new Identifier(loesungszetteldaten.uuid());

		Optional<Loesungszettel> opt = loesungszettelRepository
			.ofID(loesungszettelID);

		if (opt.isEmpty()) {

			LOG.warn("Kein Loesungszettel mit UUID {} bekannt", loesungszettelID);

			throw new NotFoundException();
		}

		authService.checkPermissionForTeilnahmenummer(veranstalterID,
			opt.get().getTheTeilnahmenummer(),
			"[loesungszettelAendern - " + loesungszettelID.toString() + "]");
		return null;
	}

	private Wettbewerb getWettbewerb() {

		if (aktuellerWettbewerb == null) {

			this.aktuellerWettbewerb = this.wettbewerbService.aktuellerWettbewerb().get();
		}

		return aktuellerWettbewerb;
	}

}
