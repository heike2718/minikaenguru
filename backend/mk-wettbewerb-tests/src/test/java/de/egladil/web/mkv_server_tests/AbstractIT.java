// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * AbstractIT
 */
public abstract class AbstractIT {

	protected EntityManager entityManager;

	protected void setUp() {

		entityManager = Persistence.createEntityManagerFactory("mkWettbewerbPU").createEntityManager();

	}

	protected void rollback(final EntityTransaction trx) {

		if (trx != null && trx.isActive()) {

			trx.rollback();
		}
	}

}
