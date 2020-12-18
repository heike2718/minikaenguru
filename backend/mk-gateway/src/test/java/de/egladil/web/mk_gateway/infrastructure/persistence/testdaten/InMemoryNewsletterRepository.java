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

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.Newsletter;
import de.egladil.web.mk_gateway.domain.mail.NewsletterRepository;

/**
 * InMemoryNewsletterRepository
 */
public class InMemoryNewsletterRepository implements NewsletterRepository {

	private final List<Newsletter> newsletters = new ArrayList<>();

	private int newsletterAdded = 0;

	private int newsletterChanged = 0;

	private int newsletterRemoved = 0;

	public InMemoryNewsletterRepository() {

		try (InputStream in = getClass().getResourceAsStream("/newsletters.json")) {

			ObjectMapper objectMapper = new ObjectMapper();

			Newsletter[] list = objectMapper.readValue(in, Newsletter[].class);

			Arrays.stream(list).forEach(l -> newsletters.add(l));

		} catch (Exception e) {

			throw new RuntimeException("Kann Testsetting nicht initialisieren: " + e.getMessage(), e);
		}

	}

	@Override
	public List<Newsletter> loadAll() {

		return newsletters;
	}

	@Override
	public Optional<Newsletter> ofId(final Identifier id) {

		return newsletters.stream().filter(l -> id.equals(l.identifier())).findFirst();
	}

	@Override
	public Newsletter addNewsletter(final Newsletter newsletter) {

		Identifier neueId = new Identifier(UUID.randomUUID().toString());
		newsletter.withIdentifier(neueId);

		if (!newsletters.contains(newsletter)) {

			newsletters.add(newsletter);
			newsletterAdded++;
		}

		return newsletter;
	}

	@Override
	public Newsletter updateNewsletter(final Newsletter newsletter) {

		Optional<Newsletter> optVorhandener = this.ofId(newsletter.identifier());

		if (optVorhandener.isPresent()) {

			newsletters.remove(optVorhandener.get());
			newsletters.add(newsletter);
			newsletterChanged++;

		}

		return newsletter;

	}

	@Override
	public void removeNewsletter(final Identifier identifier) {

		Optional<Newsletter> optVorhandener = this.ofId(identifier);

		if (optVorhandener.isPresent()) {

			newsletters.remove(optVorhandener.get());
			newsletterRemoved++;
		}

	}

	public int getNewsletterAdded() {

		return newsletterAdded;
	}

	public int getNewsletterChanged() {

		return newsletterChanged;
	}

	public int getNewsletterRemoved() {

		return newsletterRemoved;
	}

}
