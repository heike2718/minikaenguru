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

	/**
	 * @param schulkuerzel
	 *                     String das Schulkürzel
	 * @param person
	 *                     Person
	 */
	public LehrerRegisteredForSchule(final String schulkuerzel, final Person person) {

		super(person, schulkuerzel);

	}

	@Override
	public String typeName() {

		return LehrerRegisteredForSchule.class.getSimpleName();
	}

}
