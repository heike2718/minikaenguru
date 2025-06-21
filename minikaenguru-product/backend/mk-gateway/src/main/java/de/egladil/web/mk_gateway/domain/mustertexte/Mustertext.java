// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * Mustertext
 */
public class Mustertext {

	private Identifier identifier;

	private Mustertextkategorie kategorie;

	private String name;

	private String text;

	/**
	 *
	 */
	public Mustertext() {

	}

	/**
	 * @param identifier
	 */
	public Mustertext(final Identifier identifier) {

		this.identifier = identifier;
	}

	public Identifier getIdentifier() {

		return identifier;
	}

	public Mustertextkategorie getKategorie() {

		return kategorie;
	}

	public Mustertext withKategorie(final Mustertextkategorie kategorie) {

		this.kategorie = kategorie;
		return this;
	}

	public String getName() {

		return name;
	}

	public Mustertext withName(final String name) {

		this.name = name;
		return this;
	}

	public String getText() {

		return text;
	}

	public Mustertext withText(final String text) {

		this.text = text;
		return this;
	}

}
