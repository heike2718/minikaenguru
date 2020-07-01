// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import de.egladil.web.mk_wettbewerb.domain.semantik.DomainEvent;

/**
 * LehrerRegisteredForSchule
 */
@DomainEvent
public class LehrerRegisteredForSchule extends LehrerChangedSchulen {

	protected LehrerRegisteredForSchule() {

		super();
	}

	/**
	 * @param person
	 *                     Person
	 * @param schulkuerzel
	 *                     String das Schulkürzel
	 */
	public LehrerRegisteredForSchule(final Person person, final String schulkuerzel) {

		super(person, schulkuerzel);

	}

	@Override
	public String typeName() {

		return TYPE_LEHRER_REGISTERED_FOR_SCHULE;
	}

}
