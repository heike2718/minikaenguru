// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.util.ArrayList;
import java.util.List;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.Auswertungsquelle;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * AbstractLoesungszettelServiceTest
 */
public abstract class AbstractLoesungszettelServiceTest {

	protected static final String TEILNAHMENUMMER = "FFFUFGFT76";

	protected static final Identifier VERANSTALTER_ID = new Identifier("ldjdjw-wjofjp");

	protected static final Identifier REQUEST_KIND_ID = new Identifier("request-kind-uuid");

	protected static final Identifier REQUEST_LOESUNGSZETTEL_ID = new Identifier("request-loesungszettel-uuid");

	protected static final Identifier PERSISTENT_LOESUNGSZETTEL_ID = new Identifier("persistent-loesungszettel-uuid");

	protected Wettbewerb aktuellerWettbewerb = new Wettbewerb(new WettbewerbID(2021))
		.withLoesungsbuchstabenKlasse1("CEBE-DDEC-BCAE");

	protected TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
		.withTeilnahmenummer(TEILNAHMENUMMER).withWettbewerbID(aktuellerWettbewerb.id());

	protected TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifierKind = TeilnahmeIdentifierAktuellerWettbewerb
		.createForSchulteilnahme(TEILNAHMENUMMER);

	protected LoesungszettelRohdaten rohdaten = new LoesungszettelRohdaten().withNutzereingabe("EBCACCBDBNBN");

	protected Kind kindOhneIDs;

	protected LoesungszettelAPIModel requestDaten;

	protected LoesungszettelAPIModel requestDatenNeu;

	protected List<LoesungszettelZeileAPIModel> zeilen;

	protected Loesungszettel vorhandenerLoesungszettelOhneIDs;

	protected PersistenterLoesungszettel persistenterLoesungszettel;

	protected void init() {

		kindOhneIDs = new Kind().withKlassenstufe(Klassenstufe.EINS).withLandkuerzel("DE-HE")
			.withSprache(Sprache.de)
			.withTeilnahmeIdentifier(teilnahmeIdentifierKind)
			.withKlasseID(new Identifier("dqguiguq"));

		this.zeilen = createLoesungszettelZeilen();

		this.requestDaten = new LoesungszettelAPIModel().withKindID(REQUEST_KIND_ID.identifier())
			.withUuid(REQUEST_LOESUNGSZETTEL_ID.identifier())
			.withKlassenstufe(Klassenstufe.EINS).withZeilen(zeilen);

		this.requestDatenNeu = new LoesungszettelAPIModel().withKindID(REQUEST_KIND_ID.identifier()).withUuid("neu")
			.withKlassenstufe(Klassenstufe.EINS).withZeilen(zeilen);

		this.vorhandenerLoesungszettelOhneIDs = new Loesungszettel().withPunkte(5000)
			.withLaengeKaengurusprung(5).withRohdaten(rohdaten).withKlassenstufe(Klassenstufe.EINS);

		persistenterLoesungszettel = new PersistenterLoesungszettel();
		persistenterLoesungszettel.setAntwortcode("dqhuo");
		persistenterLoesungszettel.setAuswertungsquelle(Auswertungsquelle.ONLINE);
		persistenterLoesungszettel.setNutzereingabe("sadjkqh");
		persistenterLoesungszettel.setTeilnahmeart(Teilnahmeart.SCHULE);
		persistenterLoesungszettel.setTeilnahmenummer(TEILNAHMENUMMER);
		persistenterLoesungszettel.setWertungscode("fffffffff");
		persistenterLoesungszettel.setWettbewerbUuid(aktuellerWettbewerb.id().toString());
		persistenterLoesungszettel.setSprache(Sprache.de);
	}

	/**
	 * @return
	 */
	protected List<LoesungszettelZeileAPIModel> createLoesungszettelZeilen() {

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
		return zeilen;
	}

}
