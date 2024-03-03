// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletters;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.newsletters.Newsletter;
import de.egladil.web.mk_gateway.domain.newsletters.NewsletterAPIModel;

/**
 * NewsletterTest
 */
public class NewsletterTest {

	@Test
	void should_serializeWork() throws JsonProcessingException {

		// Arrange
		Newsletter newsletter = new Newsletter()
			.withIdentifier(new Identifier(AbstractDomainServiceTest.NEWSLETTER_TEST_UND_ALLE_UUID))
			.withBetreff("Wichtige Information")
			.withText("MjaApiRestClient bla bla blablablabla bla bla");

		NewsletterAPIModel apiModel = NewsletterAPIModel.createFromNewsletter(newsletter);

		// Act
		String serialisierung = new ObjectMapper().writeValueAsString(apiModel);

		System.out.println(serialisierung);

		// {"uuid":"NEWSLETTER_TEST_UND_ALLE_UUID","betreff":"Wichtige Information","text":"MjaApiRestClient bla bla blablablabla bla
		// bla","versandinfoIDs":[]}
		assertEquals(
			"{\"uuid\":\"NEWSLETTER_TEST_UND_ALLE_UUID\",\"betreff\":\"Wichtige Information\",\"text\":\"MjaApiRestClient bla bla blablablabla bla bla\",\"versandinfoIDs\":[]}",
			serialisierung);
	}

	@Test
	void should_serializeNeuerWork() throws JsonProcessingException {

		// Arrange
		Newsletter newsletter = new Newsletter()
			.withIdentifier(new Identifier(Newsletter.KEINE_UUID))
			.withBetreff("Wichtige Information")
			.withText("MjaApiRestClient bla bla blablablabla bla bla");

		NewsletterAPIModel apiModel = NewsletterAPIModel.createFromNewsletter(newsletter);

		// Act
		String serialisierung = new ObjectMapper().writeValueAsString(apiModel);

		System.out.println(serialisierung);

		// {"uuid":"neu","betreff":"Wichtige Information","text":"MjaApiRestClient bla bla blablablabla bla bla","versandinfoIDs":[]}
		assertEquals(
			"{\"uuid\":\"neu\",\"betreff\":\"Wichtige Information\",\"text\":\"MjaApiRestClient bla bla blablablabla bla bla\",\"versandinfoIDs\":[]}",
			serialisierung);
	}

	@Test
	void createInMemoryDaten() throws JsonProcessingException {

		// Arrange
		List<Newsletter> newsletters = new ArrayList<>();

		{

			Newsletter newsletter = new Newsletter()
				.withIdentifier(new Identifier(AbstractDomainServiceTest.NEWSLETTER_TEST_UND_ALLE_UUID))
				.withBetreff("Wichtige Information an alle")
				.withText("MjaApiRestClient bla bla blablablabla bla bla")
				.addIdVersandinformation(new Identifier(AbstractDomainServiceTest.VERSANDINFO_ALLE_UUID))
				.addIdVersandinformation(new Identifier(AbstractDomainServiceTest.VERSANDINFO_TEST_UUID));

			newsletters.add(newsletter);
		}

		{

			Newsletter newsletter = new Newsletter()
				.withIdentifier(new Identifier(AbstractDomainServiceTest.NEWSLETTER_LEHRER_UUID))
				.withBetreff("Nur für Lehrer")
				.withText("Blubb blubb blubb blubbblubbblubbblubb blubb blubb")
				.addIdVersandinformation(new Identifier(AbstractDomainServiceTest.VERSANDINFO_LEHRER_UUID));

			newsletters.add(newsletter);
		}

		{

			Newsletter newsletter = new Newsletter()
				.withIdentifier(new Identifier(AbstractDomainServiceTest.NEWSLETTER_PRIVATVERANSTALTER_UUID))
				.withBetreff("Nur für Privatveranstalter")
				.withText("Würg würg würg würgwürgwürgwürg würg würg")
				.addIdVersandinformation(new Identifier(AbstractDomainServiceTest.VERSANDINFO_PRIVATVERANSTALTER_UUID));

			newsletters.add(newsletter);
		}

		// Act
		String serialisierung = new ObjectMapper().writeValueAsString(newsletters);

		System.out.println(serialisierung);

	}

}
