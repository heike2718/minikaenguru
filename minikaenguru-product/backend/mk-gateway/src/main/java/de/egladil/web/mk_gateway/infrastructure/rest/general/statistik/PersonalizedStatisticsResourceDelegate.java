// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general.statistik;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.BadRequestException;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.statistik.StatistikAnonymisierteEinzelteilnahmeService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;
import de.egladil.web.mk_gateway.infrastructure.rest.DevDelayService;

/**
 * PersonalizedStatisticsResourceDelegate
 */
@RequestScoped
public class PersonalizedStatisticsResourceDelegate {

	@Inject
	StatistikAnonymisierteEinzelteilnahmeService statistikAnonymisierteEinzelteilnahmeService;

	@Inject
	DevDelayService delayService;

	public DownloadData erstelleStatistikPDFEinzelteilnahme(@NotNull final String teilnahmeart, @NotNull final String teilnahmenummer, @NotNull final String jahr, @NotNull final String userUuid) {

		this.delayService.pause();

		WettbewerbID wettbewerbID = null;

		try {

			wettbewerbID = new WettbewerbID(Integer.valueOf(jahr));

		} catch (NumberFormatException e) {

			throw new BadRequestException("jahr muss numerisch sein.");
		}

		Teilnahmeart theTeilnahmeart = null;

		try {

			theTeilnahmeart = Teilnahmeart.valueOf(teilnahmeart.toUpperCase());
		} catch (IllegalArgumentException e) {

			throw new BadRequestException("teilnahmeart: erlaubt sind SCHULE oder PRIVAT");
		}

		TeilnahmeIdentifier identifier = new TeilnahmeIdentifier().withTeilnahmeart(theTeilnahmeart)
			.withTeilnahmenummer(teilnahmenummer).withWettbewerbID(wettbewerbID);

		DownloadData data = this.statistikAnonymisierteEinzelteilnahmeService.erstelleStatistikPDFEinzelteilnahme(identifier,
			userUuid);

		return data;
	}

}
