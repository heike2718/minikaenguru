// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploadmonitoring.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.ws.rs.NotFoundException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.kataloge.SchulkatalogService;
import de.egladil.web.mk_gateway.domain.uploadmonitoring.api.UploadMonitoringInfo;
import de.egladil.web.mk_gateway.domain.uploadmonitoring.api.UploadMonitoringService;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.UploadsMonitoringViewItem;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UploadHibernateRepository;

/**
 * UploadKlassenlisteMonitoringServiceImpl
 */
@ApplicationScoped
public class UploadMonitoringServiceImpl implements UploadMonitoringService {

	private static final String NAME_UPLOAD_DIR = "upload";

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadMonitoringServiceImpl.class);

	@ConfigProperty(name = "path.external.files")
	String pathExternalFiles;

	@Inject
	UploadRepository uploadRepository;

	@Inject
	SchulkatalogService schulkatalogService;

	public static UploadMonitoringServiceImpl createForIntegrationTests(final EntityManager em) {

		UploadMonitoringServiceImpl result = new UploadMonitoringServiceImpl();
		result.uploadRepository = UploadHibernateRepository.createForIntegrationTests(em);

		result.pathExternalFiles = "/home/heike/git/testdaten/minikaenguru/integrationtests";
		return result;
	}

	@Override
	public Long countUploads() {

		long anzahl = uploadRepository.countUploads();

		return Long.valueOf(anzahl);
	}

	@Override
	public List<UploadMonitoringInfo> loadUploads(final int limit, final int offset) {

		List<UploadsMonitoringViewItem> viewItems = uploadRepository.loadUploadsPage(limit, offset);

		return this.mapResultList(viewItems, offset);
	}

	@Override
	public List<UploadMonitoringInfo> findUploadsKlassenlisteWithTeilnahmenummer(final String teilnahmenummer) {

		List<UploadsMonitoringViewItem> viewItems = uploadRepository
			.findUploadsWithUploadTypeAndTeilnahmenummer(UploadType.KLASSENLISTE, teilnahmenummer);

		return this.mapResultList(viewItems, 0);
	}

	public List<UploadMonitoringInfo> mapResultList(final List<UploadsMonitoringViewItem> viewItems, final int offset) {

		if (viewItems.isEmpty()) {

			return new ArrayList<>();
		}

		return viewItems.stream().map(vi -> map(vi)).collect(Collectors.toList());
	}

	/**
	 * @param  teilnahmenummer
	 * @param  viewItem
	 * @return
	 */
	private UploadMonitoringInfo map(final UploadsMonitoringViewItem viewItem) {

		DateiTyp dateiTyp = DateiTyp.valueOfTikaName(viewItem.getMediatype());

		String filename = viewItem.getTeilnahmenummer() + "-" + viewItem.getUuid();

		UploadMonitoringInfo uploadInfo = new UploadMonitoringInfo()
			.withDateiTyp(dateiTyp)
			.withTeilnahmenummer(viewItem.getTeilnahmenummer())
			.withUploadStatus(viewItem.getStatus())
			.withUploadType(viewItem.getUploadType())
			.withUuid(viewItem.getUuid())
			.withEmailLehrer(viewItem.getEmailLehrer())
			.withNameLehrer(viewItem.getNameLehrer())
			.withSortnumber(viewItem.getSortNumber())
			.withFileName(filename + dateiTyp.getSuffixWithPoint())
			.withNameFehlerreport(filename + "-fehlerreport.csv");

		String uploadDate = CommonTimeUtils.format(CommonTimeUtils.transformFromDate(viewItem.getUploadDate()));
		uploadInfo.setUploadDatum(uploadDate);

		if (this.schulkatalogService != null) {

			// kann in einigen Tests null sein
			Optional<SchuleAPIModel> optSchule = this.schulkatalogService.findSchuleQuietly(viewItem.getTeilnahmenummer());

			if (optSchule.isPresent()) {

				uploadInfo.setSchule(optSchule.get());
			}

		}

		return uploadInfo;
	}

	@Override
	public DownloadData getUploadedFile(final String uploadUuid) {

		Optional<PersistenterUpload> optUpload = uploadRepository.findByUuid(uploadUuid);

		if (optUpload.isEmpty()) {

			LOGGER.error("PersistenterUpload uuid {} ist verschwunden", uploadUuid);

			throw new NotFoundException();

		}

		PersistenterUpload upload = optUpload.get();

		DateiTyp dateiTyp = DateiTyp.valueOfTikaName(upload.getMediatype());
		String path = getPathUploadedFile(dateiTyp, uploadUuid);

		try {

			byte[] bytes = MkGatewayFileUtils.readBytesFromFile(path);

			String filename = getFilenameWithoutSuffix(upload) + dateiTyp.getSuffixWithPoint();
			DownloadData result = new DownloadData(filename, bytes);

			return result;
		} catch (MkGatewayRuntimeException e) {

			if (e.getCause() instanceof FileNotFoundException) {

				throw new NotFoundException();
			}

			throw e;
		}
	}

	@Override
	public DownloadData getFehlerReport(final String uploadUuid) {

		Optional<PersistenterUpload> optUpload = uploadRepository.findByUuid(uploadUuid);

		if (optUpload.isEmpty()) {

			LOGGER.error("PersistenterUpload mit uuid {} ist verschwunden", uploadUuid);

			throw new NotFoundException();

		}

		PersistenterUpload upload = optUpload.get();

		String path = getPathFehlerreport(uploadUuid);

		try {

			byte[] bytes = MkGatewayFileUtils.readBytesFromFile(path);
			String filename = getFilenameWithoutSuffix(upload) + "-fehlerreport.csv";
			DownloadData result = new DownloadData(filename, bytes);

			return result;
		} catch (MkGatewayRuntimeException e) {

			if (e.getCause() instanceof FileNotFoundException) {

				throw new NotFoundException();
			}

			throw e;
		}
	}

	/**
	 * @param  dateiTyp
	 * @param  uuid
	 * @return
	 */
	private String getPathUploadedFile(final DateiTyp dateiTyp, final String uuid) {

		return getPathUploadDir() + File.separator + uuid + dateiTyp.getSuffixWithPoint();
	}

	/**
	 * @param  dateiTyp
	 * @param  uuid
	 * @return
	 */
	private String getPathFehlerreport(final String uuid) {

		return getPathUploadDir() + File.separator + uuid + "-fehlerreport.csv";
	}

	private String getPathUploadDir() {

		return pathExternalFiles + File.separator + NAME_UPLOAD_DIR;
	}

	private String getFilenameWithoutSuffix(final PersistenterUpload upload) {

		return upload.getTeilnahmenummer() + "-" + upload.getUuid();
	}
}
