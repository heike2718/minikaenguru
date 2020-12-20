// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.api.VersandinfoAPIModel;

/**
 * VersandinformationTest
 */
public class VersandinformationTest {

	@Test
	void should_serializeWork() throws JsonProcessingException {

		// Arrange
		Versandinformation versandinfo = new Versandinformation()
			.withAnzahlAktuellVersendet(13)
			.withAnzahlEmpaenger(42)
			.withIdentifier(new Identifier(AbstractDomainServiceTest.VERSANDINFO_ALLE_UUID))
			.withNewsletterID(new Identifier(AbstractDomainServiceTest.NEWSLETTER_TEST_UND_ALLE_UUID))
			.withEmpfaengertyp(Empfaengertyp.ALLE)
			.withVersandBegonnenAm("16.12.2020 10:54:01")
			.withVersandBeendetAm("16.12.2020 11:03:22");

		VersandinfoAPIModel apiModel = VersandinfoAPIModel.createFromVersandinfo(versandinfo);

		// Act
		String serialisierung = new ObjectMapper().writeValueAsString(apiModel);

		System.out.println(serialisierung);

		// {"uuid":"VERSANDINFO_ALLE_UUID","newsletterID":"NEWSLETTER_TEST_UND_ALLE_UUID","empfaengertyp":"ALLE","anzahlAktuellVersendet":13,"anzahlEmpaenger":42,"versandBegonnenAm":"16.12.2020
		// 10:54:01","versandBeendetAm":"16.12.2020 11:03:22","versandMitFehler":false}
		assertEquals(
			"{\"uuid\":\"VERSANDINFO_ALLE_UUID\",\"newsletterID\":\"NEWSLETTER_TEST_UND_ALLE_UUID\",\"empfaengertyp\":\"ALLE\",\"anzahlAktuellVersendet\":13,\"anzahlEmpaenger\":42,\"versandBegonnenAm\":\"16.12.2020 10:54:01\",\"versandBeendetAm\":\"16.12.2020 11:03:22\",\"versandMitFehler\":false}",
			serialisierung);
	}

	@Test
	void createInMemoryDaten() throws JsonProcessingException {

		// Arrange
		List<Versandinformation> versandinfos = new ArrayList<>();

		{

			Versandinformation versandinfo = new Versandinformation()
				.withAnzahlAktuellVersendet(42)
				.withAnzahlEmpaenger(42)
				.withIdentifier(new Identifier(AbstractDomainServiceTest.VERSANDINFO_ALLE_UUID))
				.withNewsletterID(new Identifier(AbstractDomainServiceTest.NEWSLETTER_TEST_UND_ALLE_UUID))
				.withEmpfaengertyp(Empfaengertyp.ALLE)
				.withVersandBegonnenAm("15.12.2020 10:54:01")
				.withVersandBeendetAm("15.12.2020 11:03:22");

			versandinfos.add(versandinfo);
		}

		{

			Versandinformation versandinfo = new Versandinformation()
				.withAnzahlAktuellVersendet(0)
				.withAnzahlEmpaenger(2)
				.withIdentifier(new Identifier(AbstractDomainServiceTest.VERSANDINFO_TEST_UUID))
				.withNewsletterID(new Identifier(AbstractDomainServiceTest.NEWSLETTER_TEST_UND_ALLE_UUID))
				.withEmpfaengertyp(Empfaengertyp.TEST);

			versandinfos.add(versandinfo);
		}

		{

			Versandinformation versandinfo = new Versandinformation()
				.withAnzahlAktuellVersendet(13)
				.withAnzahlEmpaenger(42)
				.withIdentifier(new Identifier(AbstractDomainServiceTest.VERSANDINFO_LEHRER_UUID))
				.withNewsletterID(new Identifier(AbstractDomainServiceTest.NEWSLETTER_LEHRER_UUID))
				.withEmpfaengertyp(Empfaengertyp.LEHRER)
				.withVersandBegonnenAm("16.12.2020 10:54:01");

			versandinfos.add(versandinfo);
		}

		{

			Versandinformation versandinfo = new Versandinformation()
				.withAnzahlAktuellVersendet(0)
				.withAnzahlEmpaenger(9)
				.withIdentifier(new Identifier(AbstractDomainServiceTest.VERSANDINFO_PRIVATVERANSTALTER_UUID))
				.withNewsletterID(new Identifier(AbstractDomainServiceTest.NEWSLETTER_PRIVATVERANSTALTER_UUID))
				.withEmpfaengertyp(Empfaengertyp.PRIVATVERANSTALTER);

			versandinfos.add(versandinfo);
		}

		// Act
		String serialisierung = new ObjectMapper().writeValueAsString(versandinfos.toArray(new Versandinformation[0]));

		System.out.println(serialisierung);
	}

}
