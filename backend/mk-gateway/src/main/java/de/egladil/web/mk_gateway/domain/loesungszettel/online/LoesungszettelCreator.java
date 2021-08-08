// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.online;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.loesungszettel.Wertungsrechner;
import de.egladil.web.mk_gateway.domain.loesungszettel.Wettbewerbswertung;
import de.egladil.web.mk_gateway.domain.loesungszettel.online.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;

/**
 * LoesungszettelCreator
 */
public class LoesungszettelCreator {

	public Loesungszettel createLoesungszettel(final LoesungszettelAPIModel loesungszetteldaten, final Wettbewerb wettbewerb, final Kind kind) {

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
			.withTeilnahmeart(kind.teilnahmeIdentifier().teilnahmeart())
			.withTeilnahmenummer(kind.teilnahmeIdentifier().teilnahmenummer())
			.withWettbewerbID(wettbewerb.id());

		String antwortcode = loesungszetteldaten.antwortcode();

		String loesungsbuchstaben = wettbewerb.loesungsbuchstabenOf(kind.klassenstufe());

		String wertungscode = new AntwortcodeWertungscodeMapper().apply(antwortcode, loesungsbuchstaben);

		LoesungszettelRohdaten rohdaten = new LoesungszettelRohdaten()
			.withAntwortcode(antwortcode)
			.withNutzereingabe(antwortcode)
			.withTypo(false)
			.withWertungscode(wertungscode);

		Wettbewerbswertung wertung = new Wertungsrechner().getWertung(wertungscode, kind.klassenstufe());

		int version = LoesungszettelAPIModel.KEINE_UUID.equals(loesungszetteldaten.uuid()) ? 0 : loesungszetteldaten.version();

		Loesungszettel loesungszettel = new Loesungszettel()
			.withAuswertungsquelle(Auswertungsquelle.ONLINE)
			.withKindID(kind.identifier())
			.withKlassenstufe(kind.klassenstufe())
			.withLandkuerzel(kind.landkuerzel())
			.withSprache(kind.sprache())
			.withTeilnahmeIdentifier(teilnahmeIdentifier)
			.withRohdaten(rohdaten)
			.withPunkte(wertung.punkte())
			.withLaengeKaengurusprung(wertung.kaengurusprung())
			.withVersion(version);

		return loesungszettel;
	}
}
