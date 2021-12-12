// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.infrastructure.persistence.SortNumberGenerator;

/**
 * SortNumberGeneratorImpl
 */
@ApplicationScoped
public class SortNumberGeneratorImpl implements SortNumberGenerator {

	private static final Logger LOGGER = LoggerFactory.getLogger(SortNumberGeneratorImpl.class);

	@Inject
	EntityManager entityManager;

	private AtomicLong lastSortnumberLoesungszettel;

	private AtomicLong lastSortnumberUploads;

	public static SortNumberGeneratorImpl createForIntegrationTest(final EntityManager entityManager) {

		SortNumberGeneratorImpl result = new SortNumberGeneratorImpl();
		result.entityManager = entityManager;
		return result;
	}

	@Override
	public synchronized long getNextSortnumberLoesungszettel() {

		if (lastSortnumberLoesungszettel == null) {

			String stmt = "SELECT MAX(SORTNR) FROM LOESUNGSZETTEL";

			@SuppressWarnings("unchecked")
			List<BigInteger> trefferliste = entityManager.createNativeQuery(stmt).getResultList();

			long value = trefferliste.get(0).longValue();

			lastSortnumberLoesungszettel = new AtomicLong(value);

			LOGGER.info("=======> LOESUNGSZETTEL: MAX(SORTNR) = ", value);

		}

		return lastSortnumberLoesungszettel.incrementAndGet();
	}

	@Override
	public synchronized long getNextSortnumberUploads() {

		if (lastSortnumberUploads == null) {

			String stmt = "SELECT MAX(SORTNR) FROM UPLOADS";

			@SuppressWarnings("unchecked")
			List<BigInteger> trefferliste = entityManager.createNativeQuery(stmt).getResultList();

			long value = trefferliste.get(0).longValue();

			lastSortnumberUploads = new AtomicLong(value);

			LOGGER.info("=======> UPLOADS: MAX(SORTNR) = ", value);

		}

		return lastSortnumberUploads.incrementAndGet();
	}
}
