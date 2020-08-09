// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_wettbewerb.domain.semantik.DomainCommand;

/**
 * CreateOrUpdateLehrerCommand hat die gleiche Signatur wie der body eines entsprechenden mk-gateway-DomainEvents und entsteht durch
 * Deserialisierung eines REST-Payloads..
 */
@DomainCommand
public class CreateOrUpdateLehrerCommand {

	@JsonIgnore
	private LocalDateTime occouredOn;

	@JsonProperty
	private String rolle;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	@JsonProperty
	private String schulkuerzel;

	@JsonProperty
	private boolean newsletterEmpfaenger;

	public static CreateOrUpdateLehrerCommand createForTest(final String uuid, final String fullName, final String schulkuerzel) {

		CreateOrUpdateLehrerCommand result = new CreateOrUpdateLehrerCommand();
		result.rolle = Rolle.LEHRER.name();
		result.fullName = fullName;
		result.schulkuerzel = schulkuerzel;
		result.occouredOn = LocalDateTime.now();
		result.uuid = uuid;

		return result;

	}

	public static CreateOrUpdateLehrerCommand newInstance() {

		CreateOrUpdateLehrerCommand result = new CreateOrUpdateLehrerCommand();
		result.rolle = Rolle.LEHRER.name();
		result.occouredOn = LocalDateTime.now();
		return result;

	}

	public String uuid() {

		return uuid;
	}

	public String fullName() {

		return fullName;
	}

	public String schulkuerzel() {

		return schulkuerzel;
	}

	public CreateOrUpdateLehrerCommand withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public CreateOrUpdateLehrerCommand withFullName(final String fullName) {

		this.fullName = fullName;
		return this;
	}

	public CreateOrUpdateLehrerCommand withSchulkuerzel(final String schulkuerzel) {

		this.schulkuerzel = schulkuerzel;
		return this;
	}

	public boolean newsletterEmpfaenger() {

		return newsletterEmpfaenger;
	}

	public CreateOrUpdateLehrerCommand withNewsletterEmpfaenger(final boolean newsletterEmpfaenger) {

		this.newsletterEmpfaenger = newsletterEmpfaenger;
		return this;
	}

}
