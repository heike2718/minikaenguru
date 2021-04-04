// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.domain.loesungszettel;

import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.ws.rs.NotFoundException;

import org.junit.jupiter.api.BeforeEach;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelService;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mkv_server_tests.AbstractIT;

/**
 * AbstractLoesungszettelTest
 */
public abstract class AbstractLoesungszettelTest extends AbstractIT {

	protected KinderRepository kinderRepository;

	protected LoesungszettelRepository loesungszettelRepository;

	protected LoesungszettelService loesungszettelService;

	@BeforeEach
	protected void init() {

		super.setUp();

		kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManagerWettbewerbDB);
		loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManagerWettbewerbDB);

		loesungszettelService = LoesungszettelService.createForIntegrationTest(entityManagerWettbewerbDB);
	}

	protected ResponsePayload loesungszettelAnlegen(final LoesungszettelAPIModel loesungszetteldaten, final String veranstalterUuid) throws NotFoundException {

		EntityTransaction trx = entityManagerWettbewerbDB.getTransaction();

		try {

			trx.begin();

			ResponsePayload result = loesungszettelService.loesungszettelAnlegen(loesungszetteldaten,
				new Identifier(veranstalterUuid));

			trx.commit();

			return result;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();

			throw new RuntimeException("Lösungszettel konnte nicht angelegt werden");
		}

	}

	protected ResponsePayload loesungszettelAendern(final LoesungszettelAPIModel loesungszetteldaten, final String veranstalterUuid) {

		EntityTransaction trx = entityManagerWettbewerbDB.getTransaction();

		try {

			trx.begin();

			ResponsePayload responsePayload = loesungszettelService.loesungszettelAendern(loesungszetteldaten,
				new Identifier(veranstalterUuid));

			trx.commit();

			return responsePayload;

		} catch (PersistenceException e) {

			trx.rollback();
			e.printStackTrace();

			throw new RuntimeException("Lösungszettel konnte nicht angelegt werden");
		}

	}

}
