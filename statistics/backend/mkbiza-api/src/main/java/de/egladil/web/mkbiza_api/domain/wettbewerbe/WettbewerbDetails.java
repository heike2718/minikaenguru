// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.wettbewerbe;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkbiza_api.domain.Klassenstufe;
import de.egladil.web.mkbiza_api.domain.dto.Gruppierungsitem;

/**
 * WettbewerbDetails
 */
@Schema(description = "Stellt die statistischen Details eine Minikänguru-Wettbewerbs zur Verfügung")
public class WettbewerbDetails {

	@JsonProperty
	private int jahr;

	@JsonProperty
	private int anzahlKinderGesamt;

	@JsonProperty
	private long anzahlPrivatanmeldungen;

	@JsonProperty
	private long anzahlSchulanmeldungen;

	@JsonProperty
	private long teilnehmendeSchulenGesamt;

	@JsonProperty
	private List<Klassenstufe> klassenstufen = new ArrayList<>();

	@JsonProperty
	private List<Gruppierungsitem> schulenJeLand = new ArrayList<>();

	@JsonProperty
	private List<Gruppierungsitem> schulkinderJeLand;

	@JsonProperty
	private List<Gruppierungsitem> kinderJeTeilnahmeart;

	@JsonProperty
	private List<Gruppierungsitem> kinderJeKlassenstufe;

	@JsonProperty
	private List<Gruppierungsitem> kinderJeSprache;

}
