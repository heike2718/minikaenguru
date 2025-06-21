// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities;

import java.util.Objects;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;

/**
 * Schule
 */
@Entity
@Table(name = "SCHULEN")
@NamedQueries({
	@NamedQuery(
		name = Schule.FIND_SCHULEN_MIT_NAME, query = "select s from Schule s where lower(s.name) like :name and s.name != :excluded"),
	@NamedQuery(
		name = Schule.LOAD_SCHULEN_IN_ORT,
		query = "select s from Schule s where s.ortKuerzel = :ortKuerzel and s.name != :excluded order by s.name"),
	@NamedQuery(
		name = Schule.FIND_SCHULEN_IN_ORT,
		query = "select s from Schule s where s.ortKuerzel = :ortKuerzel and lower(s.name) like :name and s.name != :excluded order by s.name"),
	@NamedQuery(
		name = Schule.FIND_SCHULE_IN_ORT_MIT_NAME,
		query = "select s from Schule s where s.ortKuerzel = :ortKuerzel and lower(s.name) = :name order by s.name"),
	@NamedQuery(
		name = Schule.LOAD_SCHULEN_WITH_ORTKUERZEL,
		query = "select s from Schule s where s.ortKuerzel = :ortKuerzel order by s.name"),
	@NamedQuery(
		name = Schule.LOAD_SCHULEN_WITH_LANDKUERZEL,
		query = "select s from Schule s where s.landKuerzel = :landKuerzel order by s.name"),
	@NamedQuery(
		name = Schule.FIND_BY_KUERZEL, query = "select s from Schule s where s.kuerzel = :kuerzel"),
	@NamedQuery(
		name = Schule.FIND_SCHULEN_WITH_KUERZELN, query = "select s from Schule s where s.kuerzel IN :kuerzeln order by s.name"),
	@NamedQuery(
		name = Schule.COUNT_WITH_KUERZEL, query = "select count(s) from Schule s where s.kuerzel = :kuerzel"),
	@NamedQuery(
		name = Schule.COUNT_IN_ORT, query = "select count(s) from Schule s where s.ortKuerzel = :kuerzel and s.name != :excluded")
})
public class Schule {

	public static final String FIND_SCHULEN_MIT_NAME = "Schule.FIND_MIT_NAME";

	public static final String LOAD_SCHULEN_IN_ORT = "Schule.LOAD_SCHULEN_IN_ORT";

	public static final String FIND_SCHULEN_IN_ORT = "Schule.FIND_SCHULEN_IN_ORT";

	public static final String FIND_BY_KUERZEL = "Schule.FIND_BY_KUERZEL";

	public static final String FIND_SCHULEN_WITH_KUERZELN = "Schule.FIND_WITH_KUERZELN";

	public static final String COUNT_IN_ORT = "Schule.COUNT_IN_ORT";

	public static final String COUNT_WITH_KUERZEL = "Schule.COUNT_WITH_KUERZEL";

	public static final String FIND_SCHULE_IN_ORT_MIT_NAME = "Schule.FIND_SCHULE_IN_ORT_MIT_NAME";

	public static final String LOAD_SCHULEN_WITH_ORTKUERZEL = "Schule.LOAD_WITH_ORTKUERZEL";

	public static final String LOAD_SCHULEN_WITH_LANDKUERZEL = "Schule.LOAD_WITH_LANDKUERZEL";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "schule_id_generator")
	@GenericGenerator(
		name = "schule_id_generator", strategy = "de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.dao.SchuleIdGenerator")
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
