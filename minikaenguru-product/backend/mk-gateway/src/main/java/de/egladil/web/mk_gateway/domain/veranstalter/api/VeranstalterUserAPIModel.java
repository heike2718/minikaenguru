// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.api;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;

/**
 * VeranstalterUserAPIModel
 */
public class VeranstalterUserAPIModel {

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String fullName;

	@JsonProperty
	private String email;

	@JsonProperty
	private boolean newsletterAbonniert;

	@JsonProperty
	private Rolle rolle;

	@JsonProperty
	private ZugangUnterlagen zugangsstatusUnterlagen;

	@JsonProperty
	private List<String> teilnahmenummern;

	public String uuid() {

		return uuid;
	}

	public VeranstalterUserAPIModel withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public String fullName() {

		return fullName;
	}

	public VeranstalterUserAPIModel withFullName(final String fullName) {

		this.fullName = fullName;
		return this;
	}

	public String email() {

		return email;
	}

	public VeranstalterUserAPIModel withEmail(final String email) {

		this.email = email;
		return this;
	}

	public boolean newsletterAbonniert() {

		return newsletterAbonniert;
	}

	public VeranstalterUserAPIModel withNewsletterAbonniert(final boolean newsletterAbonniert) {

		this.newsletterAbonniert = newsletterAbonniert;
		return this;
	}

	public Rolle rolle() {

		return rolle;
	}

	public VeranstalterUserAPIModel withRolle(final Rolle rolle) {

		this.rolle = rolle;
		return this;
	}

	public ZugangUnterlagen zugangsstatusUnterlagen() {

		return zugangsstatusUnterlagen;
	}

	public VeranstalterUserAPIModel withZugangsstatusUnterlagen(final ZugangUnterlagen zugangsstatusUnterlagen) {

		this.zugangsstatusUnterlagen = zugangsstatusUnterlagen;
		return this;
	}

	public List<String> teilnahmenummern() {

		return teilnahmenummern;
	}

	public VeranstalterUserAPIModel withTeilnahmenummern(final List<String> teilnahmenummern) {

		this.teilnahmenummern = teilnahmenummern;
		return this;
	}

}
