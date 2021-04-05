// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests;

import java.util.ArrayList;
import java.util.List;

import de.egladil.web.mk_gateway.domain.loesungszettel.ZulaessigeLoesungszetteleingabe;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * TestUtils
 */
public class TestUtils {

	public static LoesungszettelAPIModel createLoesungszettelRequestDatenKlasseZWEIKreuzeABC(final String loesungszettelUuid, final String kindUuid) {

		List<LoesungszettelZeileAPIModel> zeilen = new ArrayList<>();

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(0).withName("A-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(1).withName("A-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(2).withName("A-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(3).withName("A-4"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(4).withName("A-5"));

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(5).withName("B-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(6).withName("B-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(7).withName("B-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(8).withName("B-4"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(9).withName("B-5"));

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(10).withName("C-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(11).withName("C-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(12).withName("C-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(13).withName("C-4"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(14).withName("C-5"));

		LoesungszettelAPIModel requestData = new LoesungszettelAPIModel().withKindID(kindUuid).withKlassenstufe(Klassenstufe.ZWEI)
			.withUuid(loesungszettelUuid).withZeilen(zeilen);

		return requestData;
	}

	public static LoesungszettelAPIModel createLoesungszettelRequestDatenKlasseEinsKreuzeABC(final String loesungszettelUuid, final String kindUuid) {

		List<LoesungszettelZeileAPIModel> zeilen = new ArrayList<>();

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(0).withName("A-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(1).withName("A-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(2).withName("A-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.A).withIndex(3).withName("A-4"));

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(4).withName("B-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(5).withName("B-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(6).withName("B-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.B).withIndex(7).withName("B-4"));

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(8).withName("C-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(9).withName("C-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(10).withName("C-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.C).withIndex(11).withName("C-4"));

		LoesungszettelAPIModel requestData = new LoesungszettelAPIModel().withKindID(kindUuid).withKlassenstufe(Klassenstufe.EINS)
			.withUuid(loesungszettelUuid).withZeilen(zeilen);

		return requestData;
	}

	public static LoesungszettelAPIModel createLoesungszettelRequestDatenKlasseEinsKreuzeDEN(final String loesungszettelUuid, final String kindUuid) {

		List<LoesungszettelZeileAPIModel> zeilen = new ArrayList<>();

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.D).withIndex(0).withName("A-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.D).withIndex(1).withName("A-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.D).withIndex(2).withName("A-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.D).withIndex(3).withName("A-4"));

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.E).withIndex(4).withName("B-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.E).withIndex(5).withName("B-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.E).withIndex(6).withName("B-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.E).withIndex(7).withName("B-4"));

		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.N).withIndex(8).withName("C-1"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.N).withIndex(9).withName("C-2"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.N).withIndex(10).withName("C-3"));
		zeilen.add(new LoesungszettelZeileAPIModel().withAnzahlSpalten(5)
			.withEingabe(ZulaessigeLoesungszetteleingabe.N).withIndex(11).withName("C-4"));

		LoesungszettelAPIModel requestData = new LoesungszettelAPIModel().withKindID(kindUuid).withKlassenstufe(Klassenstufe.EINS)
			.withUuid(loesungszettelUuid).withZeilen(zeilen);

		return requestData;
	}

}
