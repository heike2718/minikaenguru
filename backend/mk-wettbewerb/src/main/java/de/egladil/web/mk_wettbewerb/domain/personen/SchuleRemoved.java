// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

/**
 * SchuleRemoved
 */
public class SchuleRemoved extends LehrerChangedSchulen {

	/**
	 * @param person
	 * @param schulkuerzel
	 */
	public SchuleRemoved(final Person person, final String schulkuerzel) {

		super(person, schulkuerzel);

	}

	@Override
	public String typeName() {

		return SchuleRemoved.class.getSimpleName();
	}

}
