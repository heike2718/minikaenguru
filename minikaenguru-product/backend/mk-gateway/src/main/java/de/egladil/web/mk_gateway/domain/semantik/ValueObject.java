// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.semantik;

import static java.lang.annotation.ElementType.TYPE;

import java.lang.annotation.Documented;
import java.lang.annotation.Target;

@Documented
@Target({ TYPE })
/**
 * ValueObject markiert ein DDD-ValueObject
 */
public @interface ValueObject {

}
