// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import java.util.Comparator;

/**
 * IdentifierComparator
 */
public class IdentifierComparator implements Comparator<Identifier> {

	/**
	 *
	 */
	public IdentifierComparator() {

	}

	@Override
	public int compare(final Identifier o1, final Identifier o2) {

		return o1.identifier().compareTo(o2.identifier());
	}

}
