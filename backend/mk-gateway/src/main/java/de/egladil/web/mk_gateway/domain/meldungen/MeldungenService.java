// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.meldungen;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * MeldungenService
 */
@ApplicationScoped
public class MeldungenService {

	private static final String FILENAME = "aktuelle-meldung.json";

	private static final Logger LOG = LoggerFactory.getLogger(MeldungenService.class);

	@ConfigProperty(name = "path.external.files")
	String pathExternalFiles;

	public static final MeldungenService createForTest(final String path) {

		MeldungenService service = new MeldungenService();
		service.pathExternalFiles = path;
		return service;
	}

	/**
	 * Liest die Meldung ein.
	 *
	 * @return Meldung
	 */
	public Meldung loadMeldung() {

		File file = getMeldungenFile();

		if (!file.isFile() || !file.canRead()) {

			LOG.debug("Es gibt anscheinend keine Meldung");
			return new Meldung("");
		}

		try (InputStream in = new FileInputStream(file)) {

			return new ObjectMapper().readValue(in, Meldung.class);
		} catch (IOException e) {

			LOG.warn("Meldung kann nicht gelesen werden: {} ", e.getMessage());

		}

		return new Meldung("");

	}

	/**
	 * Überschreibt die Meldung
	 *
	 * @param meldung
	 *                Meldung
	 */
	public void saveMeldung(final Meldung meldung) {

		File file = getMeldungenFile();

		try (OutputStream out = new FileOutputStream(file)) {

			new ObjectMapper().writeValue(out, meldung);
			out.flush();

			LOG.info("Aktuelle Meldung geändert: {}", meldung);

		} catch (IOException e) {

			LOG.error("Fehler beim Speichen der Meldung: {}", e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte meldung nicht speichern: " + e.getMessage(), e);

		}
	}

	/**
	 * Löscht die Meldung.
	 */
	public void deleteMeldung() {

		File file = getMeldungenFile();

		if (file.isFile() && file.canWrite()) {

			file.delete();

			LOG.info("aktuelle Meldung {} geloescht", file.getAbsolutePath());
		}
	}

	private File getMeldungenFile() {

		String path = pathExternalFiles + File.separator + FILENAME;

		return new File(path);

	}

}
