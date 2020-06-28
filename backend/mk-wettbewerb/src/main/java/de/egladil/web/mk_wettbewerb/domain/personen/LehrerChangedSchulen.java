// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_wettbewerb.domain.event.WettbewerbDomainEvent;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainEvent;

/**
 * LehrerChangedSchulen
 */
@DomainEvent
public abstract class LehrerChangedSchulen implements WettbewerbDomainEvent {

	private final Person person;

	private final String schulkuerzel;

	private final LocalDateTime occuredOn;

	/**
	 * @param person
	 * @param alteSchulkuerzel
	 * @param neueSchulkuerzel
	 */
	public LehrerChangedSchulen(final Person person, final String schulkuerzel) {

		if (person == null) {

			throw new IllegalArgumentException("person darf nicht null sein.");
		}

		if (StringUtils.isBlank(schulkuerzel)) {

			throw new IllegalArgumentException("schulkuerzel darf nicht blank sein.");
		}

		this.person = person;
		this.schulkuerzel = schulkuerzel;
		this.occuredOn = CommonTimeUtils.now();
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
