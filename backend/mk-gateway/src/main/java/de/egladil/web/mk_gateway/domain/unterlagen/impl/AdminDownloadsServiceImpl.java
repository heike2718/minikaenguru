// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.unterlagen.impl;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Gruppeninfo;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Gruppenitem;
import de.egladil.web.mk_gateway.domain.unterlagen.AdminDownloadsService;
import de.egladil.web.mk_gateway.domain.unterlagen.DownloadsRepository;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * AdminDownloadsService
 */
@ApplicationScoped
public class AdminDownloadsServiceImpl implements AdminDownloadsService {

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	DownloadsRepository downloadsRepository;

	@Override
	public Gruppeninfo createKurzstatistikDownloads() {

		Gruppeninfo gruppeninfo = new Gruppeninfo("DOWNLOADS");

		Optional<Wettbewerb> optWettbewerb = wettbewerbService.aktuellerWettbewerb();

		if (optWettbewerb.isEmpty()) {

			return gruppeninfo;
		}

		for (DownloadsGruppeninfoAuspraegungsart auspaegungsart : DownloadsGruppeninfoAuspraegungsart.values()) {

			Gruppenitem item = new Gruppenitem(auspaegungsart.name);
			List<Auspraegung> auspraegungen = downloadsRepository.countAuspraegungenByColumnName(auspaegungsart.toString(),
				optWettbewerb.get().id().jahr());
			item.setAuspraegungen(auspraegungen);
			gruppeninfo.addItem(item);
		}

		if (!gruppeninfo.getGruppenItems().isEmpty()) {

			Gruppenitem erstes = gruppeninfo.getGruppenItems().get(0);
			long anzahlElemente = erstes.getAuspraegungen().stream().mapToLong(auspraegung -> auspraegung.getAnzahl()).sum();
			gruppeninfo.setAnzahlElemente(anzahlElemente);
		}

		return gruppeninfo;
	}

	private enum DownloadsGruppeninfoAuspraegungsart {

		DOWNLOAD_TYPE("Dowloadtyp");

		final String name;

		/**
		 * @param name
		 */
		private DownloadsGruppeninfoAuspraegungsart(final String name) {

			this.name = name;
		}

	}

}
