// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassenlisten;

import java.util.Objects;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * KlassenimportZeile
 */
public class KlassenimportZeile {

	private String vorname;

	private String nachname;

	private String klasse;

	private String klassenstufe;

	@Override
	public String toString() {

		return "KlassenimportZeile [vorname=" + vorname + ", nachname=" + nachname + ", klasse=" + klasse + ", klassenstufe="
			+ klassenstufe + "]";
	}

	@Override
	public int hashCode() {

		return Objects.hash(klasse, klassenstufe, nachname, vorname);
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
		KlassenimportZeile other = (KlassenimportZeile) obj;
		return Objects.equals(klasse, other.klasse) && Objects.equals(klassenstufe, other.klassenstufe)
			&& Objects.equals(nachname, other.nachname) && Objects.equals(vorname, other.vorname);
	}

	/**
	 * Mapped den importierten Wert für Klassenstufe auf eine echte Klassenstufe.
	 *
	 * @return Optional empty, wenn da Schrott drinsteht.
	 */
	public Optional<Klassenstufe> mapKlassenstufe() {

		Klassenstufe result = Klassenstufe.valueOfNumericString(klassenstufe);

		return result == null ? Optional.empty() : Optional.of(result);

	}

	public String getVorname() {

		return vorname;
	}

	public KlassenimportZeile withVorname(final String vorname) {

		this.vorname = vorname;
		return this;
	}

	public String getNachname() {

		return nachname;
	}

	public KlassenimportZeile withNachname(final String nachname) {

		this.nachname = nachname;
		return this;
	}

	public String getKlasse() {

		return klasse;
	}

	public KlassenimportZeile withKlasse(final String klasse) {

		this.klasse = klasse;
		return this;
	}

	public String getKlassenstufe() {

		return klassenstufe;
	}

	public KlassenimportZeile withKlassenstufe(final String klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

}
