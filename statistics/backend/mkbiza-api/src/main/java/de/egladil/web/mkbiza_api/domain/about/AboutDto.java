// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.about;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * AboutDto
 */
@Schema(description = "Informationen über die API")
public class AboutDto {

	@JsonProperty
	private String title;

	@JsonProperty
	private String description;

	@JsonProperty
	private String beschreibung;

	@JsonProperty
	private String[] urls;

	@JsonProperty
	private String schluesselwoerter;

	@JsonProperty
	private String keywords;

	@JsonProperty
	private String version;

	public String getTitle() {

		return title;
	}

	public void setTitle(final String title) {

		this.title = title;
	}

	public String getDescription() {

		return description;
	}

	public void setDescription(final String description) {

		this.description = description;
	}

	public String getBeschreibung() {

		return beschreibung;
	}

	public void setBeschreibung(final String beschreibung) {

		this.beschreibung = beschreibung;
	}

	public String[] getUrls() {

		return urls;
	}

	public void setUrls(final String[] urls) {

		this.urls = urls;
	}

	public String getSchluesselwoerter() {

		return schluesselwoerter;
	}

	public void setSchluesselwoerter(final String schluesselwoerter) {

		this.schluesselwoerter = schluesselwoerter;
	}

	public String getKeywords() {

		return keywords;
	}

	public void setKeywords(final String keywords) {

		this.keywords = keywords;
	}

	public String getVersion() {

		return version;
	}

	public void setVersion(final String version) {

		this.version = version;
	}

}
