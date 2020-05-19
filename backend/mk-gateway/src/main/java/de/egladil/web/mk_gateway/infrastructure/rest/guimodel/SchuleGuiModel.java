// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.guimodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * SchuleGuiModel
 */

public class SchuleGuiModel {

	@JsonProperty
	private String kuerzel; // request

	@JsonProperty
	private String name; // mk-katalog

	@JsonProperty
	private String ort; // mk-katalog

	@JsonProperty
	private String land; // mk-katalog

	@JsonProperty
	private boolean aktuellAngemeldet; // mk-wettbewerb

	@JsonProperty
	private SchuleDashboardModel dashboardModel;

	SchuleGuiModel() {

	}

	public SchuleGuiModel(final String kuerzel, final String name, final String ort, final String land) {

		this.kuerzel = kuerzel;
		this.name = name;
		this.ort = ort;
		this.land = land;
	}

	public SchuleGuiModel withAngemeldetFlag(final boolean aktuellAngemeldet) {

		this.aktuellAngemeldet = aktuellAngemeldet;
		return this;
	}

	public SchuleGuiModel withDashboardModel(final SchuleDashboardModel dashboardModel) {

		this.dashboardModel = dashboardModel;
		return this;
	}
}
