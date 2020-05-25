// =====================================================
// Project: mk-wettbewerb-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb_admin.domain.wettbewerb;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.commons_validation.ValidationDelegate;
import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
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

	private final ValidationDelegate validationDelegate;

	@Inject
	WettbewerbRepository wettbewerbRepository;

	public WettbewerbService() {

		this.validationDelegate = new ValidationDelegate();
	}

	public List<WettbewerbListAPIModel> alleWettbewerbeHolen() {

		List<Wettbewerb> wettbewerbe = this.wettbewerbRepository.loadWettbewerbe();

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

		Optional<Wettbewerb> optWettbewerb = this.wettbewerbRepository.wettbewerbMitID(new WettbewerbID(jahr));

		if (optWettbewerb.isEmpty()) {

			return Optional.empty();
		}

		WettbewerbDetailsAPIModel result = WettbewerbDetailsAPIModel.fromWettbewerb(optWettbewerb.get());

		// FIXME: hier noch Teilnahmezahlen holen.
		result = result.withTeilnahmenuebersicht(new TeilnahmenuebersichAPIModel());

		return Optional.of(result);

	}

	public Wettbewerb wettbewerbAnlegen(final WettbewerbDetailsAPIModel data) {

		validationDelegate.check(data, WettbewerbDetailsAPIModel.class);

		if (data.getJahr() < 2005) {

			throw new InvalidInputException(
				ResponsePayload.messageOnly(MessagePayload.error("Wettbewerbsjahr muss größer als 2004 sein")));
		}

		WettbewerbStatus status = null;

		try {

			status = WettbewerbStatus.valueOf(data.getStatus());
		} catch (IllegalArgumentException e) {

			throw new InvalidInputException(
				ResponsePayload.messageOnly(
					MessagePayload.error("WettbewerbStatus ist ungültig: erlaubt sind " + WettbewerbStatus.erlaubteStatus())));
		}

		Wettbewerb wettbewerb = new Wettbewerb(new WettbewerbID(data.getJahr())).withStatus(status)
			.withWettbewerbsende(CommonTimeUtils.parseToLocalDate(data.getWettbewerbsende()))
			.withDatumFreischaltungLehrer(CommonTimeUtils.parseToLocalDate(data.getDatumFreischaltungLehrer()))
			.withDatumFreischaltungPrivat(CommonTimeUtils.parseToLocalDate(data.getDatumFreischaltungPrivat()));

		if (data.getWettbewerbsbeginn() != null) {

			wettbewerb = wettbewerb.withWettbewerbsbeginn(CommonTimeUtils.parseToLocalDate(data.getWettbewerbsbeginn()));
		}

		try {

			wettbewerbRepository.addWettbewerb(wettbewerb);
			return wettbewerb;
		} catch (PersistenceException e) {

			LOG.error("Der wettbewerb {} konnte nicht gespeichert werden: {}", wettbewerb, e.getMessage(), e);
			throw new MkWettbewerbAdminRuntimeException("PersistenceException beim Speichern eines neuen Wettbewerbs");
		}

	}
}
