// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbStatus;

/**
 * ZugangUnterlagenServiceTest
 */
public class ZugangUnterlagenServiceTest {

	private static final String SCHULTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB = "GGUGUOGO";

	private static final String PRIVATTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB = "TUITITI";

	private static final String SCHULKUERZEL_NICHT_ANGEMELDET = "ZUKGKGK";

	private Lehrer lehrerAngemeldet;

	private Lehrer lehrerSonderzugangsberechtigung;

	private Lehrer lehrerZugangsberechtigungEntzogen;

	private Lehrer lehrerNichtAngemeldet;

	private Privatperson privatpersonSonderzugangsberechtigung;

	private Privatperson privatpersonZugangsberechtigungEntzogen;

	private Privatperson privatpersonAngemeldet;

	private Privatperson privatpersonNichtAngemeldet;

	private ZugangUnterlagenService service;

	private Wettbewerb wettbewerb;

	private TeilnahmenRepository teilnahmenRepository;

	@BeforeEach
	void setUp() {

		this.lehrerAngemeldet = new Lehrer(new Person("hsagdiqg", "Knoööe Nase"),
			Arrays.asList(new Identifier[] { new Identifier(SCHULTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB),
				new Identifier(SCHULKUERZEL_NICHT_ANGEMELDET) }));

		this.lehrerNichtAngemeldet = new Lehrer(new Person("vyxhjcga", "Herr Verpeilt"),
			Arrays.asList(new Identifier[] { new Identifier(SCHULKUERZEL_NICHT_ANGEMELDET) }));

		this.lehrerSonderzugangsberechtigung = new Lehrer(new Person("sabljal", "Extra Wurst"),
			Arrays.asList(new Identifier[] { new Identifier(SCHULTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB) }));
		this.lehrerSonderzugangsberechtigung.erlaubeZugangUnterlagen();

		this.lehrerZugangsberechtigungEntzogen = new Lehrer(new Person("bjkabjdb", "Ausge Schlossen"),
			Arrays.asList(new Identifier[] { new Identifier(SCHULTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB) }));
		this.lehrerZugangsberechtigungEntzogen.verwehreZugangUnterlagen();

		this.privatpersonAngemeldet = new Privatperson(new Person("chkggd", "Schrumpf Nase"),
			Arrays.asList(new Identifier[] { new Identifier(PRIVATTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB) }));

		this.privatpersonSonderzugangsberechtigung = new Privatperson(new Person("bsjjas", "Extra Nase"),
			Arrays.asList(new Identifier[] { new Identifier(PRIVATTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB) }));
		this.privatpersonSonderzugangsberechtigung.erlaubeZugangUnterlagen();

		this.privatpersonZugangsberechtigungEntzogen = new Privatperson(new Person("assddsfdf", "Augeschlossen Nase"),
			Arrays.asList(new Identifier[] { new Identifier(PRIVATTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB) }));
		this.privatpersonZugangsberechtigungEntzogen.verwehreZugangUnterlagen();

		this.privatpersonNichtAngemeldet = new Privatperson(new Person("hlvhsh", "Frau Verpeilt"),
			Arrays.asList(new Identifier("hlvhsh")));

		WettbewerbID wettbewerbId = new WettbewerbID(Integer.valueOf(2020));
		this.wettbewerb = new Wettbewerb(wettbewerbId);

		teilnahmenRepository = Mockito.mock(TeilnahmenRepository.class);

		{

			Schulteilnahme teilnahme = new Schulteilnahme(wettbewerbId, new Identifier(SCHULTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB),
				"Baumschule", new Person("agsufguagfogqoö", "Herr Mann"));
			Mockito
				.when(teilnahmenRepository.ofTeilnahmenummerArtWettbewerb(SCHULTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB,
					Teilnahmeart.SCHULE, wettbewerbId))
				.thenReturn(Optional.of(teilnahme));
		}

		{

			Privatteilnahme teilnahme = new Privatteilnahme(wettbewerbId,
				new Identifier(PRIVATTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB));
			Mockito
				.when(teilnahmenRepository.ofTeilnahmenummerArtWettbewerb(PRIVATTEILNAHMEKUERZEL_AKTUELLER_WETTBEWERB,
					Teilnahmeart.PRIVAT, wettbewerbId))
				.thenReturn(Optional.of(teilnahme));
		}

		{

			Mockito
				.when(teilnahmenRepository.ofTeilnahmenummerArtWettbewerb(SCHULKUERZEL_NICHT_ANGEMELDET, Teilnahmeart.SCHULE,
					wettbewerbId))
				.thenReturn(Optional.empty());
		}

		this.service = ZugangUnterlagenService.createForTest(teilnahmenRepository);

	}

