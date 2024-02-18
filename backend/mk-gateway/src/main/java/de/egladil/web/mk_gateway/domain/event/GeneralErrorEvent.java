// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.event;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * GeneralErrorEvent
 */
public class GeneralErrorEvent extends AbstractDomainEvent {

	@JsonProperty
	private String exceptionName;

	@JsonProperty
	private String errorMessage;

	/**
	 *
	 */
	protected GeneralErrorEvent() {

		super();

	}

	/**
	 * @param exceptionName
	 * @param errorMessage
	 */
	public GeneralErrorEvent(final String exceptionName, final String errorMessage) {

		super();
		this.exceptionName = exceptionName;
		this.errorMessage = errorMessage;
	}

	@Override
	public String typeName() {

		return EventType.GENERAL_ERROR.getLabel();
	}

}
