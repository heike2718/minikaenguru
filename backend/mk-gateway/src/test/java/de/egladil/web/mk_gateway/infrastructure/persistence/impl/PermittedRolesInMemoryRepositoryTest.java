// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.ws.rs.HttpMethod;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

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
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/lehrer", HttpMethod.GET);

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
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/lehrer/schulen", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_getSchuleDetails_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/lehrer/schulen/FFUFUFUIF/details", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_forAddPrivatteilnahme_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/teilnahmen/privat", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.PRIVAT));

		}

		@Test
		void should_permittedRollen_forAddSchulteilnahme_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/teilnahmen/schule", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_forLoadPrivatteilnahmen_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/teilnahmen/privat", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.PRIVAT));

		}

		@Test
		void should_permittedRollen_forLoadSchulteilnahmen_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/teilnahmen/schulen/ABCDEFGH", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_forLoadSpecialSchulteilnahme_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/teilnahmen/schulen/UZHT65GR", HttpMethod.GET);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_addSchule_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/lehrer/schulen/ABCDEFGH", HttpMethod.POST);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_removeSchule_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/lehrer/schulen/ABCDEFGK", HttpMethod.DELETE);

			// Assert
			assertEquals(1, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));

		}

		@Test
		void should_permittedRollen_getVeranstalterLehrer_beOk() {

			// Act
			List<Rolle> rollen = repository.permittedRollen("/veranstalter/lehrer", HttpMethod.GET);

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
			List<Rolle> rollen = repository.permittedRollen("/teilnahmen/FRTGZ76G3", HttpMethod.GET);

			// Assert
			assertEquals(2, rollen.size());
			assertTrue(rollen.contains(Rolle.LEHRER));
			assertTrue(rollen.contains(Rolle.PRIVAT));
		}
	}

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
	}

	@Nested
	class CommonAPITest {

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
	}
}
