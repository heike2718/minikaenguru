// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.admin;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AdminStatistikItem ist ein Element einer AdminStatistikGruppeninfo, das den Namen des Attributs und die Anzahl der Entitäten mit diesem Attribut
 * enthält.
 */
public class AdminStatistikItem {

	@JsonProperty
	private String name;

	@JsonProperty
	List<AdminStatistikAuspraegung> auspraegungen;

	/**
	 *
	 */
	AdminStatistikItem() {

	}

	/**
	 * @param name
	 */
	public AdminStatistikItem(final String name) {

		this.name = name;
	}

	@Override
	public String toString() {

		return "AdminStatistikItem [name=" + name + ", auspraegungen=" + auspraegungen + "]";
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public List<AdminStatistikAuspraegung> getAuspraegungen() {

		return auspraegungen;
	}

	public void setAuspraegungen(final List<AdminStatistikAuspraegung> auspraegungen) {

		this.auspraegungen = auspraegungen;
	}

	public long getAnzahlElemente() {

		return 0l;
	}

}
