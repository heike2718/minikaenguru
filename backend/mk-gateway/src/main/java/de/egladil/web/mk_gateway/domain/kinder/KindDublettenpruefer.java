// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import java.util.function.BiFunction;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * KindDublettenpruefer
 */
@Deprecated
public class KindDublettenpruefer implements BiFunction<Kind, Kind, Boolean> {

	@Override
	public Boolean apply(final Kind kind1, final Kind kind2) {

		if (kind1.equals(kind2)) {

			return Boolean.FALSE;
		}

		boolean b = new EqualsBuilder().append(kind1.klasseID(), kind2.klasseID())
			.append(kind1.klassenstufe(), kind2.klassenstufe())
			.append(kind1.getLowerVornameNullSafe(), kind2.getLowerVornameNullSafe())
			.append(kind1.getLowerNachnameNullSafe(), kind2.getLowerNachnameNullSafe())
			.append(kind1.getLowerZusatzNullSafe(), kind2.getLowerZusatzNullSafe()).isEquals();

		return Boolean.valueOf(b);
	}

}
