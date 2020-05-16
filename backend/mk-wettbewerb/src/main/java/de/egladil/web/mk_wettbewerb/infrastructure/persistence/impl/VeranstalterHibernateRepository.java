// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.error.MkWettbewerbRuntimeException;
import de.egladil.web.mk_wettbewerb.domain.personen.Lehrer;
import de.egladil.web.mk_wettbewerb.domain.personen.Person;
import de.egladil.web.mk_wettbewerb.domain.personen.Privatperson;
import de.egladil.web.mk_wettbewerb.domain.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities.PersistenterVeranstalter;

/**
 * VeranstalterHibernateRepository
 */
@RequestScoped
public class VeranstalterHibernateRepository implements VeranstalterRepository {

	private static final Logger LOG = LoggerFactory.getLogger(VeranstalterHibernateRepository.class);

	@Inject
	EntityManager em;

	@Override
	public Optional<Veranstalter> ofId(final Identifier identifier) {

		if (identifier == null) {

			throw new IllegalArgumentException("identifier darf nicht null sein");
		}

		PersistenterVeranstalter treffer = this.findByUuid(identifier.identifier());

		if (treffer == null) {

			return Optional.empty();
		}

		List<Identifier> teilnahmekuerzel = new ArrayList<>();

		if (treffer.getTeilnahmenummern() != null) {

			teilnahmekuerzel = Arrays.asList(treffer.getTeilnahmenummern().split(",")).stream()
				.map(str -> new Identifier(str))
				.collect(Collectors.toList());

		}
		Veranstalter veranstalter = null;
		Person person = new Person(treffer.getUuid(), treffer.getFullName());

		switch (treffer.getRolle()) {

		case PRIVAT:
			veranstalter = new Privatperson(person, teilnahmekuerzel);
			break;

		case LEHRER:
			veranstalter = new Lehrer(person, teilnahmekuerzel);
			break;

		default:
			throw new MkWettbewerbRuntimeException("unerwartete Rolle " + treffer.getRolle());
		}

		switch (treffer.getZugangsberechtigungUnterlagen()) {

		case ENTZOGEN:
			veranstalter.verwehreZugangUnterlagen();
			break;

		case ERTEILT:
			veranstalter.erlaubeZugangUnterlagen();
			break;

		default:
			break;
		}

		return Optional.of(veranstalter);
	}

	private PersistenterVeranstalter findByUuid(final String uuid) {

		TypedQuery<PersistenterVeranstalter> query = em
			.createNamedQuery(PersistenterVeranstalter.FIND_BY_UUID_QUERY, PersistenterVeranstalter.class)
			.setParameter("uuid", uuid);

		List<PersistenterVeranstalter> trefferliste = query.getResultList();

		if (trefferliste.isEmpty()) {

			return null;
		}

		if (trefferliste.size() > 1) {

			throw new MkWettbewerbRuntimeException("mehr als ein Veranstalter mit der UUID=" + uuid
				+ " vorhanden. Du solltest mal über Deine Datenbank nachdenken.");
		}

		return trefferliste.get(0);
	}

	@Override
	public void addVeranstalter(final Veranstalter veranstalter) {

		PersistenterVeranstalter vorhandener = this.findByUuid(veranstalter.uuid());

		if (vorhandener != null) {

			throw new IllegalStateException("Es gibt bereits einen persistenten Veranstalter " + veranstalter.toString());
		}

		PersistenterVeranstalter persistenterVeranstalter = new PersistenterVeranstalter();
		persistenterVeranstalter.setImportierteUuid(veranstalter.uuid());
		persistenterVeranstalter.setFullName(veranstalter.fullName());
		persistenterVeranstalter.setTeilnahmenummern(veranstalter.persistierbareTeilnahmekuerzel());
		persistenterVeranstalter.setZugangsberechtigungUnterlagen(veranstalter.zugangUnterlagen());
		persistenterVeranstalter.setRolle(veranstalter.rolle());

		em.persist(persistenterVeranstalter);

		LOG.info("Veranstalter {} erfolgreich angelegt.", veranstalter);
	}

	@Override
	public void changeVeranstalter(final Veranstalter veranstalter) throws IllegalStateException {

		PersistenterVeranstalter vorhandener = this.findByUuid(veranstalter.uuid());

		if (vorhandener == null) {

			throw new IllegalStateException(veranstalter.toString() + " ist noch nicht persistiert.");
		}

		if (veranstalter.rolle() != vorhandener.getRolle()) {

			throw new MkWettbewerbRuntimeException("Die Rolle darf nicht geändert werden! (rolle.persistent="
				+ vorhandener.getRolle() + ", rolle.veranstalter=" + veranstalter.rolle());
		}

		vorhandener.setFullName(veranstalter.fullName());
		vorhandener.setTeilnahmenummern(veranstalter.persistierbareTeilnahmekuerzel());
		vorhandener.setZugangsberechtigungUnterlagen(veranstalter.zugangUnterlagen());

		em.persist(vorhandener);
	}
}
