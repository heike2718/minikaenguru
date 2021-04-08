// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 * EntityManagerFactoryProvider
 */
public class EntityManagerFactoryProvider {

	private EntityManagerFactory emf;

	private static EntityManagerFactoryProvider instance;

	/**
	 *
	 */
	private EntityManagerFactoryProvider() {

		emf = Persistence.createEntityManagerFactory("mkWettbewerbPU");

		System.err.println("======> EntityManagerFactory created.");
	}

	/**
	 * @return the emf
	 */
	public EntityManagerFactory getEmf() {

		return emf;
	}

	/**
	 * @return the instance
	 */
	public static EntityManagerFactoryProvider getInstance() {

		if (instance == null) {

			instance = new EntityManagerFactoryProvider();
		}

		return instance;
	}

}
