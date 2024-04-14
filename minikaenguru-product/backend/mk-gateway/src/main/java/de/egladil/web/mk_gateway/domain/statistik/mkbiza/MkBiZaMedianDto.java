// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MkBiZaMedianDto
 */
public class MkBiZaMedianDto {

	@JsonProperty
	private int medianMalTausend;

	@JsonProperty
	private int gesamtpunkte;

	public MkBiZaMedianDto() {

		super();

	}

	public MkBiZaMedianDto(final int medianMalTausend, final int gesamtpunkte) {

		super();
		this.medianMalTausend = medianMalTausend;
		this.gesamtpunkte = gesamtpunkte;
	}

}
