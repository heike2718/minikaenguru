// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.error;

import de.egladil.web.commons_validation.payload.ResponsePayload;

/**
 * DomainObjectConcurrentlyModifiedException
 */
public class DomainObjectConcurrentlyModifiedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final ResponsePayload responsePayload;

	public DomainObjectConcurrentlyModifiedException(final ResponsePayload responsePayload) {

		this.responsePayload = responsePayload;
	}

	public ResponsePayload getResponsePayload() {

		return responsePayload;
	}

}
