// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests;

import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.infrastructure.persistence.entities.ConcurrencySafeEntity;

/**
 * TestAssertsDao
 */
public class TestAssertsDao {

	private final EntityManager entityManager;

	/**
	 * @param entityManager
	 */
	public TestAssertsDao(final EntityManager entityManager) {

		this.entityManager = entityManager;
	}

	@SuppressWarnings("unchecked")
	public <T extends ConcurrencySafeEntity> T findByIdentifier(final Class<T> type, final String uuid) {

		ConcurrencySafeEntity result = entityManager.find(type, uuid);
		return result == null ? null : (T) result;
	}

}
