// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.validation;

import de.egladil.web.commons_validation.AbstractWhitelistValidator;
import de.egladil.web.mk_commons.validation.annotations.Antwortcode;

/**
 * @author heikew
 */
public class AntwortcodeValidator extends AbstractWhitelistValidator<Antwortcode, String> {

	private static final String REGEXP = "[ABCDENabcden-]*";

	/**
	 * @see de.egladil.common.validation.validators.AbstractWhitelistValidator#getWhitelist()
	 */
	@Override
	protected String getWhitelist() {

		return REGEXP;
	}

}
