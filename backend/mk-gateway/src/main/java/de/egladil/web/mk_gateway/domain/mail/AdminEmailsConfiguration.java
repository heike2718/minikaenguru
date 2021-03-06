// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * AdminEmailsConfiguration
 */
@ApplicationScoped
public class AdminEmailsConfiguration {

	@ConfigProperty(name = "emails.testempfaenger", defaultValue = "hdwinkel@egladil.de,info@egladil.de")
	private String testempfaenger;

	@ConfigProperty(name = "emails.groupsiz", defaultValue = "35")
	private String groupsize;

	@ConfigProperty(name = "emails.wartezeitMinSec", defaultValue = "45")
	private String wartezeitMinSec;

	@ConfigProperty(name = "emails.wartezeitMaxSec", defaultValue = "90")
	private String wartezeitMaxSec;

	@ConfigProperty(name = "emails.mockup", defaultValue = "false")
	private String mockup = "false";

	public static AdminEmailsConfiguration createForTest(final String testempfaenger, final int groupsize) {

		AdminEmailsConfiguration result = new AdminEmailsConfiguration();
		result.testempfaenger = testempfaenger;
		result.groupsize = "" + groupsize;
		result.wartezeitMinSec = "" + 2;
		result.wartezeitMaxSec = "" + 5;
		result.mockup = "true";
		return result;
	}

	public String getTestempfaenger() {

		return testempfaenger;
	}

	public boolean mockup() {

		return Boolean.valueOf(this.mockup);
	}

	public int wartezeitMinSec() {

		return Integer.valueOf(this.wartezeitMinSec);
	}

	public int wartezeitMaxSec() {

		return Integer.valueOf(this.wartezeitMaxSec);
	}

	public int groupsize() {

		return Integer.valueOf(this.groupsize);
	}

}
