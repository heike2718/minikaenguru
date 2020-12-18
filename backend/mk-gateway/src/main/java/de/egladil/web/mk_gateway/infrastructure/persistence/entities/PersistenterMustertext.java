// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * PersistenterMustertext
 */
@Entity
@Table(name = "MUSTERTEXTE")
public class PersistenterMustertext extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 1L;

	@Column
	private String name;

	@Column
	private String text;

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public String getText() {

		return text;
	}

	public void setText(final String text) {

		this.text = text;
	}

}
