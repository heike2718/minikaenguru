// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.Versandinformation;
import de.egladil.web.mk_gateway.domain.mail.VersandinformationenRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenteVersandinfo;

/**
 * VersandinformationenHibernateRepository
 */
@RequestScoped
public class VersandinformationenHibernateRepository implements VersandinformationenRepository {

	@Inject
	EntityManager em;

	public static final VersandinformationenHibernateRepository createForTest(final EntityManager em) {

		VersandinformationenHibernateRepository result = new VersandinformationenHibernateRepository();
		result.em = em;
		return result;
	}

	@Override
	public List<Versandinformation> findForNewsletter(final Identifier newsletterID) {

		List<PersistenteVersandinfo> trefferliste = this.findVersandinfosWithNewsletterID(newsletterID.identifier());

		if (trefferliste.isEmpty()) {

			return new ArrayList<>();
		}

		return trefferliste.stream().map(t -> mapFromDB(t)).collect(Collectors.toList());
	}

	@Override
	public Optional<Versandinformation> ofId(final Identifier identifier) {

		Optional<PersistenteVersandinfo> existing = this.findTheExistingVersandinfo(identifier.identifier());

		return existing.isEmpty() ? Optional.empty() : Optional.of(mapFromDB(existing.get()));

	}

	@Override
	public Versandinformation addVersandinformation(final Versandinformation versandinformation) {

		if (versandinformation.identifier() != null) {

			Optional<PersistenteVersandinfo> optExisting = this
				.findTheExistingVersandinfo(versandinformation.identifier().identifier());

			if (optExisting.isPresent()) {

				throw new IllegalStateException(
					"Versandinformation mit UUID=" + versandinformation.identifier() + " existiert schon");
			}
		}

		PersistenteVersandinfo persistenteVersandinfo = new PersistenteVersandinfo();
		copyAttributesButUuid(persistenteVersandinfo, versandinformation);

		em.persist(persistenteVersandinfo);

		return mapFromDB(persistenteVersandinfo);
	}

	@Override
	public Versandinformation updateVersandinformation(final Versandinformation versandinformation) {

		Optional<PersistenteVersandinfo> optExisting = this
			.findTheExistingVersandinfo(versandinformation.identifier().identifier());

		if (optExisting.isEmpty()) {

			throw new IllegalStateException("Versandinformation mit UUID=" + versandinformation.identifier() + " existiert nicht");
		}

		PersistenteVersandinfo existing = optExisting.get();

		copyAttributesButUuid(existing, versandinformation);

		PersistenteVersandinfo merged = em.merge(existing);

		return mapFromDB(merged);
	}

	@Override
	public int removeAll(final Identifier newsletterID) {

		List<PersistenteVersandinfo> trefferliste = this.findVersandinfosWithNewsletterID(newsletterID.identifier());

		for (PersistenteVersandinfo entity : trefferliste) {

			em.remove(entity);
		}

		return trefferliste.size();
	}

	private List<PersistenteVersandinfo> findVersandinfosWithNewsletterID(final String newsletterID) {

		return em
			.createNamedQuery(PersistenteVersandinfo.FIND_FOR_NEWSLETTER, PersistenteVersandinfo.class)
			.setParameter("newsletterID", newsletterID).getResultList();
	}

	private Optional<PersistenteVersandinfo> findTheExistingVersandinfo(final String uuid) {

		List<PersistenteVersandinfo> trefferliste = em
			.createNamedQuery(PersistenteVersandinfo.FIND_BY_UUID, PersistenteVersandinfo.class)
			.setParameter("uuid", uuid).getResultList();

		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));
	}

	Versandinformation mapFromDB(final PersistenteVersandinfo fromDB) {

		String versandBegonnenAm = fromDB.getVersandBegonnenAm() == null ? null
			: CommonTimeUtils.format(fromDB.getVersandBegonnenAm());
		String versandBeendetAm = fromDB.getVersandBeendetAm() == null ? null
			: CommonTimeUtils.format(fromDB.getVersandBeendetAm());

		return new Versandinformation()
			.withAnzahlAktuellVersendet(fromDB.getAnzahlVersendet())
			.withAnzahlEmpaenger(fromDB.getAnzahlEmpfaenger())
			.withEmpfaengertyp(fromDB.getEmpfaengertyp())
			.withIdentifier(new Identifier(fromDB.getUuid()))
			.withNewsletterID(new Identifier(fromDB.getNewsletterID()))
			.withVersandBegonnenAm(versandBegonnenAm)
			.withVersandBeendetAm(versandBeendetAm);
	}

	void copyAttributesButUuid(final PersistenteVersandinfo persistente, final Versandinformation versandinfo) {

		LocalDateTime versandBegonnen = StringUtils.isBlank(versandinfo.versandBegonnenAm()) ? null
			: CommonTimeUtils.parseToLocalDateTime(versandinfo.versandBegonnenAm());

		LocalDateTime versandBeendet = StringUtils.isBlank(versandinfo.versandBeendetAm()) ? null
			: CommonTimeUtils.parseToLocalDateTime(versandinfo.versandBeendetAm());

		persistente.setAnzahlEmpfaenger(versandinfo.anzahlEmpaenger());
		persistente.setAnzahlVersendet(versandinfo.anzahlAktuellVersendet());
		persistente.setEmpfaengertyp(versandinfo.empfaengertyp());
		persistente.setNewsletterID(versandinfo.newsletterID().identifier());
		persistente.setVersandBegonnenAm(versandBegonnen);
		persistente.setVersandBeendetAm(versandBeendet);
		persistente.setVersandMitFehlern(StringUtils.isNotBlank(versandinfo.fehlermeldung()));
	}

}
