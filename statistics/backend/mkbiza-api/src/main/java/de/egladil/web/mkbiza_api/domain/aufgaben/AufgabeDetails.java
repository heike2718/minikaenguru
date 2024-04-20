// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.aufgaben;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mkbiza_api.domain.Passung;
import de.egladil.web.mkbiza_api.domain.dto.Gruppierungsitem;

/**
 * AufgabeDetails. Merge aus dem, was mja-api und mk-gateway zu dieser Aufgabe zu sagen haben.
 */
@Schema(description = "Alle Details zu einer Aufgabe eines Minkänguru-Wettbewerbs")
public class AufgabeDetails {

	@JsonProperty
	@Schema(description = "Nummer der Aufgabe")
	private String nummer;

	@JsonProperty
	@Schema(description = "Punkte für diese Aufgabe.")
	private int punkte;

	@JsonProperty
	@Schema(description = "Strafpunkte bei falscher Lösung.")
	private String strafpunkte;

	@JsonProperty
	@Schema(description = "der korrekte Lösungsbuchstabe")
	private String loesungsbuchstabe;

	@JsonProperty
	@Schema(description = "Quelle ür eine Zitatsection")
	private String quelle;

	@JsonProperty
	@Schema(description = "Images, die angezeigt werden können. Frage und optionale Lösung")
	private Images images;

	@JsonProperty
	@Schema(description = "Anzahl je Lösungsbuchstabe A-E oder N für nicht gelöst")
	private List<Gruppierungsitem> anzahlenJeLoesungsbuchstabe = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Anzahl je Wertungscode 'falsch', 'richtig', 'nicht gelöst'")
	private List<Gruppierungsitem> anzahlenJeWertungscode = new ArrayList<>();

	@JsonProperty
	@Schema(description = "Grad der Zugehörigkeit zur gewählten Aufgabenkatedorie")
	private String gradZugehoerigkeitZuAufgabenkategorie;

	@JsonProperty
	@Schema(description = "Passung der Aufgabe zu ihrer Kategorie")
	private Passung passung;

	@JsonProperty
	@Schema(description = "prozentualer Anteil richtiger Lösungen")
	private String prozentRichtigerLoesungen;

	public String getNummer() {

		return nummer;
	}

	public void setNummer(final String nummer) {

		this.nummer = nummer;
	}

	public int getPunkte() {

		return punkte;
	}

	public void setPunkte(final int punkte) {

		this.punkte = punkte;
	}

	public String getStrafpunkte() {

		return strafpunkte;
	}

	public void setStrafpunkte(final String strafpunkte) {

		this.strafpunkte = strafpunkte;
	}

	public String getLoesungsbuchstabe() {

		return loesungsbuchstabe;
	}

	public void setLoesungsbuchstabe(final String loesungsbuchstabe) {

		this.loesungsbuchstabe = loesungsbuchstabe;
	}

	public String getQuelle() {

		return quelle;
	}

	public void setQuelle(final String quelle) {

		this.quelle = quelle;
	}

	public Images getImages() {

		return images;
	}

	public void setImages(final Images images) {

		this.images = images;
	}

	public List<Gruppierungsitem> getAnzahlenJeLoesungsbuchstabe() {

		return anzahlenJeLoesungsbuchstabe;
	}

	public void setAnzahlenJeLoesungsbuchstabe(final List<Gruppierungsitem> anzahlenJeLoesungsbuchstabe) {

		this.anzahlenJeLoesungsbuchstabe = anzahlenJeLoesungsbuchstabe;
	}

	public List<Gruppierungsitem> getAnzahlenJeWertungscode() {

		return anzahlenJeWertungscode;
	}

	public void setAnzahlenJeWertungscode(final List<Gruppierungsitem> anzahlenJeWertungscode) {

		this.anzahlenJeWertungscode = anzahlenJeWertungscode;
	}

	public String getGradZugehoerigkeitZuAufgabenkategorie() {

		return gradZugehoerigkeitZuAufgabenkategorie;
	}

	public void setGradZugehoerigkeitZuAufgabenkategorie(final String membershipDegree) {

		this.gradZugehoerigkeitZuAufgabenkategorie = membershipDegree;
	}

	public Passung getPassung() {

		return passung;
	}

	public void setPassung(final Passung passung) {

		this.passung = passung;
	}

	public String getProzentRichtigerLoesungen() {

		return prozentRichtigerLoesungen;
	}

	public void setProzentRichtigerLoesungen(final String prozentRichtigerLoesungen) {

		this.prozentRichtigerLoesungen = prozentRichtigerLoesungen;
	}
}
