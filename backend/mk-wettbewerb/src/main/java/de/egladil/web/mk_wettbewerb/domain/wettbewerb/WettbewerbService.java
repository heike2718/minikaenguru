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

	/**
	 * Sucht den aktuellen Wettbewerb und gibt ihn zurück, falls er Anmeldungen erlaubt.
	 *
	 * @return                       Wettbewerb
	 * @throws IllegalStateException
	 *                               wenn es keinen gibt oder der den falschen Status hat.
	 */
	public Wettbewerb aktuellerWettbewerbImAnmeldemodus() throws IllegalStateException {

		Optional<Wettbewerb> optWettbewerb = aktuellerWettbewerb();

		if (optWettbewerb.isEmpty()) {

			throw new IllegalStateException("Keine Anmeldung möglich. Es gibt keinen aktuellen Wettbewerb.");
		}

		Wettbewerb aktuellerWettbewerb = optWettbewerb.get();

		switch (aktuellerWettbewerb.status()) {

		case BEENDET:
			throw new IllegalStateException("Keine Anmeldung möglich. Der Wettbewerb ist beendet.");

		case ERFASST:
			throw new IllegalStateException("Keine Anmeldung möglich. Der Anmeldezeitraum hat noch nicht begonnen.");

		default:
			break;
		}

		return aktuellerWettbewerb;
	}
}
