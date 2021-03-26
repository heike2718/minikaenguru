// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.teilnahmen;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.veranstalter.Kollege;
import de.egladil.web.mk_gateway.domain.veranstalter.Schulkollegium;

/**
 * KollegiumFilter
 */
public class KollegiumFilter implements Function<Schulkollegium, List<Kollege>> {

	private final String uuidTestlehrer;

	private final Identifier identifierRequestingLehrer;

	/**
	 * @param identifierRequestingLehrer
	 *                                   Identifier darf auch null sein
	 * @param uuidTestlehrer
	 *                                   String darf null sein
	 */
	public KollegiumFilter(final Identifier identifierRequestingLehrer, final String uuidTestlehrer) {

		this.uuidTestlehrer = uuidTestlehrer;
		this.identifierRequestingLehrer = identifierRequestingLehrer;

	}

	@Override
	public List<Kollege> apply(final Schulkollegium kollegium) {

		if (this.identifierRequestingLehrer == null) {

			return kollegium.alleLehrerUnmodifiable();
		}

		return kollegium.alleLehrerUnmodifiable().stream()
			.filter(p -> !p.uuid().equals(uuidTestlehrer) && !p.uuid().equals(identifierRequestingLehrer.identifier()))
			.collect(Collectors.toList());
	}

}
