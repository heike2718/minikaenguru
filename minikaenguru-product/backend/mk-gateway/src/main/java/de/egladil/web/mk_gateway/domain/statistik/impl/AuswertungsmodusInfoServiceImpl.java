// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.impl;

import java.util.Map;

import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.statistik.AuswertungsmodusInfoService;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.statistik.ErfassungLoesungszettelInfoService;
import de.egladil.web.mk_gateway.domain.statistik.UploadTypeInfoService;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Auswertungsmodus;
import de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.dao.KinderHibernateRepository;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

/**
 * AuswertungsmodusInfoServiceImpl
 */
@RequestScoped
public class AuswertungsmodusInfoServiceImpl implements AuswertungsmodusInfoService {

	@Inject
	KinderRepository kinderRepository;

	@Inject
	UploadTypeInfoService uploadTypeInfoService;

	@Inject
	ErfassungLoesungszettelInfoService erfassungLoesungszettelInfoService;

	public static AuswertungsmodusInfoServiceImpl createForIntegrationTests(final EntityManager entityManager) {

		AuswertungsmodusInfoServiceImpl result = new AuswertungsmodusInfoServiceImpl();
		result.kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		result.uploadTypeInfoService = UploadTypeInfoServiceImpl.createForIntegrationTests(entityManager);
		result.erfassungLoesungszettelInfoService = ErfassungLoesungszettelInfoServiceImpl.createForIntegrationTests(entityManager);
		return result;
	}

	@Override
	public Auswertungsmodus ermittleAuswertungsmodusFuerTeilnahme(final TeilnahmeIdentifier teilnahmeIdentifier) {

		Map<Auswertungsquelle, Long> auswertungsquellenMap = erfassungLoesungszettelInfoService
			.ermittleLoesungszettelMitAuswertungsquellenForTeilnahme(teilnahmeIdentifier);

		if (auswertungsquellenMap.get(Auswertungsquelle.ONLINE).longValue() > 0) {

			return Auswertungsmodus.ONLINE;
		}

		if (auswertungsquellenMap.get(Auswertungsquelle.UPLOAD).longValue() > 0) {

			return Auswertungsmodus.OFFLINE;
		}

		Map<UploadType, Long> uploadTypesMap = uploadTypeInfoService.ermittleUploadTypesForTeilnahme(teilnahmeIdentifier);

		if (uploadTypesMap.get(UploadType.AUSWERTUNG).longValue() > 0) {

			return Auswertungsmodus.OFFLINE;
		}

		if (uploadTypesMap.get(UploadType.KLASSENLISTE).longValue() > 0) {

			return Auswertungsmodus.ONLINE;
		}

		long anzahlKinder = kinderRepository.countKinderZuTeilnahme(teilnahmeIdentifier);

		if (anzahlKinder > 0) {

			return Auswertungsmodus.ONLINE;
		}

		return Auswertungsmodus.INDIFFERENT;
	}

}
