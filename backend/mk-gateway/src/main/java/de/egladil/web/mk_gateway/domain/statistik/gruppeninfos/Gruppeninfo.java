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
	private String name;

	@JsonProperty
	private List<Gruppenitem> gruppenItems = new ArrayList<>();

	/**
	 *
	 */
	Gruppeninfo() {

		super();

	}

	/**
	 * @param name
	 */
	public Gruppeninfo(final String uuid, final String name) {

		this.uuid = uuid;
		this.name = name;
	}

	public void addItem(final Gruppenitem item) {

		gruppenItems.add(item);
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public List<Gruppenitem> getGruppenItems() {

		return gruppenItems;
	}

}
