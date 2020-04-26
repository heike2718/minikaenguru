// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

/**
 * SchuleLehrerAdded
 */
public class SchuleLehrerAdded extends LehrerChangedSchulen {

	/**
	 * @param schulkuerzel
	 *                     String das Schulkürzel
	 * @param person
	 *                     Person
	 */
	public SchuleLehrerAdded(final String schulkuerzel, final Person person) {

		super(person, schulkuerzel);

	}

	@Override
	public String typeName() {

		return SchuleLehrerAdded.class.getSimpleName();
	}

}
