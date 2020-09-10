// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.enterprise.context.ApplicationScoped;

import org.apache.commons.io.IOUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;

/**
 * VertragAuftragsverarbeitungPdfGenerator
 */
@ApplicationScoped
public class VertragAuftragsverarbeitungPdfGenerator {

	private static final Logger LOG = LoggerFactory.getLogger(VertragAuftragsverarbeitungPdfGenerator.class);

	private static final String PATH_SUBDIR_ADV_TEXTE = "/adv";

	@ConfigProperty(name = "path.external.files")
	String pathAdvTexteDir;

	/**
	 * @param  vertrag
	 * @return
	 */
	public byte[] generatePdf(final VertragAuftragsdatenverarbeitung vertrag) {

		// erstmal fake-implementierung
		String path = pathAdvTexteDir + PATH_SUBDIR_ADV_TEXTE + "/adv-vereinbarung-1.1.pdf";

		try (InputStream in = new FileInputStream(new File(path)); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {

			IOUtils.copy(in, bos);

			byte[] result = bos.toByteArray();

			bos.flush();

			return result;
		} catch (IOException e) {

			LOG.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException("Konnte ADV-template nicht laden");
		}
	}

}
