// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * ConcurrencySafeEntity
 */
public abstract class ConcurrencySafeEntity {

	@Version
	@Column(name = "VERSION")
	@JsonIgnore
	private int version;

	@Transient
	private String importierteUuid;

	public void setImportierteUuid(final String importierteUuid) {

		this.importierteUuid = importierteUuid;
	}

	public String getImportierteUuid() {

		return importierteUuid;
	}

}
