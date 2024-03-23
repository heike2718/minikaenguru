// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik.functions;

import java.util.function.Function;

import de.egladil.web.mk_gateway.domain.statistik.Punktintervall;

/**
 * PunktintervallStringMapper
 */
public class PunktintervallStringMapper implements Function<Punktintervall, String> {

	private final PunkteStringMapper punkteStringMapper = new PunkteStringMapper();

	@Override
	public String apply(final Punktintervall punktintervall) {

		String untereGrenze = punkteStringMapper.apply(punktintervall.getMinVal());
		String obereGrenze = punkteStringMapper.apply(punktintervall.getMaxVal());
		String text = untereGrenze + "-" + obereGrenze;
		return text;
	}

}
