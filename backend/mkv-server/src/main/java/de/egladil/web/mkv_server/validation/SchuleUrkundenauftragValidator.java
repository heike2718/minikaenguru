// =====================================================
// Projekt: mkv-server
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mkv_server.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.KuerzelValidator;
import de.egladil.web.mk_commons.domain.enums.Farbschema;
import de.egladil.web.mkv_server.payload.request.SchuleUrkundenauftrag;
import de.egladil.web.mkv_server.validation.annotations.ValidSchuleUrkundenauftrag;

/**
 * SchuleUrkundenauftragValidator
 */
public class SchuleUrkundenauftragValidator implements ConstraintValidator<ValidSchuleUrkundenauftrag, SchuleUrkundenauftrag> {

	private static final Logger LOG = LoggerFactory.getLogger(SchuleUrkundenauftragValidator.class);

	private KuerzelValidator kuerzelValidator = new KuerzelValidator();

	@Override
	public void initialize(final ValidSchuleUrkundenauftrag constraintAnnotation) {

		// nix zu initialisieren
	}

	@Override
	public boolean isValid(final SchuleUrkundenauftrag value, final ConstraintValidatorContext context) {

		if (value == null) {

			return true;
		}

		try {

			Farbschema.valueOf(value.getFarbschemaName());
		} catch (final IllegalArgumentException e) {

			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{de.egladil.constraints.schuleurkundenauftrag}").addBeanNode()
				.addConstraintViolation();
			LOG.error("ungültiger farbschemaName {}", value.getFarbschemaName());
			return false;
		}
		final String kuerzel = value.getKuerzelRootgruppe();

		if (!kuerzelValidator.isValid(kuerzel, context)) {

			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{de.egladil.constraints.schuleurkundenauftrag}").addBeanNode()
				.addConstraintViolation();
			LOG.error("SchuleUrkundenauftrag.kuerzelRootgruppe enthält ungültige Zeichen: {}", kuerzel);
			return false;
		}

		if (StringUtils.isBlank(kuerzel)) {

			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{de.egladil.constraints.teilnehmerurkundenauftrag}").addBeanNode()
				.addConstraintViolation();
			LOG.error("SchuleUrkundenauftrag.kuerzelRootgruppe enthält leere kuerzel");
			return false;
		}

		if (kuerzel.length() > 22) {

			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{de.egladil.constraints.teilnehmerurkundenauftrag}").addBeanNode()
				.addConstraintViolation();
			LOG.error("SchuleUrkundenauftrag.kuerzelRootgruppe enthält zu langes kuerzel {}", kuerzel);
			return false;
		}
		return true;
	}

}
