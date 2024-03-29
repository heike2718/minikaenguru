// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;

/**
 * ZugangUnterlagenServiceTest
 */
public class ZugangUnterlagenServiceTest {

	private static final String SCHULTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB = "GGUGUOGO";

	private static final String PRIVATTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB = "TUITITI";

	private static final String SCHULKUERZEL_NICHT_ANGEMELDET = "ZUKGKGK";

	private Lehrer lehrerAngemeldet;

	private Lehrer lehrerSonderzugangsberechtigungAngemeldet;

	private Lehrer lehrerSonderzugangsberechtigungNichtAngemeldet;

	private Lehrer lehrerZugangsberechtigungEntzogen;

	private Lehrer lehrerNichtAngemeldet;

	private Privatveranstalter privatpersonSonderzugangsberechtigung;

	private Privatveranstalter privatpersonZugangsberechtigungEntzogen;

	private Privatveranstalter privatpersonAngemeldet;

	private Privatveranstalter privatpersonNichtAngemeldet;

	private ZugangUnterlagenService service;

	private Wettbewerb wettbewerb;

	private TeilnahmenRepository teilnahmenRepository;

	private VeranstalterRepository veranstalterRepository;

	private WettbewerbService wettbewerbService;

	@BeforeEach
	void setUp() {

		this.lehrerAngemeldet = new Lehrer(new Person("hsagdiqg", "Knoööe Nase"), false,
			Arrays.asList(new Identifier[] { new Identifier(SCHULTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB),
				new Identifier(SCHULKUERZEL_NICHT_ANGEMELDET) }));

		this.lehrerNichtAngemeldet = new Lehrer(new Person("vyxhjcga", "Herr Verpeilt"), true,
			Arrays.asList(new Identifier[] { new Identifier(SCHULKUERZEL_NICHT_ANGEMELDET) }));

		this.lehrerSonderzugangsberechtigungNichtAngemeldet = new Lehrer(new Person("sabljal", "Extra Wurst"), false,
			Arrays.asList(new Identifier[] { new Identifier(SCHULKUERZEL_NICHT_ANGEMELDET) }));
		this.lehrerSonderzugangsberechtigungNichtAngemeldet.erlaubeZugangUnterlagen();

		this.lehrerSonderzugangsberechtigungAngemeldet = new Lehrer(new Person("sabljal", "Extra Wurst"), false,
			Arrays.asList(new Identifier[] { new Identifier(SCHULTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB) }));
		this.lehrerSonderzugangsberechtigungAngemeldet.erlaubeZugangUnterlagen();

		this.lehrerZugangsberechtigungEntzogen = new Lehrer(new Person("bjkabjdb", "Ausge Schlossen"), true,
			Arrays.asList(new Identifier[] { new Identifier(SCHULTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB) }));
		this.lehrerZugangsberechtigungEntzogen.verwehreZugangUnterlagen();

		this.privatpersonAngemeldet = new Privatveranstalter(new Person("chkggd", "Schrumpf Nase"), false,
			Arrays.asList(new Identifier[] { new Identifier(PRIVATTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB) }));

		this.privatpersonSonderzugangsberechtigung = new Privatveranstalter(new Person("bsjjas", "Extra Nase"), true,
			Arrays.asList(new Identifier[] { new Identifier(PRIVATTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB) }));
		this.privatpersonSonderzugangsberechtigung.erlaubeZugangUnterlagen();

		this.privatpersonZugangsberechtigungEntzogen = new Privatveranstalter(new Person("assddsfdf", "Augeschlossen Nase"), false,
			Arrays.asList(new Identifier[] { new Identifier(PRIVATTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB) }));
		this.privatpersonZugangsberechtigungEntzogen.verwehreZugangUnterlagen();

		this.privatpersonNichtAngemeldet = new Privatveranstalter(new Person("hlvhsh", "Frau Verpeilt"), true,
			Arrays.asList(new Identifier("hlvhsh")));

		WettbewerbID wettbewerbId = new WettbewerbID(Integer.valueOf(2020));
		this.wettbewerb = new Wettbewerb(wettbewerbId);

		teilnahmenRepository = Mockito.mock(TeilnahmenRepository.class);

		{

			Schulteilnahme teilnahme = new Schulteilnahme(wettbewerbId, new Identifier(SCHULTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB),
				"Baumschule", new Identifier("asdgqguwgquogowgfuq"));

			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(teilnahme);

			Mockito
				.when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier))
				.thenReturn(Optional.of(teilnahme));
		}

		{

			Privatteilnahme teilnahme = new Privatteilnahme(wettbewerbId,
				new Identifier(PRIVATTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB));

			TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier.createFromTeilnahme(teilnahme);

			Mockito
				.when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier))
				.thenReturn(Optional.of(teilnahme));
		}

		{

			TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
				.withTeilnahmenummer(SCHULKUERZEL_NICHT_ANGEMELDET).withWettbewerbID(wettbewerbId);

			Mockito
				.when(teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier))
				.thenReturn(Optional.empty());
		}

		veranstalterRepository = Mockito.mock(VeranstalterRepository.class);
		wettbewerbService = Mockito.mock(WettbewerbService.class);

		this.service = ZugangUnterlagenService.createForTest(teilnahmenRepository, veranstalterRepository, wettbewerbService);

	}

	@Nested
	class ZugangLehrerTest {

		@Test
		void should_HatZugangReturnFalse_when_WettbewerbErfasst() {

			// Arrange
			assertEquals(WettbewerbStatus.ERFASST, wettbewerb.status());

			// Act + Assert
			assertEquals(false, service.hatZugang(lehrerAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerSonderzugangsberechtigungNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_WettbewerbBeendet() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.BEENDET);

			// Act + Assert
			assertEquals(false, service.hatZugang(lehrerAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerSonderzugangsberechtigungNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_WettbewerbAnmeldung() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.ANMELDUNG);

			// Act + Assert
			assertEquals(false, service.hatZugang(lehrerAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_WettbewerbAnmeldungAberKeineTeilnahme() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.ANMELDUNG);

			// Act + Assert
			assertEquals(false, service.hatZugang(lehrerSonderzugangsberechtigungNichtAngemeldet, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_WettbewerbAnmeldungUndTeilnahme() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.ANMELDUNG);

			// Act + Assert
			assertEquals(true, service.hatZugang(lehrerSonderzugangsberechtigungAngemeldet, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_DownloadLehrer() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.DOWNLOAD_LEHRER);

			// Act + Assert
			assertEquals(false, service.hatZugang(lehrerNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerZugangsberechtigungEntzogen, wettbewerb));
			assertEquals(true, service.hatZugang(lehrerAngemeldet, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_DownloadLehrer() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.DOWNLOAD_LEHRER);

			// Act + Assert
			assertEquals(true, service.hatZugang(lehrerAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerSonderzugangsberechtigungNichtAngemeldet, wettbewerb));
			assertEquals(true, service.hatZugang(lehrerSonderzugangsberechtigungAngemeldet, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_DownloadPrivat() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.DOWNLOAD_PRIVAT);

			// Act + Assert
			assertEquals(false, service.hatZugang(lehrerNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_DownloadPrivat() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.DOWNLOAD_PRIVAT);

			// Act + Assert
			assertEquals(true, service.hatZugang(lehrerAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerSonderzugangsberechtigungNichtAngemeldet, wettbewerb));
			assertEquals(true, service.hatZugang(lehrerSonderzugangsberechtigungAngemeldet, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_LehrerUnknown() {

			// Arrange
			String uuid = "57815815";
			Mockito.when(veranstalterRepository.ofId(new Identifier(uuid))).thenReturn(Optional.empty());

			// Act
			boolean hat = service.hatZugang(uuid);

			// Assert
			assertFalse(hat);
		}

		@Test
		void should_HatZugangReturnFalse_when_NoWettbewerb() {

			// Arrange
			Mockito.when(veranstalterRepository.ofId(new Identifier(lehrerAngemeldet.uuid())))
				.thenReturn(Optional.of(lehrerAngemeldet));
			Mockito.when(wettbewerbService.aktuellerWettbewerb()).thenReturn(Optional.empty());

			// Act
			boolean hat = service.hatZugang(lehrerAngemeldet.uuid());

			// Assert
			assertFalse(hat);
		}

	}

	@Nested
	class ZugangPrivatmenschTest {

		@Test
		void should_HatZugangReturnFalse_when_WettbewerbErfasst() {

			// Arrange
			assertEquals(WettbewerbStatus.ERFASST, wettbewerb.status());

			// Act + Assert
			assertEquals(false, service.hatZugang(privatpersonAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonSonderzugangsberechtigung, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_WettbewerbBeendet() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.BEENDET);

			// Act + Assert
			assertEquals(false, service.hatZugang(privatpersonAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonSonderzugangsberechtigung, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_WettbewerbAnmeldung() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.ANMELDUNG);

			// Act + Assert
			assertEquals(false, service.hatZugang(privatpersonAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_WettbewerbAnmeldung() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.ANMELDUNG);

			// Act + Assert
			assertEquals(true, service.hatZugang(privatpersonSonderzugangsberechtigung, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_DownloadLehrer() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.DOWNLOAD_LEHRER);

			// Act + Assert
			assertEquals(false, service.hatZugang(privatpersonAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_DownloadLehrer() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.DOWNLOAD_LEHRER);

			// Act + Assert
			assertEquals(true, service.hatZugang(privatpersonSonderzugangsberechtigung, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_DownloadPrivat() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.DOWNLOAD_PRIVAT);

			// Act + Assert
			assertEquals(false, service.hatZugang(privatpersonNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_DownloadPrivat() {

			// Arrange
			wettbewerb.withStatus(WettbewerbStatus.DOWNLOAD_PRIVAT);

			// Act + Assert
			assertEquals(true, service.hatZugang(privatpersonAngemeldet, wettbewerb));
			assertEquals(true, service.hatZugang(privatpersonSonderzugangsberechtigung, wettbewerb));

		}

	}

}
