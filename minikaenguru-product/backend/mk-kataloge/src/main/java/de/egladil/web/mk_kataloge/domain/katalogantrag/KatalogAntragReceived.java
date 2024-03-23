// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.katalogantrag;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_kataloge.domain.apimodel.SchulkatalogAntrag;
import de.egladil.web.mk_kataloge.domain.event.KatalogeDomainEvent;

/**
 * KatalogAntragReceived
 */
public class KatalogAntragReceived implements KatalogeDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private SchulkatalogAntrag body;

	KatalogAntragReceived() {

		this.occuredOn = CommonTimeUtils.now();

	}

	public KatalogAntragReceived(final SchulkatalogAntrag body) {

		this();

		this.body = body;
	}

	@Override
	public LocalDateTime occuredOn() {

		return occuredOn;
	}

	@Override
	public String typeName() {

		return TYPE_KATALOG_ANTRAG_RECEIVED;
	}

	public SchulkatalogAntrag body() {

		return body;
	}
}
