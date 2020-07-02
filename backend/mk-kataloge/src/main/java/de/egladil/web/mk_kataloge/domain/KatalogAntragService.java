// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulkatalogAntrag;
import de.egladil.web.mk_kataloge.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_kataloge.domain.event.SecurityIncidentRegistered;

/**
 * KatalogAntragService
 */
@ApplicationScoped
public class KatalogAntragService {

	private static final Logger LOG = LoggerFactory.getLogger(KatalogAntragService.class);

	@ConfigProperty(name = "bccEmpfaengerSchulkatalogantrag", defaultValue = "minikaenguru@egladil.de")
	String bccEmpfaenger;

	@Inject
	KatalogMailService katalogMailService;

	@Inject
	Event<SecurityIncidentRegistered> securityEvent;

	@Inject
	Event<KatalogAntragReceived> katalogAntragReceivedEvent;

	private SecurityIncidentRegistered securityIncident;

	private KatalogAntragReceived katalogAntragEventObject;

	public static KatalogAntragService createForTest() {

		KatalogAntragService result = new KatalogAntragService();
		result.katalogMailService = KatalogMailService.createForTest();
		result.bccEmpfaenger = "minikaenguru@egladil.de";
		return result;

	}

	public KatalogAntragService() {

		super();

	}

	/**
	 * @param antrag
	 */
	public boolean validateAndSend(final SchulkatalogAntrag antrag) {

		if (StringUtils.isNotBlank(antrag.kleber())) {

			String msg = "Honeypot des Schulkatalogantrags war nicht blank: " + antrag.toSecurityLog();

			this.securityIncident = new LoggableEventDelegate().fireSecurityEvent(msg, securityEvent);
			LOG.warn(msg);

			return false;
		}

		katalogAntragEventObject = new KatalogAntragReceived(antrag);

		DefaultEmailDaten emailDaten = createMailDaten(antrag);
		this.katalogMailService.sendMail(emailDaten);

		if (katalogAntragReceivedEvent != null) {

			katalogAntragReceivedEvent.fire(katalogAntragEventObject);
		}

		return true;

	}

	private DefaultEmailDaten createMailDaten(final SchulkatalogAntrag antrag) {

		DefaultEmailDaten result = new DefaultEmailDaten();
		result.setBetreff("Minikänguru: Schulkatalog");
		result.setText(new KatalogAntragMailtextGenerator().getSchuleKatalogantragText(antrag));
		result.setEmpfaenger(antrag.email());
		result.addHiddenEmpfaenger(bccEmpfaenger);
		return result;

	}

	SecurityIncidentRegistered getSecurityIncident() {

		return securityIncident;
	}

	KatalogAntragReceived getKatalogAntragEventObject() {

		return katalogAntragEventObject;
	}

}
