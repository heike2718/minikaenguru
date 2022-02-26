// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.impl;

import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.statistik.ErfassungLoesungszettelInfoService;
import de.egladil.web.mk_gateway.domain.statistik.UploadTypeInfoService;
import de.egladil.web.mk_gateway.domain.statistik.WettbewerbsauswertungsartInfoService;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.domain.veranstalter.api.Wettbewerbsauswertungsart;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;

/**
 * WettbewerbsauswertungsartInfoServiceImpl
 */
@RequestScoped
public class WettbewerbsauswertungsartInfoServiceImpl implements WettbewerbsauswertungsartInfoService {

	@Inject
	KinderRepository kinderRepository;

	@Inject
	UploadTypeInfoService uploadTypeInfoService;

	@Inject
	ErfassungLoesungszettelInfoService erfassungLoesungszettelInfoService;

	public static WettbewerbsauswertungsartInfoServiceImpl createForIntegrationTests(final EntityManager entityManager) {

		WettbewerbsauswertungsartInfoServiceImpl result = new WettbewerbsauswertungsartInfoServiceImpl();
		result.kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		result.uploadTypeInfoService = UploadTypeInfoServiceImpl.createForIntegrationTests(entityManager);
		result.erfassungLoesungszettelInfoService = ErfassungLoesungszettelInfoServiceImpl.createForIntegrationTests(entityManager);
		return result;
	}

	@Override
	public Wettbewerbsauswertungsart ermittleAuswertungsartFuerTeilnahme(final TeilnahmeIdentifier teilnahmeIdentifier) {

		Map<Auswertungsquelle, Long> auswertungsquellenMap = erfassungLoesungszettelInfoService
			.ermittleLoesungszettelMitAuswertungsquellenForTeilnahme(teilnahmeIdentifier);

		if (auswertungsquellenMap.get(Auswertungsquelle.ONLINE).longValue() > 0) {

			return Wettbewerbsauswertungsart.ONLINE;
		}

		if (auswertungsquellenMap.get(Auswertungsquelle.UPLOAD).longValue() > 0) {

			return Wettbewerbsauswertungsart.OFFLINE;
		}

		Map<UploadType, Long> uploadTypesMap = uploadTypeInfoService.ermittleUploadTypesForTeilnahme(teilnahmeIdentifier);

		if (uploadTypesMap.get(UploadType.AUSWERTUNG).longValue() > 0) {

			return Wettbewerbsauswertungsart.OFFLINE;
		}

		if (uploadTypesMap.get(UploadType.KLASSENLISTE).longValue() > 0) {

			return Wettbewerbsauswertungsart.ONLINE;
		}

		long anzahlKinder = kinderRepository.countKinderZuTeilnahme(teilnahmeIdentifier);

		if (anzahlKinder > 0) {

			return Wettbewerbsauswertungsart.ONLINE;
		}

		return Wettbewerbsauswertungsart.INDIFFERENT;
	}

}
