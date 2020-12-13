// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * Veranstalter
 */
@AggregateRoot
public abstract class Veranstalter {

	@JsonProperty
	private Person person;

	@JsonProperty
	private ZugangUnterlagen zugangUnterlagen = ZugangUnterlagen.DEFAULT;

	@JsonProperty
	private boolean newsletterEmpfaenger;

	Veranstalter() {

	}

	public abstract Veranstalter merge(Person person);

	/**
	 * @param person
	 */
	public Veranstalter(final Person person, final boolean newsletterEmpfaenger) {

		if (person == null) {

			throw new IllegalArgumentException("person darf nicht null sein.");
		}

		this.person = person;
		this.newsletterEmpfaenger = newsletterEmpfaenger;

	}

	/**
	 * @return Rolle
	 */
	public abstract Rolle rolle();

	/**
	 * @return Teilnahmeart
	 */
	public abstract Teilnahmeart teilnahmeart();

	public String uuid() {

		return this.person.uuid();
	}

	public Person person() {

		return person;
	}

	public String fullName() {

		return this.person.fullName();
	}

	public String email() {

		return this.person.email();
	}

	public void toggleAboNewsletter() {

		this.newsletterEmpfaenger = !this.newsletterEmpfaenger;
	}

	/**
	 * Gibt die Tielnahme-Identifier zurück, die dieser Veranstalter hat. Bei einem Lehrer sind es die Schulkürzel, bei einer
	 * Privatveranstalter ein Kürzel, welches beim Registrieren einer Privatveranstalter angelegt wird.
	 *
	 * @return List<Identifier>
	 */
	public abstract List<Identifier> teilnahmeIdentifier();

	public ZugangUnterlagen zugangUnterlagen() {

		return zugangUnterlagen;
	}

	public void erlaubeZugangUnterlagen() {

		this.zugangUnterlagen = ZugangUnterlagen.ERTEILT;

	}

	public void verwehreZugangUnterlagen() {

		this.zugangUnterlagen = ZugangUnterlagen.ENTZOGEN;

	}

	public void setzeZugangUnterlagenZurueck() {

		this.zugangUnterlagen = ZugangUnterlagen.DEFAULT;
	}

	/**
	 * Alle Teilnahmekuerzel als kommaseparierter String.
	 *
	 * @return String
	 */
	public String persistierbareTeilnahmenummern() {

		if (this.teilnahmeIdentifier().isEmpty()) {

			return null;
		}
		List<String> kuerzel = teilnahmeIdentifier().stream().map(k -> k.identifier()).collect(Collectors.toList());

		return StringUtils.join(kuerzel, ",");

	}

	@Override
	public int hashCode() {

		return Objects.hash(person);
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
		Veranstalter other = (Veranstalter) obj;
		return Objects.equals(person, other.person);
	}

	@Override
	public String toString() {

		return "Veranstalter [person=" + person + "]";
	}

	public boolean isNewsletterEmpfaenger() {

		return newsletterEmpfaenger;
	}
}
