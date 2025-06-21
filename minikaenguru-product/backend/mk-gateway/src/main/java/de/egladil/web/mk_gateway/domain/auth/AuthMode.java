// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auth;

/**
 * AuthMode
 */
// 01.09.2020: das ist häßlich, aber Quarkus' CDI scheint ein Problem mit custom Qualifiers zu haben.
public enum AuthMode {

	ADMIN,
	VERANSTALTER;

}
