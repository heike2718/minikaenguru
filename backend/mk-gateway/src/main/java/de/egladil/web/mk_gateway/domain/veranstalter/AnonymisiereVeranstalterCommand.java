// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.auth.signup.VeranstalterAnonymisiert;

/**
 * AnonymisiereVeranstalterCommand
 */
public class AnonymisiereVeranstalterCommand {

	@JsonProperty
	private String uuid;

	public String uuid() {

		return uuid;
	}

	/**
	 *
	 */
	public AnonymisiereVeranstalterCommand(final VeranstalterAnonymisiert event) {

		this.uuid = event.uuid();

	}

	@Override
	public String toString() {

		return "AnonymisiereVeranstalterCommand [uuid=" + uuid + "]";
	}

	@Override
	public int hashCode() {

		return Objects.hash(uuid);
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
		AnonymisiereVeranstalterCommand other = (AnonymisiereVeranstalterCommand) obj;
		return Objects.equals(uuid, other.uuid);
	}

}
