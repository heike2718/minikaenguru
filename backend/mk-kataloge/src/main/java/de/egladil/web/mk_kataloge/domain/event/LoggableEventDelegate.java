// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.event;

import javax.enterprise.event.Event;

/**
 * LoggableEventDelegate
 */
public class LoggableEventDelegate {

	/**
	 * Erzeugt ein DataInconsistencyRegistered-Objekt und feuert, falls der EventBus zur Verfügung steht.
	 *
	 * @param  msg
	 *                                String
	 * @param  dataInconsistencyEvent
	 *                                CDI-Event
	 * @return                        DataInconsistencyRegistered
	 */
	public DataInconsistencyRegistered fireDataInconsistencyEvent(final String msg, final Event<DataInconsistencyRegistered> dataInconsistencyEvent) {

		DataInconsistencyRegistered dataInconsistencyRegistered = new DataInconsistencyRegistered(msg);

		if (dataInconsistencyEvent != null) {

			dataInconsistencyEvent.fire(dataInconsistencyRegistered);
		}

		return dataInconsistencyRegistered;

	}

	/**
	 * Erzeugt ein SecurityIncidentRegistered-Objekt und feuert, falls der EventBus zur Verfügung steht.
	 *
	 * @param  msg
	 *                               String
	 * @param  securityIncidentEvent
	 *                               CDI-Event
	 * @return                       SecurityIncidentRegistered
	 */
	public SecurityIncidentRegistered fireSecurityEvent(final String msg, final Event<SecurityIncidentRegistered> securityIncidentEvent) {

		SecurityIncidentRegistered securityIncidentRegistered = new SecurityIncidentRegistered(msg);

		if (securityIncidentEvent != null) {

			securityIncidentEvent.fire(securityIncidentRegistered);
		}

		return securityIncidentRegistered;
	}

	/**
	 * Erzeugt MailNotSent- Objekt und feuert, falls der EventBus zur Verfügung steht.
	 *
	 * @param  msg
	 * @param  event
	 * @return       MailNotSent
	 */
	public MailNotSent fireMailNotSentEvent(final String msg, final Event<MailNotSent> event) {

		MailNotSent result = new MailNotSent(msg);

		if (event != null) {

			event.fire(result);
		}

		return result;

	}
}
