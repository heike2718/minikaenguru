// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.wettbewerb;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
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
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.EditWettbewerbModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.TeilnahmenuebersichtAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbDetailsAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.api.WettbewerbListAPIModel;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.WettbewerbHibernateRepository;

/**
 * WettbewerbService
 */
@ApplicationScoped
public class WettbewerbService {

	private static final Logger LOG = LoggerFactory.getLogger(WettbewerbService.class);

	private final ValidationDelegate validationDelegate;

	@Inject
	WettbewerbRepository wettbewerbRepository;

	public static WettbewerbService createForIntegrationTest(final EntityManager entityManager) {

		WettbewerbService result = new WettbewerbService();
		result.wettbewerbRepository = WettbewerbHibernateRepository.createForIntegrationTest(entityManager);
		return result;
	}

	@Deprecated
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
			.withWettbewerbsbeginn(CommonTimeUtils.parseToLocalDate(data.getWettbewerbsbeginn()))
			.withWettbewerbsende(CommonTimeUtils.parseToLocalDate(data.getWettbewerbsende()))
			.withDatumFreischaltungLehrer(CommonTimeUtils.parseToLocalDate(data.getDatumFreischaltungLehrer()))
			.withDatumFreischaltungPrivat(CommonTimeUtils.parseToLocalDate(data.getDatumFreischaltungPrivat()))
			.withLoesungsbuchstabenIKids(data.getLoesungsbuchstabenIkids())
			.withLoesungsbuchstabenKlasse1(data.getLoesungsbuchstabenKlasse1())
			.withLoesungsbuchstabenKlasse2(data.getLoesungsbuchstabenKlasse2());

		try {

			wettbewerbRepository.addWettbewerb(wettbewerb);
			return wettbewerb;
		} catch (PersistenceException e) {

			LOG.error("Der wettbewerb {} konnte nicht gespeichert werden: {}", wettbewerb, e.getMessage(), e);
			throw new MkGatewayRuntimeException("PersistenceException beim Speichern eines neuen Wettbewerbs");
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
			throw new MkGatewayRuntimeException("PersistenceException beim Speichern eines vorhandenen Wettbewerbs");
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
			.withWettbewerbsbeginn(CommonTimeUtils.parseToLocalDate(data.getWettbewerbsbeginn()))
			.withWettbewerbsende(CommonTimeUtils.parseToLocalDate(data.getWettbewerbsende()))
			.withDatumFreischaltungLehrer(CommonTimeUtils.parseToLocalDate(data.getDatumFreischaltungLehrer()))
			.withDatumFreischaltungPrivat(CommonTimeUtils.parseToLocalDate(data.getDatumFreischaltungPrivat()))
			.withLoesungsbuchstabenIKids(data.getLoesungsbuchstabenIkids())
			.withLoesungsbuchstabenKlasse1(data.getLoesungsbuchstabenKlasse1())
			.withLoesungsbuchstabenKlasse2(data.getLoesungsbuchstabenKlasse2());

		try {

			wettbewerbRepository.changeWettbewerb(geaendert);
			return geaendert;
		} catch (PersistenceException e) {

			LOG.error("Der vorhandene Wettbewerb {} konnte nicht gespeichert werden: {}", wettbewerb, e.getMessage(), e);
			throw new MkGatewayRuntimeException("PersistenceException beim Speichern eines vorhandenen Wettbewerbs");
		}
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
