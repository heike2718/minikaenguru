// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

/**
 * AbstractIT
 */
public abstract class AbstractIT {

	protected EntityManager entityManager;

	protected void setUp() {

		entityManager = Persistence.createEntityManagerFactory("mkWettbewerbPU").createEntityManager();

	}

}
