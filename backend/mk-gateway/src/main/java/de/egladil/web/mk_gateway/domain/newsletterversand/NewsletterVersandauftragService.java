// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.ErrorResponseDto;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.newsletters.Newsletter;
import de.egladil.web.mk_gateway.domain.newsletters.NewsletterService;
import de.egladil.web.mk_gateway.domain.newsletterversand.api.NewsletterVersandauftrag;
import de.egladil.web.mk_gateway.domain.newsletterversand.api.VersandinfoAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterMailinfoService;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.NewsletterauslieferungenRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * NewsletterVersandauftragService
 */
@ApplicationScoped
public class NewsletterVersandauftragService {

	private static final Logger LOG = LoggerFactory.getLogger(NewsletterVersandauftragService.class);

	@Inject
	VersandauftraegeRepository versandauftraegeRepo;

	@Inject
	NewsletterauslieferungenRepository auslieferungenRepository;

	@Inject
	NewsletterService newsletterService;

	@Inject
	VeranstalterMailinfoService veranstalterMailinfoService;

	/**
	 * Gibt alle nicht beendeten Versandauftrag aufsteigend nach Einstellungsdatum zurück.
	 *
	 * @return
	 */
	public List<Versandauftrag> findNichtBeendeteVersandauftraege() {

		return versandauftraegeRepo.findVersandauftraegeNotCompleted();
	}

	/**
	 * @param  versandauftragID
	 * @return                  Pair - links der Versandauftrag, rechts der Newsletter. Beides oder eins davon null sein.
	 */
	Pair<Versandauftrag, Newsletter> getVersandauftragAndNewsletterWithVersandauftragID(final Identifier versandauftragID) {

		Optional<Versandauftrag> optVersandauftrag = this.versandauftraegeRepo.ofId(versandauftragID);

		if (optVersandauftrag.isEmpty()) {

			return Pair.of(null, null);
		}

		Versandauftrag versandauftrag = optVersandauftrag.get();

		Optional<Newsletter> optNewsletter = newsletterService.findNewsletterWithID(versandauftrag.newsletterID());

		if (optNewsletter.isEmpty()) {

			return Pair.of(versandauftrag, null);
		}
		return Pair.of(versandauftrag, optNewsletter.get());
	}

	/**
	 * Gibt die Versandinfo mit der UUID zurück.
	 *
	 * @param  versandauftragId
	 *                          String
	 * @return                  Optional
	 */
	public Optional<VersandinfoAPIModel> getStatusNewsletterVersand(final String versandauftragId) {

		Optional<Versandauftrag> optVersandinfo = this.versandauftraegeRepo.ofId(new Identifier(versandauftragId));

		LOG.info("pollen Versandinfo {}", versandauftragId);

		if (optVersandinfo.isEmpty()) {

			return Optional.empty();
		}

		VersandinfoAPIModel apiModel = VersandinfoAPIModel.createFromVersandinfo(optVersandinfo.get());

		return Optional.of(apiModel);
	}

	/**
	 * Erzeugt einen neuen Versandauftrag mit zugehörigen Auslieferungen.
	 *
	 * @param  auftrag
	 * @return         ResponsePayload
	 */
	public Versandauftrag createVersandauftrag(final NewsletterVersandauftrag auftrag) {

		Optional<Newsletter> optNewsletter = this.newsletterService.findNewsletterWithID(new Identifier(auftrag.newsletterID()));

		if (optNewsletter.isEmpty()) {

			throw new WebApplicationException(
				Response.status(404).entity(ErrorResponseDto.error("kein Newsletter mit der ID vorhanden")).build());
		}

		List<List<String>> mailempfaengerGruppen = this.veranstalterMailinfoService
			.getMailempfaengerGroups(auftrag.emfaengertyp());

		if (mailempfaengerGruppen.isEmpty()) {

			throw new WebApplicationException(
				Response.status(412).entity(ErrorResponseDto.warning("keine Empfänger => kein Versand")).build());
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
			return this.scheduleAuslieferungen(versandauftrag, mailempfaengerGruppen);
		} catch (WebApplicationException e) {

			throw e;
		} catch (Exception e) {

			int anzahlEmpfaenger = anzahlEmpfaenger(mailempfaengerGruppen);
			String message = "Beim Anlegen des Versandauftrags ist ein Fehler aufgetreten: anzahl empfänger=" + anzahlEmpfaenger;
			LOG.error("Exception beim Anlegen des Versandauftrags {}: {}", auftrag, e.getMessage(), e);

			throw new WebApplicationException(Response.status(500)
				.entity(ErrorResponseDto.error(message))
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
			throw new WebApplicationException(
				Response.status(409)
					.entity(ErrorResponseDto.warning(
						message))
					.build());
		}

		String message = "Newsletter wurde bereits am " + versandauftrag.getErfasstAm() + " gespeichert. Empfaengertyp="
			+ versandauftrag.empfaengertyp() + ", Status=" + versandauftrag.getStatus() + ", Anzahl Empfänger="
			+ versandauftrag.anzahlEmpaenger();

		// Newsletter wurde bereits am 13.01.2024 gespeichert. Empfaengertyp=LEHRER, Status=WAITING
		throw new WebApplicationException(
			Response.status(409)
				.entity(ErrorResponseDto.warning(message))
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
			.withStatus(StatusAuslieferung.NEU);
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
