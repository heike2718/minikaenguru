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
import de.egladil.web.mk_gateway.domain.newsletterversand.StatusAuslieferung;
import de.egladil.web.mk_gateway.domain.newsletterversand.VersandauftraegeRepository;
import de.egladil.web.mk_gateway.domain.newsletterversand.Versandauftrag;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterVersandauftrag;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

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
	public Versandauftrag addVersandauftrag(final Versandauftrag versandinformation) {

		PersistenterVersandauftrag persistenteVersandinfo = new PersistenterVersandauftrag();
		copyAttributesButUuid(persistenteVersandinfo, versandinformation);

		em.persist(persistenteVersandinfo);

		return mapFromDB(persistenteVersandinfo);
	}

	@Override
	public Versandauftrag updateVersandauftrag(final Versandauftrag versandinformation) {

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
	public Versandauftrag saveVersandauftrag(final Versandauftrag versandauftrag) {

		if (versandauftrag.identifier() == null) {

			return this.addVersandauftrag(versandauftrag);
		}

		return this.updateVersandauftrag(versandauftrag);
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
	public List<Versandauftrag> findAuftraegeNotCompleted() {

		List<PersistenterVersandauftrag> trefferliste = em
			.createNamedQuery(PersistenterVersandauftrag.FIND_NOT_COMPLETED, PersistenterVersandauftrag.class)
			.setParameter("statusWaiting", StatusAuslieferung.WAITING)
			.setParameter("statusInProgress", StatusAuslieferung.IN_PROGRESS)
			.getResultList();

		return trefferliste.stream().map(this::mapFromDB).toList();
	}

	@Override
	@Transactional
	public void delete(final Versandauftrag versandauftrag) {

		PersistenterVersandauftrag persistenter = em.find(PersistenterVersandauftrag.class,
			versandauftrag.identifier().identifier());

		if (persistenter != null) {

			em.remove(persistenter);
		}

	}

	@Override
	public List<Versandauftrag> loadAll() {

		List<PersistenterVersandauftrag> trefferliste = em
			.createNamedQuery(PersistenterVersandauftrag.LOAD_ALL, PersistenterVersandauftrag.class).getResultList();

		if (trefferliste.isEmpty()) {

			return new ArrayList<>();
		}
		return trefferliste.stream().map(t -> mapFromDB(t)).collect(Collectors.toList());
	}

}
