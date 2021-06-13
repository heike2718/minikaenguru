// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import java.util.Optional;

import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;

/**
 * UploadRepository
 */
public interface UploadRepository {

	/**
	 * @param  uploadIdentifier
	 * @return
	 */
	Optional<PersistenterUpload> findUploadByIdentifier(UploadIdentifier uploadIdentifier);

}
