// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenart;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AuswertungDatenRepository;
import de.egladil.web.mk_gateway.domain.urkunden.daten.KinddatenUebersicht;
import de.egladil.web.mk_gateway.domain.urkunden.generator.UrkundeGenerator;

/**
 * SchulurkundenGenerator generiert die Urkunden für eine Schule.
 */
public class SchulurkundenGenerator {

	/**
	 * Generiert die Urkunden sortiert nach Klassen und Kindernamen, sowie die Kängurusprungurkunden, wenn es genau eine gibt.
	 *
	 * @param  datenRepository
	 * @return                 List<byte[]> je eine Seite für jede Urkunde.
	 */
	public List<byte[]> generiereUrkunden(final AuswertungDatenRepository datenRepository) {

		List<byte[]> result = new ArrayList<>();

		for (Klassenstufe klassenstufe : Klassenstufe.valuesSorted()) {

			List<KinddatenUebersicht> kaengurukinder = datenRepository.getKaengurugewinner(klassenstufe);

			switch (kaengurukinder.size()) {

			case 1:
				KinddatenUebersicht kindDaten = kaengurukinder.get(0);
				Optional<AbstractDatenUrkunde> optKaenguruurkundeDaten = datenRepository
					.findDatenKaengurusprungurkunde(kindDaten.uuid());

				if (optKaenguruurkundeDaten.isPresent()) {

					AbstractDatenUrkunde datenUrkunde = optKaenguruurkundeDaten.get();
					UrkundeGenerator urkundeGenerator = UrkundeGenerator.create(Urkundenart.KAENGURUSPRUNG, datenUrkunde.sprache());
					result.add(urkundeGenerator.generiereUrkunde(datenUrkunde));
				}
				break;

			default:
				break;
			}
		}

		List<AbstractDatenUrkunde> datenTeilnehmerurkunden = datenRepository.getAllDatenTeilnahmeurkundenSorted();

		for (AbstractDatenUrkunde datenUrkunde : datenTeilnehmerurkunden) {

			Sprache sprache = datenUrkunde.sprache();

			UrkundeGenerator urkundeGenerator = UrkundeGenerator.create(Urkundenart.TEILNAHME, sprache);
			result.add(urkundeGenerator.generiereUrkunde(datenUrkunde));
		}

		return result;
	}

}
