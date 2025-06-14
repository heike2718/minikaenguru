// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;

/**
 * SchuleEingetragenMailtextGenerator
 */
public class SchuleEingetragenMailtextGenerator {

	private static final String PATH_MAILTEMPLATE = "/mails/schuleEingetragen.txt";

	private static final String PATH_EMAIL_SUFFIX = "/mails/mailsuffix.txt";

	private static final Logger LOG = LoggerFactory.getLogger(SchuleEingetragenMailtextGenerator.class);

	/**
	 * Der Text, der in die Mail kommt.
	 *
	 * @param  schulePayload
	 *                       SchulePayload
	 * @return               String
	 */
	public String getSchuleEingetragenText(final SchulePayload schulePayload) {

		try (InputStream inMailtext = getClass().getResourceAsStream(PATH_MAILTEMPLATE);
			InputStream inMailSuffix = getClass().getResourceAsStream(PATH_EMAIL_SUFFIX);
			StringWriter swMailtext = new StringWriter();
			StringWriter swSuffix = new StringWriter()) {

			IOUtils.copy(inMailtext, swMailtext, Charset.forName("UTF-8"));

			String text = swMailtext.toString();
			text = text.replace("#0#", schulePayload.name());
			text = text.replace("#1#", schulePayload.nameOrt());
			text = text.replace("#2#", schulePayload.nameLand());

			IOUtils.copy(inMailSuffix, swSuffix, Charset.forName("UTF-8"));

			text += swSuffix.toString();

			return text;

		} catch (IOException e) {

			LOG.error(e.getMessage(), e);
			throw new MkGatewayRuntimeException("kann Mailtemplate nicht laden: " + e.getMessage(), e);
		}

	}

}
