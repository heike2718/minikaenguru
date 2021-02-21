// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.katalogantrag;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_kataloge.domain.apimodel.SchulkatalogAntrag;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;

/**
 * KatalogAntragMailtextGenerator
 */
public class KatalogAntragMailtextGenerator {

	private static final String PATH_MAILTEMPLATE_SCHULE_ANSCHRIFT = "/mailtemplates/katalogeintragSchule.txt";

	private static final String PATH_EMAIL_SUFFIX = "/mailtemplates/mailsuffix.txt";

	private static final Logger LOG = LoggerFactory.getLogger(KatalogAntragMailtextGenerator.class);

	/**
	 * Der Text, der in die Mail kommt.
	 *
	 * @param  antrag
	 *                SchkatalogAntrag
	 * @return        String
	 */
	public String getSchuleKatalogantragText(final SchulkatalogAntrag antrag) {

		try (InputStream inMailtext = getClass().getResourceAsStream(PATH_MAILTEMPLATE_SCHULE_ANSCHRIFT);
			InputStream inMailSuffix = getClass().getResourceAsStream(PATH_EMAIL_SUFFIX);
			StringWriter swMailtext = new StringWriter();
			StringWriter swSuffix = new StringWriter()) {

			IOUtils.copy(inMailtext, swMailtext, Charset.forName("UTF-8"));

			String text = swMailtext.toString();
			text = text.replace("#0#", antrag.schulname());
			text = text.replace("#1#", StringUtils.isBlank(antrag.plz()) ? "-" : antrag.plz());
			text = text.replace("#2#", antrag.ort());
			text = text.replace("#3#", StringUtils.isBlank(antrag.strasseUndHausnummer()) ? "-" : antrag.strasseUndHausnummer());
			text = text.replace("#4#", antrag.land());

			IOUtils.copy(inMailSuffix, swSuffix, Charset.defaultCharset());

			text += swSuffix.toString();

			return text;

		} catch (IOException e) {

			LOG.error(e.getMessage(), e);
			throw new KatalogAPIException("kann Mailtemplate nicht laden: " + e.getMessage(), e);
		}

	}

}
