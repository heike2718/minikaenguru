// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import javax.ws.rs.HttpMethod;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * PermittedRolesInMemoryRepositoryTest
 */
public class PermittedRolesInMemoryRepositoryTest {

	private PermittedRolesInMemoryRepository repository = new PermittedRolesInMemoryRepository();

	@Nested
	class VeranstalterAPITest {

		@Test
		void should_permittedRollen_getWettbewerbZugangsstatus_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/lehrer", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_forChangeStatusNewsletter_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/newsletter", HttpMethod.PUT);

			// Assert
			assertEquals(2, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
			assertTrue(rollen.contains(Rolle.PRIVAT));

		}

		@Test
		void should_permittedRollen_getVeranstalterPrivat_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/privat", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.PRIVAT));

		}

		@Test
		void should_permittedRollen_loadSchulen_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/lehrer/schulen", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_deleteAllKlassen_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/lehrer/schulen/JUHZTGF5/klassen", HttpMethod.DELETE);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_getSchuleDetails_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/lehrer/schulen/FFUFUFUIF/details", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_forAddPrivatteilnahme_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/teilnahmen/privat", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.PRIVAT));

		}

		@Test
		void should_permittedRollen_forAddSchulteilnahme_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/teilnahmen/schule", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_forLoadPrivatteilnahmen_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/teilnahmen/privat", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.PRIVAT));

		}

		@Test
		void should_permittedRollen_forLoadSchulteilnahmen_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/teilnahmen/schulen/ABCDEFGH", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_forLoadSpecialSchulteilnahme_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/teilnahmen/schulen/UZHT65GR", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_addSchule_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/lehrer/schulen/ABCDEFGH", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_removeSchule_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/lehrer/schulen/ABCDEFGK", HttpMethod.DELETE);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_getVeranstalterLehrer_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/lehrer", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_getStatistikLaender_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/statistik/laender/DE-HE/2017", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_downloadAdv_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/adv/6XOA2A11", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_createAdv_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/adv", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_getStatistikSchule_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/statistik/SCHULE/FRTGZG3/2017", HttpMethod.GET);

			// Assert
			assertEquals(2, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
			assertTrue(rollen.contains(Rolle.PRIVAT));

		}

		@Test
		void should_permittedRollen_loadAnonymeTeilnahmen_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/teilnahmen/veranstalter/FRTGZ76G3", HttpMethod.GET);

			// Assert
			assertEquals(2, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
			assertTrue(rollen.contains(Rolle.PRIVAT));
		}

		@Test
		void should_permittedRollen_kinderDelete_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/kinder/FRTGZ76G3", HttpMethod.DELETE);

			// Assert
			assertEquals(2, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
			assertTrue(rollen.contains(Rolle.PRIVAT));
		}

		@Test
		void should_permittedRollen_kinderGet_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/kinder/GGUIIFGTU", HttpMethod.GET);

			// Assert
			assertEquals(2, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
			assertTrue(rollen.contains(Rolle.PRIVAT));
		}

		@Test
		void should_permittedRollen_kinderPost_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/kinder", HttpMethod.POST);

			// Assert
			assertEquals(2, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
			assertTrue(rollen.contains(Rolle.PRIVAT));
		}

		@Test
		void should_permittedRollen_kinderDuplikatePost_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/kinder/duplikate", HttpMethod.POST);

			// Assert
			assertEquals(2, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
			assertTrue(rollen.contains(Rolle.PRIVAT));
		}

		@Test
		void should_permittedRollen_kinderPut_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/kinder", HttpMethod.PUT);

			// Assert
			assertEquals(2, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
			assertTrue(rollen.contains(Rolle.PRIVAT));
		}

		@Test
		void should_permittedRollen_klassenDelete_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/klassen/FRTGZ76G3", HttpMethod.DELETE);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
		}

		@Test
		void should_permittedRollen_klassenGet_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/klassen/GGUIIFGTU", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
		}

		@Test
		void should_permittedRollen_klassenPost_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/klassen", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
		}

		@Test
		void should_permittedRollen_klassenDuplikatePost_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/klassen/duplikate", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
		}

		@Test
		void should_permittedRollen_klassenPut_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/klassen", HttpMethod.PUT);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
		}

		@Test
		void should_permittedRollen_klassenImportreport_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/klassen/importreport/zuitz-9698", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
		}

		@Test
		void should_permittedRollen_loesungszettel_beOk() {

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen("/loesungszettel/abcdef-012345", HttpMethod.GET);

				// Assert
				assertEquals(2, rollen.size());
				assertTrue(rollen.contains(Rolle.LEHRER));
				assertTrue(rollen.contains(Rolle.PRIVAT));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen("/loesungszettel/abcdef-012345", HttpMethod.DELETE);

				// Assert
				assertEquals(2, rollen.size());
				assertTrue(rollen.contains(Rolle.LEHRER));
				assertTrue(rollen.contains(Rolle.PRIVAT));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen("/loesungszettel", HttpMethod.POST);

				// Assert
				assertEquals(2, rollen.size());
				assertTrue(rollen.contains(Rolle.LEHRER));
				assertTrue(rollen.contains(Rolle.PRIVAT));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen("/loesungszettel", HttpMethod.PUT);

				// Assert
				assertEquals(2, rollen.size());
				assertTrue(rollen.contains(Rolle.LEHRER));
				assertTrue(rollen.contains(Rolle.PRIVAT));
			}

		}

		@Test
		void should_permittedRollen_urkunden_beOk() {

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen("/urkunden/urkunde", HttpMethod.POST);

				// Assert
				assertEquals(2, rollen.size());
				assertTrue(rollen.contains(Rolle.LEHRER));
				assertTrue(rollen.contains(Rolle.PRIVAT));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen("/urkunden/schule", HttpMethod.POST);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.LEHRER));
			}
		}

		@Test
		void should_permittedRollen_unterlagenSchulen_beOk() {

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen("/unterlagen/schulen/de", HttpMethod.GET);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.LEHRER));
			}
		}

		@Test
		void should_permittedRollen_unterlagenPrivat_beOk() {

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen("/unterlagen/privat/en", HttpMethod.GET);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.PRIVAT));
			}
		}

		@Test
		void should_permittedRollen_uploadsKlassenlisten_beOk() {

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen("/uploads/klassenliste/2021/DE-HE/5RTG67F", HttpMethod.POST);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.LEHRER));
			}
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

	@Nested
	class AdminAPITest {

		@Test
		void should_permittedRollen_sucheVeranstalter_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/veranstalter/suche", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_changeZugangsstatus_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/veranstalter/80c8052f/zugangsstatus", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_deactivateNewsletter_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/veranstalter/80c8052f/newsletter", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getWettbewerbe_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/wettbewerbe", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_addWettbewerbe_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/wettbewerbe/wettbewerb", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_changeWettbewerbe_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/wettbewerbe/wettbewerb", HttpMethod.PUT);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getSingleWettbewerb_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/wettbewerbe/wettbewerb/2017", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getStatusWettbewerb_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/wettbewerbe/wettbewerb/status", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_loadLaender_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/kataloge/laender", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_changeLand_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/kataloge/laender", HttpMethod.PUT);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_changeOrt_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/kataloge/orte", HttpMethod.PUT);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_addSchule_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/kataloge/schulen", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_changeSchule_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/kataloge/schulen", HttpMethod.PUT);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_loadOrteInLand_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/kataloge/laender/DE-TH/orte", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_loadSchulenInOrt_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/kataloge/orte/HTFRDGH7/schulen", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_createKuerzel_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/kataloge/kuerzel", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_globaleKatalogsuche_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/kataloge/suche/global/ort?search=Naum", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_uploadSchuleCsv_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/upload/schulen/csv", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_countUploads_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/uploads/size", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getUploads_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/uploads", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_downloadUploadedFile_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/uploads/abcde/file", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_downloadFehlerreport_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/uploads/abcde/fehlerreport", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_uploadAuswertung_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/uploads/auswertung/2020/DE-ST/HZTFGRE5", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_changeAktuelleMeldung_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/meldungen/aktuelle-meldung", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_deleteAktuelleMeldung_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/meldungen/aktuelle-meldung", HttpMethod.DELETE);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getSchule_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/schulen/KUERZEL", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getSchuleUploadsKlassenlisten_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/schulen/KUERZEL/uploads/klassenlisten", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getPrivatteilnahmen_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/privatteilnahmen/KUERZEL", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getStatistikSchule_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/statistik/SCHULE/FRTGZG3/2017", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getStatistikAnmeldungen_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/statistik/anmeldungen", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getEventsFromDate_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/events?date=2020", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_newsletter_beOk() {

			String path = "/admin/newsletters";

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen(path, HttpMethod.GET);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.ADMIN));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen(path, HttpMethod.POST);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.ADMIN));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen(path, HttpMethod.PUT);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.ADMIN));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen(path + "/4353-37", HttpMethod.DELETE);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.ADMIN));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen(path + "/mustertext/abddef-012345",
					HttpMethod.POST);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.ADMIN));
			}

		}

		@Test
		void should_permittedRollen_mustertexte_beOk() {

			final String path = "/admin/mail/mustertexte";

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen(path, HttpMethod.GET);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.ADMIN));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen(path, HttpMethod.POST);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.ADMIN));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen(path, HttpMethod.PUT);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.ADMIN));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen(path + "/4353-37", HttpMethod.DELETE);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.ADMIN));
			}

			{

				// Act
				List<Rolle> rollen = repository.permittedRollen(path + "/newsletter/abddef-012345",
					HttpMethod.POST);

				// Assert
				assertEquals(1, rollen.size());
				assertTrue(rollen.contains(Rolle.ADMIN));
			}

		}

		@Test
		void should_permittedRollen_postVersand_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/newsletterversand", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getVersandInfo_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/newsletterversand/d34f5-10a5d", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getLoesungszettel_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/loesungszettel/2017", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}

		@Test
		void should_permittedRollen_getAnzahlLoesungszettel_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/admin/loesungszettel/2017/size", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.ADMIN));

		}
	}

	@Nested
	class OpenDataTests {

		@Test
		void should_permittedRollen_notContain_readVersion() {

			assertTrue(repository.permittedRollen("/version", HttpMethod.GET).isEmpty());
		}

		@Test
		void should_permittedRollen_notContain_readAktuelleMeldungen() {

			assertTrue(repository.permittedRollen("/meldungen/aktuelle-meldung", HttpMethod.GET).isEmpty());
		}

		@Test
		void should_permittedRollen_notContain_readAktuellenWettbewerb() {

			assertTrue(repository.permittedRollen("/wettbewerb/aktueller", HttpMethod.GET).isEmpty());
		}

		@Test
		void should_permittedRollen_notContain_readStatistiAnmeldungen() {

			assertTrue(repository.permittedRollen("/open-data/statistik/anmeldungen", HttpMethod.GET).isEmpty());
		}

		@Test
		void should_permittedRollen_notContain_readAuswertungXml() {

			for (Klassenstufe klassenstufe : Klassenstufe.values()) {

				assertTrue(repository
					.permittedRollen("/open-data/statistik/2019/" + klassenstufe.toString() + "/xml", HttpMethod.GET).isEmpty(),
					"Fehler bei Klassenstufe " + klassenstufe);
			}
		}

		@Test
		void should_permittedRollen_notContain_readStatistikMediane() {

			assertTrue(repository.permittedRollen("/open-data/statistik/2017/mediane", HttpMethod.GET).isEmpty());
		}

		@Test
		void should_permittedRollen_notContain_readStatistikPdf() {

			assertTrue(repository.permittedRollen("/open-data/statistik/2017/pdf", HttpMethod.GET).isEmpty());
		}

		@Test
		void should_permittedRollen_notContain_readProzentrang() {

			assertTrue(repository.permittedRollen("/open-data/statistik/prozentrang/2020/EINS/5000", HttpMethod.GET).isEmpty());
		}
	}
}
