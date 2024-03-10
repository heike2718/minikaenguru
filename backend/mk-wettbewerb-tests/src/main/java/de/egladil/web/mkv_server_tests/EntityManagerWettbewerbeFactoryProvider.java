// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

/**
 * EntityManagerWettbewerbeFactoryProvider
 */
public class EntityManagerWettbewerbeFactoryProvider {

	private EntityManagerFactory emf;

	private static EntityManagerWettbewerbeFactoryProvider instance;

	/**
	 *
	 */
	private EntityManagerWettbewerbeFactoryProvider() {

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
	public static EntityManagerWettbewerbeFactoryProvider getInstance() {

		if (instance == null) {

			instance = new EntityManagerWettbewerbeFactoryProvider();
		}

		return instance;
	}

}
