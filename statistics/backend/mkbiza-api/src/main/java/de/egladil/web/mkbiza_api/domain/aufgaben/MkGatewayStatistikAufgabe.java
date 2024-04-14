// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkbiza_api.domain.dto.Gruppierungsitem;

/**
 * MkGatewayStatistikAufgabe.
 */
@Schema(description = "Statistik zu einer Aufgabe")
public class MkGatewayStatistikAufgabe {

	@JsonProperty
	@Schema(description = "Nummer der Aufgabe im Wettbewerb")
	private String nummer;

	@JsonProperty
	@Schema(description = "Strafpunkte bei falscher Lösung.")
	private String strafpunkte;

	@JsonProperty
	@Schema(description = "Anzahl je Lösungsbuchstabe A-E oder N für nicht gelöst")
	private List<Gruppierungsitem> anzahlenJeLoesungsbuchstabe = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Anzahl je Wertungscode 'falsch', 'richtig', 'nicht gelöst'")
	private List<Gruppierungsitem> anzahlenJeWertungscode = new ArrayList<>();

	public String getNummer() {

		return nummer;
	}

	public String getStrafpunkte() {

		return strafpunkte;
	}

	public List<Gruppierungsitem> getAnzahlenJeLoesungsbuchstabe() {

		return anzahlenJeLoesungsbuchstabe;
	}

	public List<Gruppierungsitem> getAnzahlenJeWertungscode() {

		return anzahlenJeWertungscode;
	}
}
