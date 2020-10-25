// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import java.io.Serializable;
import java.util.Objects;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.KlassenstufeAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.SpracheAPIModel;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;

/**
 * KindAPIModel
 */
public class KindAPIModel implements Serializable {

	@JsonIgnore
	private static final long serialVersionUID = 1356650815146560419L;

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
	private String loesungszettelId;

	public static KindAPIModel create(final Klassenstufe klassenstufe, final Sprache sprache) {

		KindAPIModel result = new KindAPIModel();
		result.klassenstufe = KlassenstufeAPIModel.create(klassenstufe);
		result.sprache = SpracheAPIModel.create(sprache);

		return result;
	}

	public static KindAPIModel createFromKind(final Kind kind) {

		KindAPIModel result = KindAPIModel.create(kind.klassenstufe(), kind.sprache())
			.withNachname(kind.nachname())
			.withUuid(kind.identifier().identifier())
			.withVorname(kind.vorname())
			.withZusatz(kind.zusatz());

		if (kind.loesungszettelID() != null) {

			result = result.withLoesungszettelId(kind.loesungszettelID().identifier());

		}

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

	public SpracheAPIModel sprache() {

		return sprache;
	}

	public String loesungszettelId() {

		return loesungszettelId;
	}

	public KindAPIModel withLoesungszettelId(final String loesungszettelId) {

		this.loesungszettelId = loesungszettelId;
		return this;
	}

	@Override
	public int hashCode() {

		return Objects.hash(uuid);
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
		return Objects.equals(uuid, other.uuid);
	}
}
