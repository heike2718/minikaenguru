// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * LoesungszettelList
 */
public class LoesungszettelList {

	@JsonProperty
	private Loesungszettel[] loesungszettel;

	public List<Loesungszettel> getLoesungszettel() {

		return Arrays.asList(loesungszettel);
	}

	public void setLoesungszettel(final List<Loesungszettel> loesungszettel) {

		this.loesungszettel = loesungszettel.toArray(new Loesungszettel[0]);
	}

}
