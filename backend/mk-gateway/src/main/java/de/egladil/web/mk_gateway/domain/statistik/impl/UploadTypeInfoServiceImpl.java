// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.statistik.UploadTypeInfoService;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.domain.uploads.UploadType;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.UploadHibernateRepository;

/**
 * UploadTypeInfoServiceImpl
 */
@RequestScoped
public class UploadTypeInfoServiceImpl implements UploadTypeInfoService {

	@Inject
	private UploadRepository uploadRepository;

	public static UploadTypeInfoServiceImpl createForIntegrationTests(final EntityManager entityManager) {

		UploadTypeInfoServiceImpl result = new UploadTypeInfoServiceImpl();
		result.uploadRepository = UploadHibernateRepository.createForIntegrationTests(entityManager);
		return result;
	}

	@Override
	public Map<UploadType, Long> ermittleUploadTypesForTeilnahme(final TeilnahmeIdentifier teilnahmeIdentifier) {

		Map<UploadType, Long> result = new HashMap<>();

		List<Auspraegung> auspraegungen = uploadRepository.countAuspraegungenForTeilnahmeByColumnName(teilnahmeIdentifier,
			"UPLOAD_TYPE");

		for (UploadType uploadType : UploadType.values()) {

			Optional<Auspraegung> optAuspraegung = auspraegungen.stream().filter(a -> uploadType.toString().equals(a.getWert()))
				.findFirst();

			if (optAuspraegung.isPresent()) {

				result.put(uploadType, optAuspraegung.get().getAnzahl());
			} else {

				result.put(uploadType, 0L);
			}
		}

		return result;
	}

}
