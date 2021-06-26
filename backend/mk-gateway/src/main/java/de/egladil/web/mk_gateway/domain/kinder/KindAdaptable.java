// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * KindAdaptable
 */
public interface KindAdaptable {

	Identifier identifier();

	/**
	 * @return
	 */
	Klassenstufe getKlassenstufe();

	/**
	 * @return
	 */
	Identifier klasseID();

	/**
	 * @return
	 */
	String getLowerVornameNullSafe();

	/**
	 * @return
	 */
	String getLowerNachnameNullSafe();

	/**
	 * @return
	 */
	String getLowerZusatzNullSafe();

	boolean isNeu();

	String getType();

}
