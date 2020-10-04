// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter.admin;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterUserAPIModel;

/**
 * VeranstalterSucheService
 */
@ApplicationScoped
public class VeranstalterSucheService {

	private final VeranstalterVeranstalterUserAPIModelMapper mapper = new VeranstalterVeranstalterUserAPIModelMapper();

	@Inject
	VeranstalterRepository veranstalterRepository;

	public List<VeranstalterUserAPIModel> findVeranstalter(final VeranstalterSuchanfrage suchanfrage) {

		List<Veranstalter> trefferliste = veranstalterRepository.findVeranstalter(suchanfrage);

		return trefferliste.stream().map(v -> mapper.apply(v)).collect(Collectors.toList());
	}

}
