// =====================================================
// Projekt: mk-commons
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

import de.egladil.web.mkv_server.validation.SchuleUrkundenauftragValidator;

/**
 * Whitelist für Rollennamen.
 */
@Target({ ElementType.TYPE, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SchuleUrkundenauftragValidator.class)
@Documented
public @interface ValidSchuleUrkundenauftrag {

	String message() default "{de.egladil.constraints.schuleurkundenauftrag}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
