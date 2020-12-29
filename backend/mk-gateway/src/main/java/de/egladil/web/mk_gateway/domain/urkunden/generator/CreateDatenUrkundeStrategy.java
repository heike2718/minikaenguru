// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;

/**
 * CreateDatenUrkundeStrategy
 */
public interface CreateDatenUrkundeStrategy {

	/**
	 * Erzeugt ein Objekt mit den Daten für die Urkunde.
	 *
	 * @param  kind
	 *                        Kind das Kind, für das die Urkunde ist.
	 * @param  loesungszettel
	 *                        Loesungszettel
	 * @param  datum
	 *                        String der Datumsstring, der auf die Urkunde gedruckt wird.
	 * @param  urkundenmotiv
	 *                        Urkundenmotiv
	 * @return                AbstractDatenUrkunde.
	 */
	AbstractDatenUrkunde createDatenUrkunde(Kind kind, Loesungszettel loesungszettel, String datum, Urkundenmotiv urkundenmotiv);

}
