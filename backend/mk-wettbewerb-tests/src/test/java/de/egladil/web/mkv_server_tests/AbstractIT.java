// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests;

import java.io.File;
import java.io.FileOutputStream;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.apache.commons.io.IOUtils;

import de.egladil.web.mk_gateway.domain.DownloadData;

/**
 * AbstractIT
 */
public abstract class AbstractIT {

	protected EntityManager entityManagerWettbewerbDB;

	protected EntityManager entityManagerKatalogeDB;

	protected void setUp() {

		entityManagerWettbewerbDB = Persistence.createEntityManagerFactory("mkWettbewerbPU").createEntityManager();
		entityManagerKatalogeDB = Persistence.createEntityManagerFactory("mkKatalogePU").createEntityManager();

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
