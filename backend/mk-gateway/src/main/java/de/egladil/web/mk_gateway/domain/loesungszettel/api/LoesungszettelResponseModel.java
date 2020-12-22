// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.api;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.apimodel.auswertungen.LoesungszettelpunkteAPIModel;

/**
 * LoesungszettelResponseModel
 */
public class LoesungszettelResponseModel {

	@JsonProperty
	private LoesungszettelAPIModel loesungszettel;

	@JsonProperty
	private LoesungszettelpunkteAPIModel punkte;
}
