// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.NamedQueries;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.Table;

/**
 * PersistenterNewsletter
 */
@Entity
@Table(name = "NEWSLETTERS")
@NamedQueries({
	@NamedQuery(name = "PersistenterNewsletter.LOAD_ALL", query = "select n from PersistenterNewsletter n order by n.text"),
	@NamedQuery(name = "PersistenterNewsletter.FIND_BY_UUID", query = "select n from PersistenterNewsletter n where n.uuid = :uuid")
})
public class PersistenterNewsletter extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 1L;

	public static final String LOAD_ALL = "PersistenterNewsletter.LOAD_ALL";

	public static final String FIND_BY_UUID = "PersistenterNewsletter.FIND_BY_UUID";

	@Column
	private String betreff;

	@Column
	private String text;

	public String getBetreff() {

		return betreff;
	}

	public void setBetreff(final String betreff) {

		this.betreff = betreff;
	}

	public String getText() {

		return text;
	}

	public void setText(final String text) {

		this.text = text;
	}

}
