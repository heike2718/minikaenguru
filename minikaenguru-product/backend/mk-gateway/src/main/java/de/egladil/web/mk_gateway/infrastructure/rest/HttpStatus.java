// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest;

import java.net.HttpURLConnection;

/**
 * HttpStatus
 */
public interface HttpStatus {

	String HTTP_OK = HttpURLConnection.HTTP_OK + "";

	String HTTP_SERVER_ERROR = HttpURLConnection.HTTP_INTERNAL_ERROR + "";

}
