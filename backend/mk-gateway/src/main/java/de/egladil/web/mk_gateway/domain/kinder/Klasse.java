// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.AggregateRoot;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * Klasse
 */
@AggregateRoot
public class Klasse {

	private final Identifier identifier;

	private String name;

	private TeilnahmeIdentifier teilnahmeIdentifier;

	private List<Kind> kinder = new ArrayList<>();

	public Klasse(final Identifier identifier) {

		this.identifier = identifier;
	}

	public String getName() {

		return name;
	}

	public Klasse withName(final String name) {

		this.name = name;
		return this;
	}

	public Identifier getIdentifier() {

		return identifier;
	}

	public boolean addKind(final Kind kind) {

		this.kinder.add(kind);
		kind.withKlasseID(this.identifier);
		return true;
	}

	public boolean removeKind(final Kind kind) {

		boolean removed = this.kinder.remove(kind);

		if (removed) {

			kind.withKlasseID(null);
		}
		return removed;
	}

	/**
	 * Unmodifiable List!!!!
	 *
	 * @return List
	 */
	public List<Kind> kinder() {

		return Collections.unmodifiableList(kinder);
	}

	public TeilnahmeIdentifier teilnahmeIdentifier() {

		return teilnahmeIdentifier;
	}

	public Klasse withTeilnahmeIdentifier(final TeilnahmeIdentifier teilnahmeIdentifier) {

		this.teilnahmeIdentifier = teilnahmeIdentifier;
		return this;
	}

	public boolean kindKoennteDuplikatSein(final Kind kind) {

		KindDublettenpruefer dublettenpruefer = new KindDublettenpruefer();

		for (Kind k : kinder) {

			if (dublettenpruefer.apply(k, kind) == Boolean.TRUE) {

				return true;
			}
		}
		return false;
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
		Klasse other = (Klasse) obj;
		return Objects.equals(identifier, other.identifier);
	}
}
