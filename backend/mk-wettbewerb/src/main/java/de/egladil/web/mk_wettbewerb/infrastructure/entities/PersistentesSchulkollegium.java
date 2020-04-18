// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.entities;

/**
 * PersistentesSchulkollegium
 */
// @Entity
// @Table(name = "SCHULKOLLEGIEN")
public class PersistentesSchulkollegium extends ConcurrencySafeEntity {

	// @Id
	// @GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid_generator")
	// @GenericGenerator(
	// name = "uuid_generator", strategy = "de.egladil.web.mk_wettbewerb.infrastructure.entities.UuidGenerator")
	// @UuidString
	// @NotNull
	// @Size(min = 1, max = 40)
	// @Column(name = "UUID")
	// @JsonIgnore
	private String uuid;

	// @Column(name = "KOLLEGIUM")
	private String kollegium;

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
