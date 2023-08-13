// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

import de.egladil.web.mk_gateway.domain.mustertexte.Mustertextkategorie;

/**
 * PersistenterMustertext
 */
@Entity
@Table(name = "MUSTERTEXTE")
public class PersistenterMustertext extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 1L;

	@Column(name = "KATEGORIE")
	@Enumerated(EnumType.STRING)
	private Mustertextkategorie kategorie;

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

	public Mustertextkategorie getKategorie() {

		return kategorie;
	}

	public void setKategorie(final Mustertextkategorie kategorie) {

		this.kategorie = kategorie;
	}

}
