// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

/**
 * KaengurusprungurkundeGeneratorDeutsch
 */
public class KaengurusprungurkundeGeneratorDeutsch extends AbstractUrkundegeneratorDeutsch {

	@Override
	protected UrkundeHauptabschnittRenderer getHauptabschnittRenderer() {

		return new KaengurusprungurkundeHauptabschnittRendererDeutsch();
	}

}
