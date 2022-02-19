// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.unterlagen.Download;
import de.egladil.web.mk_gateway.domain.unterlagen.DownloadsRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenteDownloadInfo;
import de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.SortNumberGenerator;
import de.egladil.web.mk_gateway.infrastructure.persistence.sortnumbers.impl.SortNumberGeneratorImpl;

/**
 * DownloadsHibernateRepository
 */
@RequestScoped
public class DownloadsHibernateRepository implements DownloadsRepository {

	@Inject
	EntityManager entityManager;

	@Inject
	SortNumberGenerator sortNumberGenerator;

	public static DownloadsHibernateRepository createForIntegrationTests(final EntityManager entityManager) {

		DownloadsHibernateRepository result = new DownloadsHibernateRepository();
		result.entityManager = entityManager;
		result.sortNumberGenerator = SortNumberGeneratorImpl.createForIntegrationTests(entityManager);
		return result;
	}

	@Override
	public List<Download> findDoenloadsByVeranstalterAndWettbewerb(final Identifier veranstalterID, final WettbewerbID wettbewerbID) {

		List<PersistenteDownloadInfo> trefferliste = entityManager
			.createNamedQuery(PersistenteDownloadInfo.FIND_DOWNLOAD_BY_VERANSTALTER_JAHR, PersistenteDownloadInfo.class)
			.setParameter("veranstalterUuid", veranstalterID.identifier()).setParameter("jahr", wettbewerbID.jahr())
			.getResultList();

		List<Download> result = trefferliste.stream().map(treffer -> mapFromDB(treffer)).collect(Collectors.toList());
		return result;
	}

	/**
	 * @param  persistenteDownloadInfo
	 * @return
	 */
	private Download mapFromDB(final PersistenteDownloadInfo persistenteDownloadInfo) {

		return Download.createInstance(new Identifier(persistenteDownloadInfo.getUuid()))
			.withAnzahl(persistenteDownloadInfo.getAnzahl()).withDownloadTyp(persistenteDownloadInfo.getDownloadType())
			.withVeranstalterID(new Identifier(persistenteDownloadInfo.getVeranstalterUuid()))
			.withWettbewerbID(new WettbewerbID(persistenteDownloadInfo.getJahr()))
			.withSortnumber(persistenteDownloadInfo.getSortNumber());
	}

	@Override
	@Transactional
	public Download addOrUpdate(final Download download) {

		PersistenteDownloadInfo vorhandene = findDownload(download);

		if (vorhandene == null) {

			PersistenteDownloadInfo neue = new PersistenteDownloadInfo();
			neue.setAnzahl(1);
			neue.setDownloadDate(new Date());
			neue.setSortNumber(sortNumberGenerator.getNextSortnumberDownloads());
			neue.setDownloadType(download.getDownloadTyp());
			neue.setJahr(download.getWettbewerbID().jahr());
			neue.setVeranstalterUuid(download.getVeranstalterID().identifier());

			entityManager.persist(neue);

			return this.mapFromDB(neue);
		}

		vorhandene.setAnzahl(vorhandene.getAnzahl() + 1);
		vorhandene.setDownloadDate(new Date());

		PersistenteDownloadInfo persisted = entityManager.merge(vorhandene);

		return mapFromDB(persisted);
	}

	@Override
	public List<Auspraegung> countAuspraegungenByColumnName(final String columnName, final Integer jahr) {

		List<Auspraegung> result = new ArrayList<>();

		String stmt = "select d." + columnName + ", sum(d.anzahl) from DOWNLOADS d where d.jahr = :jahr group by d." + columnName;

		// System.out.println(stmt);

		@SuppressWarnings("unchecked")
		List<Object[]> trefferliste = entityManager.createNativeQuery(stmt).setParameter("jahr", jahr).getResultList();

		for (Object[] treffer : trefferliste) {

			String wert = treffer[0].toString();
			BigDecimal anzahl = (BigDecimal) treffer[1];

			result.add(new Auspraegung(wert, anzahl.longValue()));

		}

		return result;
	}

	private PersistenteDownloadInfo findDownload(final Download download) {

		List<PersistenteDownloadInfo> trefferliste = entityManager
			.createNamedQuery(PersistenteDownloadInfo.FIND_DOWNLOAD_BY_VERANSTALTER_JAHR_TYPE, PersistenteDownloadInfo.class)
			.setParameter("veranstalterUuid", download.getVeranstalterID().identifier())
			.setParameter("jahr", download.getWettbewerbID().jahr())
			.setParameter("downloadType", download.getDownloadTyp()).getResultList();

		if (trefferliste.size() > 1) {

			throw new MkGatewayRuntimeException(
				"Index auf Tabelle DOWNLOADS nicht korrekt definiert: mehr als ein Treffer zum Download " + download + ".");
		}

		return trefferliste.isEmpty() ? null : trefferliste.get(0);
	}
}