	@Nested
	class ZugangLehrerTest {

		@Test
		void should_HatZugangReturnFalse_when_WettbewerbPausiert() {

			// Arrange
			assertEquals(WettbewerbStatus.PAUSE, wettbewerb.status());

			// Act + Assert
			assertEquals(false, service.hatZugang(lehrerAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerSonderzugangsberechtigung, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_WettbewerbAnmeldung() {

			// Arrange
			wettbewerb.anmeldungFreischalten();

			// Act + Assert
			assertEquals(false, service.hatZugang(lehrerAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_WettbewerbAnmeldung() {

			// Arrange
			wettbewerb.anmeldungFreischalten();

			// Act + Assert
			assertEquals(true, service.hatZugang(lehrerSonderzugangsberechtigung, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_DownloadLehrer() {

			// Arrange
			wettbewerb.downloadFuerLehrerFreischalten();

			// Act + Assert
			assertEquals(false, service.hatZugang(lehrerNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_DownloadLehrer() {

			// Arrange
			wettbewerb.downloadFuerLehrerFreischalten();

			// Act + Assert
			assertEquals(true, service.hatZugang(lehrerAngemeldet, wettbewerb));
			assertEquals(true, service.hatZugang(lehrerSonderzugangsberechtigung, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_DownloadPrivat() {

			// Arrange
			wettbewerb.downloadFuerPrivatpersonenFreischalten();

			// Act + Assert
			assertEquals(false, service.hatZugang(lehrerNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(lehrerZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_DownloadPrivat() {

			// Arrange
			wettbewerb.downloadFuerPrivatpersonenFreischalten();

			// Act + Assert
			assertEquals(true, service.hatZugang(lehrerAngemeldet, wettbewerb));
			assertEquals(true, service.hatZugang(lehrerSonderzugangsberechtigung, wettbewerb));

		}

	}

	@Nested
	class ZugangPrivatmenschTest {

		@Test
		void should_HatZugangReturnFalse_when_WettbewerbPausiert() {

			// Arrange
			assertEquals(WettbewerbStatus.PAUSE, wettbewerb.status());

			// Act + Assert
			assertEquals(false, service.hatZugang(privatpersonAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonSonderzugangsberechtigung, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_WettbewerbAnmeldung() {

			// Arrange
			wettbewerb.anmeldungFreischalten();

			// Act + Assert
			assertEquals(false, service.hatZugang(privatpersonAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_WettbewerbAnmeldung() {

			// Arrange
			wettbewerb.anmeldungFreischalten();

			// Act + Assert
			assertEquals(true, service.hatZugang(privatpersonSonderzugangsberechtigung, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_DownloadLehrer() {

			// Arrange
			wettbewerb.downloadFuerLehrerFreischalten();

			// Act + Assert
			assertEquals(false, service.hatZugang(privatpersonAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_DownloadLehrer() {

			// Arrange
			wettbewerb.downloadFuerLehrerFreischalten();

			// Act + Assert
			assertEquals(true, service.hatZugang(privatpersonSonderzugangsberechtigung, wettbewerb));

		}

		@Test
		void should_HatZugangReturnFalse_when_DownloadPrivat() {

			// Arrange
			wettbewerb.downloadFuerPrivatpersonenFreischalten();

			// Act + Assert
			assertEquals(false, service.hatZugang(privatpersonNichtAngemeldet, wettbewerb));
			assertEquals(false, service.hatZugang(privatpersonZugangsberechtigungEntzogen, wettbewerb));

		}

		@Test
		void should_HatZugangReturnTrue_when_DownloadPrivat() {

			// Arrange
			wettbewerb.downloadFuerPrivatpersonenFreischalten();

			// Act + Assert
			assertEquals(true, service.hatZugang(privatpersonAngemeldet, wettbewerb));
			assertEquals(true, service.hatZugang(privatpersonSonderzugangsberechtigung, wettbewerb));

		}

	}

}
