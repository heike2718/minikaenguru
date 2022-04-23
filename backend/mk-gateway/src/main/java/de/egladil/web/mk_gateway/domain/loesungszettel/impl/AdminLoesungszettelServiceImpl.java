// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.impl;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.loesungszettel.AdminLoesungszettelService;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Gruppeninfo;
import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Gruppenitem;
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
	public Gruppeninfo createKurzstatistikLoesungszettel() {

		Gruppeninfo gruppeninfo = new Gruppeninfo("LOESUNGSZETTEL");

		Optional<Wettbewerb> optWettbewerb = wettbewerbService.aktuellerWettbewerb();

		if (optWettbewerb.isEmpty()) {

			return gruppeninfo;
		}

		Wettbewerb aktuellerWettbewerb = optWettbewerb.get();

		for (LoesungszettelGruppeninfoAuspraegungsart auspraegungsart : LoesungszettelGruppeninfoAuspraegungsart.values()) {

			Gruppenitem item = new Gruppenitem(auspraegungsart.name);

			List<Auspraegung> auspraegungen = loesungszettelRepository
				.countAuspraegungenForWettbewerbByColumnName(aktuellerWettbewerb.id(), auspraegungsart.toString());

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
