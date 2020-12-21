// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * LoesungszettelAPIModel
 */
public class LoesungszettelAPIModel {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String kindID;

	@JsonProperty
	private Klassenstufe klassenstufe;

	@JsonProperty
	private List<LoesungszettelZeileAPIModel> zeilen;
}
