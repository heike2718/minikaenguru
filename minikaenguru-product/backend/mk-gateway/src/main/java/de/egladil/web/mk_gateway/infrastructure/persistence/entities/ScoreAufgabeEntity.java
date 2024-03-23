// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import de.egladil.web.mk_gateway.domain.statistik.Aufgabenkategorie;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

/**
 * ScoreAufgabeEntity
 */
@Entity
@Table(name = "SCORES_AUFGABEN")
public class ScoreAufgabeEntity {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "ID_SCORE_KLASSENSTUFE")
	private int idScoreKlassenstufe;

	@Column(name = "AUFGABE_NUMMER")
	private String aufgabeNummer;

	@Column(name = "SCORE_KATEGORIE")
	@Enumerated(EnumType.STRING)
	private Aufgabenkategorie scoreKategorie;

	@Column(name = "SCORE_SCHWIERIGKEITSGRAD")
	private int scoreSchwierigkeitsgrad;

	@Column(name = "SCORE_VERSTAENDLICHKEIT")
	private int scoreVerstaendlichkeit;

	@Column(name = "SCORE_LEHRPLAN")
	private int scoreLehrplankompatibilitaet;

	@Column
	private String freitext;

	@Version
	@Column(name = "VERSION")
	private int version;

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (!(obj instanceof ScoreAufgabeEntity)) {

			return false;
		}
		ScoreAufgabeEntity other = (ScoreAufgabeEntity) obj;

		if (id != other.id) {

			return false;
		}
		return true;
	}

	public int getId() {

		return id;
	}

	public void setId(final int id) {

		this.id = id;
	}

	public int getIdScoreKlassenstufe() {

		return idScoreKlassenstufe;
	}

	public void setIdScoreKlassenstufe(final int scoreKlassenstufeId) {

		this.idScoreKlassenstufe = scoreKlassenstufeId;
	}

	public String getAufgabeNummer() {

		return aufgabeNummer;
	}

	public void setAufgabeNummer(final String aufgabeNummer) {

		this.aufgabeNummer = aufgabeNummer;
	}

	public Aufgabenkategorie getScoreKategorie() {

		return scoreKategorie;
	}

	public void setScoreKategorie(final Aufgabenkategorie scoreKategorie) {

		this.scoreKategorie = scoreKategorie;
	}

	public int getScoreSchwierigkeitsgrad() {

		return scoreSchwierigkeitsgrad;
	}

	public void setScoreSchwierigkeitsgrad(final int scoreSchwierigkeitsgrad) {

		this.scoreSchwierigkeitsgrad = scoreSchwierigkeitsgrad;
	}

	public int getScoreVerstaendlichkeit() {

		return scoreVerstaendlichkeit;
	}

	public void setScoreVerstaendlichkeit(final int scoreVerstaendlichkeit) {

		this.scoreVerstaendlichkeit = scoreVerstaendlichkeit;
	}

	public int getScoreLehrplankompatibilitaet() {

		return scoreLehrplankompatibilitaet;
	}

	public void setScoreLehrplankompatibilitaet(final int scoreLehrplankompatibilitaet) {

		this.scoreLehrplankompatibilitaet = scoreLehrplankompatibilitaet;
	}

	public String getFreitext() {

		return freitext;
	}

	public void setFreitext(final String freitext) {

		this.freitext = freitext;
	}

}
