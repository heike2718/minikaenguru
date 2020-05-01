// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.error.MkWettbewerbRuntimeException;
import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.personen.SchulkollegienRepository;
import de.egladil.web.mk_wettbewerb.domain.personen.Schulkollegium;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.PersistentesSchulkollegium;

/**
 * SchulkollegienHibernateRepository
 */
@RequestScoped
public class SchulkollegienHibernateRepository implements SchulkollegienRepository {

	@Inject
	EntityManager em;

	@Override
	public Optional<Schulkollegium> ofSchulkuerzel(final Identifier identifier) {

		PersistentesSchulkollegium persistentes = this.findByUuid(identifier.identifier());

		if (persistentes == null) {

			return Optional.empty();
		}

		String kollegiumSerialized = persistentes.getKollegium();
		Person[] personen = this.deserializeKollegen(kollegiumSerialized);
		Schulkollegium result = new Schulkollegium(identifier, personen);

		return Optional.of(result);
	}

	private PersistentesSchulkollegium findByUuid(final String uuid) {

		TypedQuery<PersistentesSchulkollegium> query = em
			.createNamedQuery(PersistentesSchulkollegium.FIND_BY_UUID_QUERY, PersistentesSchulkollegium.class)
			.setParameter("uuid", uuid);

		List<PersistentesSchulkollegium> trefferliste = query.getResultList();

		if (trefferliste.isEmpty()) {

			return null;
		}

		if (trefferliste.size() > 1) {

			throw new MkWettbewerbRuntimeException("mehr als ein Schulkollegium mit der UUID=" + uuid
				+ " vorhanden. Du solltest mal über Deine Datenbank nachdenken.");
		}

		return trefferliste.get(0);

	}

	Person[] deserializeKollegen(final String serializedKollegen) {

		try {

			return new ObjectMapper().readValue(serializedKollegen, Person[].class);
		} catch (JsonProcessingException e) {

			throw new MkWettbewerbRuntimeException("Konnte personen nicht deserialisieren: " + e.getMessage(), e);
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

		if (persistentes == null) {

			throw new IllegalStateException(
				"Ein Schulkollegium mit der UUID '" + uuid + "' gibt es noch nict");
		}

		persistentes.setKollegium(schulkollegium.personenAlsJSON());
		em.persist(persistentes);
	}

	@Override
	@Transactional(value = TxType.REQUIRES_NEW)
	public void replaceKollegien(final List<Schulkollegium> geaenderteSchulkollegien) {

		for (Schulkollegium schulkollegium : geaenderteSchulkollegien) {

			this.replaceKollegen(schulkollegium);
		}

	}

}
