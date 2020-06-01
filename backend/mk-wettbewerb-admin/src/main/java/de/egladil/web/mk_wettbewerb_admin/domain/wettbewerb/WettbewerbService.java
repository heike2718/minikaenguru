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
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.commons_validation.ValidationDelegate;
import de.egladil.web.commons_validation.exception.InvalidInputException;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.EditWettbewerbModel;
import de.egladil.web.mk_wettbewerb_admin.domain.apimodel.TeilnahmenuebersichtAPIModel;
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

	public static WettbewerbService createForTest(final WettbewerbRepository repository) {

		WettbewerbService result = new WettbewerbService();
		result.wettbewerbRepository = repository;
		return result;
	}

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
		result = result.withTeilnahmenuebersicht(new TeilnahmenuebersichtAPIModel());

		return Optional.of(result);

	}

	public Wettbewerb wettbewerbAnlegen(final EditWettbewerbModel data) {

		validationDelegate.check(data, EditWettbewerbModel.class);

		if (data.getJahr() < 2005) {

			throw new InvalidInputException(
				ResponsePayload.messageOnly(MessagePayload.error("Wettbewerbsjahr muss größer als 2004 sein.")));
		}

		WettbewerbStatus status = WettbewerbStatus.nextStatus(null);

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

	/**
	 * @param  jahr
	 * @return      Wettbewerb
	 */
	public WettbewerbStatus starteNaechstePhase(final Integer jahr) {

		Optional<Wettbewerb> optWettbewerb = wettbewerbRepository.wettbewerbMitID(new WettbewerbID(jahr));

		if (!optWettbewerb.isPresent()) {

			throw new NotFoundException();

		}

		Wettbewerb wettbewerb = optWettbewerb.get();

		try {

			wettbewerb.naechsterStatus();
			wettbewerbRepository.changeWettbewerbStatus(wettbewerb.id(), wettbewerb.status());
			return wettbewerb.status();

		} catch (IllegalStateException e) {

			LOG.error("{} - {}", e.getMessage(), wettbewerb);
			ResponsePayload responsePayload = ResponsePayload
				.messageOnly(MessagePayload.error(e.getMessage()));
			throw new WebApplicationException(Response.status(412).entity(responsePayload).build());
		} catch (PersistenceException e) {

			LOG.error("Der wettbewerb {} konnte nicht gespeichert werden: {}", wettbewerb, e.getMessage(), e);
			throw new MkWettbewerbAdminRuntimeException("PersistenceException beim Speichern eines vorhandenen Wettbewerbs");
		}
	}

	public Wettbewerb wettbewerbAendern(final EditWettbewerbModel data) {

		validationDelegate.check(data, EditWettbewerbModel.class);

		Optional<Wettbewerb> optWettbewerb = wettbewerbRepository.wettbewerbMitID(new WettbewerbID(data.getJahr()));

		if (!optWettbewerb.isPresent()) {

			throw new NotFoundException();

		}

		Wettbewerb wettbewerb = optWettbewerb.get();

		if (wettbewerb.isBeendet()) {

			LOG.warn("Versuch, den beendeten Wettbewerb {} zu ändern", wettbewerb);
			ResponsePayload responsePayload = ResponsePayload
				.messageOnly(MessagePayload.error("Wettbewerb hat sein Lebensende erreicht und kann nicht mehr geändert werden."));
			throw new WebApplicationException(Response.status(412).entity(responsePayload).build());
		}

		Wettbewerb geaendert = new Wettbewerb(wettbewerb.id()).withStatus(wettbewerb.status())
			.withWettbewerbsende(CommonTimeUtils.parseToLocalDate(data.getWettbewerbsende()))
			.withDatumFreischaltungLehrer(CommonTimeUtils.parseToLocalDate(data.getDatumFreischaltungLehrer()))
			.withDatumFreischaltungPrivat(CommonTimeUtils.parseToLocalDate(data.getDatumFreischaltungPrivat()));

		if (data.getWettbewerbsbeginn() != null) {

			geaendert.withWettbewerbsbeginn(CommonTimeUtils.parseToLocalDate(data.getWettbewerbsbeginn()));
		}

		try {

			wettbewerbRepository.changeWettbewerb(geaendert);
			return geaendert;
		} catch (PersistenceException e) {

			LOG.error("Der vorhandene Wettbewerb {} konnte nicht gespeichert werden: {}", wettbewerb, e.getMessage(), e);
			throw new MkWettbewerbAdminRuntimeException("PersistenceException beim Speichern eines vorhandenen Wettbewerbs");
		}
	}
}
