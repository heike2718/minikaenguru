// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.klassen.Klasse;
import de.egladil.web.mk_gateway.domain.klassen.KlassenRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.entities.InMemoryKlassenList;

/**
 * InMemoryKlassenRepository
 */
public class InMemoryKlassenRepository implements KlassenRepository {

	final List<Klasse> klassen = new ArrayList<>();

	public InMemoryKlassenRepository() {

		try (InputStream in = getClass().getResourceAsStream("/klassenCollection.json")) {

			ObjectMapper objectMapper = new ObjectMapper();

			InMemoryKlassenList list = objectMapper.readValue(in, InMemoryKlassenList.class);

			list.getKlassen().stream().forEach(klasse -> {

				klassen.add(klasse);
			});

		} catch (Exception e) {

			throw new RuntimeException("Kann Testsetting nicht initialisieren: " + e.getMessage(), e);
		}

	}

	@Override
	public Optional<Klasse> ofIdentifier(final Identifier klasseID) {

		return this.klassen.stream().filter(kl -> kl.identifier().equals(klasseID)).findFirst();
	}

	@Override
	public List<Klasse> findKlassenWithSchule(final Identifier schuleID) {

		return this.klassen.stream().filter(kl -> kl.schuleID().equals(schuleID)).collect(Collectors.toList());
	}

	@Override
	public Klasse addKlasse(final Klasse klasse) {

		Identifier klasseID = new Identifier(UUID.randomUUID().toString());

		Klasse neue = new Klasse(klasseID).withName(klasse.name()).withSchuleID(klasse.schuleID());

		this.klassen.add(neue);

		return neue;
	}

}
