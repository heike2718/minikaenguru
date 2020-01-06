// =====================================================
// Project: mk-commons
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_commons.dao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

/**
 * AbstractIntegrationTest
 */
public abstract class AbstractIntegrationTest {

	protected EntityManager entityManager;

	protected void initEntityManager() throws Exception {

		entityManager = Persistence.createEntityManagerFactory("mk-commons-IntegrationTest-PU").createEntityManager();

	}

	protected EntityTransaction beginTransaction() {

		EntityTransaction trx = entityManager.getTransaction();
		trx.begin();

		return trx;
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
