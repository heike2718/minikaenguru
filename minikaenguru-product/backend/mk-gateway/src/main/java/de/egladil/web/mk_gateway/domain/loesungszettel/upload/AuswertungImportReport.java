// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;

/**
 * AuswertungImportReport
 */
public class AuswertungImportReport {

	@JsonProperty
	AnonymisierteTeilnahmeAPIModel teilnahme;

	@JsonProperty
	private List<String> fehlerhafteZeilen = new ArrayList<>();

	public List<String> getFehlerhafteZeilen() {

		return fehlerhafteZeilen;
	}

	public void setFehlerhafteZeilen(final List<String> fehlerhafteZeilen) {

		this.fehlerhafteZeilen = fehlerhafteZeilen;
	}

	public AnonymisierteTeilnahmeAPIModel getTeilnahme() {

		return teilnahme;
	}

	public void setTeilnahme(final AnonymisierteTeilnahmeAPIModel teilnahme) {

		this.teilnahme = teilnahme;
	}

}
