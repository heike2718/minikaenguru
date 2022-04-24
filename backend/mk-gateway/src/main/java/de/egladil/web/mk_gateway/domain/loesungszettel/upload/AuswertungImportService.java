// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.UploadFormatException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.loesungszettel.Wertungsrechner;
import de.egladil.web.mk_gateway.domain.loesungszettel.Wettbewerbswertung;
import de.egladil.web.mk_gateway.domain.statistik.AnonymisierteTeilnahmenService;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadStatus;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UploadHibernateRepository;

/**
 * AuswertungImportService
 */
@ApplicationScoped
public class AuswertungImportService {

	private static final String NAME_UPLOAD_DIR = "upload";

	private static final Logger LOGGER = LoggerFactory.getLogger(AuswertungImportService.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@ConfigProperty(name = "path.external.files")
	String pathExternalFiles;

	@Inject
	UploadRepository uploadRepository;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	AnonymisierteTeilnahmenService anonymisierteTeilnahmenService;

	public static AuswertungImportService createForIntegrationTest(final EntityManager em) {

		AuswertungImportService result = new AuswertungImportService();
		result.uploadRepository = UploadHibernateRepository.createForIntegrationTests(em);
		result.loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(em);
		result.anonymisierteTeilnahmenService = AnonymisierteTeilnahmenService.createForIntegrationTest(em);
		result.pathExternalFiles = "/home/heike/git/testdaten/minikaenguru/integrationtests";
		return result;
	}

	/**
	 * @param uploadContext
	 * @param persistenterUpload
	 */
	public ResponsePayload importiereAuswertung(final UploadAuswertungContext uploadContext, final PersistenterUpload persistenterUpload) {

		Wettbewerb wettbewerb = uploadContext.getWettbewerb();

		if (persistenterUpload.getStatus() != UploadStatus.HOCHGELADEN) {

			return handleBereitsVerarbeitet(uploadContext, persistenterUpload, wettbewerb.id());
		}

		AuswertungImportReport report = new AuswertungImportReport();

		Rolle rolle = uploadContext.getRolle();

		if (wettbewerb.isBeendet() && Rolle.ADMIN != rolle) {

			LOGGER.error(
				"Upload Auswertung zu einem beendeten Wettbewerb durch {} abgewiesen: wettbewerbsjahr {}: benutzerUUID={}, teilnahmenummer={}",
				rolle, uploadContext.getWettbewerb(),
				persistenterUpload.getBenutzerUuid(), persistenterUpload.getTeilnahmenummer());

			this.doUpdateTheUploadStatus(persistenterUpload, UploadStatus.ABGEWIESEN);

			String msg = MessageFormat.format(applicationMessages.getString("auswertungimport.forbidden.wettbewerbBeendet"),
				wettbewerb.id().toString());

			return ResponsePayload
				.messageOnly(
					MessagePayload.error(msg));

		}

		String path = getPathUploadDir() + File.separator + persistenterUpload.getUuid() + ".csv";

		List<AuswertungimportZeile> zeilen = new AuswertungCSVToAuswertungimportZeilenMapper()
			.apply(MkGatewayFileUtils.readLines(path, persistenterUpload.getEncoding()));

		AuswertungimportZeileSensor sensor = new AuswertungimportZeileSensor();
		AuswertungimportZeile ueberschrift = zeilen.isEmpty() ? null : zeilen.get(0);

		if (ueberschrift == null) {

			MessagePayload messagePayload = null;

			if (rolle == Rolle.ADMIN) {

				messagePayload = MessagePayload
					.warn(MessageFormat.format(applicationMessages.getString("auswertungimport.ohneUeberschrift.admin"),
						persistenterUpload.getDateiname(),
						persistenterUpload.getUuid(),
						persistenterUpload.getTeilnahmenummer()));

			} else {

				messagePayload = MessagePayload.info(applicationMessages.getString("auswertungimport.ohneUeberschrift.lehrer"));

			}

			Optional<AnonymisierteTeilnahmeAPIModel> optTeilnahme = getTeilnahme(persistenterUpload, wettbewerb.id());

			if (optTeilnahme.isPresent()) {

				report.setTeilnahme(optTeilnahme.get());
			}

			this.updateUploadstatusQuietly(persistenterUpload, UploadStatus.DATENFEHLER);

			return new ResponsePayload(messagePayload, report);
		}

		if (zeilen.size() == 1) {

			MessagePayload messagePayload = null;

			if (rolle == Rolle.ADMIN) {

				messagePayload = MessagePayload
					.warn(MessageFormat.format(applicationMessages.getString("auswertungimport.leer.admin"),
						persistenterUpload.getDateiname(),
						persistenterUpload.getUuid(),
						persistenterUpload.getTeilnahmenummer()));
			} else {

				messagePayload = MessagePayload
					.warn(MessageFormat.format(applicationMessages.getString("auswertungimport.leer.lehrer"),
						persistenterUpload.getDateiname()));
			}

			Optional<AnonymisierteTeilnahmeAPIModel> optTeilnahme = getTeilnahme(persistenterUpload, wettbewerb.id());

			if (optTeilnahme.isPresent()) {

				report.setTeilnahme(optTeilnahme.get());
			}

			this.updateUploadstatusQuietly(persistenterUpload, UploadStatus.LEER);

			return new ResponsePayload(messagePayload, report);

		}

		Klassenstufe klassenstufe = null;
		boolean auswertungMitNamen = false;

		try {

			klassenstufe = sensor.detectKlassenstufe(ueberschrift.getRohdaten());
			auswertungMitNamen = sensor.hasNamenSpalte(ueberschrift);

		} catch (UploadFormatException e) {

			LOGGER.warn(
				"Upload Auswertung durch {} misslungen: wettbewerbsjahr {}: benutzerUUID={}, teilnahmenummer={}, fehler={}",
				rolle, uploadContext.getWettbewerb(),
				persistenterUpload.getBenutzerUuid(), persistenterUpload.getTeilnahmenummer(), e.getMessage());

			MessagePayload messagePayload = null;

			if (rolle == Rolle.ADMIN) {

				messagePayload = MessagePayload
					.warn(MessageFormat.format(applicationMessages.getString("auswertungimport.leer.admin "),
						persistenterUpload.getDateiname(),
						persistenterUpload.getUuid(),
						persistenterUpload.getTeilnahmenummer()));

			} else {

				messagePayload = MessagePayload.info(applicationMessages.getString("auswertungimport.datenfehler.lehrer"));

			}

			ResponsePayload responsePayload = new ResponsePayload(messagePayload,
				report);

			this.updateUploadstatusQuietly(persistenterUpload, UploadStatus.HOCHGELADEN);

			return responsePayload;
		}

		final ExtractWertungscodeRohdatenMapper extractWertungscodeMapper = new ExtractWertungscodeRohdatenMapper(
			auswertungMitNamen);
		final Wertungsrechner wertungsrechner = new Wertungsrechner();

		List<Loesungszettel> neueLoesungszettel = new ArrayList<>();

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer(persistenterUpload.getTeilnahmenummer()).withWettbewerbID(wettbewerb.id());

		List<AuswertungimportZeile> fehlerhafteZeilen = new ArrayList<>();

		for (int i = 1; i < zeilen.size(); i++) {

			AuswertungimportZeile zeile = zeilen.get(i);

			try {

				String loesungsbuchstaben = null;

				switch (klassenstufe) {

				case IKID:
					loesungsbuchstaben = wettbewerb.loesungsbuchstabenIkids();
					break;

				case EINS:
					loesungsbuchstaben = wettbewerb.loesungsbuchstabenKlasse1();
					break;

				default:
					loesungsbuchstaben = wettbewerb.loesungsbuchstabenKlasse2();
					break;
				}

				loesungsbuchstaben = loesungsbuchstaben.replaceAll("-", "");

				String nutzereingabe = zeile.getRohdaten();

				Pair<String, String> extracted = extractWertungscodeMapper.apply(nutzereingabe, loesungsbuchstaben);

				String rohdaten = extracted.getLeft().replaceAll(";", "");
				String nutzereibgabenOhneName = extracted.getRight().replaceAll(";", "");

				if (rohdaten.isEmpty()) {

					continue;
				}

				Wettbewerbswertung wertung = wertungsrechner.getWertung(rohdaten, klassenstufe);

				LoesungszettelRohdaten loesungszettelRohdaten = new LoesungszettelRohdaten()
					.withNutzereingabe(nutzereibgabenOhneName)
					.withTypo(false)
					.withWertungscode(rohdaten);

				Loesungszettel loesungszettel = new Loesungszettel().withAuswertungsquelle(Auswertungsquelle.UPLOAD)
					.withKlassenstufe(klassenstufe).withLandkuerzel(uploadContext.getKuerzelLand())
					.withSprache(uploadContext.getSprache()).withTeilnahmeIdentifier(teilnahmeIdentifier)
					.withRohdaten(loesungszettelRohdaten).withPunkte(wertung.punkte())
					.withLaengeKaengurusprung(wertung.kaengurusprung());

				neueLoesungszettel.add(loesungszettel);
			} catch (Exception e) {

				zeile.setFehlermeldung(e.getMessage());
				fehlerhafteZeilen.add(zeile);

			}
		}

		if (!fehlerhafteZeilen.isEmpty()) {

			String pathFehlerreport = getPathUploadDir() + File.separator + persistenterUpload.getUuid() + "-fehlerreport.csv";

			List<String> fehlermeldungen = fehlerhafteZeilen.stream().map(AuswertungimportZeile::getFehlerreportItem)
				.collect(Collectors.toList());
			MkGatewayFileUtils.writeLines(fehlermeldungen, pathFehlerreport);

			Optional<AnonymisierteTeilnahmeAPIModel> optTeilnahme = getTeilnahme(persistenterUpload, wettbewerb.id());

			if (optTeilnahme.isPresent()) {

				report.setTeilnahme(optTeilnahme.get());
			}

			MessagePayload messagePayload = null;

			if (Rolle.ADMIN == rolle) {

				report.setFehlerhafteZeilen(fehlermeldungen);
				String msg = MessageFormat.format(applicationMessages.getString("auswertungimport.datenfehler.admin"),
					persistenterUpload.getDateiname(), persistenterUpload.getUuid(), persistenterUpload.getTeilnahmenummer());
				messagePayload = MessagePayload.error(msg);

			} else {

				messagePayload = MessagePayload.info(applicationMessages.getString("auswertungimport.datenfehler.lehrer"));
			}

			ResponsePayload responsePayload = new ResponsePayload(messagePayload,
				report);

			this.updateUploadstatusQuietly(persistenterUpload, UploadStatus.DATENFEHLER);

			return responsePayload;
		}

		try {

			loesungszettelSpeichern(neueLoesungszettel);
			this.doUpdateTheUploadStatus(persistenterUpload, UploadStatus.IMPORTIERT);

			Optional<AnonymisierteTeilnahmeAPIModel> optTeilnahme = getTeilnahme(persistenterUpload, wettbewerb.id());

			if (optTeilnahme.isPresent()) {

				report.setTeilnahme(optTeilnahme.get());
			}

			ResponsePayload responsePayload = new ResponsePayload(
				MessagePayload.info(applicationMessages.getString("auswertungimport.success")), report);

			return responsePayload;

		} catch (PersistenceException e) {

			String msg = applicationMessages.getString("klassenimport.error");
			LOGGER.error("{}: {}", msg, e.getMessage(), e);

			updateUploadstatusQuietly(persistenterUpload, UploadStatus.EXCEPTION);

			return ResponsePayload.messageOnly(MessagePayload.error(msg));
		}
	}

	/**
	 * @param  uploadContext
	 * @param  persistenterUpload
	 * @param  report
	 * @return
	 */
	ResponsePayload handleBereitsVerarbeitet(final UploadAuswertungContext uploadContext, final PersistenterUpload persistenterUpload, final WettbewerbID wettbewerbID) {

		AuswertungImportReport report = new AuswertungImportReport();

		MessagePayload messagePayload = null;

		switch (persistenterUpload.getStatus()) {

		case IMPORTIERT:
			messagePayload = MessagePayload.info(applicationMessages.getString("auswertungimport.success"));
			break;

		case ABGEWIESEN:
			if (uploadContext.getRolle() == Rolle.ADMIN) {

				messagePayload = MessagePayload
					.warn(
						MessageFormat.format(applicationMessages.getString("auswertungsimport.bereitsImportiert.abgewiesen.admin"),
							persistenterUpload.getUuid(), persistenterUpload.getTeilnahmenummer()));
			} else {

				messagePayload = MessagePayload
					.warn(applicationMessages.getString("auswertungsimport.bereitsImportiert.abgewiesen.lehrer"));
			}
			break;

		case DATENFEHLER:
			if (uploadContext.getRolle() == Rolle.ADMIN) {

				messagePayload = MessagePayload
					.warn(
						MessageFormat.format(applicationMessages.getString("auswertungsimport.bereitsImportiert.datenfehler.admin"),
							persistenterUpload.getUuid(), persistenterUpload.getTeilnahmenummer()));
			} else {

				messagePayload = MessagePayload
					.info(applicationMessages.getString("auswertungsimport.bereitsImportiert.datenfehler.lehrer"));
			}
			break;

		case LEER:

			if (uploadContext.getRolle() == Rolle.ADMIN) {

				messagePayload = MessagePayload
					.warn(
						MessageFormat.format(applicationMessages.getString("auswertungsimport.bereitsImportiert.datenfehler.admin"),
							persistenterUpload.getUuid(), persistenterUpload.getTeilnahmenummer()));
			} else {

				messagePayload = MessagePayload
					.warn(MessageFormat.format(applicationMessages.getString("auswertungimport.leer.lehrer"),
						persistenterUpload.getDateiname()));
			}
			break;

		case EXCEPTION:
			if (uploadContext.getRolle() == Rolle.ADMIN) {

				messagePayload = MessagePayload
					.warn(
						MessageFormat.format(applicationMessages.getString("auswertungsimport.bereitsImportiert.exception.admin"),
							persistenterUpload.getUuid(), persistenterUpload.getTeilnahmenummer()));
			} else {

				messagePayload = MessagePayload
					.warn(applicationMessages.getString("auswertungsimport.bereitsImportiert.exception.lehrer"));
			}

			break;

		default:
			break;
		}

		Optional<AnonymisierteTeilnahmeAPIModel> optTeilnahme = getTeilnahme(persistenterUpload, wettbewerbID);

		if (optTeilnahme.isPresent()) {

			report.setTeilnahme(optTeilnahme.get());
		}

		return new ResponsePayload(messagePayload, report);
	}

	private Optional<AnonymisierteTeilnahmeAPIModel> getTeilnahme(final PersistenterUpload persistenterUpload, final WettbewerbID wettbewerbID) {

		List<AnonymisierteTeilnahmeAPIModel> teilnahmen = anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(
			persistenterUpload.getTeilnahmenummer(),
			persistenterUpload.getBenutzerUuid());

		Optional<AnonymisierteTeilnahmeAPIModel> optTeilnahme = teilnahmen.stream()
			.filter(t -> wettbewerbID.jahr().equals(Integer.valueOf(t.identifier().jahr()))).findFirst();
		return optTeilnahme;
	}

	/**
	 * @param uploadMetadata
	 * @param neuerStatus
	 */
	private void updateUploadstatusQuietly(final PersistenterUpload uploadMetadata, final UploadStatus neuerStatus) {

		try {

			doUpdateTheUploadStatus(uploadMetadata, neuerStatus);
		} catch (PersistenceException e) {

			String msg = applicationMessages.getString("auswertungimport.error");
			LOGGER.error("{}: {}", msg, e.getMessage(), e);

			// TODO: hier eine Telegram-Message senden?

		}

	}

	@Transactional
	int loesungszettelSpeichern(final List<Loesungszettel> neueLoesungszettel) {

		for (Loesungszettel loesungszettel : neueLoesungszettel) {

			loesungszettelRepository.addLoesungszettel(loesungszettel);
		}
		return neueLoesungszettel.size();
	}

	@Transactional
	private void doUpdateTheUploadStatus(final PersistenterUpload persistenterUpload, final UploadStatus uploadStatus) {

		persistenterUpload.setStatus(uploadStatus);
		uploadRepository.updateUpload(persistenterUpload);

	}

	private String getPathUploadDir() {

		return pathExternalFiles + File.separator + NAME_UPLOAD_DIR;
	}

	void setPathExternalFiles(final String pathExternalFiles) {

		this.pathExternalFiles = pathExternalFiles;
	}

}
