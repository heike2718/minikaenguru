// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.impl;

import java.util.concurrent.atomic.AtomicLong;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import de.egladil.web.mk_gateway.infrastructure.persistence.impl.SortnumberHibernateRepositoryImpl;
import de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.SortNumberGenerator;
import de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.SortedTable;
import de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.SortnumberRepository;

/**
 * SortNumberGeneratorImpl
 */
@ApplicationScoped
public class SortNumberGeneratorImpl implements SortNumberGenerator {

	@Inject
	SortnumberRepository sortnumberRepository;

	private AtomicLong lastSortnumberLoesungszettel;

	private AtomicLong lastSortnumberUploads;

	private AtomicLong lastSortnumberDownloads;

	public static SortNumberGeneratorImpl createForIntegrationTests(final EntityManager entityManager) {

		SortNumberGeneratorImpl result = new SortNumberGeneratorImpl();
		result.sortnumberRepository = SortnumberHibernateRepositoryImpl.createForIntegrationTests(entityManager);
		return result;
	}

	@Override
	public synchronized long getNextSortnumberLoesungszettel() {

		if (lastSortnumberLoesungszettel == null) {

			long value = sortnumberRepository.getMaxSortnumber(SortedTable.LOESUNGSZETTEL);

			lastSortnumberLoesungszettel = new AtomicLong(value);

		}

		return lastSortnumberLoesungszettel.incrementAndGet();
	}

	@Override
	public synchronized long getNextSortnumberUploads() {

		if (lastSortnumberUploads == null) {

			long value = sortnumberRepository.getMaxSortnumber(SortedTable.UPLOADS);

			lastSortnumberUploads = new AtomicLong(value);

		}

		return lastSortnumberUploads.incrementAndGet();
	}

	@Override
	public synchronized long getNextSortnumberDownloads() {

		if (lastSortnumberDownloads == null) {

			long value = sortnumberRepository.getMaxSortnumber(SortedTable.DOWNLOADS);

			lastSortnumberDownloads = new AtomicLong(value);
		}

		return lastSortnumberDownloads.incrementAndGet();
	}
}
