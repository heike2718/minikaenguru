// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

/**
 * KaengurusprungurkundeGeneratorEnglisch
 */
public class KaengurusprungurkundeGeneratorEnglisch extends AbstractUrkundegeneratorEnglisch {

	@Override
	protected UrkundeHauptabschnittRenderer getHauptabschnittRenderer() {

		return new KaengurusprungurkundeHauptabschnittRendererEnglisch();
	}
}
