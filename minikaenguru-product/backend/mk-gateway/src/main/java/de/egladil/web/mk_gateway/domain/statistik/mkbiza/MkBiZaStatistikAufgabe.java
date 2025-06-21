// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MkBiZaStatistikAufgabe
 */
@Schema(description = "Statistik zu einer Wettbewerbsaufgabe")
public class MkBiZaStatistikAufgabe {

	@JsonProperty
	@Schema(description = "Nummer der Aufgabe im Wettbewerb")
	private String nummer;

	@JsonProperty
	@Schema(description = "Die Punktzahl die bei falscher Lösung angezogen wird, als String mit Komma")
	private String strafpunkte;

	@JsonProperty
	@Schema(description = "Anzahl Kinder die einen gegebenen Lösungsbuchstaben angekreuzt haben. N für nicht gelöst.")
	private List<MkBiZaGruppierungsitem> anzahlenJeLoesungsbuchstabe = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Anzahl Kinder die einen gegebenen Wertungscode erhalten haben f,r,n")
	private List<MkBiZaGruppierungsitem> anzahlenJeWertungscode = new ArrayList<>();

	public void setNummer(final String nummer) {

		this.nummer = nummer;
	}

	public void setStrafpunkte(final String strafpunkte) {

		this.strafpunkte = strafpunkte;
	}

	public void addAnzahleJeWertungscode(final MkBiZaGruppierungsitem anzahlJeWertungscode) {

		this.anzahlenJeWertungscode.add(anzahlJeWertungscode);
	}

	public void setAnzahlenJeLoesungsbuchstabe(final List<MkBiZaGruppierungsitem> anzahlenJeLoesungsbuchstabe) {

		this.anzahlenJeLoesungsbuchstabe = anzahlenJeLoesungsbuchstabe;
	}

}
