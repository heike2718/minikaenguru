// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.veranstalter.SchuleKatalogResponseMapper;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;

/**
 * SchulkatalogService
 */
@ApplicationScoped
public class SchulkatalogService {

	private static final Logger LOG = LoggerFactory.getLogger(SchulkatalogService.class);

	@Inject
	MkKatalogeResourceAdapter katalogeResourceAdapter;

	public static final SchulkatalogService createForTest(final MkKatalogeResourceAdapter katalogeResourceAdapter) {

		SchulkatalogService result = new SchulkatalogService();
		result.katalogeResourceAdapter = katalogeResourceAdapter;
		return result;
	}

	/**
	 * Fragt beim mk-kataloge-Microservice nach Daten der Schule mit gegebenem schulkuerzel an. Exceptions weren nur geloggt.
	 *
	 * @param  schulkuerzel
	 * @return              Optional
	 */
	public Optional<SchuleAPIModel> findSchuleQuietly(final String schulkuerzel) {

		try {

			Response katalogeResponse = katalogeResourceAdapter.findSchulen(schulkuerzel);

			List<SchuleAPIModel> trefferliste = new SchuleKatalogResponseMapper().getSchulenFromKatalogeAPI(katalogeResponse);

			return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));

		} catch (MkGatewayRuntimeException e) {

			LOG.warn("Können Schule nicht ermitteln: {}", e.getMessage());
			return Optional.empty();
		}
	}

}