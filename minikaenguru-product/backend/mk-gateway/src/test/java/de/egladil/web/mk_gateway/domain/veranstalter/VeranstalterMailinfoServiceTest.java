// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.mail.AdminEmailsConfiguration;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

/**
 * VeranstalterMailinfoServiceTest
 */
@QuarkusTest
public class VeranstalterMailinfoServiceTest {

	@InjectMock
	AdminEmailsConfiguration mailConfiguration;

	@InjectMock
	VeranstalterRepository veranstalterRepository;

	@InjectMock
	VeranstalterService veranstalterService;

	@Inject
	VeranstalterMailinfoService service;

	@Nested
	class GroupMailempfaengerTests {

		@Test
		void shouldGroupReturnSingleGroup_when_groupsizeEqualsListSize() {

			// Arrange
			when(mailConfiguration.groupsize()).thenReturn(Integer.valueOf(3));
			List<String> alleEmpfaenger = Arrays.asList(new String[] { "1@web.de", "2@web.de", "3@web.de" });

			// Act
			List<List<String>> result = service.group(alleEmpfaenger);

			// Assert
			assertEquals(1, result.size());
			List<String> gruppe = result.get(0);
			assertEquals(3, gruppe.size());

			assertTrue(gruppe.contains("1@web.de"));
			assertTrue(gruppe.contains("2@web.de"));
			assertTrue(gruppe.contains("3@web.de"));

		}

		@Test
		void shouldGroupReturnTwoGroups_when_groupsizeGreaterListSize() {

			// Arrange
			when(mailConfiguration.groupsize()).thenReturn(Integer.valueOf(2));
			List<String> alleEmpfaenger = Arrays.asList(new String[] { "1@web.de", "2@web.de", "3@web.de" });

			// Act
			List<List<String>> result = service.group(alleEmpfaenger);

			// Assert
			assertEquals(2, result.size());

			{

				List<String> gruppe = result.get(0);
				assertEquals(2, gruppe.size());

				assertTrue(gruppe.contains("1@web.de"));
				assertTrue(gruppe.contains("2@web.de"));
			}

			{

				List<String> gruppe = result.get(1);
				assertEquals(1, gruppe.size());

				assertTrue(gruppe.contains("3@web.de"));
			}

		}

		@Test
		void shouldGroupReturnTwoGroups_when_groupsizeTwoTimesListSize() {

			// Arrange
			when(mailConfiguration.groupsize()).thenReturn(Integer.valueOf(2));
			List<String> alleEmpfaenger = Arrays.asList(new String[] { "1@web.de", "2@web.de", "3@web.de", "4@web.de" });

			// Act
			List<List<String>> result = service.group(alleEmpfaenger);

			// Assert
			assertEquals(2, result.size());

			{

				List<String> gruppe = result.get(0);
				assertEquals(2, gruppe.size());

				assertTrue(gruppe.contains("1@web.de"));
				assertTrue(gruppe.contains("2@web.de"));
			}

			{

				List<String> gruppe = result.get(1);
				assertEquals(2, gruppe.size());

				assertTrue(gruppe.contains("3@web.de"));
				assertTrue(gruppe.contains("4@web.de"));
			}
		}
	}

	@Nested
	class GetMailempfaengerGroupsTests {

		@Test
		void should_getMailempfaengerGroupsNotCallTheRepository_when_EmpfaengertypTest() {

			// Arrange
			String testempfaenger = "tomate@gmx.de, info@egladil.de";
			when(mailConfiguration.groupsize()).thenReturn(Integer.valueOf(35));
			when(mailConfiguration.getTestempfaenger()).thenReturn(testempfaenger);
			when(veranstalterService.getEmailsNewsletterempfaengerAktuellerWettbewerb())
				.thenReturn(Arrays.asList(new String[] { "tomate@gmx.de", "info@egladil.de" }));

			// Act
			List<List<String>> result = service.getMailempfaengerGroups(Empfaengertyp.TEST, true);

			// Assert
			assertEquals(1, result.size());
			List<String> gruppe = result.get(0);
			assertEquals(2, gruppe.size());

			assertTrue(gruppe.contains("tomate@gmx.de"));
			assertTrue(gruppe.contains("info@egladil.de"));

			verify(veranstalterRepository, never()).findEmailsNewsletterAbonnenten(Empfaengertyp.TEST);
			verify(veranstalterService, never()).getEmailsNewsletterempfaengerAktuellerWettbewerb();
		}

		@Test
		void should_getMailempfaengerGroupsReturnEmptyList_when_KeineMailempfaenger() {

			// Arrange
			Empfaengertyp empfaengertyp = Empfaengertyp.PRIVATVERANSTALTER;

			when(veranstalterRepository.findEmailsNewsletterAbonnenten(empfaengertyp)).thenReturn(Collections.emptyList());

			// Act
			List<List<String>> result = service.getMailempfaengerGroups(empfaengertyp, true);

			// Assert
			assertEquals(0, result.size());

			verify(veranstalterRepository).findEmailsNewsletterAbonnenten(empfaengertyp);
			verify(mailConfiguration, never()).getTestempfaenger();
			verify(mailConfiguration, never()).groupsize();
			verify(veranstalterService, never()).getEmailsNewsletterempfaengerAktuellerWettbewerb();
		}

		@Test
		void should_getMailempfaengerGroupsNotAddTheTestempfaenger_when_EmpfaengertypAlle() {

			// Arrange
			Empfaengertyp empfaengertyp = Empfaengertyp.ALLE;

			List<String> alleEmpfaenger = Arrays.asList(new String[] { "1@web.de", "2@web.de", "3@web.de" });
			List<String> angemeldete = Arrays.asList(new String[] { "1@web.de", "3@web.de" });

			when(mailConfiguration.groupsize()).thenReturn(Integer.valueOf(35));
			when(veranstalterRepository.findEmailsNewsletterAbonnenten(empfaengertyp)).thenReturn(alleEmpfaenger);
			when(veranstalterService.getEmailsNewsletterempfaengerAktuellerWettbewerb()).thenReturn(angemeldete);

			// Act
			List<List<String>> result = service.getMailempfaengerGroups(empfaengertyp, true);

			// Assert
			assertEquals(1, result.size());
			List<String> gruppe = result.get(0);
			assertEquals(2, gruppe.size());

			assertTrue(gruppe.contains("1@web.de"));
			assertFalse(gruppe.contains("2@web.de"));
			assertTrue(gruppe.contains("3@web.de"));

			verify(veranstalterRepository, never()).findEmailsNewsletterAbonnenten(Empfaengertyp.TEST);
			verify(veranstalterService).getEmailsNewsletterempfaengerAktuellerWettbewerb();

		}
	}
}
