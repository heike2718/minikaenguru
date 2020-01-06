// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.validation;

import de.egladil.web.commons_validation.AbstractWhitelistValidator;
import de.egladil.web.mk_commons.validation.annotations.Loesungscode;

/**
 * @author heikew
 */
public class LoesungscodeValidator extends AbstractWhitelistValidator<Loesungscode, String> {

	private static final String REGEXP = "[ABCDE]*";

	/**
	 * @see de.egladil.common.validation.validators.AbstractWhitelistValidator#getWhitelist()
	 */
	@Override
	protected String getWhitelist() {

		return REGEXP;
	}

}
