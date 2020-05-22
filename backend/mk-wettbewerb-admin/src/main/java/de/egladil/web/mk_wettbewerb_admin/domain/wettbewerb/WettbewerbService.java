// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.WettbewerbAPIModel;
import de.egladil.web.mk_wettbewerb_admin.domain.error.MkWettbewerbAdminRuntimeException;

/**
 * WettbewerbService
 */
@ApplicationScoped
public class WettbewerbService {

	private static final Logger LOG = LoggerFactory.getLogger(WettbewerbService.class);

	@Inject
	WettbewerbRepository wettbewerbRepository;

	public List<WettbewerbAPIModel> alleWettbewerbeHolen() {

		List<Wettbewerb> result = new ArrayList<>();

		// FIXME: hier result nochmal durchsortieren mit dem WettbewerbeDescendingComparator

		System.err.println("WettbewerbService.alleWettbewerbeHolen() ist noch ein Mockup");
		LocalDate now = LocalDate.now();
		result.add(new Wettbewerb(new WettbewerbID(2020)).withDatumFreischaltungLehrer(now)
			.withDatumFreischaltungPrivat(now).withStatus(WettbewerbStatus.ANMELDUNG)
			.withWettbewerbsbeginn(now));

		return result.stream().map(w -> WettbewerbAPIModel.fromWettbewerb(w)).collect(Collectors.toList());
	}

	/**
	 * Ermittelt den aktuellen Wettbewerb. Es kann auch sein, dass es keinen gibt.
	 *
	 * @return Optional
	 */
	public Optional<Wettbewerb> aktuellerWettbewerb() {

		List<Wettbewerb> wettbewerbe = wettbewerbRepository.loadWettbewerbe();

		if (wettbewerbe.isEmpty()) {

			return Optional.empty();
		}

		Collections.sort(wettbewerbe, new WettbewerbeDescendingComparator());

		return Optional.of(wettbewerbe.get(0));
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
		throw new MkWettbewerbAdminRuntimeException("Methode ist noch nicht fertig implementiert");

	}
}
