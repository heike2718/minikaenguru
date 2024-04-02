// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MinikaenguruAufgabenDto
 */
@Schema(description = "Aufgaben und Lösungen eines Minkänguru-Wettbewerbs für eine gegebene Klassenstufe")
public class MinikaenguruAufgabenDto {

	@JsonProperty
	@Schema(description = "Jahr des Wettbewerbs")
	private String wettbewerbsjahr;

	@JsonProperty
	@Schema(description = "Klassenstufe in menschenlesbarer Form")
	private String klassenstufe;

	@JsonProperty
	@Schema(description = "die einzelnen Aufgaben mit Lösungen")
	private List<MinikaenguruAufgabe> aufgaben;

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

	public List<MinikaenguruAufgabe> getAufgaben() {

		return aufgaben;
	}

	public void setAufgaben(final List<MinikaenguruAufgabe> aufgaben) {

		this.aufgaben = aufgaben;
	}

}
