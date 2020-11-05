// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.klassen;

import java.util.function.BiFunction;

import org.apache.commons.lang3.builder.EqualsBuilder;

/**
 * KlasseDuplettenpruefer
 */
public class KlasseDuplettenpruefer implements BiFunction<Klasse, Klasse, Boolean> {

	@Override
	public Boolean apply(final Klasse klasse1, final Klasse klasse2) {

		if (klasse1.equals(klasse2)) {

			return false;
		}

		boolean b = new EqualsBuilder().append(klasse1.schuleID(), klasse2.schuleID())
			.append(klasse1.name().toLowerCase(), klasse2.name().toLowerCase()).build();

		return b;
	}

}
