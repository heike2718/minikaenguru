// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.KlassenstufeAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.SpracheAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * KindEditorModel
 */
public class KindEditorModel {

	@JsonProperty
	@NotBlank
	@StringLatin
	private String vorname;

	@JsonProperty
	@StringLatin
	private String nachname;

	@JsonProperty
	@StringLatin
	private String zusatz;

	@JsonProperty
	@NotNull
	private KlassenstufeAPIModel klassenstufe;

	@JsonProperty
	@NotNull
	private SpracheAPIModel sprache;

	public static KindEditorModel create(final Klassenstufe klassenstufe, final Sprache sprache) {

		KindEditorModel result = new KindEditorModel();
		result.klassenstufe = KlassenstufeAPIModel.create(klassenstufe);
		result.sprache = SpracheAPIModel.create(sprache);

		return result;
	}

	public String vorname() {

		return vorname;
	}

	public String nachname() {

		return nachname;
	}

	public String zusatz() {

		return zusatz;
	}

	public KlassenstufeAPIModel klassenstufe() {

		return klassenstufe;
	}

	public SpracheAPIModel sprache() {

		return sprache;
	}

	public KindEditorModel withVorname(final String vorname) {

		this.vorname = vorname;
		return this;
	}

	public KindEditorModel withNachname(final String nachname) {

		this.nachname = nachname;
		return this;
	}

	public KindEditorModel withZusatz(final String zusatz) {

		this.zusatz = zusatz;
		return this;
	}

	public KindEditorModel withKlassenstufe(final KlassenstufeAPIModel klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public KindEditorModel withSprache(final SpracheAPIModel sprache) {

		this.sprache = sprache;
		return this;
	}

	@Override
	public int hashCode() {

		return Objects.hash(klassenstufe, nachname, sprache, vorname, zusatz);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		KindEditorModel other = (KindEditorModel) obj;
		return Objects.equals(klassenstufe, other.klassenstufe) && Objects.equals(nachname, other.nachname)
			&& Objects.equals(sprache, other.sprache) && Objects.equals(vorname, other.vorname)
			&& Objects.equals(zusatz, other.zusatz);
	}

}
