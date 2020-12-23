// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.util.List;
import java.util.function.Function;

import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelAPIModel;
import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;

/**
 * LoesungszettelToAPIModelMapper
 */
public class LoesungszettelToAPIModelMapper implements Function<Loesungszettel, LoesungszettelAPIModel> {

	@Override
	public LoesungszettelAPIModel apply(final Loesungszettel loesungszettel) {

		LoesungszettelRohdaten rohdaten = loesungszettel.rohdaten();

		List<LoesungszettelZeileAPIModel> zeilen = new LoesungszettelRohdatenAPIZeileMapper(loesungszettel.klassenstufe())
			.apply(rohdaten);

		LoesungszettelAPIModel result = new LoesungszettelAPIModel()
			.withKindID(loesungszettel.kindID().identifier())
			.withKlassenstufe(loesungszettel.klassenstufe())
			.withUuid(loesungszettel.identifier().identifier())
			.withZeilen(zeilen);

		return result;
	}

}
