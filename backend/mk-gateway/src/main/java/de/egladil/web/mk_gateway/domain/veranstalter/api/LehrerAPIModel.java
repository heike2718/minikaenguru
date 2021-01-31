// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LehrerAPIModel
 */
public class LehrerAPIModel {

	@JsonProperty
	private boolean hatZugangZuUnterlagen;

	@JsonProperty
	private boolean newsletterAbonniert;

	public static LehrerAPIModel create(final boolean zugangZuUnterlagen, final boolean newsletterAbonniert) {

		LehrerAPIModel result = new LehrerAPIModel();
		result.hatZugangZuUnterlagen = zugangZuUnterlagen;
		result.newsletterAbonniert = newsletterAbonniert;
		return result;
	}

	public boolean hatZugangZuUnterlagen() {

		return hatZugangZuUnterlagen;
	}

	public boolean newsletterAbonniert() {

		return newsletterAbonniert;
	}

}
