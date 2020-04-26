// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import de.egladil.web.mk_wettbewerb.domain.semantik.DomainEvent;

/**
 * LehrerDeregistredFromSchule
 */
@DomainEvent
public class LehrerDeregistredFromSchule extends LehrerChangedSchulen {

	/**
	 * @param lehrer
	 * @param schulkuerzel
	 */
	public LehrerDeregistredFromSchule(final Person lehrer, final String schulkuerzel) {

		super(lehrer, schulkuerzel);

	}

	@Override
	public String typeName() {

		return LehrerDeregistredFromSchule.class.getSimpleName();
	}

}
