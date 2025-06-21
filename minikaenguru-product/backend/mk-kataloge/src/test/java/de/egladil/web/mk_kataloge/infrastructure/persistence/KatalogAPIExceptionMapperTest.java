// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.infrastructure.persistence;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashSet;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_kataloge.domain.error.DuplicateEntityException;
import de.egladil.web.mk_kataloge.infrastructure.error.KatalogAPIExceptionMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.NoContentException;
import jakarta.ws.rs.core.Response;

/**
 * KatalogAPIExceptionMapperTest
 */
@QuarkusTest
public class KatalogAPIExceptionMapperTest {

	@Test
	void should_mapNoContent() {

		Response response = new KatalogAPIExceptionMapper().toResponse(new NoContentException("kein content"));
		assertEquals(204, response.getStatus());
	}

	@Test
	void should_mapNotFound() {

		Response response = new KatalogAPIExceptionMapper().toResponse(new NotFoundException("kein content"));
		assertEquals(404, response.getStatus());
	}

	@Test
	void should_mapIllegalArgumentException() {

		Response response = new KatalogAPIExceptionMapper().toResponse(new IllegalArgumentException("tja"));
		assertEquals(400, response.getStatus());
	}

	@Test
	void should_mapIllegalCVException() {

		Response response = new KatalogAPIExceptionMapper().toResponse(new ConstraintViolationException("tja", new HashSet<>()));
		assertEquals(400, response.getStatus());
	}

	@Test
	void should_mapDuplicateEntryException() {

		Response response = new KatalogAPIExceptionMapper().toResponse(new DuplicateEntityException("tja"));
		assertEquals(409, response.getStatus());
	}

	@Test
	void should_mapWebApplicationExceptionWithPayloadResponse() {

		Response payload = Response.status(404).build();

		Response response = new KatalogAPIExceptionMapper().toResponse(new WebApplicationException(payload));
		assertEquals(404, response.getStatus());
	}

	@Test
	void should_mapWebApplicationExceptionWithoutPayloadResponse() {

		Response response = new KatalogAPIExceptionMapper().toResponse(new WebApplicationException());
		assertEquals(500, response.getStatus());
	}

	@Test
	void should_mapUnexpectedException() {

		Response response = new KatalogAPIExceptionMapper().toResponse(new RuntimeException("schlimmer server error"));
		assertEquals(500, response.getStatus());
	}

}
