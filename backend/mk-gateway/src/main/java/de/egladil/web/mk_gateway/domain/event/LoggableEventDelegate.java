// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

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
		} else {

			System.out.println(dataInconsistencyRegistered.serializeQuietly());
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
		} else {

			System.out.println(securityIncidentRegistered.serializeQuietly());
		}

		return securityIncidentRegistered;
	}

}
