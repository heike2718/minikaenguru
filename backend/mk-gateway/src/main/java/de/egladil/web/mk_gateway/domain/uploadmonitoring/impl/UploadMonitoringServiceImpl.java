// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploadmonitoring.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.uploadmonitoring.api.UploadMonitoringInfo;
import de.egladil.web.mk_gateway.domain.uploadmonitoring.api.UploadMonitoringService;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.uploads.impl.DateiTyp;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.UploadsMonitoringViewItem;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UploadHibernateRepository;

/**
 * UploadKlassenlisteMonitoringServiceImpl
 */
@ApplicationScoped
public class UploadMonitoringServiceImpl implements UploadMonitoringService {

	@Inject
	UploadRepository uploadRepository;

	public static UploadMonitoringServiceImpl createForIntegrationTests(final EntityManager em) {

		UploadMonitoringServiceImpl result = new UploadMonitoringServiceImpl();
		result.uploadRepository = UploadHibernateRepository.createForIntegrationTests(em);
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

		UploadMonitoringInfo uploadInfo = new UploadMonitoringInfo()
			.withDateiTyp(DateiTyp.valueOfTikaName(viewItem.getMediatype()))
			.withTeilnahmenummer(viewItem.getTeilnahmenummer())
			.withNameSchule(viewItem.getNameSchule())
			.withUploadStatus(viewItem.getStatus())
			.withUploadType(viewItem.getUploadType())
			.withUuid(viewItem.getUuid())
			.withEmailLehrer(viewItem.getEmailLehrer())
			.withNameLehrer(viewItem.getNameLehrer())
			.withSortnumber(viewItem.getSortNumber());

		String uploadDate = CommonTimeUtils.format(CommonTimeUtils.transformFromDate(viewItem.getUploadDate()));
		uploadInfo.setUploadDatum(uploadDate);
		return uploadInfo;
	}
}
