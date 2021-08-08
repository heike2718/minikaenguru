// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.function.BiFunction;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * Dublettenpruefer
 */
public class Dublettenpruefer implements BiFunction<KindAdaptable, KindAdaptable, Boolean> {

	@Override
	public Boolean apply(final KindAdaptable kind1, final KindAdaptable kind2) {

		boolean b = new EqualsBuilder().append(kind1.klasseID(), kind2.klasseID())
			.append(kind1.getKlassenstufe(), kind2.getKlassenstufe())
			.append(kind1.getLowerVornameNullSafe(), kind2.getLowerVornameNullSafe())
			.append(kind1.getLowerNachnameNullSafe(), kind2.getLowerNachnameNullSafe())
			.append(kind1.getLowerZusatzNullSafe(), kind2.getLowerZusatzNullSafe()).isEquals();

		if (!b) {

			return Boolean.FALSE;
		}

		if (kind1.isNeu() && kind2.isNeu()) {

			return kind1.getAdaptedObject() != kind2.getAdaptedObject();
		}

		if (kind1.isNeu() && !kind2.isNeu() || !kind1.isNeu() && kind2.isNeu()) {

			return true;
		}

		return !kind1.identifier().equals(kind2.identifier());
	}

}
