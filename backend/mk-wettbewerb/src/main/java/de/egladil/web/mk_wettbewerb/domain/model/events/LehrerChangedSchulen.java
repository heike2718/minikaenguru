// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.model.events;

import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_wettbewerb.domain.model.personen.Person;

/**
 * LehrerChangedSchulen
 */
public abstract class LehrerChangedSchulen implements DomainEvent {

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

	@Override
	public String typeName() {

		return LehrerChangedSchulen.class.getSimpleName();
	}

	public Person person() {

		return person;
	}

	@Override
	public LocalDateTime occuredOn() {

		return this.occuredOn;
	}

	public String schulkuerzel() {

		return schulkuerzel;
	}

}
