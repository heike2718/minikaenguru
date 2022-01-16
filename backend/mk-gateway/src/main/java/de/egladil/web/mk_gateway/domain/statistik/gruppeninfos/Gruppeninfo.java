// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.gruppeninfos;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Gruppeninfo
 */
public class Gruppeninfo {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private long anzahlElemente;

	@JsonProperty
	private List<Gruppenitem> gruppenItems = new ArrayList<>();

	/**
	 *
	 */
	Gruppeninfo() {

		super();

	}

	/**
	 */
	public Gruppeninfo(final String uuid) {

		this.uuid = uuid;
	}

	public void addItem(final Gruppenitem item) {

		gruppenItems.add(item);
	}

	public List<Gruppenitem> getGruppenItems() {

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
