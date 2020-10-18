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
import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.KlassenstufeAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.SpracheAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * KindAPIModel
 */
public class KindAPIModel {

	@JsonProperty
	@UuidString
	private String uuid;

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

	@JsonProperty
	@UuidString
	private String loesingszettelId;

	public static KindAPIModel create(final Klassenstufe klassenstufe, final Sprache sprache) {

		KindAPIModel result = new KindAPIModel();
		result.klassenstufe = KlassenstufeAPIModel.create(klassenstufe);
		result.sprache = SpracheAPIModel.create(sprache);

		return result;
	}

	public String uuid() {

		return uuid;
	}

	public KindAPIModel withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public String vorname() {

		return vorname;
	}

	public KindAPIModel withVorname(final String vorname) {

		this.vorname = vorname;
		return this;
	}

	public String nachname() {

		return nachname;
	}

	public KindAPIModel withNachname(final String nachname) {

		this.nachname = nachname;
		return this;
	}

	public String zusatz() {

		return zusatz;
	}

	public KindAPIModel withZusatz(final String zusatz) {

		this.zusatz = zusatz;
		return this;
	}

	public KlassenstufeAPIModel klassenstufe() {

		return klassenstufe;
	}

	public KindAPIModel withKlassenstufe(final KlassenstufeAPIModel klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public SpracheAPIModel sprache() {

		return sprache;
	}

	public KindAPIModel withSprache(final SpracheAPIModel sprache) {

		this.sprache = sprache;
		return this;
	}

	public String loesingszettelId() {

		return loesingszettelId;
	}

	public KindAPIModel withLoesingszettelId(final String loesingszettelId) {

		this.loesingszettelId = loesingszettelId;
		return this;
	}

	@Override
	public int hashCode() {

		return Objects.hash(klassenstufe, loesingszettelId, nachname, sprache, uuid, vorname, zusatz);
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
		KindAPIModel other = (KindAPIModel) obj;
		return Objects.equals(klassenstufe, other.klassenstufe) && Objects.equals(loesingszettelId, other.loesingszettelId)
			&& Objects.equals(nachname, other.nachname) && Objects.equals(sprache, other.sprache)
			&& Objects.equals(uuid, other.uuid) && Objects.equals(vorname, other.vorname) && Objects.equals(zusatz, other.zusatz);
	}
}
