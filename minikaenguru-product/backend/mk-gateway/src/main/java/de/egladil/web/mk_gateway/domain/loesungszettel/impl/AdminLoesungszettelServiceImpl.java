// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.impl;

import java.util.List;
import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import de.egladil.web.mk_gateway.domain.loesungszettel.AdminLoesungszettelService;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.admin.AdminStatistikAuspraegung;
import de.egladil.web.mk_gateway.domain.statistik.admin.AdminStatistikGruppeninfo;
import de.egladil.web.mk_gateway.domain.statistik.admin.AdminStatistikItem;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * AdminLoesungszettelServiceImpl
 */
@ApplicationScoped
public class AdminLoesungszettelServiceImpl implements AdminLoesungszettelService {

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	@Override
	public AdminStatistikGruppeninfo createKurzstatistikLoesungszettel() {

		AdminStatistikGruppeninfo gruppeninfo = new AdminStatistikGruppeninfo("LOESUNGSZETTEL");

		Optional<Wettbewerb> optWettbewerb = wettbewerbService.aktuellerWettbewerb();

		if (optWettbewerb.isEmpty()) {

			return gruppeninfo;
		}

		Wettbewerb aktuellerWettbewerb = optWettbewerb.get();

		for (LoesungszettelGruppeninfoAuspraegungsart auspraegungsart : LoesungszettelGruppeninfoAuspraegungsart.values()) {

			AdminStatistikItem item = new AdminStatistikItem(auspraegungsart.name);

			List<AdminStatistikAuspraegung> auspraegungen = loesungszettelRepository
				.countAuspraegungenForWettbewerbByColumnName(aktuellerWettbewerb.id(), auspraegungsart.toString());

			item.setAuspraegungen(auspraegungen);
			gruppeninfo.addItem(item);
		}

		if (!gruppeninfo.getGruppenItems().isEmpty()) {

			AdminStatistikItem erstes = gruppeninfo.getGruppenItems().get(0);
			long anzahlElemente = erstes.getAuspraegungen().stream().mapToLong(auspraegung -> auspraegung.getAnzahl()).sum();
			gruppeninfo.setAnzahlElemente(anzahlElemente);
		}

		return gruppeninfo;
	}

	private enum LoesungszettelGruppeninfoAuspraegungsart {
		KLASSENSTUFE("Klassenstufe"),
		SPRACHE("Sprache"),
		TEILNAHMEART("Teilnahmeart"),
		QUELLE("Quelle");

		final String name;

		/**
		 * @param name
		 */
		private LoesungszettelGruppeninfoAuspraegungsart(final String name) {

			this.name = name;
		}

	}

}
