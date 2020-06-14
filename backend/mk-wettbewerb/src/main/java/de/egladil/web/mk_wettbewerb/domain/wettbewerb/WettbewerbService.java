// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.wettbewerb;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;

/**
 * WettbewerbService
 */
@ApplicationScoped
@DomainService
public class WettbewerbService {

	@Inject
	WettbewerbRepository wettbewerbRepository;

	public static WettbewerbService createForTest(final WettbewerbRepository repo) {

		WettbewerbService result = new WettbewerbService();
		result.wettbewerbRepository = repo;
		return result;
	}

	/**
	 * Ermittelt den aktuellen Wettbewerb. Es kann auch sein, dass es keinen gibt.
	 *
	 * @return Optional
	 */
	public Optional<Wettbewerb> aktuellerWettbewerb() {

		Optional<Wettbewerb> optLaufend = wettbewerbRepository.loadWettbewerbWithMaxJahr();

		return optLaufend;
	}
}
