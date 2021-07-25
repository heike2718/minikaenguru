// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.upload;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.impl.LoesungszettelHibernateRepository;

/**
 * UploadAuswertungService
 */
@ApplicationScoped
public class UploadAuswertungService {

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	public static UploadAuswertungService createForIntegrationTest(final EntityManager em) {

		UploadAuswertungService result = new UploadAuswertungService();
		result.loesungszettelRepository = LoesungszettelHibernateRepository.createForIntegrationTest(em);
		return result;
	}

}
