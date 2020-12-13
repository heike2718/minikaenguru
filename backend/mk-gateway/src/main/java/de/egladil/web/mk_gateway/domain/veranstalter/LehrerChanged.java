// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.event.AbstractDomainEvent;
import de.egladil.web.mk_gateway.domain.event.EventType;
import de.egladil.web.mk_gateway.domain.semantik.DomainEvent;

/**
 * LehrerChanged
 */
@DomainEvent
public class LehrerChanged extends AbstractDomainEvent {

	@JsonProperty
	private Person person;

	@JsonProperty
	private String alteSchulkuerzel;

	public String neueSchulkuerzel() {

		return neueSchulkuerzel;
	}

	@JsonProperty
	private String neueSchulkuerzel;

	@JsonProperty
	private boolean newsletterAbonnieren;

	@JsonIgnore
	private final LocalDateTime occuredOn;

	protected LehrerChanged() {

		this.occuredOn = CommonTimeUtils.now();
	}

	/**
	 * @param person
	 * @param alteSchulkuerzel
	 * @param neueSchulkuerzel
	 */
	public LehrerChanged(final Person person, final String alteSchulkuerzel, final String neueSchulkuerzel, final boolean newsletter) {

		this();

		if (person == null) {

			throw new IllegalArgumentException("person darf nicht null sein.");
		}

		if (StringUtils.isBlank(neueSchulkuerzel)) {

			throw new IllegalArgumentException("neueSchulkuerzel darf nicht blank sein.");
		}

		this.person = person;
		this.alteSchulkuerzel = alteSchulkuerzel;
		this.neueSchulkuerzel = neueSchulkuerzel;
		this.newsletterAbonnieren = newsletter;
	}

	public Person person() {

		return person;
	}

	public LocalDateTime occuredOn() {

		return this.occuredOn;
	}

	/**
	 * @return String das kann eins oder mehrere Schulkürzel sein. Mehrere Kürzel werden durch ein Komma voneinander getrennt.
	 */
	public String alteSchulkuerzel() {

		return alteSchulkuerzel;
	}

	public boolean newsletterAbonnieren() {

		return newsletterAbonnieren;
	}

	@Override
	public String typeName() {

		return EventType.LEHRER_CHANGED.getLabel();
	}

}
