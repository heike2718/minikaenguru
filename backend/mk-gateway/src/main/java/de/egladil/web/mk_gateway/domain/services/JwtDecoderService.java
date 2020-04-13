// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.services;

import org.eclipse.microprofile.jwt.Claims;

import com.auth0.jwt.interfaces.DecodedJWT;

import de.egladil.web.commons_crypto.JWTService;
import de.egladil.web.mk_gateway.domain.session.SessionUtils;

/**
 * JwtDecoderService
 */
public class JwtDecoderService {

	public DecodedJWT decodeJwt(final String jwt, final JWTService jwtService) {

		DecodedJWT decodedJWT = jwtService.verify(jwt, SessionUtils.getPublicKey());

		return decodedJWT;

	}

	public String getFullName(final DecodedJWT decodedJWT) {

		return decodedJWT.getClaim(Claims.full_name.name()).asString();
	}

}
