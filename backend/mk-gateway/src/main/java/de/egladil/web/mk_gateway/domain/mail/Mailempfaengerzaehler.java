// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import com.google.common.base.Function;

/**
 * Mailempfaengerzaehler
 */
public class Mailempfaengerzaehler implements Function<List<List<String>>, Integer> {

	@Override
	public @Nullable Integer apply(@Nullable final List<List<String>> mailempfaengerGruppen) {

		int count = 0;

		for (List<String> gruppe : mailempfaengerGruppen) {

			count += gruppe != null ? gruppe.size() : 0;
		}

		return count;
	}

}
