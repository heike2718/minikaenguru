// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_wettbewerb.domain.event.WettbewerbDomainEvent;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainEvent;

/**
 * LehrerChangedSchulen
 */
@DomainEvent
public abstract class LehrerChangedSchulen implements WettbewerbDomainEvent {

	@JsonProperty
	private Person person;

	@JsonProperty
	private String schulkuerzel;

	@JsonIgnore
	private final LocalDateTime occuredOn;

	protected LehrerChangedSchulen() {

		this.occuredOn = CommonTimeUtils.now();
	}

	/**
	 * @param person
	 * @param alteSchulkuerzel
	 * @param neueSchulkuerzel
	 */
	public LehrerChangedSchulen(final Person person, final String schulkuerzel) {

		this();

		if (person == null) {

			throw new IllegalArgumentException("person darf nicht null sein.");
		}

		if (StringUtils.isBlank(schulkuerzel)) {

			throw new IllegalArgumentException("schulkuerzel darf nicht blank sein.");
		}

		this.person = person;
		this.schulkuerzel = schulkuerzel;
	}

	public Person person() {

		return person;
	}

	public LocalDateTime occuredOn() {

		return this.occuredOn;
	}

	public String schulkuerzel() {

		return schulkuerzel;
	}

	public abstract String typeName();

}
