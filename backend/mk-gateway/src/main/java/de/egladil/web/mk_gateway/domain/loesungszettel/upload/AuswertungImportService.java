// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
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

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
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
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbeDescendingComparator;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UploadHibernateRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.WettbewerbHibernateRepository;

/**
 * AuswertungImportService
 */
@ApplicationScoped
public class AuswertungImportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuswertungImportService.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@ConfigProperty(name = "upload.folder.path")
	String pathUploadDir;

	@Inject
	private WettbewerbRepository wettbewerbRepository;

	@Inject
	private UploadRepository uploadRepository;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Inject
	AnonymisierteTeilnahmenService anonymisierteTeilnahmenService;

	public static AuswertungImportService createForIntegrationTest(final EntityManager em) {

		AuswertungImportService result = new AuswertungImportService();
		result.wettbewerbRepository = WettbewerbHibernateRepository.createForIntegrationTest(em);
		result.uploadRepository = UploadHibernateRepository.createForIntegrationTests(em);
		result.loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(em);
		result.anonymisierteTeilnahmenService = AnonymisierteTeilnahmenService.createForIntegrationTest(em);
		result.pathUploadDir = "/home/heike/mkv/upload";
		return result;
	}

	/**
	 * @param uploadContext
	 * @param persistenterUpload
	 */
	public ResponsePayload importiereAuswertung(final UploadAuswertungContext uploadContext, final PersistenterUpload persistenterUpload) {

		if (persistenterUpload.getStatus() != UploadStatus.HOCHGELADEN) {

			return handleBereitsVerarbeitet(uploadContext, persistenterUpload);
		}

		AuswertungImportReport report = new AuswertungImportReport();
		List<Wettbewerb> wettbewerbe = wettbewerbRepository.loadWettbewerbe();

		Optional<Wettbewerb> optWettbewerb = null;

		final Integer wettbewerbsjahr = uploadContext.getWettbewerbsjahr();

		if (wettbewerbsjahr != null) {

			optWettbewerb = wettbewerbe.stream().filter(w -> w.id().jahr().equals(wettbewerbsjahr)).findFirst();

		} else {

			Collections.sort(wettbewerbe, new WettbewerbeDescendingComparator());
			optWettbewerb = Optional.of(wettbewerbe.get(0));
		}

		if (optWettbewerb.isEmpty()) {

			LOGGER.error(
				"Upload Auswertung zu einem nicht vorhandenen Wettbewerb abgewiesen: wettbewerbsjahr={}, benutzerUUID={}, teilnahmenummer={}",
				uploadContext.getWettbewerbsjahr(),
				persistenterUpload.getBenutzerUuid(), persistenterUpload.getTeilnahmenummer());

			this.doUpdateTheUploadStatus(persistenterUpload, UploadStatus.ABGEWIESEN);

			return ResponsePayload
				.messageOnly(MessagePayload.error(wettbewerbsjahr + " gab es keinen Wettbewerb"));
		}

		Wettbewerb wettbewerb = optWettbewerb.get();
		Rolle rolle = uploadContext.getRolle();

		if (wettbewerb.isBeendet() && Rolle.ADMIN != rolle) {

			LOGGER.error(
				"Upload Auswertung zu einem beendeten Wettbewerb durch {} abgewiesen: wettbewerbsjahr {}: benutzerUUID={}, teilnahmenummer={}",
				rolle, uploadContext.getWettbewerbsjahr(),
				persistenterUpload.getBenutzerUuid(), persistenterUpload.getTeilnahmenummer());

			this.doUpdateTheUploadStatus(persistenterUpload, UploadStatus.ABGEWIESEN);

			String msg = MessageFormat.format(applicationMessages.getString("auswertungimport.forbidden.wettbewerbBeendet"),
				wettbewerb.id().toString());

			return ResponsePayload
				.messageOnly(
					MessagePayload.error(msg));

		}

		String path = pathUploadDir + File.separator + persistenterUpload.getUuid() + ".csv";

		List<AuswertungimportZeile> zeilen = new AuswertungCSVToAuswertungimportZeilenMapper()
			.apply(MkGatewayFileUtils.readLines(path));

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

			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = getTeilnahmen(persistenterUpload);
			report.setTeilnahmen(teilnahmen);

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

			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = getTeilnahmen(persistenterUpload);
			report.setTeilnahmen(teilnahmen);

			this.updateUploadstatusQuietly(persistenterUpload, UploadStatus.LEER);

			return new ResponsePayload(messagePayload, report);

		}

		Klassenstufe klassenstufe = sensor.detectKlassenstufe(ueberschrift.getRohdaten());

		final ExtractWertungscodeRohdatenMapper extractWertungscodeMapper = new ExtractWertungscodeRohdatenMapper();
		final Wertungsrechner wertungsrechner = new Wertungsrechner();

		List<Loesungszettel> neueLoesungszettel = new ArrayList<>();

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
			.withTeilnahmenummer(persistenterUpload.getTeilnahmenummer()).withWettbewerbID(wettbewerb.id());

		List<AuswertungimportZeile> fehlerhafteZeilen = new ArrayList<>();

		for (int i = 1; i < zeilen.size(); i++) {

			AuswertungimportZeile zeile = zeilen.get(i);

			try {

				String rohdaten = extractWertungscodeMapper.apply(zeile.getRohdaten());
				rohdaten = rohdaten.replaceAll(",,", "");

				Wettbewerbswertung wertung = wertungsrechner.getWertung(rohdaten, klassenstufe);

				LoesungszettelRohdaten loesungszettelRohdaten = new LoesungszettelRohdaten().withNutzereingabe(rohdaten)
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

			String pathFehlerreport = pathUploadDir + File.separator + persistenterUpload.getUuid() + "-fehlerreport.csv";

			List<String> fehlermeldungen = fehlerhafteZeilen.stream().map(AuswertungimportZeile::getFehlerreportItem)
				.collect(Collectors.toList());
			MkGatewayFileUtils.writeLines(fehlermeldungen, pathFehlerreport);

			List<AnonymisierteTeilnahmeAPIModel> teilnahmen = getTeilnahmen(persistenterUpload);
			report.setTeilnahmen(teilnahmen);

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

			List<AnonymisierteTeilnahmeAPIModel> anonymisierteTeilnahmen = getTeilnahmen(persistenterUpload);
			report.setTeilnahmen(anonymisierteTeilnahmen);

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
	ResponsePayload handleBereitsVerarbeitet(final UploadAuswertungContext uploadContext, final PersistenterUpload persistenterUpload) {

		AuswertungImportReport report = new AuswertungImportReport();

		List<AnonymisierteTeilnahmeAPIModel> teilnahmen = getTeilnahmen(persistenterUpload);

		report.setTeilnahmen(teilnahmen);

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

		return new ResponsePayload(messagePayload, report);
	}

	private List<AnonymisierteTeilnahmeAPIModel> getTeilnahmen(final PersistenterUpload persistenterUpload) {

		return anonymisierteTeilnahmenService.loadAnonymisierteTeilnahmen(persistenterUpload.getTeilnahmenummer(),
			persistenterUpload.getBenutzerUuid());
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

}
