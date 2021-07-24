// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

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
	public DataInconsistencyRegistered fireDataInconsistencyEvent(final String msg, final DomainEventHandler domainEventHandler) {

		DataInconsistencyRegistered dataInconsistencyRegistered = new DataInconsistencyRegistered(msg);

		if (domainEventHandler != null) {

			domainEventHandler.handleEvent(dataInconsistencyRegistered);
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
	public SecurityIncidentRegistered fireSecurityEvent(final String msg, final DomainEventHandler domainEventHandler) {

		SecurityIncidentRegistered securityIncidentRegistered = new SecurityIncidentRegistered(msg);

		if (domainEventHandler != null) {

			domainEventHandler.handleEvent(securityIncidentRegistered);
		} else {

			System.out.println(securityIncidentRegistered.serializeQuietly());
		}

		return securityIncidentRegistered;
	}

}
