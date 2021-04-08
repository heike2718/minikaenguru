// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_kataloge_tests;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * AbstractIntegrationTest
 */
public abstract class AbstractIntegrationTest {

	protected EntityManager entityManager;

	protected void init() {

		entityManager = EntityManagerFactoryProvider.getInstance().getEmf().createEntityManager();

	}

	protected void commit(final EntityTransaction trx) {

		if (trx != null && trx.isActive()) {

			trx.commit();
		}
	}

	protected void rollback(final EntityTransaction trx) {

		if (trx != null && trx.isActive()) {

			trx.rollback();
		}
	}
}
