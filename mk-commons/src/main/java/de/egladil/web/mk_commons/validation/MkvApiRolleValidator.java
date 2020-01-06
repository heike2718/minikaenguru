// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.validation;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_commons.validation.annotations.MkvApiRolle;

/**
 * @author heikew
 */
public class MkvApiRolleValidator implements ConstraintValidator<MkvApiRolle, String> {

	private List<String> allowedStrings = Arrays.asList(new String[] { "LEHRER", "PRIVAT" });

	@Override
	public void initialize(final MkvApiRolle constraintAnnotation) {

		// nix zu tun
	}

	@Override
	public boolean isValid(final String value, final ConstraintValidatorContext context) {

		if (StringUtils.isBlank(value)) {

			return true;
		}
		return allowedStrings.contains(value);
	}

}
