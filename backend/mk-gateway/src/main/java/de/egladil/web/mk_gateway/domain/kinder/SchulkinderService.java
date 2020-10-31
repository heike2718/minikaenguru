// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import javax.inject.Inject;

import de.egladil.web.mk_gateway.domain.kinder.api.SchulkindRequestData;
import de.egladil.web.mk_gateway.domain.kinder.api.KindAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;

/**
 * SchulkinderService
 */
public class SchulkinderService {

	@Inject
	KinderRepository kinderRepository;

	@Inject
	KlassenRepository klassenRepository;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	public boolean pruefeDubletteSchueler(final SchulkindRequestData daten) {

		return false;
	}

	public KindAPIModel schulkindAnlegen(final SchulkindRequestData daten) {

		return null;
	}

}
