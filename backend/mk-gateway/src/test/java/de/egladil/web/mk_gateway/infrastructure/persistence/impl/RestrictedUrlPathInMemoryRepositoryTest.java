// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.permissions.RestrictedUrlPath;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * RestrictedUrlPathInMemoryRepositoryTest
 */
public class RestrictedUrlPathInMemoryRepositoryTest {

	private RestrictedUrlPathInMemoryRepository repository = new RestrictedUrlPathInMemoryRepository();

	@Nested
	class MkWettbewerbPathsTest {

		@Test
		void should_OfPathContainTeilnahmePrivat() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wettbewerb/teilnahmen/privat");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wettbewerb/teilnahmen/privat", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainTeilnahmeSchule() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wettbewerb/teilnahmen/schule");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wettbewerb/teilnahmen/schule", restrictedUrlPath.path());

			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainTeilnahmeSchulen() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wettbewerb/teilnahmen/schulen/UZHT65GR");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wettbewerb/teilnahmen/schulen/*", restrictedUrlPath.path());

			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainLaender_when_statistikLaender() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/statistik/laender/DE-HE/2017");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/statistik/laender/*/*", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainSchulen_when_statistikSchulen() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/statistik/schulen/H635FRZ6/2017");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();
			assertEquals("/statistik/schulen/*/*", restrictedUrlPath.path());

			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainPrivat_when_statistikSchulen() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/statistik/privat/H635FRZ6/2017");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/statistik/privat/*/*", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainSchulen() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wettbewerb/lehrer/schulen");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wettbewerb/lehrer/schulen", restrictedUrlPath.path());

			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainSchuldetails() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wettbewerb/lehrer/schulen/FFUFUFUIF/details");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wettbewerb/lehrer/schulen/*/details", restrictedUrlPath.path());

			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainZugangsstatus() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wettbewerb/veranstalter/zugangsstatus");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wettbewerb/veranstalter/zugangsstatus", restrictedUrlPath.path());

			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainVeranstalterPrivat() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wettbewerb/veranstalter/privat");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wettbewerb/veranstalter/privat", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathNotContainVeranstalterLehrer() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wettbewerb/veranstalter/lehrer");

			assertTrue(opt.isEmpty());
		}
	}

	@Nested
	class MkWettbewerbAdminPathsTest {

		@Test
		void should_OfPathContainWettbewerbe() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/wettbewerbe");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/wettbewerbe", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainWettbewerbOhneJahr() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/wettbewerbe/wettbewerb");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/wettbewerbe/wettbewerb", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainWettbewerbMitJahr() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/wettbewerbe/wettbewerb/2017");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/wettbewerbe/wettbewerb/*", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainWettbewerbChangeStatus() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/wettbewerbe/wettbewerb/status");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/wettbewerbe/wettbewerb/status", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainLaender() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/kataloge/laender");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/kataloge/laender", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainOrte() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/kataloge/orte");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/kataloge/orte", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainSchulen() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/kataloge/schulen");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/kataloge/schulen", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainLaenderWithKuerzel() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/kataloge/laender/DE-BY");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/kataloge/laender/*", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainOrteWithKuerzel() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/kataloge/orte/Z7TFRTZ6");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/kataloge/orte/*", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainSchulenWithKuerzel() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/kataloge/schulen/Z7TFRTZ6");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/kataloge/schulen/*", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainKuerzel() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/kuerzel");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/kuerzel", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

		@Test
		void should_OfPathContainUploadSchulen() {

			Optional<RestrictedUrlPath> opt = repository.ofPath("/wb-admin/upload/schulen");

			assertTrue(opt.isPresent());

			RestrictedUrlPath restrictedUrlPath = opt.get();

			assertEquals("/wb-admin/upload/schulen", restrictedUrlPath.path());

			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
			assertFalse(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
			assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.ADMIN));
		}

	}

	@Test
	void should_OfPathNotContainVersion() {

		assertFalse(repository.ofPath("/version").isPresent());
	}

}
