// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * SchuleInverse
 */
@Entity
@Table(name = "mkverwaltung.vw_schulen")
public class SchuleInverse {

	@Id
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME")
	private String name;

	@Column(name = "KUERZEL")
	private String kuerzel;

	@Column(name = "ORT_NAME")
	private String ortName;

	@Column(name = "ORT_KUERZEL")
	private String ortKuerzel;

	@Column(name = "LAND_NAME")
	private String landName;

	@Column(name = "LAND_KUERZEL")
	private String landKuerzel;

	/**
	 *
	 */
	public SchuleInverse() {

	}

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	public String getName() {

		return name;
	}

	public void setName(final String schuleName) {

		this.name = schuleName;
	}

	public String getKuerzel() {

		return kuerzel;
	}

	public void setKuerzel(final String schuleKuerzel) {

		this.kuerzel = schuleKuerzel;
	}

	public String getOrtName() {

		return ortName;
	}

	public void setOrtName(final String ortName) {

		this.ortName = ortName;
	}

	public String getOrtKuerzel() {

		return ortKuerzel;
	}

	public void setOrtKuerzel(final String ortKuerzel) {

		this.ortKuerzel = ortKuerzel;
	}

	public String getLandName() {

		return landName;
	}

	public void setLandName(final String landName) {

		this.landName = landName;
	}

	public String getLandKuerzel() {

		return landKuerzel;
	}

	public void setLandKuerzel(final String landKuerzel) {

		this.landKuerzel = landKuerzel;
	}

}
