// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.Objects;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * Mailempfaenger
 */
public class Mailempfaenger {

	private final Identifier identifier;

	private final String email;

	private final Rolle rolle;

	public Mailempfaenger(final Identifier identifier, final String email, final Rolle rolle) {

		this.identifier = identifier;
		this.email = email;
		this.rolle = rolle;
	}

	@Override
	public int hashCode() {

		return Objects.hash(identifier);
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
		Mailempfaenger other = (Mailempfaenger) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {

		return "[identifier=" + identifier.identifier() + ", email=" + email + " (" + rolle + ")]";
	}

	public Identifier identifier() {

		return identifier;
	}

	public String email() {

		return email;
	}

	public Rolle rolle() {

		return rolle;
	}

}
