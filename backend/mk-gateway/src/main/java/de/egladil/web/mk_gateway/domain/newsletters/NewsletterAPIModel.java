// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletters;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * NewsletterAPIModel
 */
public class NewsletterAPIModel {

	public static final String KEINE_UUID = "neu";

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String betreff;

	@JsonProperty
	private String text;

	@JsonProperty
	private List<String> versandinfoIDs = new ArrayList<>();

	public static NewsletterAPIModel createFromNewsletter(final Newsletter newsletter) {

		NewsletterAPIModel result = new NewsletterAPIModel();
		result.uuid = newsletter.identifier().identifier();
		result.betreff = newsletter.betreff();
		result.text = newsletter.text();

		result.versandinfoIDs = newsletter.idsVersandinformationen().stream().map(id -> id.identifier())
			.collect(Collectors.toList());

		return result;

	}

	NewsletterAPIModel() {

	}

	public NewsletterAPIModel(final String uuid, final String betreff, final String text) {

		this.uuid = uuid;
		this.betreff = betreff;
		this.text = text;
	}

	public String uuid() {

		return uuid;
	}

	public String betreff() {

		return betreff;
	}

	public String text() {

		return text;
	}

	public List<String> versandinfoIDs() {

		return versandinfoIDs;
	}

	public NewsletterAPIModel withBetreff(final String betreff) {

		this.betreff = betreff;
		return this;
	}

	public NewsletterAPIModel withText(final String text) {

		this.text = text;
		return this;
	}
}
