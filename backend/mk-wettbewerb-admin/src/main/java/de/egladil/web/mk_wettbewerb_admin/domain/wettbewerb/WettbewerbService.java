// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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

import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.TeilnahmenuebersichAPIModel;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.WettbewerbDetailsAPIModel;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.WettbewerbListAPIModel;
import de.egladil.web.mk_wettbewerb_admin.domain.error.MkWettbewerbAdminRuntimeException;

/**
 * WettbewerbService
 */
@ApplicationScoped
public class WettbewerbService {

	private static final Logger LOG = LoggerFactory.getLogger(WettbewerbService.class);

	private List<Wettbewerb> wettbewerbe;

	@Inject
	WettbewerbRepository wettbewerbRepository;

	public WettbewerbService() {

		this.wettbewerbe = new ArrayList<>();
		LocalDate now = LocalDate.now();
		now.minus(365, ChronoUnit.DAYS);
		wettbewerbe.add(new Wettbewerb(new WettbewerbID(2019)).withDatumFreischaltungLehrer(now)
			.withDatumFreischaltungPrivat(now).withStatus(WettbewerbStatus.BEENDET)
			.withWettbewerbsbeginn(now));

		now = LocalDate.now();
		wettbewerbe.add(new Wettbewerb(new WettbewerbID(2020)).withDatumFreischaltungLehrer(now)
			.withDatumFreischaltungPrivat(now).withStatus(WettbewerbStatus.ANMELDUNG)
			.withWettbewerbsbeginn(now));
	}

	public List<WettbewerbListAPIModel> alleWettbewerbeHolen() {

		Collections.sort(wettbewerbe, new WettbewerbeDescendingComparator());

		return wettbewerbe.stream().map(w -> WettbewerbListAPIModel.fromWettbewerb(w)).collect(Collectors.toList());
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

	public Optional<WettbewerbDetailsAPIModel> wettbewerbMitJahr(final Integer jahr) {

		if (jahr == null) {

			return Optional.empty();
		}

		// Optional<Wettbewerb> optWettbewerb = this.wettbewerbRepository.wettbewerbMitID(new WettbewerbID(jahr));

		System.err.println("WettbewerbService.wettbewerbMitJahr() ist noch ein Mockup");

		Optional<Wettbewerb> optWettbewerb = this.wettbewerbe.stream().filter(w -> w.id().jahr().equals(jahr)).findFirst();

		if (optWettbewerb.isEmpty()) {

			return Optional.empty();
		}

		WettbewerbDetailsAPIModel result = WettbewerbDetailsAPIModel.fromWettbewerb(optWettbewerb.get());

		// FIXME: hier noch Teilnahmezahlen holen.
		result = result.withTeilnahmenuebersicht(new TeilnahmenuebersichAPIModel());

		return Optional.of(result);

	}
}
