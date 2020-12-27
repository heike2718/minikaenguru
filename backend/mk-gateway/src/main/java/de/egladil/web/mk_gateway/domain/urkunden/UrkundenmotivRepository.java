// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;

/**
 * UrkundenmotivRepository
 */
@ApplicationScoped
public class UrkundenmotivRepository {

	private Map<Farbschema, Urkundenmotiv> urkundenmotive = new ConcurrentHashMap<>();

	public Urkundenmotiv findByFarbschema(final Farbschema farbschema) {

		Urkundenmotiv urkundenmotiv = urkundenmotive.get(farbschema);

		if (urkundenmotiv == null) {

			urkundenmotiv = Urkundenmotiv.createFromFarbschema(farbschema);
			urkundenmotive.put(farbschema, urkundenmotiv);
		}

		return urkundenmotiv;
	}
}
