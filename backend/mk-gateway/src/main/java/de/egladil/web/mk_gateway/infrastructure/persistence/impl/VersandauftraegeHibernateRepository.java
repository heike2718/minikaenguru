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

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.StatusAuslieferung;
import de.egladil.web.mk_gateway.domain.mail.Versandauftrag;
import de.egladil.web.mk_gateway.domain.mail.VersandauftraegeRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterVersandauftrag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

/**
 * VersandauftraegeHibernateRepository
 */
@RequestScoped
public class VersandauftraegeHibernateRepository implements VersandauftraegeRepository {

	@Inject
	EntityManager em;

	public static final VersandauftraegeHibernateRepository createForTest(final EntityManager em) {

		VersandauftraegeHibernateRepository result = new VersandauftraegeHibernateRepository();
		result.em = em;
		return result;
	}

	@Override
	public List<Versandauftrag> findForNewsletter(final Identifier newsletterID) {

		List<PersistenterVersandauftrag> trefferliste = this.findVersandinfosWithNewsletterID(newsletterID.identifier());

		if (trefferliste.isEmpty()) {

			return new ArrayList<>();
		}

		return trefferliste.stream().map(t -> mapFromDB(t)).collect(Collectors.toList());
	}

	@Override
	public Optional<Versandauftrag> ofId(final Identifier identifier) {

		Optional<PersistenterVersandauftrag> existing = this.findTheExistingVersandinfo(identifier.identifier());

		return existing.isEmpty() ? Optional.empty() : Optional.of(mapFromDB(existing.get()));

	}

	@Override
	public Versandauftrag addVersandinformation(final Versandauftrag versandinformation) {

		if (versandinformation.identifier() != null) {

			Optional<PersistenterVersandauftrag> optExisting = this
				.findTheExistingVersandinfo(versandinformation.identifier().identifier());

			if (optExisting.isPresent()) {

				throw new IllegalStateException(
					"Versandauftrag mit UUID=" + versandinformation.identifier() + " existiert schon");
			}
		}

		PersistenterVersandauftrag persistenteVersandinfo = new PersistenterVersandauftrag();
		copyAttributesButUuid(persistenteVersandinfo, versandinformation);

		em.persist(persistenteVersandinfo);

		return mapFromDB(persistenteVersandinfo);
	}

	@Override
	public Versandauftrag updateVersandinformation(final Versandauftrag versandinformation) {

		Optional<PersistenterVersandauftrag> optExisting = this
			.findTheExistingVersandinfo(versandinformation.identifier().identifier());

		if (optExisting.isEmpty()) {

			throw new IllegalStateException("Versandauftrag mit UUID=" + versandinformation.identifier() + " existiert nicht");
		}

		PersistenterVersandauftrag existing = optExisting.get();

		copyAttributesButUuid(existing, versandinformation);

		PersistenterVersandauftrag merged = em.merge(existing);

		return mapFromDB(merged);
	}

	@Override
	public int removeAll(final Identifier newsletterID) {

		List<PersistenterVersandauftrag> trefferliste = this.findVersandinfosWithNewsletterID(newsletterID.identifier());

		for (PersistenterVersandauftrag entity : trefferliste) {

			em.remove(entity);
		}

		return trefferliste.size();
	}

	private List<PersistenterVersandauftrag> findVersandinfosWithNewsletterID(final String newsletterID) {

		return em
			.createNamedQuery(PersistenterVersandauftrag.FIND_FOR_NEWSLETTER, PersistenterVersandauftrag.class)
			.setParameter("newsletterID", newsletterID).getResultList();
	}

	private Optional<PersistenterVersandauftrag> findTheExistingVersandinfo(final String uuid) {

		List<PersistenterVersandauftrag> trefferliste = em
			.createNamedQuery(PersistenterVersandauftrag.FIND_BY_UUID, PersistenterVersandauftrag.class)
			.setParameter("uuid", uuid).getResultList();

		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));
	}

	Versandauftrag mapFromDB(final PersistenterVersandauftrag fromDB) {

		String erfasstAm = CommonTimeUtils.format(fromDB.getErfasstAm());
		String versandBegonnenAm = fromDB.getVersandBegonnenAm() == null ? null
			: CommonTimeUtils.format(fromDB.getVersandBegonnenAm());
		String versandBeendetAm = fromDB.getVersandBeendetAm() == null ? null
			: CommonTimeUtils.format(fromDB.getVersandBeendetAm());

		return new Versandauftrag()
			.withAnzahlAktuellVersendet(fromDB.getAnzahlVersendet())
			.withAnzahlEmpaenger(fromDB.getAnzahlEmpfaenger())
			.withEmpfaengertyp(fromDB.getEmpfaengertyp())
			.withIdentifier(new Identifier(fromDB.getUuid()))
			.withNewsletterID(new Identifier(fromDB.getNewsletterID()))
			.withVersandBegonnenAm(versandBegonnenAm)
			.withVersandBeendetAm(versandBeendetAm)
			.withErfasstAm(erfasstAm)
			.withStatus(fromDB.getStatus());
	}

	void copyAttributesButUuid(final PersistenterVersandauftrag persistente, final Versandauftrag versandinfo) {

		LocalDateTime erfasstAm = StringUtils.isBlank(versandinfo.getErfasstAm()) ? LocalDateTime.now()
			: CommonTimeUtils.parseToLocalDateTime(versandinfo.getErfasstAm());

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
		persistente.setErfasstAm(erfasstAm);
		persistente.setStatus(versandinfo.getStatus());

		persistente.setVersandMitFehlern(StringUtils.isNotBlank(versandinfo.fehlermeldung()));
	}

	@Override
	public List<Versandauftrag> findNichtBeendeteVersandinfos() {

		List<PersistenterVersandauftrag> trefferliste = em
			.createNamedQuery(PersistenterVersandauftrag.FIND_NOT_COMPLETED, PersistenterVersandauftrag.class)
			.setParameter("statusCompleted", StatusAuslieferung.COMPLETED)
			.getResultList();

		return trefferliste.stream().map(this::mapFromDB).toList();
	}

}
