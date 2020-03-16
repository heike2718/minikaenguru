// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.endpoints;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_kataloge.persistence.impl.entities.Schule;

/**
 * SchuleMessage
 */
public class SchuleMessage {

	@NotBlank
	private String secret;

	@NotNull
	private Schule schule;

	public String getSecret() {

		return secret;
	}

	public Schule getSchule() {

		return schule;
	}

	@Override
	public String toString() {

		return "SchuleMessage [secret=" + StringUtils.abbreviate(secret, 4) + schule.printForLog() + "]";
	}

}
