// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import org.eclipse.microprofile.jwt.Claims;

import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * DecodedJWTReader
 */
public class DecodedJWTReader {

	private DecodedJWT decodedJWT;

	/**
	 * @param decodedJWT
	 */
	public DecodedJWTReader(final DecodedJWT decodedJWT) {

		if (decodedJWT == null) {

			throw new IllegalArgumentException("decodedJWT darf nicht null sein.");
		}

		this.decodedJWT = decodedJWT;
	}

	public String getFullName() {

		return decodedJWT.getClaim(Claims.full_name.name()).asString();
	}

}
