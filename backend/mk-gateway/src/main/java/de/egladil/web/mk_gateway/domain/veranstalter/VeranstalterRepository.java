// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.Repository;

/**
 * VeranstalterRepository
 */
@Repository
public interface VeranstalterRepository {

	/**
	 * Falls es einen Veranstalter mit diesem Identifier gibt, wird dieser zurückgegeben.
	 *
	 * @param  identifier
	 * @return            Optional
	 */
	Optional<Veranstalter> ofId(Identifier identifier);

	/**
	 * Fügt einen neuen Veranstalter hinzu.
	 *
	 * @param veranstalter
	 *                     Veranstalter darf nicht null sein.
	 */
	void addVeranstalter(Veranstalter veranstalter) throws IllegalStateException;

	/**
	 * Ändert einen vorhandenen Veranstalter.
	 *
	 * @param veranstalter
	 *                     Veranstalter darf nicht null sein. Es muss bereits einen gespeicherten Veranstalter mit der gleichen uuid
	 *                     geben.
	 */
	void changeVeranstalter(Veranstalter veranstalter) throws IllegalStateException;

	@Deprecated(forRemoval = true)
	List<Veranstalter> loadPrivatveranstalter();

}