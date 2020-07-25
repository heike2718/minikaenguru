// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.schulimport;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.error.DuplicateEntityException;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * ImportSchulenService
 */
@ApplicationScoped
public class ImportSchulenService {

	private static final Logger LOG = LoggerFactory.getLogger(ImportSchulenService.class);

	@Inject
	SchuleRepository schuleRepository;

	public ResponsePayload schulenImportieren(final FileResource fileResource) {

		try (ByteArrayInputStream bin = new ByteArrayInputStream(fileResource.file().readAllBytes());
			InputStreamReader streamReader = new InputStreamReader(bin, StandardCharsets.UTF_8);
			BufferedReader bufferedReader = new BufferedReader(streamReader)) {

			String line;
			int addedCount = 0;
			int notAddedCount = 0;

			while ((line = bufferedReader.readLine()) != null) {

				boolean added = this.createAndAddSchule(line);

				if (added) {

					addedCount++;
				} else {

					notAddedCount++;
				}
			}

			int gesamt = addedCount + notAddedCount;
			String message = "Anzahl gesamt=" + gesamt + ", davon importiert:" + addedCount + ", nicht importiert:" + notAddedCount;

			LOG.info(message);
			return ResponsePayload.messageOnly(MessagePayload.info(message));

		} catch (IOException e) {

			LOG.error("IOException beim Verarbeiten der hochgeladenen Datei {}: {}", fileResource.fileName(), e.getMessage(), e);
			throw new KatalogAPIException("Datei " + fileResource.fileName() + " konnte nicht verarbeitet werden.");
		}
	}

	private boolean createAndAddSchule(final String line) {

		String[] tokens = line.split(";");

		Schule schule = null;

		if (tokens.length == 6) {

			schule = new Schule();
			schule.setImportiertesKuerzel(unwrap(tokens[0]));
			schule.setName(unwrap(tokens[1]));
			schule.setOrtKuerzel(unwrap(tokens[2]));
			schule.setOrtName(unwrap(tokens[3]));
			schule.setLandKuerzel(unwrap(tokens[4]));
			schule.setLandName(unwrap(tokens[5]));

			try {

				this.schuleRepository.addSchule(schule);

				return true;
			} catch (DuplicateEntityException e) {

				LOG.info("Schule mit Kürzel {} gab es bereits", tokens[0]);
				return false;
			} catch (PersistenceException e) {

				LOG.error("Schule {} konnte nicht gespeichert werden: {}", schule, e.getMessage(), e);
				return false;
			}
		}

		LOG.warn("Unerwartete Zeile {}: wird übersprungen", line);
		return false;

	}

	private String unwrap(final String token) {

		return token.replaceAll("#", "");

	}

}
