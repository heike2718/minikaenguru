// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.permissions.RestrictedUrlPath;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * RestrictedUrlPathInMemoryRepositoryTest
 */
public class RestrictedUrlPathInMemoryRepositoryTest {

	private RestrictedUrlPathInMemoryRepository repository = new RestrictedUrlPathInMemoryRepository();

	@Test
	void should_OfPathContainWettbewerb_when_test1() {

		Optional<RestrictedUrlPath> opt = repository.ofPath("/wettbewerb/anmeldungen/anmeldung");

		assertTrue(opt.isPresent());

		RestrictedUrlPath restrictedUrlPath = opt.get();

		assertEquals("/wettbewerb/anmeldungen/anmeldung", restrictedUrlPath.path());

		assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.LEHRER));
		assertTrue(restrictedUrlPath.isAllowedForRolle(Rolle.PRIVAT));
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
	void should_OfPathNotContainVersion() {

		assertFalse(repository.ofPath("/version").isPresent());
	}

}
