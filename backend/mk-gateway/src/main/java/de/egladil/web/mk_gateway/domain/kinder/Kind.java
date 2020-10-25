// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.Objects;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * Kind
 */
@AggregateRoot
public class Kind {

	private Identifier identifier;

	private TeilnahmeIdentifier teilnahmeIdentifier;

	private String vorname;

	private String nachname;

	private String zusatz;

	private Klassenstufe klassenstufe;

	private Sprache sprache;

	private Identifier loesungszettelID;

	private Identifier klasseID;

	public static Kind createFromAPIModelWithoutKlasseID(final KindAPIModel apiModel) {

		Kind result = new Kind();
		result.vorname = apiModel.vorname() == null ? null : apiModel.vorname().trim();
		result.nachname = apiModel.nachname() == null ? null : apiModel.nachname().trim();
		result.zusatz = apiModel.zusatz() == null ? null : apiModel.zusatz().trim();
		result.identifier = apiModel.uuid() == null ? null : new Identifier(apiModel.uuid());
		result.loesungszettelID = apiModel.loesungszettelId() == null ? null : new Identifier(apiModel.loesungszettelId());
		result.sprache = apiModel.sprache().sprache();
		result.klassenstufe = apiModel.klassenstufe().klassenstufe();
		return result;
	}

	private Kind() {

	}

	public Kind(final Identifier identifier) {

		this.identifier = identifier;
	}

	public Identifier identifier() {

		return identifier;
	}

	public void setIdentifier(final Identifier identifier) {

		if (this.identifier != null) {

			throw new IllegalStateException("Der identifier darf nicht geändert werden");
		}
		this.identifier = identifier;

	}

	public TeilnahmeIdentifier teilnahmeIdentifier() {

		return teilnahmeIdentifier;
	}

	public Kind withTeilnahmeIdentifier(final TeilnahmeIdentifier teilnahmeIdentifier) {

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

		return nachname;
	}

	public Kind withNachname(final String nachname) {

		this.nachname = nachname;
		return this;
	}

	public String zusatz() {

		return zusatz;
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

	public String getLowerVornameNullSafe() {

		return vorname == null ? null : vorname.trim().toLowerCase();
	}

	public String getLowerNachnameNullSafe() {

		return nachname == null ? null : nachname.trim().toLowerCase();
	}

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
}
