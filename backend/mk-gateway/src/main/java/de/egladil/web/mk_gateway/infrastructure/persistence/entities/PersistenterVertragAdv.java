// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * PersistenterVertragAdv
 */
@Entity
@Table(name = "VERTRAEGE_ADV")
@NamedQueries({
	@NamedQuery(
		name = "PersistenterVertragAdv.FIND_BY_UUID",
		query = "select v from PersistenterVertragAdv v where v.uuid = :uuid"),
	@NamedQuery(
		name = "PersistenterVertragAdv.FIND_BY_SCHULKUERZEL",
		query = "select v from PersistenterVertragAdv v where v.schulkuerzel = :schulkuerzel")
})
public class PersistenterVertragAdv extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 7323501865208010275L;

	public static final String FIND_BY_UUID = "PersistenterVertragAdv.FIND_BY_UUID";

	public static final String FIND_BY_SCHULKUERZEL = "PersistenterVertragAdv.FIND_BY_SCHULKUERZEL";

	@NotNull
	@Size(max = 36)
	@Column(name = "SCHULKUERZEL")
	private String schulkuerzel;

	@NotNull
	@Size(max = 100)
	@Column(name = "SCHULNAME")
	private String schulname;

	@NotNull
	@Size(max = 100)
	@Column(name = "STRASSE")
	private String strasse;

	@NotNull
	@Size(max = 10)
	@Column(name = "HAUSNR")
	private String hausnummer;

	@NotNull
	@Size(max = 10)
	@Column(name = "PLZ")
	private String plz;

	@NotNull
	@Size(max = 100)
	@Column(name = "ORT")
	private String ort;

	@NotNull
	@Size(max = 2)
	@Column(name = "LAENDERCODE")
	private String laendercode;

	@NotNull
	@Size(max = 19)
	@Column(name = "ABGESCHLOSSEN_AM")
	private String abgeschlossenAm;

	@NotNull
	@Size(max = 36)
	@Column(name = "ABGESCHLOSSEN_DURCH")
	private String abgeschlossenDurch;

	@ManyToOne(optional = false)
	@JoinColumn(name = "VERTRAG_ADV_TEXT_UUID")
	private PersistenterVertragAdvText advText;

	public String getSchulkuerzel() {

		return schulkuerzel;
	}

	public void setSchulkuerzel(final String schulkuerzel) {

		this.schulkuerzel = schulkuerzel;
	}

	public String getSchulname() {

		return schulname;
	}

	public void setSchulname(final String schulname) {

		this.schulname = schulname;
	}

	public String getStrasse() {

		return strasse;
	}

	public void setStrasse(final String strasse) {

		this.strasse = strasse;
	}

	public String getHausnummer() {

		return hausnummer;
	}

	public void setHausnummer(final String hausnummer) {

		this.hausnummer = hausnummer;
	}

	public String getPlz() {

		return plz;
	}

	public void setPlz(final String plz) {

		this.plz = plz;
	}

	public String getOrt() {

		return ort;
	}

	public void setOrt(final String ort) {

		this.ort = ort;
	}

	public String getLaendercode() {

		return laendercode;
	}

	public void setLaendercode(final String laendercode) {

		this.laendercode = laendercode;
	}

	public String getAbgeschlossenAm() {

		return abgeschlossenAm;
	}

	public void setAbgeschlossenAm(final String abgeschlossenAm) {

		this.abgeschlossenAm = abgeschlossenAm;
	}

	public String getAbgeschlossenDurch() {

		return abgeschlossenDurch;
	}

	public void setAbgeschlossenDurch(final String abgeschlossenDurch) {

		this.abgeschlossenDurch = abgeschlossenDurch;
	}

	public PersistenterVertragAdvText getAdvText() {

		return advText;
	}

	public void setAdvText(final PersistenterVertragAdvText advText) {

		this.advText = advText;
	}

}
