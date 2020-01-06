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
import de.egladil.web.mkv_server.payload.request.TeilnehmerUrkundenauftrag;
import de.egladil.web.mkv_server.validation.annotations.ValidTeilnehmerUrkundenauftrag;

/**
 * TeilnehmerUrkundenauftragValidator
 */
public class TeilnehmerUrkundenauftragValidator implements ConstraintValidator<ValidTeilnehmerUrkundenauftrag, TeilnehmerUrkundenauftrag> {

	private static final Logger LOG = LoggerFactory.getLogger(TeilnehmerUrkundenauftragValidator.class);

	private KuerzelValidator kuerzelValidator = new KuerzelValidator();

	@Override
	public void initialize(final ValidTeilnehmerUrkundenauftrag constraintAnnotation) {

		// nix zu initialisieren
	}

	@Override
	public boolean isValid(final TeilnehmerUrkundenauftrag value, final ConstraintValidatorContext context) {

		if (value == null) {

			return true;
		}

		try {

			Farbschema.valueOf(value.getFarbschemaName());
		} catch (final IllegalArgumentException e) {

			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{de.egladil.constraints.teilnehmerurkundenauftrag}").addBeanNode()
				.addConstraintViolation();
			LOG.error("ungültiger farbschemaName {}", value.getFarbschemaName());
			return false;
		}

		if (value.getTeilnehmerKuerzel() == null) {

			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{de.egladil.constraints.teilnehmerurkundenauftrag}").addBeanNode()
				.addConstraintViolation();
			LOG.error("TeilnehmerUrkundenauftrag.teilnehmerKuerzel null");
			return false;
		}

		if (value.getTeilnehmerKuerzel().length == 0) {

			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("{de.egladil.constraints.teilnehmerurkundenauftrag}").addBeanNode()
				.addConstraintViolation();
			LOG.error("TeilnehmerUrkundenauftrag.teilnehmerKuerzel leer");
			return false;
		}

		for (final String kuerzel : value.getTeilnehmerKuerzel()) {

			if (!kuerzelValidator.isValid(kuerzel, context)) {

				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("{de.egladil.constraints.teilnehmerurkundenauftrag}").addBeanNode()
					.addConstraintViolation();
				LOG.error("TeilnehmerUrkundenauftrag.teilnehmerKuerzel enthält ungültige Zeichen: {}", kuerzel);
				return false;
			}

			if (StringUtils.isBlank(kuerzel)) {

				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("{de.egladil.constraints.teilnehmerurkundenauftrag}").addBeanNode()
					.addConstraintViolation();
				LOG.error("TeilnehmerUrkundenauftrag.teilnehmerKuerzel enthält leere kuerzel");
				return false;
			}

			if (kuerzel.length() > 22) {

				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("{de.egladil.constraints.teilnehmerurkundenauftrag}").addBeanNode()
					.addConstraintViolation();
				LOG.error("TeilnehmerUrkundenauftrag.teilnehmerKuerzel enthält zu langes kuerzel {}", kuerzel);
				return false;
			}
		}
		return true;
	}

}
