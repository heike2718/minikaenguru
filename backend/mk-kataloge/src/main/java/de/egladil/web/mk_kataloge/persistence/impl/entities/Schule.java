// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Schule
 */
@Entity
@Table(name = "SCHULEN")
public class Schule {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "schule_id_generator")
	@GenericGenerator(name = "schule_id_generator", strategy = "de.egladil.web.mk_kataloge.persistence.impl.SchuleIdGenerator")
	@Column(name = "KUERZEL")
	private String kuerzel;

	@Column(name = "NAME")
	private String name;

	@Column(name = "ORT_KUERZEL")
	private String ortKuerzel;

	@Column(name = "ORT_NAME")
	private String ortName;

	@Column(name = "LAND_KUERZEL")
	private String landKuerzel;

	@Column(name = "LAND_NAME")
	private String landName;

	@Version
	@Column(name = "VERSION")
	@JsonIgnore
	private int version;

	@Transient
	private String importiertesKuerzel;

	/**
	 *
	 */
	public Schule() {

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

	public String getImportiertesKuerzel() {

		return importiertesKuerzel;
	}

	public void setImportiertesKuerzel(final String importiertesKuerzel) {

		this.importiertesKuerzel = importiertesKuerzel;
	}

	@Override
	public int hashCode() {

		return Objects.hash(kuerzel);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		Schule other = (Schule) obj;
		return Objects.equals(kuerzel, other.kuerzel);
	}

	public String printForLog() {

		return "Schule [kuerzel=" + kuerzel + ", name=" + name + ", ortKuerzel=" + ortKuerzel + ", ortName=" + ortName
			+ ", landKuerzel=" + landKuerzel + ", landName=" + landName + ", importiertesKuerzel=" + importiertesKuerzel + "]";
	}

	@Override
	public String toString() {

		return "Schule [kuerzel=" + kuerzel + ", name=" + name + "]";
	}

}