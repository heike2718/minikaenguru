// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel.teilnahmen;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AnonymisierteTeilnahmeAPIModel
 */
public class AnonymisierteTeilnahmeAPIModel {

	@JsonProperty
	private TeilnahmeIdentifier identifier;

	@JsonProperty
	private int anzahlKinder;

	AnonymisierteTeilnahmeAPIModel() {

	}

	public static AnonymisierteTeilnahmeAPIModel create(final TeilnahmeIdentifier identifier) {

		AnonymisierteTeilnahmeAPIModel result = new AnonymisierteTeilnahmeAPIModel();
		result.identifier = identifier;
		return result;
	}

	public TeilnahmeIdentifier identifier() {

		return identifier;
	}

	public int anzahlKinder() {

		return anzahlKinder;
	}

	public AnonymisierteTeilnahmeAPIModel withAnzahlKinder(final int anzahlKinder) {

		this.anzahlKinder = anzahlKinder;
		return this;
	}
}
