// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LoescheVeranstalterCommand
 */
public class LoescheVeranstalterCommand {

	@JsonProperty
	private String syncToken;

	@JsonProperty
	private String uuid;

	public String uuid() {

		return uuid;
	}

	@Override
	public String toString() {

		return "LoescheVeranstalterCommand [uuid=" + uuid + "]";
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
		LoescheVeranstalterCommand other = (LoescheVeranstalterCommand) obj;
		return Objects.equals(uuid, other.uuid);
	}

	public String syncToken() {

		return syncToken;
	}

}
