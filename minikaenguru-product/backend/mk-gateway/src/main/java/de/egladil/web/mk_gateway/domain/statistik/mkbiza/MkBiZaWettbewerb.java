// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;

/**
 * MkBiZaWettbewerb
 */
public class MkBiZaWettbewerb {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private WettbewerbStatus status;

	@JsonProperty
	private List<MkBiZaGruppierungsitem> medianeJeKlassenstufe = new ArrayList<>();

	public MkBiZaWettbewerb() {

		super();

	}

	public MkBiZaWettbewerb(final int jahr, final WettbewerbStatus status, final Integer medianIkids, final Integer medianKlasseEins, final Integer medianKlasseZwei) {

		super();
		this.jahr = jahr;
		this.status = status;

		if (medianIkids != null && !medianIkids.equals(Integer.valueOf(0))) {

			medianeJeKlassenstufe.add(new MkBiZaGruppierungsitem().withAnzahl(Long.valueOf(medianIkids)).withName("Median IKID"));
		}

		if (medianKlasseEins != null && !medianKlasseEins.equals(Integer.valueOf(0))) {

			medianeJeKlassenstufe
				.add(new MkBiZaGruppierungsitem().withAnzahl(Long.valueOf(medianKlasseEins)).withName("Median Klasse 1"));
		}

		if (medianKlasseZwei != null && !medianKlasseZwei.equals(Integer.valueOf(0))) {

			medianeJeKlassenstufe
				.add(new MkBiZaGruppierungsitem().withAnzahl(Long.valueOf(medianKlasseZwei)).withName("Median Klasse 2"));
		}

	}

}
