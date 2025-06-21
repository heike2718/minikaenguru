// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback.scores.dto;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AufgabenvorschauDto
 */
@Schema(description = "Aufgaben und Lösungen eines Minkänguru-Wettbewerbs für eine gegebene Klassenstufe")
public class AufgabenvorschauDto {

	@JsonProperty
	@Schema(description = "Jahr des Wettbewerbs")
	private String wettbewerbsjahr;

	@JsonProperty
	@Schema(description = "Klassenstufe in menschenlesbarer Form")
	private String klassenstufe;

	@JsonProperty
	@Schema(description = "die einzelnen Aufgaben mit Lösungen")
	private List<Aufgabe> aufgaben;

	public String getWettbewerbsjahr() {

		return wettbewerbsjahr;
	}

	public void setWettbewerbsjahr(final String wettbewerbsjahr) {

		this.wettbewerbsjahr = wettbewerbsjahr;
	}

	public String getKlassenstufe() {

		return klassenstufe;
	}

	public void setKlassenstufe(final String klassenstufe) {

		this.klassenstufe = klassenstufe;
	}

	public List<Aufgabe> getAufgaben() {

		return aufgaben;
	}

	public void setAufgaben(final List<Aufgabe> aufgaben) {

		this.aufgaben = aufgaben;
	}

}
