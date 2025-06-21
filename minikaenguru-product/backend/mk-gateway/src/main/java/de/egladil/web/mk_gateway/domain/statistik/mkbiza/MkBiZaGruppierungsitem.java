// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.mkbiza;

import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * MkBiZaGruppierungsitem
 */
public class MkBiZaGruppierungsitem {

	@JsonProperty
	private String name;

	@JsonProperty
	private long anzahl;

	/**
	 *
	 */
	public MkBiZaGruppierungsitem() {
		super();
	}

	/**
	 * Nützlich für Tests.
	 *
	 * @param name
	 * @param anzahl
	 */
	public MkBiZaGruppierungsitem(String name, long anzahl) {
		super();
		this.name = name;
		this.anzahl = anzahl;
	}

	@Override
	public int hashCode() {
		return Objects.hash(anzahl, name);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MkBiZaGruppierungsitem other = (MkBiZaGruppierungsitem) obj;
		return anzahl == other.anzahl && Objects.equals(name, other.name);
	}

	@Override
	public String toString() {
		return "MkBiZaGruppierungsitem [name=" + name + ", anzahl=" + anzahl + "]";
	}

	public String getName() {

		return name;
	}

	public MkBiZaGruppierungsitem withName(final String name) {

		this.name = name;
		return this;
	}

	public long getAnzahl() {

		return anzahl;
	}

	public MkBiZaGruppierungsitem withAnzahl(final long anzahl) {

		this.anzahl = anzahl;
		return this;
	}

}
