// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail.api;

import java.util.Objects;

import jakarta.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;

/**
 * NewsletterVersandauftrag
 */
public class NewsletterVersandauftrag {

	@NotBlank
	@UuidString
	@JsonProperty
	private String newsletterID;

	@NotBlank
	@JsonProperty
	private Empfaengertyp emfaengertyp;

	public static NewsletterVersandauftrag create(final String newsletterID, final Empfaengertyp empfaengertyp) {

		NewsletterVersandauftrag result = new NewsletterVersandauftrag();
		result.newsletterID = newsletterID;
		result.emfaengertyp = empfaengertyp;
		return result;
	}

	public String newsletterID() {

		return newsletterID;
	}

	public Empfaengertyp emfaengertyp() {

		return emfaengertyp;
	}

	@Override
	public int hashCode() {

		return Objects.hash(emfaengertyp, newsletterID);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		NewsletterVersandauftrag other = (NewsletterVersandauftrag) obj;
		return emfaengertyp == other.emfaengertyp && Objects.equals(newsletterID, other.newsletterID);
	}

	@Override
	public String toString() {

		return "NewsletterVersandauftrag [newsletterID=" + newsletterID + ", emfaengertyp=" + emfaengertyp + "]";
	}

}
