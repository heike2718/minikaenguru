// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.gruppeninfos;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Gruppenitem ist ein Element einer Gruppeninfo, das den Namen des Attributs und die Anzahl der Entitäten mit diesem Attribut
 * enthält.
 */
public class Gruppenitem {

	@JsonProperty
	private String name;

	@JsonProperty
	List<Auspraegung> auspraegungen;

	/**
	 *
	 */
	Gruppenitem() {

	}

	/**
	 * @param name
	 */
	public Gruppenitem(final String name) {

		this.name = name;
	}

	@Override
	public String toString() {

		return "Gruppenitem [name=" + name + ", auspraegungen=" + auspraegungen + "]";
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public List<Auspraegung> getAuspraegungen() {

		return auspraegungen;
	}

	public void setAuspraegungen(final List<Auspraegung> auspraegungen) {

		this.auspraegungen = auspraegungen;
	}

	public long getAnzahlElemente() {

		return 0l;
	}

}
