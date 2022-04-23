// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.mail.AdminEmailsConfiguration;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.Lehrer;
import de.egladil.web.mk_gateway.domain.veranstalter.Person;
import de.egladil.web.mk_gateway.domain.veranstalter.Privatveranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagen;
import de.egladil.web.mk_gateway.domain.veranstalter.admin.VeranstalterSuchkriterium;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterVeranstalter;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterVeranstalterVeranstalterMapper;

/**
 * VeranstalterHibernateRepository
 */
@RequestScoped
public class VeranstalterHibernateRepository implements VeranstalterRepository {

	private static final Logger LOG = LoggerFactory.getLogger(VeranstalterHibernateRepository.class);

	@Inject
	AdminEmailsConfiguration mailconfiguration;

	private final PersistenterVeranstalterVeranstalterMapper mapper = new PersistenterVeranstalterVeranstalterMapper();

	@Inject
	EntityManager em;

	/**
	 * @param  entityManager
	 * @return
	 */
	public static VeranstalterHibernateRepository createForIntegrationTest(final EntityManager entityManager) {

		VeranstalterHibernateRepository result = new VeranstalterHibernateRepository();
		result.em = entityManager;
		result.mailconfiguration = AdminEmailsConfiguration.createForTest("hdwinkel@egladil.de,info@egladil.de", 5);
		return result;
	}

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
		Person person = new Person(treffer.getUuid(), treffer.getFullName()).withEmail(treffer.getEmail());

		switch (treffer.getRolle()) {

		case PRIVAT:
			veranstalter = new Privatveranstalter(person, treffer.isNewsletterEmpfaenger(), teilnahmekuerzel);
			break;

		case LEHRER:
			veranstalter = new Lehrer(person, treffer.isNewsletterEmpfaenger(), teilnahmekuerzel);
			break;

		default:
			throw new MkGatewayRuntimeException("unerwartete Rolle " + treffer.getRolle());
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

		return em.find(PersistenterVeranstalter.class, uuid);
	}

	@Override
	public List<Veranstalter> loadVeranstalterByUuids(final List<String> veranstalterUUIDs) {

		List<PersistenterVeranstalter> trefferliste = em.createNamedQuery(PersistenterVeranstalter.FIND_BY_UUIDS_QUERY,
			PersistenterVeranstalter.class).setParameter("uuids", veranstalterUUIDs).getResultList();

		return trefferliste.stream().map(pv -> mapper.apply(pv)).collect(Collectors.toList());
	}

	@Override
	public void addVeranstalter(final Veranstalter veranstalter) {

		PersistenterVeranstalter vorhandener = this.findByUuid(veranstalter.uuid());

		if (vorhandener != null) {

			throw new IllegalStateException("Es gibt bereits einen persistenten Veranstalter " + veranstalter.toString());
		}

		PersistenterVeranstalter persistenterVeranstalter = mapFromVeranstalter(veranstalter);

		em.persist(persistenterVeranstalter);

		LOG.info("Veranstalter {} erfolgreich angelegt.", veranstalter);
	}

	/**
	 * @param  veranstalter
	 * @return
	 */
	PersistenterVeranstalter mapFromVeranstalter(final Veranstalter veranstalter) {

		PersistenterVeranstalter persistenterVeranstalter = new PersistenterVeranstalter();
		persistenterVeranstalter.setImportierteUuid(veranstalter.uuid());
		persistenterVeranstalter.setFullName(veranstalter.fullName());
		persistenterVeranstalter.setEmail(veranstalter.email());
		persistenterVeranstalter.setTeilnahmenummern(veranstalter.persistierbareTeilnahmenummern());
		persistenterVeranstalter.setZugangsberechtigungUnterlagen(veranstalter.zugangUnterlagen());
		persistenterVeranstalter.setRolle(veranstalter.rolle());
		persistenterVeranstalter.setNewsletterEmpfaenger(veranstalter.isNewsletterEmpfaenger());
		return persistenterVeranstalter;
	}

	@Override
	public boolean changeVeranstalter(final Veranstalter veranstalter) throws IllegalStateException {

		PersistenterVeranstalter vorhandener = this.findByUuid(veranstalter.uuid());

		if (vorhandener == null) {

			throw new IllegalStateException(veranstalter.toString() + " ist noch nicht persistiert.");
		}

		this.mergeFromVeranstalter(vorhandener, veranstalter);

		em.persist(vorhandener);

		return true;
	}

