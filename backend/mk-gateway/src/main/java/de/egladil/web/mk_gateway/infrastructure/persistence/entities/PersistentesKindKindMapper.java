// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import java.util.function.Function;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;

/**
 * PersistentesKindKindMapper
 */
public class PersistentesKindKindMapper implements Function<PersistentesKind, Kind> {

	@Override
	public Kind apply(final PersistentesKind persistentesKind) {

		Identifier klasse = persistentesKind.getKlasseUUID() == null ? null : new Identifier(persistentesKind.getKlasseUUID());
		Identifier loesungszettel = persistentesKind.getLoesungszettelUUID() == null ? null
			: new Identifier(persistentesKind.getLoesungszettelUUID());

		TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier = new TeilnahmeIdentifierAktuellerWettbewerb(
			persistentesKind.getTeilnahmenummer(), persistentesKind.getTeilnahmeart());

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
