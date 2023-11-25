// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen.admin;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.PrivatteilnahmeAPIModel;

/**
 * PrivatteilnahmeAdminOverview
 */
public class PrivatteilnahmeAdminOverview {

	@JsonProperty
	private boolean aktuellAngemeldet = false;

	@JsonProperty
	private int anzahlTeilnahmen = 0;

	@JsonProperty
	private PrivatteilnahmeAPIModel aktuelleTeilnahme;

	@JsonProperty
	private List<AnonymisierteTeilnahmeAPIModel> teilnahmen = new ArrayList<>();

	public boolean aktuellAngemeldet() {

		return aktuellAngemeldet;
	}

	public int anzahlTeilnahmen() {

		return anzahlTeilnahmen;
	}

	public List<AnonymisierteTeilnahmeAPIModel> teilnahmen() {

		return teilnahmen;
	}

	public PrivatteilnahmeAdminOverview withTeilnahmen(final List<AnonymisierteTeilnahmeAPIModel> teilnahmen) {

		this.teilnahmen = teilnahmen;
		this.anzahlTeilnahmen = teilnahmen.size();
		return this;
	}

	public PrivatteilnahmeAPIModel aktuelleTeilnahme() {

		return aktuelleTeilnahme;
	}

	public PrivatteilnahmeAdminOverview withAktuelleTeilnahme(final PrivatteilnahmeAPIModel aktuelleTeilnahme) {

		this.aktuelleTeilnahme = aktuelleTeilnahme;
		this.aktuellAngemeldet = true;
		return this;
	}
}
