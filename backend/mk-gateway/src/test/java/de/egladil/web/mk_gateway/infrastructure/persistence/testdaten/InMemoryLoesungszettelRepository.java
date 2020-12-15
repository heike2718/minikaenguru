// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten;

import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.auswertungen.StatistikTestUtils;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterLoesungszettel;

/**
 * InMemoryLoesungszettelRepository
 */
public class InMemoryLoesungszettelRepository implements LoesungszettelRepository {

	private Map<Identifier, Loesungszettel> alleLoesungszettel = new HashMap<>();;

	public InMemoryLoesungszettelRepository() {

		try {

			List<Loesungszettel> alle = StatistikTestUtils.loadTheLoesungszettel();

			alle.stream().forEach(l -> alleLoesungszettel.put(l.identifier(), l));

		} catch (Exception e) {

			e.printStackTrace();
			fail("Testsetting fehlerhaft: " + e.getMessage());
		}

	}

	@Override
	public List<Loesungszettel> loadAllForWettbewerb(final WettbewerbID wettbewerbID) {

		return this.alleLoesungszettel.values().stream()
			.filter(l -> l.teilnahmeIdentifier().wettbewerbID().equals(wettbewerbID.jahr().toString()))
			.collect(Collectors.toList());
	}

	@Override
	public List<Loesungszettel> loadAllForWettbewerbAndKlassenstufe(final WettbewerbID wettbewerbID, final Klassenstufe klassenstufe) {

		List<Loesungszettel> vomWettbewerb = this.loadAllForWettbewerb(wettbewerbID);

		return vomWettbewerb.stream().filter(w -> w.klassenstufe() == klassenstufe).collect(Collectors.toList());
	}

	@Override
	public List<Loesungszettel> loadAll(final TeilnahmeIdentifier teilnahmeIdentifier) {

		return this.alleLoesungszettel.values().stream()
			.filter(l -> l.teilnahmeIdentifier().equals(teilnahmeIdentifier))
			.collect(Collectors.toList());
	}

	@Override
	public int anzahlLoesungszettel(final TeilnahmeIdentifier teilnahmeIdentifier) {

		return alleLoesungszettel.size();
	}

	@Override
	public Optional<PersistenterLoesungszettel> findByIdentifier(final Identifier identifier) {

		Loesungszettel loesungszettel = alleLoesungszettel.get(identifier);

		if (loesungszettel == null) {

			return Optional.empty();
		}

		return Optional.of(mapFromLoesungszettel(loesungszettel));
	}

	@Override
	public Identifier addLosungszettel(final Loesungszettel loesungszettel) {

		Identifier identifier = new Identifier(UUID.randomUUID().toString());

		Loesungszettel neuer = new Loesungszettel(identifier)
			.withAuswertungsquelle(loesungszettel.auswertungsquelle())
			.withKlassenstufe(loesungszettel.klassenstufe())
			.withLaengeKaengurusprung(loesungszettel.laengeKaengurusprung())
			.withLandkuerzel(loesungszettel.landkuerzel())
			.withKindID(loesungszettel.kindID())
			.withPunkte(loesungszettel.punkte())
			.withRohdaten(loesungszettel.rohdaten())
			.withSprache(loesungszettel.sprache())
			.withTeilnahmeIdentifier(loesungszettel.teilnahmeIdentifier());

		this.alleLoesungszettel.put(identifier, neuer);
		return identifier;
	}

	@Override
	public boolean updateLoesungszettel(final Loesungszettel loesungszettel) {

		Loesungszettel vorhandener = alleLoesungszettel.get(loesungszettel.identifier());

		if (vorhandener == null) {

			return false;
		}

		alleLoesungszettel.put(loesungszettel.identifier(), loesungszettel);

		return true;
	}

	@Override
	public PersistenterLoesungszettel updateLoesungszettelInTransaction(final PersistenterLoesungszettel persistenterLoesungszettel) {

		Loesungszettel vorhandener = alleLoesungszettel.get(new Identifier(persistenterLoesungszettel.getUuid()));

		if (vorhandener == null) {

			return null;
		}

		Loesungszettel aktualisierter = mapFromDB(persistenterLoesungszettel);

		alleLoesungszettel.put(aktualisierter.identifier(), aktualisierter);

		return persistenterLoesungszettel;
	}

	@Override
	public boolean removeLoesungszettel(final Identifier identifier, final String veranstalterUuid) {

		Loesungszettel geloeschter = alleLoesungszettel.remove(identifier);

		return geloeschter != null;
	}

	private Loesungszettel mapFromDB(final PersistenterLoesungszettel persistenter) {

		WettbewerbID wettbewerbID = new WettbewerbID(persistenter.getWettbewerbUuid());

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
			.withTeilnahmeart(persistenter.getTeilnahmeart())
			.withTeilnahmenummer(persistenter.getTeilnahmenummer())
			.withWettbewerbID(wettbewerbID);

		LoesungszettelRohdaten rohdaten = new LoesungszettelRohdaten()
			.withAntwortcode(persistenter.getAntwortcode())
			.withNutzereingabe(persistenter.getNutzereingabe())
			.withTypo(persistenter.isTypo())
			.withWertungscode(persistenter.getWertungscode());

		Loesungszettel result = new Loesungszettel(new Identifier(persistenter.getUuid()))
			.withAuswertungsquelle(persistenter.getAuswertungsquelle())
			.withKlassenstufe(persistenter.getKlassenstufe())
			.withLaengeKaengurusprung(persistenter.getKaengurusprung())
			.withLandkuerzel(persistenter.getLandkuerzel())
			.withKindID(persistenter.getKindID())
			.withPunkte(persistenter.getPunkte())
			.withRohdaten(rohdaten)
			.withSprache(persistenter.getSprache())
			.withTeilnahmeIdentifier(teilnahmeIdentifier);

		return result;
	}

	private PersistenterLoesungszettel mapFromLoesungszettel(final Loesungszettel loesungszettel) {

		LoesungszettelRohdaten rohdaten = loesungszettel.rohdaten();
		PersistenterLoesungszettel result = new PersistenterLoesungszettel()
			.withAntwortcode(rohdaten.antwortcode())
			.withAuswertungsquelle(loesungszettel.auswertungsquelle())
			.withKaengurusprung(loesungszettel.laengeKaengurusprung())
			.withKindID(loesungszettel.kindID())
			.withKlassenstufe(loesungszettel.klassenstufe())
			.withLandkuerzel("") // TODO
			.withNutzereingabe(rohdaten.nutzereingabe())
			.withPunkte(loesungszettel.punkte())
			.withSprache(loesungszettel.sprache())
			.withTeilnahmeart(loesungszettel.teilnahmeIdentifier().teilnahmeart())
			.withTeilnahmenummer(loesungszettel.teilnahmeIdentifier().teilnahmenummer())
			.withTypo(rohdaten.hatTypo())
			.withWertungscode(rohdaten.wertungscode())
			.withWettbewerbUuid(loesungszettel.teilnahmeIdentifier().wettbewerbID());

		if (loesungszettel.identifier() != null) {

			result.setUuid(loesungszettel.identifier().identifier());
		}
		return result;
	}
}