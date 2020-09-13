// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen.impl;

import java.util.List;

import de.egladil.web.mk_gateway.domain.auswertungen.Loesungszettel;
import de.egladil.web.mk_gateway.domain.auswertungen.VerteilungRechner;
import de.egladil.web.mk_gateway.domain.auswertungen.statistik.DoubleStringMapper;
import de.egladil.web.mk_gateway.domain.auswertungen.statistik.GesamtpunktverteilungKlassenstufeDaten;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * VerteilungRechnerImpl
 */
public class VerteilungRechnerImpl implements VerteilungRechner {

	private final DoubleStringMapper doubleStringMapper = new DoubleStringMapper();

	@Override
	public GesamtpunktverteilungKlassenstufeDaten berechne(final WettbewerbID wettbewerbId, final Klassenstufe klassenstufe, final List<Loesungszettel> alleLoesungszettel) {

		return null;
	}

	@Override
	public String berechneMedian(final List<Loesungszettel> loesungszettel) {

		Double median = new LoesungszettelMedianMapper().apply(loesungszettel);

		return doubleStringMapper.apply(median);
	}

}