	void mergeFromVeranstalter(final PersistenterVeranstalter vorhandener, final Veranstalter veranstalter) {

		if (veranstalter.rolle() != vorhandener.getRolle()) {

			throw new MkGatewayRuntimeException("Die Rolle darf nicht geändert werden! (rolle.persistent="
				+ vorhandener.getRolle() + ", rolle.veranstalter=" + veranstalter.rolle());
		}

		vorhandener.setFullName(veranstalter.fullName());
		vorhandener.setEmail(veranstalter.email());
		vorhandener.setTeilnahmenummern(veranstalter.persistierbareTeilnahmenummern());
		vorhandener.setZugangsberechtigungUnterlagen(veranstalter.zugangUnterlagen());
		vorhandener.setNewsletterEmpfaenger(veranstalter.isNewsletterEmpfaenger());
	}

	@Override
	public void removeVeranstalter(final Veranstalter veranstalter) {

		PersistenterVeranstalter persistenterVeranstalter = em.find(PersistenterVeranstalter.class, veranstalter.person().uuid());

		if (persistenterVeranstalter != null) {

			em.remove(persistenterVeranstalter);
		}
	}

	@Override
	public List<Veranstalter> findVeranstalter(final VeranstalterSuchanfrage suchanfrage) {

		TypedQuery<PersistenterVeranstalter> query = null;

		switch (suchanfrage.getSuchkriterium()) {

		case EMAIL:
			query = em.createNamedQuery(PersistenterVeranstalter.FIND_BY_PARTIAL_EMAIL_QUERY, PersistenterVeranstalter.class);
			break;

		case NAME:
			query = em.createNamedQuery(PersistenterVeranstalter.FIND_BY_PARTIAL_NAME_QUERY, PersistenterVeranstalter.class);
			break;

		case TEILNAHMENUMMER:
			query = em.createNamedQuery(PersistenterVeranstalter.FIND_BY_TEILNAHMENUMMER_QUERY, PersistenterVeranstalter.class);
			break;

		case UUID:
			query = em.createNamedQuery(PersistenterVeranstalter.FIND_BY_PARTIAL_UUID_QUERY, PersistenterVeranstalter.class);
			break;

		case ZUGANGSSTATUS:
			query = em.createNamedQuery(PersistenterVeranstalter.FIND_BY_ZUGANGSSTATUS_QUERY, PersistenterVeranstalter.class);
			break;

		default:
			LOG.error("Unbekanntes Suchkriterium: geben leere Liste zurueck");
			return new ArrayList<>();
		}

		Object value = null;

		if (suchanfrage.getSuchkriterium() == VeranstalterSuchkriterium.ZUGANGSSTATUS) {

			try {

				value = ZugangUnterlagen.valueOf(suchanfrage.getSuchstring().toUpperCase());

			} catch (IllegalArgumentException e) {

				LOG.error(e.getMessage());

				return new ArrayList<>();
			}
		} else {

			value = "%" + suchanfrage.getSuchstring().trim().toLowerCase() + "%";
		}

		List<PersistenterVeranstalter> trefferliste = query.setParameter("suchstring", value).getResultList();

		return trefferliste.stream().map(pv -> mapper.apply(pv)).collect(Collectors.toList());
	}

	@Override
	public List<String> findEmailsNewsletterAbonnenten(final Empfaengertyp empfaengertyp) {

		if (empfaengertyp == Empfaengertyp.TEST) {

			String testempfaenger = this.mailconfiguration.getTestempfaenger();
			LOG.info("Senden Mails an {}", testempfaenger);
			return Arrays.asList(testempfaenger.split(","));
		}

		String stmt = null;

		switch (empfaengertyp) {

		case ALLE:
			stmt = "select v.EMAIL from VERANSTALTER v where v.NEWSLETTER = :newsletter and v.EMAIL IS NOT NULL";
			break;

		default:
			stmt = "select v.EMAIL from VERANSTALTER v where v.NEWSLETTER = :newsletter and v.ROLLE = :rolle and v.EMAIL IS NOT NULL";
			break;
		}

		Query query = em.createNativeQuery(stmt).setParameter("newsletter", 1);

		switch (empfaengertyp) {

		case LEHRER:
			query.setParameter("rolle", Rolle.LEHRER.toString());
			break;

		case PRIVATVERANSTALTER:
			query.setParameter("rolle", Rolle.PRIVAT.toString());
			break;

		default:
			break;
		}

		@SuppressWarnings("unchecked")
		List<Object> trefferliste = query.getResultList();

		return trefferliste.stream().map(o -> o.toString()).collect(Collectors.toList());
	}

}
