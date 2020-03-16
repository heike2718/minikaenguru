// =====================================================
// Projekt: de.egladil.persistence.tools
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mkv_server.validation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import de.egladil.web.mkv_server.validation.TeilnehmerUrkundenauftragValidator;

/**
 * Whitelist für Rollennamen.
 */
@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = TeilnehmerUrkundenauftragValidator.class)
@Documented
public @interface ValidTeilnehmerUrkundenauftrag {

	String message() default "{de.egladil.constraints.teilnehmerurkundenauftrag}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
