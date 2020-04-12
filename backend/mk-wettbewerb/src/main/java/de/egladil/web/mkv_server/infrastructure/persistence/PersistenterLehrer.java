// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * PersistenterLehrer
 */
@Entity
@Table(name = "LEHRER")
public class PersistenterLehrer extends ConcurrencySafeEntity {

	@Column(name = "SCHULKUERZEL")
	private String schulkuerzel;

	public String getSchulkuerzel() {

		return schulkuerzel;
	}

	public void setSchulkuerzel(final String schulkuerzel) {

		this.schulkuerzel = schulkuerzel;
	}

}
