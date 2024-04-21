// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.klassenstufen;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MedianDto<br>
 * <br>
 * ist gleich MkBiZaMedianDto aus mk-gateway.
 */
public class MedianDto {

	@JsonProperty
	private int medianMalTausend;

	@JsonProperty
	private int gesamtpunkte;

	public void setMedianMalTausend(final int medianMalTausend) {

		this.medianMalTausend = medianMalTausend;
	}

	public void setGesamtpunkte(final int gesamtpunkte) {

		this.gesamtpunkte = gesamtpunkte;
	}

	public int getMedianMalTausend() {

		return medianMalTausend;
	}
}
