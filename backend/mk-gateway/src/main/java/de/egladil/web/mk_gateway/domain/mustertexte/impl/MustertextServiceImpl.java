// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte.impl;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.mustertexte.MustertexteRepository;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertextkategorie;

/**
 * MustertextServiceImpl
 */
@ApplicationScoped
public class MustertextServiceImpl {

	@Inject
	MustertexteRepository mustertexteRepository;

	public ResponsePayload getMustertexteByKategorie(final Mustertextkategorie kategorie) {

		return null;
	}

}
