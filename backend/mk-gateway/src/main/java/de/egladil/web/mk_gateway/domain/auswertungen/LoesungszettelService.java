// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
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
	AuthorizationService authService;

	private LoesungszettelChanged loesungszettelChanged;

	public static LoesungszettelService createForTest(final AuthorizationService authService, final LoesungszettelRepository loesungszettelRepository) {

		LoesungszettelService result = new LoesungszettelService();
		result.authService = authService;
		result.loesungszettelRepository = loesungszettelRepository;
		return result;
	}

	@Transactional
	public boolean loesungszettelLoeschenWithAuthorizationCheck(final Identifier identifier, final String veranstalterUuid) {

		Optional<PersistenterLoesungszettel> opt = loesungszettelRepository.findByIdentifier(identifier);

		if (opt.isEmpty()) {

			return false;
		}

		authService.checkPermissionForTeilnahmenummer(new Identifier(veranstalterUuid),
			new Identifier(opt.get().getTeilnahmenummer()),
			"[loesungszettelLoeschenWithAuthorizationCheck - " + identifier.toString() + "]");

		return this.loesungszettelLoeschenWithoutAuthorizationCheck(identifier, veranstalterUuid);

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
			.findByIdentifier(identifier);

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

}
