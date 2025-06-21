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
	private long anzahlKinder;

	@JsonProperty
	private List<MkBiZaGruppierungsitem> kinderJeKlassenstufe = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaGruppierungsitem> medianeJeKlassenstufe = new ArrayList<>();

	@JsonProperty
	private List<MkBiZaGruppierungsitem> kumulierteLoesungszettelJeWoche = new ArrayList<>();

	@JsonProperty
	private MkBiZaWettbewerbColors colors;

	public MkBiZaWettbewerb() {

		super();

	}

	public MkBiZaWettbewerb(final int jahr, final WettbewerbStatus status, final Integer medianIkids, final Integer medianKlasseEins, final Integer medianKlasseZwei) {

		super();
		this.jahr = jahr;
		this.status = status;

		// müssen immer alle füllen, sonst gibt es Farb- und Zuordnungsklash im Frontend
		// Das Frontend wertet das Label gar nicht aus, sondern ordnet per Index zu (0=IKID,1=EINS,2=ZWEI)

		if (medianIkids != null && !medianIkids.equals(Integer.valueOf(0))) {

			medianeJeKlassenstufe.add(new MkBiZaGruppierungsitem().withAnzahl(Long.valueOf(medianIkids)).withName("Median IKID"));
		} else {

			medianeJeKlassenstufe.add(new MkBiZaGruppierungsitem().withAnzahl(0).withName("Median IKID"));
		}

		if (medianKlasseEins != null && !medianKlasseEins.equals(Integer.valueOf(0))) {

			medianeJeKlassenstufe
				.add(new MkBiZaGruppierungsitem().withAnzahl(Long.valueOf(medianKlasseEins)).withName("Median Klasse 1"));
		} else {

			medianeJeKlassenstufe
				.add(new MkBiZaGruppierungsitem().withAnzahl(0).withName("Median Klasse 1"));
		}

		if (medianKlasseZwei != null && !medianKlasseZwei.equals(Integer.valueOf(0))) {

			medianeJeKlassenstufe
				.add(new MkBiZaGruppierungsitem().withAnzahl(Long.valueOf(medianKlasseZwei)).withName("Median Klasse 2"));
		} else {

			medianeJeKlassenstufe
				.add(new MkBiZaGruppierungsitem().withAnzahl(0).withName("Median Klasse 2"));
		}

	}

	public int getJahr() {

		return jahr;
	}

	public void setAnzahlKinder(final long anzahlKinder) {

		this.anzahlKinder = anzahlKinder;
	}

	public void setKinderJeKlassenstufe(final List<MkBiZaGruppierungsitem> kinderJeKlassenstufe) {

		this.kinderJeKlassenstufe = kinderJeKlassenstufe;
	}

	public List<MkBiZaGruppierungsitem> getKumulierteLoesungszettelJeWoche() {
		return kumulierteLoesungszettelJeWoche;
	}

	public void setKumulierteLoesungszettelJeWoche(List<MkBiZaGruppierungsitem> loesungszettelJeWoche) {
		this.kumulierteLoesungszettelJeWoche = loesungszettelJeWoche;
	}

	public MkBiZaWettbewerbColors getColors() {
		return colors;
	}

	public void setColors(MkBiZaWettbewerbColors colors) {
		this.colors = colors;
	}

}
