// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.admin;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AdminStatistikGruppeninfo
 */
public class AdminStatistikGruppeninfo {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private long anzahlElemente;

	@JsonProperty
	private List<AdminStatistikItem> gruppenItems = new ArrayList<>();

	/**
	 *
	 */
	AdminStatistikGruppeninfo() {

		super();

	}

	/**
	 */
	public AdminStatistikGruppeninfo(final String uuid) {

		this.uuid = uuid;
	}

	public void addItem(final AdminStatistikItem item) {

		gruppenItems.add(item);
	}

	public List<AdminStatistikItem> getGruppenItems() {

		return gruppenItems;
	}

	public long getAnzahlElemente() {

		return anzahlElemente;
	}

	public void setAnzahlElemente(final long anzahlElemente) {

		this.anzahlElemente = anzahlElemente;
	}

	public String getUuid() {

		return uuid;
	}

}
