// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.validation;

import de.egladil.web.commons_validation.AbstractWhitelistValidator;
import de.egladil.web.mk_commons.validation.annotations.Wertungscode;

/**
 * Erlaubte Zeichen sind r,f und n.
 */
public class WertungscodeValidator extends AbstractWhitelistValidator<Wertungscode, String> {

	private static final String REGEXP = "[rfn]*";

	/**
	 * @see de.egladil.common.validation.validators.AbstractWhitelistValidator#getWhitelist()
	 */
	@Override
	protected String getWhitelist() {

		return REGEXP;
	}

}
