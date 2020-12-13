// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten;

import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.veranstalter.SchulkollegienRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.Schulkollegium;

/**
 * InMemorySchulkollegienRepository
 */
public class InMemorySchulkollegienRepository implements SchulkollegienRepository {

	private Map<Identifier, Schulkollegium> schulkollegien = new HashMap<>();

	public InMemorySchulkollegienRepository() {

		try (InputStream in = getClass().getResourceAsStream("/schulkollegien.json")) {

			ObjectMapper objectMapper = new ObjectMapper();

			Schulkollegium[] theKollegien = objectMapper.readValue(in, Schulkollegium[].class);

			Arrays.stream(theKollegien).forEach(kollegium -> {

				schulkollegien.put(kollegium.schulkuerzel(), kollegium);
			});

		} catch (Exception e) {

			throw new RuntimeException("Kann Testsetting nicht initialisieren: " + e.getMessage(), e);
		}
	}

	@Override
	public Optional<Schulkollegium> ofSchulkuerzel(final Identifier identifier) {

		Schulkollegium result = this.schulkollegien.get(identifier);

		return result == null ? Optional.empty() : Optional.of(result);
	}

	@Override
	public void addKollegium(final Schulkollegium schulkollegium) throws IllegalStateException {

		if (!this.schulkollegien.containsKey(schulkollegium.schulkuerzel())) {

			this.schulkollegien.put(schulkollegium.schulkuerzel(), schulkollegium);
		}

	}

	@Override
	public void replaceKollegen(final Schulkollegium schulkollegium) {

		Schulkollegium vorhandenes = this.schulkollegien.get(schulkollegium.schulkuerzel());

		if (vorhandenes == null) {

			throw new MkGatewayRuntimeException("Testsetting: das Schulkollegium " + schulkollegium + " ist nicht geladen");
		}

		schulkollegien.put(schulkollegium.schulkuerzel(), schulkollegium);

	}

	@Override
	public void deleteKollegium(final Schulkollegium kollegium) {

		this.schulkollegien.remove(kollegium.schulkuerzel());

	}

}
