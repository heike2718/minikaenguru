// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.wettbewerb;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * WettbewerbService
 */
@ApplicationScoped
public class WettbewerbService {

	@Inject
	WettbewerbRepository wettbewerbRepository;

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
