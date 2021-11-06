// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.api;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.StringLatin;
import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.KlassenstufeAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;
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
	private LoesungszettelpunkteAPIModel punkte;

	@JsonProperty
	@UuidString
	private String klasseId;

	@JsonProperty
	private boolean klassenstufePruefen;

	@JsonProperty
	private boolean dublettePruefen;

	public static KindAPIModel create(final Klassenstufe klassenstufe, final Sprache sprache) {

		KindAPIModel result = new KindAPIModel();
		result.klassenstufe = KlassenstufeAPIModel.create(klassenstufe);
		result.sprache = SpracheAPIModel.create(sprache);

		return result;
	}

	public static KindAPIModel createFromKind(final Kind kind, final Optional<LoesungszettelpunkteAPIModel> optPunkte) {

		KindAPIModel result = KindAPIModel.create(kind.klassenstufe(), kind.sprache())
			.withNachname(kind.nachname())
			.withUuid(kind.identifier().identifier())
			.withVorname(kind.vorname())
			.withZusatz(kind.zusatz())
			.withPunkte(optPunkte.isEmpty() ? null : optPunkte.get());

		if (kind.klasseID() != null) {

			result = result.withKlasseId(kind.klasseID().identifier());
		}

		result.setDublettePruefen(kind.isDublettePruefen());
		result.setKlassenstufePruefen(kind.isKlassenstufePruefen());
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

	public LoesungszettelpunkteAPIModel punkte() {

		return this.punkte;
	}

	public KindAPIModel withPunkte(final LoesungszettelpunkteAPIModel punkte) {

		this.punkte = punkte;
		return this;
	}

	public String klasseId() {

		return klasseId;
	}

	public KindAPIModel withKlasseId(final String klasseId) {

		this.klasseId = klasseId;
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

	public boolean isKlassenstufePruefen() {

		return klassenstufePruefen;
	}

	public void setKlassenstufePruefen(final boolean importiertMitFehler) {

		this.klassenstufePruefen = importiertMitFehler;
	}

	public boolean isDublettePruefen() {

		return dublettePruefen;
	}

	public void setDublettePruefen(final boolean dublettePruefen) {

		this.dublettePruefen = dublettePruefen;
	}
}
