// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * VertragAuftragsverarbeitungRepository
 */
public interface VertragAuftragsverarbeitungRepository {

	/**
	 * Gibt das Aggregat zurück, falls es existiert.
	 *
	 * @param  uuid
	 * @return      Optional
	 */
	Optional<VertragAuftragsdatenverarbeitung> ofUuid(String uuid);

	/**
	 * Persistiert einen neuen Vertrag
	 *
	 * @param  vertrag
	 * @return              Identifier die neue ID
	 */
	Identifier addVertrag(VertragAuftragsdatenverarbeitung vertrag);

	/**
	 * Sucht den Vertrag für die gegebene Schule.
	 *
	 * @param  schuleIdentity
	 * @return                Optional
	 */
	Optional<VertragAuftragsdatenverarbeitung> findVertragForSchule(Identifier schuleIdentity);
}
