// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;

/**
 * ZugangsstatusPayload
 */
public class ZugangsstatusPayload {

	@JsonProperty
	private ZugangUnterlagen zugangsstatus;

	public ZugangUnterlagen getZugangsstatus() {

		return zugangsstatus;
	}

	public void setZugangsstatus(final ZugangUnterlagen zugangsstatus) {

		this.zugangsstatus = zugangsstatus;
	}

}
