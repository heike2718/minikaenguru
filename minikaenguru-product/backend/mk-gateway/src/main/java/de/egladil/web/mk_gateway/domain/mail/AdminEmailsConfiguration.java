// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;

/**
 * AdminEmailsConfiguration
 */
@ApplicationScoped
public class AdminEmailsConfiguration {

	@ConfigProperty(name = "emails.testempfaenger")
	String testempfaenger;

	@ConfigProperty(name = "emails.groupsize")
	String groupsize;

	@ConfigProperty(name = "emails.mockup")
	boolean mockup;

	public static AdminEmailsConfiguration createForTest(final String testempfaenger, final int groupsize) {

		AdminEmailsConfiguration result = new AdminEmailsConfiguration();
		result.testempfaenger = testempfaenger;
		result.groupsize = "" + groupsize;
		result.mockup = true;
		return result;
	}

	public String getTestempfaenger() {

		return testempfaenger;
	}

	public boolean mockup() {

		return this.mockup;
	}

	public int groupsize() {

		return Integer.valueOf(this.groupsize);
	}

}
