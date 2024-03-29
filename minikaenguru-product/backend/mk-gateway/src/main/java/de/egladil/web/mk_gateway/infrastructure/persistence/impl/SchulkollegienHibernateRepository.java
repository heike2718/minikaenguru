// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.veranstalter.Kollege;
import de.egladil.web.mk_gateway.domain.veranstalter.SchulkollegienRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.Schulkollegium;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistentesSchulkollegium;

/**
 * SchulkollegienHibernateRepository
 */
@RequestScoped
public class SchulkollegienHibernateRepository implements SchulkollegienRepository {

	@Inject
	EntityManager em;

	public static SchulkollegienHibernateRepository createForIntegrationTest(final EntityManager em) {

		SchulkollegienHibernateRepository result = new SchulkollegienHibernateRepository();
		result.em = em;
		return result;
	}

	@Override
	public Optional<Schulkollegium> ofSchulkuerzel(final Identifier identifier) {

		PersistentesSchulkollegium persistentes = this.findByUuid(identifier.identifier());

		if (persistentes == null) {

			return Optional.empty();
		}

		String kollegiumSerialized = persistentes.getKollegium();
		Kollege[] kollegen = this.deserializeKollegen(kollegiumSerialized);
		Schulkollegium result = new Schulkollegium(identifier, kollegen);

		return Optional.of(result);
	}

	private PersistentesSchulkollegium findByUuid(final String uuid) {

		TypedQuery<PersistentesSchulkollegium> query = em
			.createNamedQuery(PersistentesSchulkollegium.FIND_BY_UUID, PersistentesSchulkollegium.class)
			.setParameter("uuid", uuid);

		List<PersistentesSchulkollegium> trefferliste = query.getResultList();

		if (trefferliste.isEmpty()) {

			return null;
		}

		if (trefferliste.size() > 1) {

			throw new MkGatewayRuntimeException("mehr als ein Schulkollegium mit der UUID=" + uuid
				+ " vorhanden. Du solltest mal über Deine Datenbank nachdenken.");
		}

		return trefferliste.get(0);

	}

	Kollege[] deserializeKollegen(final String serializedKollegen) {

		try {

			return new ObjectMapper().readValue(serializedKollegen, Kollege[].class);
		} catch (JsonProcessingException e) {

			throw new MkGatewayRuntimeException("Konnte personen nicht deserialisieren: " + e.getMessage(), e);
		}
	}

	@Override
	public void addKollegium(final Schulkollegium schulkollegium) {

		String uuid = schulkollegium.schulkuerzel().identifier();
		PersistentesSchulkollegium persistentes = this.findByUuid(uuid);

		if (persistentes != null) {

			throw new IllegalStateException(
				"Es gibt bereits ein Schulkollegium mit der UUID '" + uuid + "'");
		}

		PersistentesSchulkollegium neues = new PersistentesSchulkollegium();
		neues.setImportierteUuid(uuid);
		neues.setKollegium(schulkollegium.personenAlsJSON());

		em.persist(neues);

	}

	@Override
	public void replaceKollegen(final Schulkollegium schulkollegium) {

		String uuid = schulkollegium.schulkuerzel().identifier();

		PersistentesSchulkollegium persistentes = this.findByUuid(uuid);

		if (persistentes != null) {

			persistentes.setKollegium(schulkollegium.personenAlsJSON());
			em.persist(persistentes);
		}
	}

	@Override
	public void deleteKollegium(final Schulkollegium schulkollegium) {

		String uuid = schulkollegium.schulkuerzel().identifier();

		PersistentesSchulkollegium persistentes = this.findByUuid(uuid);

		if (persistentes != null) {

			em.remove(persistentes);
		}
	}

}
