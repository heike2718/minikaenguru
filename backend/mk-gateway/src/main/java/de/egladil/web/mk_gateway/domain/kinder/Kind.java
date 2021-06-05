// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.api.KindEditorModel;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;

/**
 * Kind
 */
@AggregateRoot
public class Kind {

	@JsonProperty
	private Identifier identifier;

	@JsonProperty
	private TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier;

	@JsonProperty
	private String vorname;

	@JsonProperty
	private String nachname;

	@JsonProperty
	private String zusatz;

	@JsonProperty
	private Klassenstufe klassenstufe;

	@JsonProperty
	private Sprache sprache;

	@JsonProperty
	private Identifier loesungszettelID;

	@JsonProperty
	private Identifier klasseID;

	@JsonProperty
	private String landkuerzel;

	@JsonProperty
	private boolean klassenstufePruefen;

	@JsonProperty
	private boolean dublettePruefen;

	public Kind() {

	}

	public Kind(final Identifier identifier) {

		this.identifier = identifier;
	}

	public Kind withDaten(final KindEditorModel kindEditorModel) {

		this.vorname = kindEditorModel.vorname() == null ? null : kindEditorModel.vorname().trim();
		this.nachname = kindEditorModel.nachname() == null ? null : kindEditorModel.nachname().trim();
		this.zusatz = kindEditorModel.zusatz() == null ? null : kindEditorModel.zusatz().trim();
		this.sprache = kindEditorModel.sprache().sprache();
		this.klassenstufe = kindEditorModel.klassenstufe().klassenstufe();

		if (StringUtils.isNotBlank(kindEditorModel.klasseUuid())) {

			this.klasseID = new Identifier(kindEditorModel.klasseUuid());
		}
		return this;
	}

	public Identifier identifier() {

		return identifier;
	}

	public Kind withIdentifier(final Identifier identifier) {

		this.identifier = identifier;
		return this;
	}

	public TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier() {

		return teilnahmeIdentifier;
	}

	public Kind withTeilnahmeIdentifier(final TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier) {

		this.teilnahmeIdentifier = teilnahmeIdentifier;
		return this;
	}

	public String vorname() {

		return vorname;
	}

	public Kind withVorname(final String vorname) {

		this.vorname = vorname;
		return this;
	}

	public String nachname() {

		return StringUtils.isBlank(nachname) ? null : nachname;
	}

	public Kind withNachname(final String nachname) {

		this.nachname = nachname;
		return this;
	}

	public String zusatz() {

		return StringUtils.isBlank(zusatz) ? null : zusatz;
	}

	public Kind withZusatz(final String zusatz) {

		this.zusatz = zusatz;
		return this;
	}

	public Klassenstufe klassenstufe() {

		return klassenstufe;
	}

	public Kind withKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public Sprache sprache() {

		return sprache;
	}

	public Kind withSprache(final Sprache sprache) {

		this.sprache = sprache;
		return this;
	}

	public Identifier loesungszettelID() {

		return loesungszettelID;
	}

	public Kind withLoesungszettelID(final Identifier loesungszettelID) {

		this.loesungszettelID = loesungszettelID;
		return this;
	}

	public Identifier klasseID() {

		return klasseID;
	}

	public Kind withKlasseID(final Identifier klasseID) {

		this.klasseID = klasseID;
		return this;
	}

	public String landkuerzel() {

		return landkuerzel;
	}

	public Kind withLandkuerzel(final String landkuerzel) {

		this.landkuerzel = landkuerzel;
		return this;
	}

	@JsonIgnore
	public String getLowerVornameNullSafe() {

		return vorname == null ? null : vorname.trim().toLowerCase();
	}

	@JsonIgnore
	public String getLowerNachnameNullSafe() {

		return nachname == null ? null : nachname.trim().toLowerCase();
	}

	@JsonIgnore
	public String getLowerZusatzNullSafe() {

		return zusatz == null ? null : zusatz.trim().toLowerCase();
	}

	@Override
	public int hashCode() {

		return Objects.hash(identifier);
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
		Kind other = (Kind) obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {

		return "Kind [vorname=" + vorname + ", nachname=" + nachname + ", zusatz=" + zusatz + ", klassenstufe=" + klassenstufe
			+ ", sprache=" + sprache + ", identifier=" + identifier + "]";
	}

	/**
	 * Gibt den vollen Namen für die Urkunde zurück.
	 *
	 * @return Sring
	 */
	public String nameUrkunde() {

		if (StringUtils.isNotBlank(nachname)) {

			return vorname + " " + nachname;
		}

		return vorname;
	}

	/**
	 * Gibt den vollen Namen für die Schulstatistik zurück.
	 *
	 * @return Sring
	 */
	public String nameStatistik() {

		if (StringUtils.isNotBlank(zusatz) && StringUtils.isNotBlank(nachname)) {

			return vorname + " " + nachname + " (" + zusatz + ")";
		}

		if (StringUtils.isNotBlank(nachname)) {

			return vorname + " " + nachname;
		}

		return vorname;
	}

	public void deleteLoesungszettel() {

		this.loesungszettelID = null;
	}

	public boolean isKlassenstufePruefen() {

		return klassenstufePruefen;
	}

	public void setKlassenstufePruefen(final boolean klassenstufePruefen) {

		this.klassenstufePruefen = klassenstufePruefen;
	}

	public boolean isDublettePruefen() {

		return dublettePruefen;
	}

	public void setDublettePruefen(final boolean dublettePruefen) {

		this.dublettePruefen = dublettePruefen;
	}

}
