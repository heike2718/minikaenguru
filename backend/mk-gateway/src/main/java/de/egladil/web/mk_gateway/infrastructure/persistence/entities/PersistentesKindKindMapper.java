// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import java.util.function.Function;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * PersistentesKindKindMapper
 */
public class PersistentesKindKindMapper implements Function<PersistentesKind, Kind> {

	private final WettbewerbID wettbewerbID;

	public PersistentesKindKindMapper(final WettbewerbID wettbewerbID) {

		this.wettbewerbID = wettbewerbID;
	}

	@Override
	public Kind apply(final PersistentesKind persistentesKind) {

		TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier()
			.withTeilnahmeart(persistentesKind.getTeilnahmeart())
			.withTeilnahmenummer(persistentesKind.getTeilnahmenummer())
			.withWettbewerbID(wettbewerbID);

		Identifier klasse = persistentesKind.getKlasseUUID() == null ? null : new Identifier(persistentesKind.getKlasseUUID());
		Identifier loesungszettel = persistentesKind.getLoesungszettelUUID() == null ? null
			: new Identifier(persistentesKind.getLoesungszettelUUID());

		return new Kind(new Identifier(persistentesKind.getUuid()))
			.withKlasseID(klasse)
			.withKlassenstufe(persistentesKind.getKlassenstufe())
			.withLoesungszettelID(loesungszettel)
			.withNachname(persistentesKind.getNachname())
			.withSprache(persistentesKind.getSprache())
			.withTeilnahmeIdentifier(teilnahmeIdentifier)
			.withVorname(persistentesKind.getVorname())
			.withZusatz(persistentesKind.getZusatz());
	}

}
