// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import org.apache.commons.lang3.StringUtils;
import org.jboss.weld.exceptions.IllegalArgumentException;

/**
 * UploadIdentifier ist der fachliche Schlüssel.
 */
public class UploadIdentifier {

	private final String teilnahmenummer;

	private final Long checksumme;

	public UploadIdentifier(final String teilnahmenummer, final Long checksumme) {

		super();

		if (StringUtils.isBlank(teilnahmenummer)) {

			throw new IllegalArgumentException("teilnahmenummer blank");
		}

		if (checksumme == null) {

			throw new IllegalArgumentException("checksumme null");
		}

		this.teilnahmenummer = teilnahmenummer;
		this.checksumme = checksumme;
	}

	@Override
	public String toString() {

		return "UploadIdentifier [teilnahmenummer=" + teilnahmenummer + ", checksumme=" + checksumme + "]";
	}

	public String getTeilnahmenummer() {

		return teilnahmenummer;
	}

	public Long getChecksumme() {

		return checksumme;
	}

}
