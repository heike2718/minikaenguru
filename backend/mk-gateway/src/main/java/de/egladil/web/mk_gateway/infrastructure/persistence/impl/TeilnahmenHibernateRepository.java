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

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenteTeilnahme;

/**
 * TeilnahmenHibernateRepository
 */
@RequestScoped
public class TeilnahmenHibernateRepository implements TeilnahmenRepository {

	private static final Logger LOG = LoggerFactory.getLogger(TeilnahmenHibernateRepository.class);

	@Inject
	EntityManager em;

	@Override
	public Optional<Teilnahme> ofTeilnahmenummerArtWettbewerb(final String teilnahmenummer, final Teilnahmeart art, final WettbewerbID wettbewerbId) {

		if (art == null) {

			throw new IllegalArgumentException("art darf nicht null sein.");
		}

		if (wettbewerbId == null) {

			throw new IllegalArgumentException("wettbewerbId darf nicht null sein.");
		}

		List<PersistenteTeilnahme> teilnahmen = this.findAllByNummer(teilnahmenummer);

		Optional<PersistenteTeilnahme> optTeilnahme = teilnahmen.stream()
			.filter(t -> t.getTeilnahmeart() == art && t.getWettbewerbUUID().equals(wettbewerbId.jahr().toString()))
			.findFirst();

		if (optTeilnahme.isEmpty()) {

			return Optional.empty();
		}

		PersistenteTeilnahme persistente = optTeilnahme.get();

		Teilnahme teilnahme = mapToTeilnahme(persistente);

		return Optional.of(teilnahme);
	}

	@Override
	public List<Teilnahme> ofTeilnahmenummerArt(final String teilnahmenummer, final Teilnahmeart art) {

		List<Teilnahme> teilnahmen = this.ofTeilnahmenummer(teilnahmenummer);

		return teilnahmen.stream().filter(t -> art == t.teilnahmeart()).collect(Collectors.toList());
	}

	@Override
	public List<Teilnahme> ofTeilnahmenummer(final String teilnahmenummer) {

		List<PersistenteTeilnahme> teilnahmen = this.findAllByNummer(teilnahmenummer);

		List<Teilnahme> result = new ArrayList<>();

		teilnahmen.stream().forEach(t -> result.add(mapToTeilnahme(t)));

		return result;
	}

	@Override
	public boolean addTeilnahme(final Teilnahme teilnahme) {

		Optional<Teilnahme> opt = this.ofTeilnahmenummerArtWettbewerb(teilnahme.teilnahmenummer().identifier(),
			teilnahme.teilnahmeart(), teilnahme.wettbewerbID());

		if (opt.isPresent()) {

			throw new IllegalStateException("Es gibt diese Teilnahme bereits: " + teilnahme.toString());
		}

		PersistenteTeilnahme persistenteTeilnahme = mapFromTeilnahme(teilnahme);

		em.persist(persistenteTeilnahme);

		LOG.info("Teilnahme {} erfolreich angelegt", teilnahme);

		return true;

	}

	/**
	 * @param  teilnahme
	 * @return
	 */
	PersistenteTeilnahme mapFromTeilnahme(final Teilnahme teilnahme) {

		PersistenteTeilnahme persistenteTeilnahme = new PersistenteTeilnahme();

		if (Teilnahmeart.SCHULE == teilnahme.teilnahmeart()) {

			Schulteilnahme schulteilnahme = (Schulteilnahme) teilnahme;
			persistenteTeilnahme.setSchulname(schulteilnahme.nameSchule());
			persistenteTeilnahme.setAngemeldetDurch(schulteilnahme.angemeldetDurchVeranstalterId().identifier());
		}
		persistenteTeilnahme.setTeilnahmeart(teilnahme.teilnahmeart());
		persistenteTeilnahme.setWettbewerbUUID(teilnahme.wettbewerbID().toString());
		persistenteTeilnahme.setTeilnahmenummer(teilnahme.teilnahmenummer().identifier());
		return persistenteTeilnahme;
	}

	@Override
	public void changeTeilnahme(final Schulteilnahme teilnahme, final String uuidAenderer) throws IllegalStateException {

		List<PersistenteTeilnahme> persistenteTeilnahmen = this.findAllByNummer(teilnahme.teilnahmenummer().identifier());

		Optional<PersistenteTeilnahme> opt = persistenteTeilnahmen.stream()
			.filter(pt -> pt.getTeilnahmeart() == teilnahme.teilnahmeart()
				&& pt.getWettbewerbUUID().equals(teilnahme.wettbewerbID().toString()))
			.findFirst();

		if (!opt.isPresent()) {

			throw new IllegalStateException(teilnahme.toString() + " ist noch nicht persistiert");
		}

		PersistenteTeilnahme vorhandene = opt.get();
		vorhandene.setSchulname(teilnahme.nameSchule());

		em.merge(vorhandene);
	}

	private List<PersistenteTeilnahme> findAllByNummer(final String teilnahmenummer) {

		if (StringUtils.isBlank(teilnahmenummer)) {

			throw new IllegalArgumentException("teilnahmenummer darf nicht blank sein");
		}

		return em.createNamedQuery(PersistenteTeilnahme.FIND_BY_NUMMER, PersistenteTeilnahme.class)
			.setParameter("teilnahmenummer", teilnahmenummer).getResultList();

	}

	Teilnahme mapToTeilnahme(final PersistenteTeilnahme persistente) {

		Teilnahme teilnahme = null;

		WettbewerbID wettbewerbID = new WettbewerbID(Integer.valueOf(persistente.getWettbewerbUUID()));
		Identifier teilnahmenummer = new Identifier(persistente.getTeilnahmenummer());

		switch (persistente.getTeilnahmeart()) {

		case PRIVAT:
			teilnahme = new Privatteilnahme(wettbewerbID, teilnahmenummer);
			break;

		case SCHULE:
			String angemeldetDurch = persistente.getAngemeldetDurch();
			teilnahme = angemeldetDurch != null
				? teilnahme = new Schulteilnahme(wettbewerbID, teilnahmenummer, persistente.getSchulname(),
					new Identifier(angemeldetDurch))
				: new Schulteilnahme(wettbewerbID, teilnahmenummer, persistente.getSchulname(),
					null);
			break;

		default:
			throw new MkGatewayRuntimeException("unbekannte Teilnahmeart " + persistente.getTeilnahmeart());
		}
		return teilnahme;
	}

	@Override
	public List<Teilnahme> loadAllPrivatteilnahmen() {

		String stmt = "select t from PersistenteTeilnahme t where t.teilnahmeart = :teilnahmeart";

		List<PersistenteTeilnahme> persistente = em.createQuery(stmt, PersistenteTeilnahme.class)
			.setParameter("teilnahmeart", Teilnahmeart.PRIVAT).getResultList();

		return persistente.stream().map(t -> mapToTeilnahme(t)).collect(Collectors.toList());
	}

}
