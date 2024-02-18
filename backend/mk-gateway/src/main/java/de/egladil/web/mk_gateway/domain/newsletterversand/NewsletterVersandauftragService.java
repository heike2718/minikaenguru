// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayWebApplicationException;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.newsletters.Newsletter;
import de.egladil.web.mk_gateway.domain.newsletters.NewsletterService;
import de.egladil.web.mk_gateway.domain.newsletterversand.api.NewsletterVersandauftrag;
import de.egladil.web.mk_gateway.domain.newsletterversand.api.VersandauftragDTO;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterMailinfoService;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterauslieferungenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;

/**
 * NewsletterVersandauftragService
 */
@ApplicationScoped
public class NewsletterVersandauftragService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NewsletterVersandauftragService.class);

	@Inject
	VersandauftraegeRepository versandauftraegeRepo;

	@Inject
	NewsletterauslieferungenRepository auslieferungenRepository;

	@Inject
	NewsletterService newsletterService;

	@Inject
	VeranstalterMailinfoService veranstalterMailinfoService;

	/**
	 * Gibt alle Versandaufträge mit Status WAITING aufsteigend nach Einstellungsdatum zurück.
	 *
	 * @return
	 */
	public List<VersandauftragDTO> loadAll() {

		List<Versandauftrag> versandauftraege = versandauftraegeRepo.loadAll();

		List<VersandauftragDTO> result = new ArrayList<>();

		for (Versandauftrag auftrag : versandauftraege) {

			VersandauftragDTO dto = VersandauftragDTO.createFromVersandauftrag(auftrag);

			Optional<Newsletter> theNewsletter = newsletterService.findNewsletterWithID(auftrag.newsletterID());

			if (theNewsletter.isPresent()) {

				dto.setNewsletterBetreff(theNewsletter.get().betreff());
			} else {

				dto.setNewsletterBetreff("Cucumber Error: kein Newsletter. Reset Universe!");
			}

			result.add(dto);
		}

		return result;
	}

	/**
	 * Gibt alle Versandaufträge mit Status WAITING aufsteigend nach Einstellungsdatum zurück.
	 *
	 * @return
	 */
	public List<Versandauftrag> findAllNotCompleted() {

		return versandauftraegeRepo.findAuftraegeNotCompleted();
	}

	/**
	 * @param  versandauftragID
	 * @return                           Pair - links der Versandauftrag, rechts der Newsletter.
	 * @throws MkGatewayRuntimeException
	 *                                   wenn eines der Dinge null ist.
	 */
	Pair<Versandauftrag, Newsletter> getVersandauftragAndNewsletterWithVersandauftragID(final Identifier versandauftragID) throws MkGatewayRuntimeException {

		Optional<Versandauftrag> optVersandauftrag = this.versandauftraegeRepo.ofId(versandauftragID);

		if (optVersandauftrag.isEmpty()) {

			throw new MkGatewayRuntimeException(
				"Datenmatsch: es gibt NewsletterAuslieferungen ohne Versandauftrag: VersandauftragID="
					+ versandauftragID);
		}

		Versandauftrag versandauftrag = optVersandauftrag.get();

		Optional<Newsletter> optNewsletter = newsletterService.findNewsletterWithID(versandauftrag.newsletterID());

		if (optNewsletter.isEmpty()) {

			throw new MkGatewayRuntimeException(
				"Datenmatsch: es gibt Versandauftraege ohne Newsletter: VersandauftragID="
					+ versandauftragID);
		}

		return Pair.of(versandauftrag, optNewsletter.get());
	}

	/**
	 * Gibt die VersandauftragDTO mit der UUID zurück.
	 *
	 * @param  versandauftragId
	 *                          String
	 * @return                  Optional
	 */
	public Optional<VersandauftragDTO> getStatusNewsletterVersand(final String versandauftragId) {

		Optional<Versandauftrag> optVersandinfo = this.versandauftraegeRepo.ofId(new Identifier(versandauftragId));

		LOGGER.info("pollen Versandinfo {}", versandauftragId);

		if (optVersandinfo.isEmpty()) {

			return Optional.empty();
		}

		Versandauftrag versandauftragDTO = optVersandinfo.get();

		Optional<Newsletter> optNewsletter = newsletterService.findNewsletterWithID(versandauftragDTO.newsletterID());

		if (optNewsletter.isEmpty()) {

			throw new MkGatewayRuntimeException(
				"Datenmatsch: es gibt Versandauftraege ohne Newsletter: VersandauftragID="
					+ versandauftragDTO.identifier().identifier());
		}

		VersandauftragDTO apiModel = VersandauftragDTO.createFromVersandauftrag(versandauftragDTO);
		apiModel.setNewsletterBetreff(optNewsletter.get().betreff());

		return Optional.of(apiModel);
	}

	/**
	 * Erzeugt einen neuen Versandauftrag mit zugehörigen Auslieferungen.
	 *
	 * @param  auftrag
	 * @return         ResponsePayload
	 */
	public ResponsePayload createVersandauftrag(final NewsletterVersandauftrag auftrag) {

		Optional<Newsletter> optNewsletter = this.newsletterService.findNewsletterWithID(new Identifier(auftrag.newsletterID()));

		if (optNewsletter.isEmpty()) {

			throw new MkGatewayWebApplicationException(
				Response.status(404)
					.entity(ResponsePayload.messageOnly(MessagePayload.error("kein Newsletter mit der ID vorhanden"))).build());
		}

		List<List<String>> mailempfaengerGruppen = this.veranstalterMailinfoService
			.getMailempfaengerGroups(auftrag.emfaengertyp());

		if (mailempfaengerGruppen.isEmpty()) {

			throw new MkGatewayWebApplicationException(
				Response.status(412).entity(ResponsePayload.messageOnly(MessagePayload.warn("keine Empfänger => kein Versand")))
					.build());
		}

		Newsletter newsletter = optNewsletter.get();
		// Versandauftrag versandauftrag = checkCompleted(newsletter.identifier(), auftrag.emfaengertyp(),
		// anzahlEmpfaenger(mailempfaengerGruppen));

		Versandauftrag versandauftrag = null;

		try {

			List<Versandauftrag> vorhandene = versandauftraegeRepo.findForNewsletter(newsletter.identifier());

			if (vorhandene.isEmpty()) {

				versandauftrag = initVersandauftrag(new Identifier(auftrag.newsletterID()), auftrag.emfaengertyp(),
					anzahlEmpfaenger(mailempfaengerGruppen));
			} else {

				Optional<Versandauftrag> optVersandauftrag = vorhandene.stream()
					.filter(a -> auftrag.emfaengertyp() == a.empfaengertyp()).findFirst();

				if (optVersandauftrag.isEmpty()) {

					versandauftrag = initVersandauftrag(new Identifier(auftrag.newsletterID()), auftrag.emfaengertyp(),
						anzahlEmpfaenger(mailempfaengerGruppen));
				} else {

					versandauftrag = blockOrUpdate(optVersandauftrag.get());
				}
			}
			Versandauftrag auslieferung = this.scheduleAuslieferungen(versandauftrag, mailempfaengerGruppen);
			VersandauftragDTO versandauftragDTO = VersandauftragDTO.createFromVersandauftrag(auslieferung);
			versandauftragDTO.setNewsletterBetreff(newsletter.betreff());
			ResponsePayload responsePayload = new ResponsePayload(MessagePayload.info("Newsletterversand erfolgreich beauftragt"),
				versandauftragDTO);
			return responsePayload;
		} catch (MkGatewayWebApplicationException e) {

			throw e;
		} catch (Exception e) {

			int anzahlEmpfaenger = anzahlEmpfaenger(mailempfaengerGruppen);
			String message = "Beim Anlegen des Versandauftrags ist ein Fehler aufgetreten: anzahl empfänger=" + anzahlEmpfaenger;
			LOGGER.error("Exception beim Anlegen des Versandauftrags {}: {}", auftrag, e.getMessage(), e);

			throw new MkGatewayWebApplicationException(Response.status(500)
				.entity(ResponsePayload.messageOnly(MessagePayload.error(message)))
				.build());
		}

	}

	/**
	 * Blockiert den Versandauftrag, weil es schon einen gibt, oder legt einen neuen an für Testempfänger.
	 *
	 * @param versandauftrag
	 */
	Versandauftrag blockOrUpdate(final Versandauftrag versandauftrag) {

		if (Empfaengertyp.TEST == versandauftrag.empfaengertyp()) {

			versandauftraegeRepo.delete(versandauftrag);
			return initVersandauftrag(versandauftrag.newsletterID(), versandauftrag.empfaengertyp(),
				versandauftrag.anzahlEmpaenger());
		}

		if (StatusAuslieferung.COMPLETED == versandauftrag.getStatus() || StatusAuslieferung.ERRORS == versandauftrag.getStatus()) {

			String message = "Newsletter wurde bereits am " + versandauftrag.versandBeendetAm()
				+ " an " + versandauftrag.anzahlEmpaenger() + " " + versandauftrag.empfaengertyp() + " versendet";
			throw new MkGatewayWebApplicationException(
				Response.status(409)
					.entity(ResponsePayload.messageOnly(MessagePayload.warn(message)))
					.build());
		}

		String message = "Newsletterversand wurde bereits am " + versandauftrag.getErfasstAm() + " gespeichert. Empfaengertyp="
			+ versandauftrag.empfaengertyp() + ", Status=" + versandauftrag.getStatus() + ", Anzahl Empfänger="
			+ versandauftrag.anzahlEmpaenger();

		// Newsletter wurde bereits am 13.01.2024 gespeichert. Empfaengertyp=LEHRER, Status=WAITING
		throw new MkGatewayWebApplicationException(
			Response.status(409)
				.entity(ResponsePayload.messageOnly(MessagePayload.warn(message)))
				.build());
	}

	/**
	 * @param  newsletter
	 * @param  empfaengertyp
	 * @param  anzahlEmpfaenger
	 * @return                  Versandauftrag
	 */
	Versandauftrag initVersandauftrag(final Identifier newsletterIdentifier, final Empfaengertyp empfaengertyp, final int anzahlEmpfaenger) {

		return new Versandauftrag()
			.withEmpfaengertyp(empfaengertyp)
			.withNewsletterID(newsletterIdentifier)
			.withAnzahlEmpaenger(anzahlEmpfaenger)
			.withStatus(StatusAuslieferung.NEW);
	}

	@Transactional
	Versandauftrag scheduleAuslieferungen(final Versandauftrag versandauftrag, final List<List<String>> gruppen) {

		Versandauftrag persistierter = versandauftragSpeichern(versandauftrag);

		int sortnr = 0;

		for (List<String> gruppe : gruppen) {

			NewsletterAuslieferung auslieferung = new NewsletterAuslieferung()
				.withEmpfaenger(gruppe.toArray(new String[0]))
				.withVersandauftragId(persistierter.identifier())
				.withStatus(StatusAuslieferung.WAITING)
				.withSortnummer(sortnr);

			sortnr++;

			auslieferungenRepository.addAuslieferung(auslieferung);

		}

		persistierter.setStatus(StatusAuslieferung.WAITING);
		return versandauftragSpeichern(persistierter);
	}

	@Transactional
	public Versandauftrag versandauftragSpeichern(final Versandauftrag versandauftrag) {

		Versandauftrag result = versandauftraegeRepo.saveVersandauftrag(versandauftrag);
		return result;
	}

	int anzahlEmpfaenger(final List<List<String>> groups) {

		return (int) groups.stream()
			.flatMap(List::stream) // flatten the lists
			.count();
	}
}
