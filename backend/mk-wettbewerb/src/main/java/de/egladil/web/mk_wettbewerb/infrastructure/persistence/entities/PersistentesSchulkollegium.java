// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.GenericGenerator;

import de.egladil.web.commons_validation.annotations.UuidString;

/**
 * PersistentesSchulkollegium
 */
@Entity
@Table(name = "SCHULKOLLEGIEN")
@NamedQueries(@NamedQuery(
	name = "FIND_SCHULKOLLEGIUM_BY_UUID", query = "select sk from PersistentesSchulkollegium sk where sk.uuid = :uuid"))
public class PersistentesSchulkollegium extends ConcurrencySafeEntity {

	public static final String FIND_BY_UUID_QUERY = "FIND_SCHULKOLLEGIUM_BY_UUID";

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid_generator")
	@GenericGenerator(
		name = "uuid_generator", strategy = "de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.UuidGenerator")
	@UuidString
	@NotNull
	@Size(min = 1, max = 40)
	@Column(name = "UUID")
	private String uuid;

	@Column(name = "KOLLEGIUM")
	private String kollegium;

	@Version
	@Column(name = "VERSION")
	private int version;

	public String getUuid() {

		return uuid;
	}

	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}

	public String getKollegium() {

		return kollegium;
	}

	public void setKollegium(final String kollegium) {

		this.kollegium = kollegium;
	}

}
