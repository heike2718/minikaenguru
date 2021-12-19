// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.math.BigInteger;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.SortedTable;
import de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.SortnumberRepository;

/**
 * SortnumberRepositoryImpl
 */
@RequestScoped
public class SortnumberHibernateRepositoryImpl implements SortnumberRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(SortnumberHibernateRepositoryImpl.class);

	@Inject
	EntityManager entityManager;

	public static SortnumberRepository createForIntegrationTests(final EntityManager entityManager) {

		SortnumberHibernateRepositoryImpl result = new SortnumberHibernateRepositoryImpl();
		result.entityManager = entityManager;
		return result;
	}

	@Override
	public long getMaxSortnumber(final SortedTable sortedTable) {

		String stmt = "SELECT MAX(SORTNR) FROM " + sortedTable.toString();

		@SuppressWarnings("unchecked")
		List<BigInteger> trefferliste = entityManager.createNativeQuery(stmt).getResultList();

		long value = trefferliste.get(0).longValue();

		LOGGER.info("=======> {}: MAX(SORTNR) = {}", sortedTable, value);

		return value;

	}

}
