// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * PrivatveranstalterAPIModel
 */
public class PrivatveranstalterAPIModel {

	@JsonProperty
	private boolean hatZugangZuUnterlangen;

	@JsonProperty
	private int anzahlVergangeneTeilnahmen = 0;

	@JsonProperty
	private boolean aktuellAngemeldet;

	@JsonProperty
	private PrivatteilnahmeAPIModel aktuelleTeilnahme;

	@JsonProperty
	private boolean vergangeneTeilnahmenGeladen;

	@JsonProperty
	private List<AnonymisierteTeilnahmeAPIModel> vergangeneTeilnahmen = new ArrayList<>();

	public static PrivatveranstalterAPIModel create(final boolean zugangZuUnterlagen) {

		PrivatveranstalterAPIModel result = new PrivatveranstalterAPIModel();
		result.hatZugangZuUnterlangen = zugangZuUnterlagen;
		return result;
	}

	PrivatveranstalterAPIModel() {

	}

	public PrivatveranstalterAPIModel withAnzahlVergangeneTeilnahmen(final int anzahlVergangeneTeilnahmen) {

		this.anzahlVergangeneTeilnahmen = anzahlVergangeneTeilnahmen;
		return this;
	}

	public PrivatveranstalterAPIModel withTeilnahme(final PrivatteilnahmeAPIModel aktuelleTeilnahme) {

		this.aktuelleTeilnahme = aktuelleTeilnahme;
		this.aktuellAngemeldet = aktuelleTeilnahme != null;
		return this;
	}

	public boolean hatZugangZuUnterlangen() {

		return hatZugangZuUnterlangen;
	}

	public int anzahlVergangeneTeilnahmen() {

		return anzahlVergangeneTeilnahmen;
	}

	public boolean aktuellAngemeldet() {

		return aktuellAngemeldet;
	}

	public PrivatteilnahmeAPIModel aktuelleTeilnahme() {

		return aktuelleTeilnahme;
	}

	public boolean vergangeneTeilnahmenGeladen() {

		return vergangeneTeilnahmenGeladen;
	}

	public List<AnonymisierteTeilnahmeAPIModel> vergangeneTeilnahmen() {

		return vergangeneTeilnahmen;
	}

}