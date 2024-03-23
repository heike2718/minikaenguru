// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.admin;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterUserAPIModel;

/**
 * VeranstalterVeranstalterUserAPIModelMapper
 */
public class VeranstalterVeranstalterUserAPIModelMapper implements Function<Veranstalter, VeranstalterUserAPIModel> {

	@Override
	public VeranstalterUserAPIModel apply(final Veranstalter veranstalter) {

		List<String> teilnahmenummern = veranstalter.teilnahmeIdentifier().stream().map(n -> n.identifier())
			.collect(Collectors.toList());

		VeranstalterUserAPIModel result = new VeranstalterUserAPIModel()
			.withEmail(veranstalter.email())
			.withFullName(veranstalter.fullName())
			.withNewsletterAbonniert(veranstalter.isNewsletterEmpfaenger())
			.withRolle(veranstalter.rolle())
			.withTeilnahmenummern(teilnahmenummern)
			.withUuid(veranstalter.uuid().substring(0, 8))
			.withZugangsstatusUnterlagen(veranstalter.zugangUnterlagen());

		return result;
	}

}
