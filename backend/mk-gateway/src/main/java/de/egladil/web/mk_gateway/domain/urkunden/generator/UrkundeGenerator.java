// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenart;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.KaengurusprungurkundeGeneratorDeutsch;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.KaengurusprungurkundeGeneratorEnglisch;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.TeilnahmeurkundeGeneratorDeutsch;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.TeilnahmeurkundeGeneratorEnglisch;

/**
 * UrkundeGenerator
 */
public interface UrkundeGenerator {

	/**
	 * Generiert das PDF für eine Teilnahmeurkunde.
	 *
	 * @param  datenUrkunde
	 *                      AbstractDatenUrkunde
	 * @return              byte[]
	 */
	byte[] generiereUrkunde(final AbstractDatenUrkunde datenUrkunde);

	static UrkundeGenerator create(final Urkundenart urkundenart, final Sprache sprache) {

		switch (urkundenart) {

		case KAENGURUSPRUNG:

			switch (sprache) {

			case de:

				return new KaengurusprungurkundeGeneratorDeutsch();

			case en:
				return new KaengurusprungurkundeGeneratorEnglisch();

			default:
				throw new IllegalArgumentException("unzulaessige Sprache " + sprache);
			}
		case TEILNAHME:

			switch (sprache) {

			case de:

				return new TeilnahmeurkundeGeneratorDeutsch();

			case en:
				return new TeilnahmeurkundeGeneratorEnglisch();

			default:
				throw new IllegalArgumentException("unzulaessige Sprache " + sprache);
			}

		default:
			throw new IllegalArgumentException("unzulaessige Urkundenart " + urkundenart);
		}
	}

}
