// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder.impl;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.kinder.AdminKinderService;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Gruppeninfo;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Gruppenitem;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.KinderHibernateRepository;

/**
 * AdminKinderServiceImpl
 */
@ApplicationScoped
public class AdminKinderServiceImpl implements AdminKinderService {

	private enum KinderGruppeninfoAuspraegungsart {
		IMPORTIERT("importiert"),
		KLASSENSTUFE("Klassenstufe"),
		SPRACHE("Sprache"),
		TEILNAHMEART("Teilnahmeart");

		final String name;

		/**
		 * @param name
		 */
		private KinderGruppeninfoAuspraegungsart(final String name) {

			this.name = name;
		}

	}

	@Inject
	KinderRepository kinderRepository;

	public static AdminKinderServiceImpl createForIntegrationTest(final EntityManager entityManager) {

		AdminKinderServiceImpl result = new AdminKinderServiceImpl();
		result.kinderRepository = KinderHibernateRepository.createForIntegrationTest(entityManager);
		return result;
	}

	@Override
	public Gruppeninfo createKurzstatistikKinder() {

		Gruppeninfo gruppeninfo = new Gruppeninfo("KINDER", "Kinder");

		for (KinderGruppeninfoAuspraegungsart auspaegungsart : KinderGruppeninfoAuspraegungsart.values()) {

			Gruppenitem item = new Gruppenitem(auspaegungsart.name);
			List<Auspraegung> auspraegungen = kinderRepository.countAuspraegungenByColumnName(auspaegungsart.toString());
			item.setAuspraegungen(auspraegungen);

			gruppeninfo.addItem(item);

		}

		return gruppeninfo;
	}
}
