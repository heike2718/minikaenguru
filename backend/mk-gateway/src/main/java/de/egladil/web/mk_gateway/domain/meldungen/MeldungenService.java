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

	private static final Logger LOG = LoggerFactory.getLogger(MeldungenService.class);

	@ConfigProperty(name = "mk.gateway.meldung.path")
	String pfadMeldung;

	public static final MeldungenService createForTest(final String path) {

		MeldungenService service = new MeldungenService();
		service.pfadMeldung = path;
		return service;
	}

	public Meldung loadMeldung() {

		File file = new File(pfadMeldung);

		try (InputStream in = new FileInputStream(file)) {

			return new ObjectMapper().readValue(in, Meldung.class);
		} catch (IOException e) {

			LOG.debug("Es gibt anscheinend keine Meldung: {} ", e.getMessage());

		}

		return new Meldung("");

	}

	public void saveMeldng(final Meldung meldung) {

		File file = new File(pfadMeldung);

		try (OutputStream out = new FileOutputStream(file)) {

			new ObjectMapper().writeValue(out, meldung);
			out.flush();

			LOG.info("Aktuelle Meldung geändert: {}", meldung);

		} catch (IOException e) {

			LOG.error("Fehler beim Speichen der Meldung: {}", e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte meldung nicht speichern: " + e.getMessage(), e);

		}

	}

}
