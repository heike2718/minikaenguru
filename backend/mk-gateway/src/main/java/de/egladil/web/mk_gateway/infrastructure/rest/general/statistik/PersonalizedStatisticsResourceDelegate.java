// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general.statistik;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.statistik.StatistikAnonymisierteEinzelteilnahmeService;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * PersonalizedStatisticsResourceDelegate
 */
@ApplicationScoped
public class PersonalizedStatisticsResourceDelegate {

	@Inject
	StatistikAnonymisierteEinzelteilnahmeService statistikAnonymisierteEinzelteilnahmeService;

	public DownloadData erstelleStatistikPDFEinzelteilnahme(final String teilnahmeart, final String teilnahmenummer, final String jahr, final String userUuid) {

		WettbewerbID wettbewerbID = null;

		try {

			wettbewerbID = new WettbewerbID(Integer.valueOf(jahr));

		} catch (NumberFormatException e) {

			throw new BadRequestException("jahr muss numerisch sein.");
		}
		TeilnahmeIdentifier identifier = new TeilnahmeIdentifier().withTeilnahmeart(teilnahmeart)
			.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(wettbewerbID);

		DownloadData data = this.statistikAnonymisierteEinzelteilnahmeService.erstelleStatistikPDFEinzelteilnahme(identifier,
			userUuid);

		return data;
	}

}
