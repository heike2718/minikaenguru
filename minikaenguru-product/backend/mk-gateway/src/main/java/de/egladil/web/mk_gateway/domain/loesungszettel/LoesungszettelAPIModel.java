// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;

/**
 * LoesungszettelAPIModel
 */
public class LoesungszettelAPIModel {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private long sortnumber;

	@JsonProperty
	private Teilnahmeart teilnahmeart;

	@JsonProperty
	private String teilnahmenummer;

	@JsonProperty
	private Auswertungsquelle quelle;

	@JsonProperty
	private Klassenstufe klassenstufe;

	@JsonProperty
	private Sprache sprache;

	@JsonProperty
	private String punkte;

	@JsonProperty
	private int kaengurusprung;

	@JsonProperty
	private SchuleAPIModel schule;

	@JsonProperty
	private String kindUuid;

	@JsonProperty
	private LoesungszettelRohdaten rohdaten;

	public static LoesungszettelAPIModel createFromLoesungszettel(final Loesungszettel loesungszettel) {

		LoesungszettelAPIModel result = new LoesungszettelAPIModel();
		result.kaengurusprung = loesungszettel.laengeKaengurusprung();
		result.kindUuid = loesungszettel.kindID() != null ? loesungszettel.kindID().identifier().substring(0, 8) : null;
		result.klassenstufe = loesungszettel.klassenstufe();
		result.punkte = loesungszettel.punkteAsString();
		result.quelle = loesungszettel.auswertungsquelle();
		result.rohdaten = loesungszettel.rohdaten();
		result.sortnumber = loesungszettel.sortnumber();
		result.sprache = loesungszettel.sprache();
		result.teilnahmeart = loesungszettel.teilnahmeIdentifier().teilnahmeart();
		result.teilnahmenummer = loesungszettel.teilnahmeIdentifier().teilnahmenummer();
		result.uuid = loesungszettel.identifier().identifier();
		return result;

	}
}
