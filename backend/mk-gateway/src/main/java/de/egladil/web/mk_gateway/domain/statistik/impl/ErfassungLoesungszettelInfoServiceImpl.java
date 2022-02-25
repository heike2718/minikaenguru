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

import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.statistik.ErfassungLoesungszettelInfoService;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;

/**
 * ErfassungLoesungszettelInfoServiceImpl
 */
@RequestScoped
public class ErfassungLoesungszettelInfoServiceImpl implements ErfassungLoesungszettelInfoService {

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	public static ErfassungLoesungszettelInfoServiceImpl createForIntegrationTests(final EntityManager entityManager) {

		ErfassungLoesungszettelInfoServiceImpl result = new ErfassungLoesungszettelInfoServiceImpl();
		result.loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(entityManager);
		return result;
	}

	@Override
	public Map<Auswertungsquelle, Long> ermittleLoesungszettelMitAuswertungsquellenForTeilnahme(final Teilnahme teilnahme) {

		List<Auspraegung> auspraegungen = loesungszettelRepository
			.countAuspraegungenForTeilnahmeByColumnName(teilnahme, "QUELLE");

		long anzahlOnline = 0;
		long anzahlUpload = 0;

		Optional<Auspraegung> optOnline = auspraegungen.stream()
			.filter(a -> Auswertungsquelle.ONLINE.toString().equals(a.getWert())).findFirst();

		if (optOnline.isPresent()) {

			anzahlOnline = optOnline.get().getAnzahl();
		}

		Optional<Auspraegung> optUpload = auspraegungen.stream()
			.filter(a -> Auswertungsquelle.UPLOAD.toString().equals(a.getWert())).findFirst();

		if (optUpload.isPresent()) {

			anzahlUpload = optUpload.get().getAnzahl();
		}

		Map<Auswertungsquelle, Long> result = new HashMap<>();

		result.put(Auswertungsquelle.ONLINE, Long.valueOf(anzahlOnline));
		result.put(Auswertungsquelle.UPLOAD, Long.valueOf(anzahlUpload));

		return result;
	}

}
