// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.Versandinformation;
import de.egladil.web.mk_gateway.domain.mail.VersandinformationenRepository;

/**
 * InMemoryVersandinfosRepository
 */
public class InMemoryVersandinfosRepository implements VersandinformationenRepository {

	private final List<Versandinformation> versandinfos = new ArrayList<>();

	private int versandinfoAdded = 0;

	private int versandinfoChanged = 0;

	public InMemoryVersandinfosRepository() {

		try (InputStream in = getClass().getResourceAsStream("/versandinformationen.json")) {

			ObjectMapper objectMapper = new ObjectMapper();

			Versandinformation[] list = objectMapper.readValue(in, Versandinformation[].class);

			Arrays.stream(list).forEach(l -> versandinfos.add(l));

		} catch (Exception e) {

			throw new RuntimeException("Kann Testsetting nicht initialisieren: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Versandinformation> findForNewsletter(final Identifier newsletterID) {

		return versandinfos.stream().filter(i -> newsletterID.equals(i.newsletterID())).collect(Collectors.toList());
	}

	@Override
	public Optional<Versandinformation> ofId(final Identifier identifier) {

		return versandinfos.stream().filter(i -> identifier.equals(i.identifier())).findFirst();
	}

	@Override
	public Versandinformation addVersandinformation(final Versandinformation versandinformation) {

		Identifier neueId = new Identifier(UUID.randomUUID().toString());

		versandinformation.withIdentifier(neueId);

		if (!versandinfos.contains(versandinformation)) {

			versandinfos.add(versandinformation);
			versandinfoAdded++;
		}

		return versandinformation;
	}

	@Override
	public Versandinformation updateVersandinformation(final Versandinformation versandinformation) {

		Optional<Versandinformation> optVorhandene = this.ofId(versandinformation.identifier());

		if (optVorhandene.isPresent()) {

			versandinfos.remove(optVorhandene.get());
			versandinfos.add(versandinformation);
			versandinfoChanged++;
		}

		return versandinformation;
	}

	@Override
	public int removeAll(final Identifier newsletterID) {

		List<Versandinformation> mitNewsletter = this.findForNewsletter(newsletterID);

		versandinfos.removeAll(mitNewsletter);

		return mitNewsletter.size();
	}

	public int getVersandinfoAdded() {

		return versandinfoAdded;
	}

	public int getVersandinfoChanged() {

		return versandinfoChanged;
	}

}
