// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.klassenstufen;

import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkbiza_api.domain.aufgaben.MjaAufgabeDetails;

/**
 * MjaAufgabenKlassenstufeDto. Rsponse-Payload der REST-API mja-api/public/minikaenguru/{jahr}/{klassenstufe}<br>
 * <br>
 * Ist das MinikaenguruAufgabenKlassenstufeDto von der mja-api
 */
@Schema(description = "Aufgaben und Lösungen eines Minkänguru-Wettbewerbs für eine gegebene Klassenstufe.")
public class MjaAufgabenKlassenstufeDto {

	@JsonProperty
	@Schema(description = "Jahr des Wettbewerbs")
	private String wettbewerbsjahr;

	@JsonProperty
	@Schema(description = "Klassenstufe in menschenlesbarer Form")
	private String klassenstufe;

	@JsonProperty
	@Schema(description = "die einzelnen Aufgaben mit Lösungen")
	private List<MjaAufgabeDetails> aufgaben;

	public String getWettbewerbsjahr() {

		return wettbewerbsjahr;
	}

	public String getKlassenstufe() {

		return klassenstufe;
	}

	public List<MjaAufgabeDetails> getAufgaben() {

		return aufgaben;
	}

}
