// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.commons_validation.annotations.Kuerzel;
import de.egladil.web.mk_commons.domain.ITeilnahmeIdentifierProvider;
import de.egladil.web.mk_commons.domain.enums.Teilnahmeart;

/**
 * TeilnahmeIdentifier fasst die Attribute zusammen, die zur Identifizierung von Entities dienen, die semantisch mit
 * dieser Teilnahme verknüpft sind.
 */
public class TeilnahmeIdentifier implements ITeilnahmeIdentifierProvider {

	@NotNull
	private Teilnahmeart teilnahmeart;

	@Digits(fraction = 0, integer = 4)
	private String jahr;

	@NotNull
	@Kuerzel
	@Size(min = 8, max = 8)
	private String kuerzel;

	public static TeilnahmeIdentifier createFromTeilnahmeIdentifierProvider(final ITeilnahmeIdentifierProvider iTeilnahmeIdentifierProvider) {

		return iTeilnahmeIdentifierProvider.provideTeilnahmeIdentifier();
	}

	/**
	 * @param  teilnahmeart
	 *                      Teilnahmeart
	 * @param  kuerzel
	 *                      String
	 * @param  jahr
	 *                      String
	 * @return
	 */
	public static TeilnahmeIdentifier create(final Teilnahmeart teilnahmeart, final String kuerzel, final String jahr) {

		return new TeilnahmeIdentifier(teilnahmeart, kuerzel, jahr);
	}

	/**
	 * Erzeugt einen TeilnahmeIdentifier für eine Schulteilnahme
	 *
	 * @param  kuerzel
	 *                 String das kuerzel
	 * @param  jahr
	 *                 String das jahr
	 * @return         TeilnahmeIdentifier
	 */
	public static TeilnahmeIdentifier createSchulteilnahmeIdentifier(final String kuerzel, final String jahr) {

		return create(Teilnahmeart.S, kuerzel, jahr);
	}

	/**
	 * Erzeugt einen TeilnahmeIdentifier für eine Privatteilnahme
	 *
	 * @param  kuerzel
	 *                 String das kuerzel
	 * @param  jahr
	 *                 String das jahr
	 * @return         TeilnahmeIdentifier
	 */
	public static TeilnahmeIdentifier createPrivatteilnahmeIdentifier(final String kuerzel, final String jahr) {

		return create(Teilnahmeart.P, kuerzel, jahr);
	}

	/**
	 * TeilnahmeIdentifier
	 */
	TeilnahmeIdentifier() {

		super();
	}

	/**
	 * TeilnahmeIdentifier
	 */
	private TeilnahmeIdentifier(final Teilnahmeart teilnahmeart, final String kuerzel, final String jahr) {

		this.teilnahmeart = teilnahmeart;
		this.kuerzel = kuerzel;
		this.jahr = jahr;
	}

	public Teilnahmeart getTeilnahmeart() {

		return teilnahmeart;
	}

	public String getJahr() {

		return jahr;
	}

	/**
	 * Im Fall einer Schulteilnahme ist es das schulkuerzel, im Fall einer Privatteilnahme das kuerzel der
	 * Privatteilnahme.
	 *
	 * @return
	 */
	public String getKuerzel() {

		return kuerzel;
	}

	@Override
	public String toString() {

		return "TeilnahmeIdentifier [teilnahmeart=" + teilnahmeart + ", jahr=" + jahr + ", kuerzel=" + kuerzel + "]";
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((jahr == null) ? 0 : jahr.hashCode());
		result = prime * result + ((kuerzel == null) ? 0 : kuerzel.hashCode());
		result = prime * result + ((teilnahmeart == null) ? 0 : teilnahmeart.hashCode());
		return result;
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
		final TeilnahmeIdentifier other = (TeilnahmeIdentifier) obj;

		if (jahr == null) {

			if (other.jahr != null) {

				return false;
			}
		} else if (!jahr.equals(other.jahr)) {

			return false;
		}

		if (kuerzel == null) {

			if (other.kuerzel != null) {

				return false;
			}
		} else if (!kuerzel.equals(other.kuerzel)) {

			return false;
		}

		if (teilnahmeart != other.teilnahmeart) {

			return false;
		}
		return true;
	}

	@Override
	public TeilnahmeIdentifier provideTeilnahmeIdentifier() {

		return this;
	}
}
