// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests;

import java.io.File;
import java.io.FileOutputStream;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

import org.apache.commons.io.IOUtils;

import de.egladil.web.mk_gateway.domain.DownloadData;

/**
 * AbstractIT
 */
public abstract class AbstractIntegrationTest {

	protected EntityManager entityManager;

	protected void setUp() {

		entityManager = EntityManagerWettbewerbeFactoryProvider.getInstance().getEmf().createEntityManager();

	}

	protected EntityTransaction startTransaction() {

		EntityTransaction transaction = entityManager.getTransaction();

		if (!transaction.isActive()) {

			transaction.begin();
		}

		return transaction;

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

	protected void print(final DownloadData data, final boolean deleteOnExit) throws Exception {

		String prefix = data.filename().substring(0, data.filename().length() - 4) + "-";

		File pdf = File.createTempFile(prefix, ".pdf");

		if (deleteOnExit) {

			pdf.deleteOnExit();
		}

		try (FileOutputStream out = new FileOutputStream(pdf)) {

			IOUtils.write(data.data(), out);

			out.flush();

			if (!deleteOnExit) {

				System.out.println("output ist here: " + pdf.getAbsolutePath());
			}
		}
	}

}
