// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.adv.Anschrift;
import de.egladil.web.mk_gateway.domain.adv.VertragAuftragsdatenverarbeitung;
import de.egladil.web.mk_gateway.domain.adv.VertragAuftragsverarbeitungRepository;
import de.egladil.web.mk_gateway.domain.adv.Vertragstext;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterVertragAdv;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterVertragAdvText;

/**
 * VertragAuftragsverarbeitungHibernateRepository
 */
@RequestScoped
public class VertragAuftragsverarbeitungHibernateRepository implements VertragAuftragsverarbeitungRepository {

	private static final Logger LOG = LoggerFactory.getLogger(VertragAuftragsverarbeitungHibernateRepository.class);

	@Inject
	EntityManager em;

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	@Override
	public Optional<VertragAuftragsdatenverarbeitung> ofUuid(final String uuid) {

		List<PersistenterVertragAdv> trefferliste = em
			.createNamedQuery(PersistenterVertragAdv.FIND_BY_UUID, PersistenterVertragAdv.class).setParameter("uuid", uuid)
			.getResultList();

		if (trefferliste.isEmpty()) {

			return Optional.empty();
		}

		if (trefferliste.size() > 1) {

			String msg = "mehr als ein PersistenterVertragAdv mit UUID='" + uuid + "' in der DB";

			LOG.error(msg);

			if (dataInconsistencyEvent != null) {

				DataInconsistencyRegistered entity = new DataInconsistencyRegistered(msg);
				dataInconsistencyEvent.fire(entity);
			}
			throw new MkGatewayRuntimeException(msg);
		}

		VertragAuftragsdatenverarbeitung result = this.mapFromDb(trefferliste.get(0));

		return Optional.of(result);
	}

	@Override
	public Optional<VertragAuftragsdatenverarbeitung> findVertragForSchule(final Identifier schuleIdentity) {

		List<PersistenterVertragAdv> trefferliste = em
			.createNamedQuery(PersistenterVertragAdv.FIND_BY_SCHULKUERZEL, PersistenterVertragAdv.class)
			.setParameter("schulkuerzel", schuleIdentity.identifier())
			.getResultList();

		if (trefferliste.isEmpty()) {

			return Optional.empty();
		}

		if (trefferliste.size() > 1) {

			String msg = "mehr als ein PersistenterVertragAdv mit SCHULKUERZEL='" + schuleIdentity.toString() + "' in der DB";

			LOG.error(msg);

			if (dataInconsistencyEvent != null) {

				DataInconsistencyRegistered entity = new DataInconsistencyRegistered(msg);
				dataInconsistencyEvent.fire(entity);
			}
			throw new MkGatewayRuntimeException(msg);
		}

		VertragAuftragsdatenverarbeitung result = this.mapFromDb(trefferliste.get(0));

		return Optional.of(result);
	}

	@Override
	@Transactional
	public Identifier addVertrag(final VertragAuftragsdatenverarbeitung vertrag) {

		PersistenterVertragAdv persistenterVertrag = mapFromAggregate(vertrag);

		if (persistenterVertrag.getUuid() == null) {

			em.persist(persistenterVertrag);

			return new Identifier(persistenterVertrag.getUuid());
		} else {

			PersistenterVertragAdv persisted = em.merge(persistenterVertrag);
			return new Identifier(persisted.getUuid());
		}
	}

	PersistenterVertragAdv mapFromAggregate(final VertragAuftragsdatenverarbeitung vertrag) {

		String idVertragstext = vertrag.idVertragstext().identifier();
		PersistenterVertragAdvText persistenterText = em.find(PersistenterVertragAdvText.class,
			idVertragstext);

		if (persistenterText == null) {

			throw new MkGatewayRuntimeException(
				"Vertragstext mit UUID='" + idVertragstext + "' existiert nicht");
		}

		PersistenterVertragAdv persistenterVertrag = em.find(PersistenterVertragAdv.class, vertrag.identifier().identifier());

		if (persistenterVertrag == null) {

			persistenterVertrag = new PersistenterVertragAdv();
		}

		Anschrift anschrift = vertrag.anschrift();

		persistenterVertrag.setAbgeschlossenAm(vertrag.unterzeichnetAm());
		persistenterVertrag.setAbgeschlossenDurch(vertrag.unterzeichnenderLehrer().identifier());
		persistenterVertrag.setAdvText(persistenterText);
		persistenterVertrag.setHausnummer(anschrift.hausnummer());
		persistenterVertrag.setLaendercode(anschrift.laendercode());
		persistenterVertrag.setOrt(anschrift.ort());
		persistenterVertrag.setPlz(anschrift.plz());
		persistenterVertrag.setStrasse(anschrift.strasse());
		persistenterVertrag.setSchulkuerzel(vertrag.schulkuerzel().identifier());
		persistenterVertrag.setSchulname(anschrift.schulname());

		if (persistenterVertrag.getUuid() == null) {

			persistenterVertrag.setImportierteUuid(vertrag.identifier().identifier());
		}

		return persistenterVertrag;

	}

	VertragAuftragsdatenverarbeitung mapFromDb(final PersistenterVertragAdv persistenterVertrag) {

		PersistenterVertragAdvText persistenterVertragstext = persistenterVertrag.getAdvText();

		Anschrift anschrift = new Anschrift().withHausnummer(persistenterVertrag.getHausnummer())
			.withLaendercode(persistenterVertrag.getLaendercode()).withOrt(persistenterVertrag.getOrt())
			.withPlz(persistenterVertrag.getPlz()).withSchulname(persistenterVertrag.getSchulname())
			.withStrasse(persistenterVertrag.getStrasse());

		return new VertragAuftragsdatenverarbeitung()
			.withIdentifier(new Identifier(persistenterVertrag.getUuid()))
			.withAnschrift(anschrift)
			.withSchulkuerzel(new Identifier(persistenterVertrag.getSchulkuerzel()))
			.withUnterzeichnenderLehrer(new Identifier(persistenterVertrag.getAbgeschlossenDurch()))
			.withUnterzeichnetAm(persistenterVertrag.getAbgeschlossenAm())
			.withVertragstext(new Identifier(persistenterVertragstext.getUuid()));

	}

	Vertragstext mapFromDb(final PersistenterVertragAdvText persistenterVertragstext) {

		return new Vertragstext().withIdentifier(new Identifier(persistenterVertragstext.getUuid()))
			.withChecksumme(persistenterVertragstext.getChecksumme())
			.withDateiname(persistenterVertragstext.getDateiname())
			.withVersionsnummer(persistenterVertragstext.getVersionsnummer());
	}

}
