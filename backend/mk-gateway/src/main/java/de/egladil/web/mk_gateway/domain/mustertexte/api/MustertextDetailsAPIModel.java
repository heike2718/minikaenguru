// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte.api;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertextkategorie;

/**
 * MustertextDetailsAPIModel
 */
public class MustertextDetailsAPIModel implements Serializable {

	public static final String KEINE_UUID = "neu";

	@JsonIgnore
	private static final long serialVersionUID = -5107176073474638679L;

	@JsonProperty
	@UuidString
	private String uuid;

	@JsonProperty
	private Mustertextkategorie kategorie;

	@JsonProperty
	private String name;

	@JsonProperty
	private String text;

	public String getUuid() {

		return uuid;
	}

	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}

	public Mustertextkategorie getKategorie() {

		return kategorie;
	}

	public void setKategorie(final Mustertextkategorie kategorie) {

		this.kategorie = kategorie;
	}

	public String getName() {

		return name;
	}

	public void setName(final String name) {

		this.name = name;
	}

	public String getText() {

		return text;
	}

	public void setText(final String text) {

		this.text = text;
	}

}
