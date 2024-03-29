// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.entities.InMemoryTeilnahme;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.entities.InMemoryTeilnahmeList;

/**
 * InMemoryTeilnahmenRepository
 */
public class InMemoryTeilnahmenRepository implements TeilnahmenRepository {

	private int teilnahmeAdded = 0;

	private int teilnahmeChanged = 0;

	private final List<Teilnahme> teilnahmen = new ArrayList<>();

	public InMemoryTeilnahmenRepository() {

		try (InputStream in = getClass().getResourceAsStream("/teilnahmen.json")) {

			ObjectMapper objectMapper = new ObjectMapper();

			InMemoryTeilnahmeList list = objectMapper.readValue(in, InMemoryTeilnahmeList.class);

			list.getTeilnahmen().stream().forEach(tn -> {

				Teilnahme teilnahme = mapToTeilnahme(tn);

				if (teilnahme != null) {

					teilnahmen.add(teilnahme);
				} else {

					System.err.println("keine korrekte Teilnahmeart: " + tn.getArt());
				}
			});

		} catch (Exception e) {

			throw new RuntimeException("Kann Testsetting nicht initialisieren: " + e.getMessage(), e);
		}

	}

	/**
	 * @param  tn
	 * @return
	 */
	private Teilnahme mapToTeilnahme(final InMemoryTeilnahme tn) {

		if (InMemoryTeilnahme.ART_PRIVAT.equals(tn.getArt())) {

			return new Privatteilnahme(new WettbewerbID(tn.getWettbewerbJahr()), tn.getTeilnahmenummer());
		}

		if (InMemoryTeilnahme.ART_SCHULE.equals(tn.getArt())) {

			return new Schulteilnahme(new WettbewerbID(tn.getWettbewerbJahr()), tn.getTeilnahmenummer(),
				tn.getNameSchule(), tn.getAngemeldetDurchVeranstalterId());
		}

		return null;
	}

	@Override
	public Optional<Teilnahme> ofTeilnahmeIdentifier(final TeilnahmeIdentifier teilnahmeIdentifier) {

		return this.teilnahmen.stream()
			.filter(tn -> teilnahmeIdentifier.teilnahmenummer()
				.equals(tn.teilnahmenummer().identifier()) && teilnahmeIdentifier.teilnahmeart() == tn.teilnahmeart()
				&& tn.wettbewerbID().jahr().toString().equals(teilnahmeIdentifier.wettbewerbID()))
			.findFirst();
	}

	@Override
	public List<Teilnahme> ofTeilnahmenummerArt(final String teilnahmenummer, final Teilnahmeart art) {

		return this.teilnahmen.stream()
			.filter(tn -> teilnahmenummer
				.equals(tn.teilnahmenummer().identifier()) && art == tn.teilnahmeart())
			.collect(Collectors.toList());
	}

	@Override
	public List<Teilnahme> ofTeilnahmenummer(final String teilnahmenummer) {

		return this.teilnahmen.stream()
			.filter(tn -> teilnahmenummer
				.equals(tn.teilnahmenummer().identifier()))
			.collect(Collectors.toList());
	}

	@Override
	public boolean addTeilnahme(final Teilnahme teilnahme) {

		this.teilnahmen.add(teilnahme);

		teilnahmeAdded++;
		return true;
	}

	@Override
	public void changeTeilnahme(final Schulteilnahme teilnahme) throws IllegalStateException {

		teilnahmeChanged++;

	}

	public int getTeilnahmeAdded() {

		return teilnahmeAdded;
	}

	public int getTeilnahmeChanged() {

		return teilnahmeChanged;
	}

	@Override
	public List<Teilnahme> loadAllForWettbewerb(final WettbewerbID wettbewerbID) {

		return teilnahmen.stream().filter(t -> wettbewerbID.toString().equals(t.teilnahmeIdentifier().wettbewerbID()))
			.collect(Collectors.toList());
	}

}
