// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.wettbewerb;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb.domain.error.MkWettbewerbRuntimeException;

/**
 * WettbewerbService
 */
@ApplicationScoped
public class WettbewerbService {

	private static final Logger LOG = LoggerFactory.getLogger(WettbewerbService.class);

	@Inject
	WettbewerbRepository wettbewerbRepository;

	/**
	 * Ermittelt den aktuellen Wettbewerb. Es kann auch sein, dass es keinen gibt.
	 *
	 * @return Optional
	 */
	public Optional<Wettbewerb> aktuellerWettbewerb() {

		List<Wettbewerb> wettbewerbe = wettbewerbRepository.loadWettbewerbe();

		Optional<Wettbewerb> optLaufend = wettbewerbe.stream().filter(w -> WettbewerbStatus.BEENDET != w.status()).findFirst();

		return optLaufend;
	}

	@Transactional
	public void wettbewerbStarten(final WettbewerbID wettbewerbID) {

		List<Wettbewerb> wettbewerbe = wettbewerbRepository.loadWettbewerbe();

		Optional<Wettbewerb> optWettbewerb = wettbewerbe.stream().filter(w -> w.id().equals(wettbewerbID)).findFirst();

		if (optWettbewerb.isEmpty()) {

			LOG.debug("Wettbewerb mit ID {} existiert nicht", wettbewerbID);
			return;
		}

		// Alle vorherigen Wettbewerbe müssen beendet werden, damit es höchstens einen mit einem anderen Status gibt.
		throw new MkWettbewerbRuntimeException("Methode ist noch nicht fertig implementiert");

	}
}
