// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Schule
 */
@Entity
@Table(name = "SCHULEN")
@NamedQueries({
	@NamedQuery(
		name = "SCHULE_FIND_MIT_NAME", query = "select s from Schule s where lower(s.name) like :name and s.name != :excluded"),
	@NamedQuery(
		name = "SCHULE_LOAD_SCHULEN_IN_ORT",
		query = "select s from Schule s where s.ortKuerzel = :ortKuerzel and s.name != :excluded"),
	@NamedQuery(
		name = "SCHULE_FIND_SCHULEN_IN_ORT",
		query = "select s from Schule s where s.ortKuerzel = :ortKuerzel and lower(s.name) like :name and s.name != :excluded"),
	@NamedQuery(
		name = "SCHULE_FIND_SCHULE_IN_ORT_MIT_NAME",
		query = "select s from Schule s where s.ortKuerzel = :ortKuerzel and lower(s.name) = :name"),
	@NamedQuery(
		name = "SCHULE_LOAD_WITH_ORTKUERZEL",
		query = "select s from Schule s where s.ortKuerzel = :ortKuerzel"),
	@NamedQuery(
		name = "SCHULE_LOAD_WITH_LANDKUERZEL",
		query = "select s from Schule s where s.landKuerzel = :landKuerzel"),
	@NamedQuery(
		name = "SCHULE_FIND_BY_KUERZEL", query = "select s from Schule s where s.kuerzel = :kuerzel"),
	@NamedQuery(
		name = "SCHULE_FIND_WITH_KUERZELN", query = "select s from Schule s where s.kuerzel IN :kuerzeln"),
	@NamedQuery(
		name = "SCHULE_COUNT_WITH_KUERZEL", query = "select count(s) from Schule s where s.kuerzel = :kuerzel"),
	@NamedQuery(
		name = "SCHULE_COUNT_IN_ORT", query = "select count(s) from Schule s where s.ortKuerzel = :kuerzel and s.name != :excluded")
})
public class Schule {

	public static final String QUERY_FIND_SCHULEN_MIT_NAME = "SCHULE_FIND_MIT_NAME";

	public static final String QUERY_LOAD_SCHULEN_IN_ORT = "SCHULE_LOAD_SCHULEN_IN_ORT";

	public static final String QUERY_FIND_SCHULEN_IN_ORT = "SCHULE_FIND_SCHULEN_IN_ORT";

	public static final String QUERY_FIND_BY_KUERZEL = "SCHULE_FIND_BY_KUERZEL";

	public static final String QUERY_FIND_SCHULEN_WITH_KUERZELN = "SCHULE_FIND_WITH_KUERZELN";

	public static final String QUERY_COUNT_IN_ORT = "SCHULE_COUNT_IN_ORT";

	public static final String QUERY_COUNT_WITH_KUERZEL = "SCHULE_COUNT_WITH_KUERZEL";

	public static final String QUERY_FIND_SCHULE_IN_ORT_MIT_NAME = "SCHULE_FIND_SCHULE_IN_ORT_MIT_NAME";

	public static final String QUERY_LOAD_SCHULEN_WITH_ORTKUERZEL = "SCHULE_LOAD_WITH_ORTKUERZEL";

	public static final String QUERY_LOAD_SCHULEN_WITH_LANDKUERZEL = "SCHULE_LOAD_WITH_LANDKUERZEL";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "schule_id_generator")
	@GenericGenerator(
		name = "schule_id_generator", strategy = "de.egladil.web.mk_kataloge.infrastructure.persistence.impl.SchuleIdGenerator")
	@Column(name = "KUERZEL")
	@JsonProperty
	private String kuerzel;

	@Column(name = "NAME")
	@JsonProperty
	private String name;

	@Column(name = "ORT_KUERZEL")
	@JsonProperty
	private String ortKuerzel;

	@Column(name = "ORT_NAME")
	@JsonProperty
	private String ortName;

	@Column(name = "LAND_KUERZEL")
	@JsonProperty
	private String landKuerzel;

	@Column(name = "LAND_NAME")
	@JsonProperty
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
