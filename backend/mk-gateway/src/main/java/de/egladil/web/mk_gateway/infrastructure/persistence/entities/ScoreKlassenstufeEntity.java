// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import de.egladil.web.mk_gateway.domain.feedback.scores.Schriftart;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
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
 * ScoreKlassenstufeEntity
 */
@Entity
@Table(name = "SCORES_KLASSENSTUFEN")
public class ScoreKlassenstufeEntity {

	@Id
	@Column
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(name = "KLASSENSTUFE")
	@Enumerated(EnumType.STRING)
	private Klassenstufe klassenstufe;

	@Column(name = "WETTBEWERB_UUID")
	private String wettbewerbUuid;

	@Column(name = "LANDKUERZEL")
	private String landkuerzel;

	@Column(name = "SCORE_SPASS")
	private int scoreSpass;

	@Column(name = "SCORE_ZUFRIEDENHEIT")
	private int scoreZufriedenheit;

	@Column(name = "FONT")
	@Enumerated(EnumType.STRING)
	private Schriftart schriftart;

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

		if (!(obj instanceof ScoreKlassenstufeEntity)) {

			return false;
		}
		ScoreKlassenstufeEntity other = (ScoreKlassenstufeEntity) obj;

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

	public Klassenstufe getKlassenstufe() {

		return klassenstufe;
	}

	public void setKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
	}

	public String getWettbewerbUuid() {

		return wettbewerbUuid;
	}

	public void setWettbewerbUuid(final String wettbewerbUuid) {

		this.wettbewerbUuid = wettbewerbUuid;
	}

	public String getLandkuerzel() {

		return landkuerzel;
	}

	public void setLandkuerzel(final String landkuerzel) {

		this.landkuerzel = landkuerzel;
	}

	public int getScoreSpass() {

		return scoreSpass;
	}

	public void setScoreSpass(final int scoreSpass) {

		this.scoreSpass = scoreSpass;
	}

	public int getScoreZufriedenheit() {

		return scoreZufriedenheit;
	}

	public void setScoreZufriedenheit(final int scoreZufriedenheit) {

		this.scoreZufriedenheit = scoreZufriedenheit;
	}

	public String getFreitext() {

		return freitext;
	}

	public void setFreitext(final String freitext) {

		this.freitext = freitext;
	}

	public Schriftart getSchriftart() {

		return schriftart;
	}

	public void setSchriftart(final Schriftart schriftart) {

		this.schriftart = schriftart;
	}

}
