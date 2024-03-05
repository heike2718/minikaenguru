// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.feedback.scores;

import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.mk_gateway.domain.feedback.scores.dto.BewertungAufgabe;
import de.egladil.web.mk_gateway.domain.feedback.scores.dto.BewertungsbogenKlassenstufe;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
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

/**
 * BewertungService
 */
@ApplicationScoped
public class BewertungService {

	private static final Logger LOGGER = LoggerFactory.getLogger(BewertungService.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	SchulkatalogService schulkatalogService;

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

		Optional<Wettbewerb> optWb = wettbewerbService.aktuellerWettbewerb();

		if (optWb.isEmpty() || WettbewerbStatus.ERFASST == optWb.get().status()) {

			LOGGER.error(
				"aktueller Wettbewerb konnte nicht ermittelt werden oder hat den falschen Status. Aber um die Leute nicht zu demotivieren, wird so getan, als wäre gespeichert worden.");

		}

		Wettbewerb wettbewerb = optWb.get();
		String kuerzelLand = getKuerzelLand();

		this.speichern(bewertungsbogen, wettbewerb, kuerzelLand);

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

	private String getKuerzelLand() {

		return getSchule().kuerzelLand();
	}

	private SchuleAPIModel getSchule() {

		// FIXME: hier muss man sich die Schulen des Lehrers holen. Davon wird einfach die erste genommen. Wenn es keine gibt, weil
		// der Lehrer sich von allem abgemeldet hat, wird NN genommen.
		// findSchuleQuifor (etly
		return SchuleAPIModel.withKuerzelLand("DE-HE");
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

}
