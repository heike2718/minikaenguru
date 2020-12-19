// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.mail.Newsletter;
import de.egladil.web.mk_gateway.domain.mail.NewsletterRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterNewsletter;

/**
 * NewsletterHibernateRepository
 */
@RequestScoped
public class NewsletterHibernateRepository implements NewsletterRepository {

	private static final Logger LOG = LoggerFactory.getLogger(NewsletterHibernateRepository.class);

	@Inject
	EntityManager em;

	public static NewsletterHibernateRepository createForIntegrationTest(final EntityManager em) {

		NewsletterHibernateRepository result = new NewsletterHibernateRepository();
		result.em = em;
		return result;
	}

	@Override
	public List<Newsletter> loadAll() {

		List<PersistenterNewsletter> trefferliste = em
			.createNamedQuery(PersistenterNewsletter.LOAD_ALL, PersistenterNewsletter.class).getResultList();

		if (trefferliste.isEmpty()) {

			return new ArrayList<>();
		}

		return trefferliste.stream().map(db -> mapFromDB(db)).collect(Collectors.toList());
	}

	@Override
	public Optional<Newsletter> ofId(final Identifier id) {

		Optional<PersistenterNewsletter> opt = this.findTheExistingNewsletter(id.identifier());

		return opt.isEmpty() ? Optional.empty() : Optional.of(mapFromDB(opt.get()));
	}

	@Override
	public Newsletter addNewsletter(final Newsletter newsletter) {

		if (!newsletter.isNeu()) {

			throw new IllegalStateException("Der newsletter mit der UUID " + newsletter.identifier() + " ist existiert bereits.");
		}

		PersistenterNewsletter neuer = new PersistenterNewsletter();
		copyAttributesButUuid(neuer, newsletter);

		em.persist(neuer);

		return mapFromDB(neuer);
	}

	@Override
	public Newsletter updateNewsletter(final Newsletter newsletter) {

		Optional<PersistenterNewsletter> optExisting = findTheExistingNewsletter(newsletter.identifier().identifier());

		if (optExisting.isEmpty()) {

			throw new MkGatewayRuntimeException(
				"Newsletter mit UUID = " + newsletter.identifier() + " existiert nicht oder nicht mehr");
		}

		PersistenterNewsletter persistenter = optExisting.get();

		copyAttributesButUuid(persistenter, newsletter);

		PersistenterNewsletter merged = em.merge(persistenter);

		return mapFromDB(merged);
	}

	@Override
	public void removeNewsletter(final Identifier identifier) {

		Optional<PersistenterNewsletter> optExisting = findTheExistingNewsletter(identifier.identifier());

		if (optExisting.isEmpty()) {

			LOG.info("Newsletter mit UUID = " + identifier + " existiert nicht oder nicht mehr");
			return;
		}

		PersistenterNewsletter persistenter = optExisting.get();
		em.remove(persistenter);

	}

	private Optional<PersistenterNewsletter> findTheExistingNewsletter(final String uuid) {

		List<PersistenterNewsletter> trefferliste = em
			.createNamedQuery(PersistenterNewsletter.FIND_BY_UUID, PersistenterNewsletter.class)
			.setParameter("uuid", uuid)
			.getResultList();

		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));

	}

	Newsletter mapFromDB(final PersistenterNewsletter fromDB) {

		return new Newsletter().withIdentifier(new Identifier(fromDB.getUuid()))
			.withBetreff(fromDB.getBetreff())
			.withText(fromDB.getText());
	}

	void copyAttributesButUuid(final PersistenterNewsletter persistenter, final Newsletter newsletter) {

		persistenter.setBetreff(newsletter.betreff());
		persistenter.setText(newsletter.text());
	}
}
