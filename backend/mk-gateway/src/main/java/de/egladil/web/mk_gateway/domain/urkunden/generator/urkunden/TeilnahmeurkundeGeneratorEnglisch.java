// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

/**
 * TeilnahmeurkundeGeneratorEnglisch
 */
public class TeilnahmeurkundeGeneratorEnglisch extends AbstractUrkundegeneratorEnglisch {

	@Override
	protected UrkundeHauptabschnittRenderer getHauptabschnittRenderer() {

		return new TeilnahmeurkundeHauptabschnittRendererEnglisch();
	}
}
