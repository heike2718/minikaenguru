// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.veranstalter.Lehrer;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Privatveranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;

/**
 * PersistenterVeranstalterVeranstalterMapper
 */
public class PersistenterVeranstalterVeranstalterMapper implements Function<PersistenterVeranstalter, Veranstalter> {

	@Override
	public Veranstalter apply(final PersistenterVeranstalter persistenterVeranstalter) {

		Person person = new Person(persistenterVeranstalter.getUuid(), persistenterVeranstalter.getFullName()).withEmail(persistenterVeranstalter.getEmail());
		List<Identifier> teilnahmenummern = Arrays.stream(persistenterVeranstalter.getTeilnahmenummern().split(",")).map(n -> new Identifier(n))
			.collect(Collectors.toList());

		switch (persistenterVeranstalter.getRolle()) {

		case PRIVAT:
			return new Privatveranstalter(person, persistenterVeranstalter.isNewsletterEmpfaenger(), teilnahmenummern);

		case LEHRER:
			return new Lehrer(person, persistenterVeranstalter.isNewsletterEmpfaenger(), teilnahmenummern);

		default:
			throw new MkGatewayRuntimeException("unerwartete Rolle " + persistenterVeranstalter.getRolle());
		}
	}

}
