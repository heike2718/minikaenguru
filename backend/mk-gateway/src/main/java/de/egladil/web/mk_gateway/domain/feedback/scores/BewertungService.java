// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback.scores;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.mk_gateway.domain.feedback.scores.dto.BewertungAufgabe;
import de.egladil.web.mk_gateway.domain.feedback.scores.dto.BewertungsbogenKlassenstufe;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
import de.egladil.web.mk_gateway.domain.mail.AdminMailService;
import de.egladil.web.mk_gateway.domain.veranstalter.LehrerService;
import de.egladil.web.mk_gateway.domain.veranstalter.api.LehrerAPIModel;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.ScoreAufgabeEntity;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.ScoreKlassenstufeEntity;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.ScoresDao;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.SecurityContext;

/**
 * BewertungService
 */
@ApplicationScoped
public class BewertungService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BewertungService.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@ConfigProperty(name = "email.admin")
	String emailAdmin;

	@Context
	SecurityContext securityContext;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	SchulkatalogService schulkatalogService;

	@Inject
	LehrerService lehrerService;

	@Inject
	AdminMailService mailService;

	@Inject
	ScoresDao scoresDao;

	/**
	 * Speichert einen Bewertungsbogen zum aktuellen Wettbewerb.
	 *
	 * @param  bewertungsbogen
	 *                         BewertungsbogenKlassenstufe
	 * @return                 MessagePayload
	 */
	public MessagePayload bewertungSpeichern(final BewertungsbogenKlassenstufe bewertungsbogen) {

		String userId = securityContext.getUserPrincipal().getName();

		Optional<Wettbewerb> optWb = wettbewerbService.aktuellerWettbewerb();

		if (optWb.isEmpty() || WettbewerbStatus.ERFASST == optWb.get().status()) {

			LOGGER.error(
				"aktueller Wettbewerb konnte nicht ermittelt werden oder hat den falschen Status. Aber um die Leute nicht zu demotivieren, wird so getan, als wäre gespeichert worden.");

		}

		Wettbewerb wettbewerb = optWb.get();
		String kuerzelLand = getKuerzelLand(userId);

		try {

			this.speichern(bewertungsbogen, wettbewerb, kuerzelLand);

		} catch (Exception e) {

			sendAsMailQuietly(bewertungsbogen, userId);
			LOGGER.error(
				"Unwertwartete Exception beim Speichern eines Bewertungsbogens. Wird nicht propagiert, um die Leute nicht zu demotivieren: {}",
				e.getMessage(), e);
		}

		return MessagePayload.info(applicationMessages.getString("bewertung.response"));
	}

	@Transactional
	void speichern(final BewertungsbogenKlassenstufe bewertungsbogen, final Wettbewerb wettbewerb, final String kuerzelLand) {

		ScoreKlassenstufeEntity scoreKlassenstufeEntity = mapToEntity(bewertungsbogen, wettbewerb.id().toString(), kuerzelLand);
		int id = scoresDao.insertScoreKlassenstufe(scoreKlassenstufeEntity);

		for (BewertungAufgabe bewertungAufgabe : bewertungsbogen.getBewertungenAufgaben()) {

			ScoreAufgabeEntity aufgabeEntity = mapToEntity(bewertungAufgabe, id);
			scoresDao.insertScoreAufgabe(aufgabeEntity);
		}

		LOGGER.info("Bewertungsbogen gespeichert: id={}, klassenstufe={}", id, bewertungsbogen.getKlassenstufe());
	}

	private String getKuerzelLand(final String userId) {

		LehrerAPIModel lehrer = lehrerService.findLehrer(userId);
		List<String> schulkuerzel = lehrer.getTeilnahmenummern();

		for (String kuerzel : schulkuerzel) {

			Optional<SchuleAPIModel> optSchule = schulkatalogService.findSchuleQuietly(kuerzel);

			if (optSchule.isPresent()) {

				return optSchule.get().kuerzelLand();
			}

		}
		return "NN";
	}

	ScoreAufgabeEntity mapToEntity(final BewertungAufgabe bewertungAufgabe, final int idBewertungsbogen) {

		ScoreAufgabeEntity result = new ScoreAufgabeEntity();
		result.setAufgabeNummer(bewertungAufgabe.getNummer());
		result.setFreitext(bewertungAufgabe.getFreitext());
		result.setIdScoreKlassenstufe(idBewertungsbogen);
		result.setScoreKategorie(bewertungAufgabe.getEmpfohleneKategorie());
		result.setScoreLehrplankompatibilitaet(bewertungAufgabe.getScoreLehrplankompatibilitaet());
		result.setScoreSchwierigkeitsgrad(bewertungAufgabe.getScoreSchwierigkeitsgrad());
		result.setScoreVerstaendlichkeit(bewertungAufgabe.getScoreVerstaendlichkeit());
		return result;
	}

	ScoreKlassenstufeEntity mapToEntity(final BewertungsbogenKlassenstufe bewertungsbogen, final String wettbewerbId, final String kuerzelLand) {

		ScoreKlassenstufeEntity result = new ScoreKlassenstufeEntity();
		result.setFreitext(bewertungsbogen.getFreitext());
		result.setKlassenstufe(bewertungsbogen.getKlassenstufe());
		result.setLandkuerzel(kuerzelLand);
		result.setScoreSpass(bewertungsbogen.getScoreSpassfaktor());
		result.setScoreZufriedenheit(bewertungsbogen.getScoreZufriedenheit());
		result.setWettbewerbUuid(wettbewerbId);

		return result;

	}

	void sendAsMailQuietly(final BewertungsbogenKlassenstufe bewertungsbogen, final String userId) {

		try {

			String bewertung = new ObjectMapper().writeValueAsString(bewertungsbogen);

			DefaultEmailDaten mailDaten = new DefaultEmailDaten();
			mailDaten.setEmpfaenger(emailAdmin);
			mailDaten.setBetreff("Bewertungsbogen von " + StringUtils.abbreviate(userId, 11));
			mailDaten.setText(bewertung);

			mailService.sendMail(mailDaten);
		} catch (JsonProcessingException e) {

			LOGGER.error("Fehler beim Serialisieren der Bewertung: " + e.getMessage(), e);
		} catch (Exception e) {

			LOGGER.error("Mail mit Bwertung von {} konnte nicht gesendetet werden: {}", userId, e.getMessage(), e);
		}
	}

}
