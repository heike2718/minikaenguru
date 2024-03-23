// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.mail.Empfaengertyp;
import de.egladil.web.mk_gateway.domain.semantik.Repository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.VeranstalterSuchanfrage;

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
	 * @param  veranstalter
	 *                      Veranstalter darf nicht null sein. Es muss bereits einen gespeicherten Veranstalter mit der gleichen
	 *                      uuid
	 *                      geben.
	 * @return              boolean - damit man Mocken kann
	 */
	boolean changeVeranstalter(Veranstalter veranstalter) throws IllegalStateException;

	/**
	 * Löscht den gegebenen Veranstalter ersatzlos. Das ist unkritisch, da Daten nicht mit dem Veranstalter verknüft sind.
	 *
	 * @param veranstalter
	 */
	void removeVeranstalter(Veranstalter veranstalter);

	/**
	 * @param  suchkriterium
	 * @return               List
	 */
	List<Veranstalter> findVeranstalter(VeranstalterSuchanfrage suchanfrage);

	/**
	 * Gibt die Mailadressen der Newsletterempfaenger des gegebenen Empfaengertyps zurück.
	 *
	 * @param  empfaengertyp
	 * @return
	 */
	List<String> findEmailsNewsletterAbonnenten(Empfaengertyp empfaengertyp);

	/**
	 * Läd die Veranstalter mit den gegebenen Identifiern.
	 *
	 * @param  veranstalterUUIDs
	 *                           List
	 * @return                   List
	 */
	List<Veranstalter> loadVeranstalterByUuids(final List<String> veranstalterUUIDs);

}
