// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.statistik.ErfassungLoesungszettelInfoService;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
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
	public Map<Auswertungsquelle, Long> ermittleLoesungszettelMitAuswertungsquellenForTeilnahme(final TeilnahmeIdentifier identifier) {

		List<Auspraegung> auspraegungen = loesungszettelRepository
			.countAuspraegungenForTeilnahmeByColumnName(identifier, "QUELLE");

		Map<Auswertungsquelle, Long> result = new HashMap<>();

		for (Auswertungsquelle auswertungsquelle : Auswertungsquelle.values()) {

			Optional<Auspraegung> optAuspraegung = auspraegungen.stream()
				.filter(a -> auswertungsquelle.toString().equals(a.getWert())).findFirst();

			if (optAuspraegung.isPresent()) {

				result.put(auswertungsquelle, Long.valueOf(optAuspraegung.get().getAnzahl()));
			} else {

				result.put(auswertungsquelle, Long.valueOf(0));
			}

		}

		return result;
	}

}
