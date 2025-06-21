// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

/**
 * TeilnahmeurkundeGeneratorDeutsch
 */
public class TeilnahmeurkundeGeneratorDeutsch extends AbstractUrkundegeneratorDeutsch {

	@Override
	protected UrkundeHauptabschnittRenderer getHauptabschnittRenderer() {

		return new TeilnahmeurkundeHauptabschnittRendererDeutsch();
	}

}
