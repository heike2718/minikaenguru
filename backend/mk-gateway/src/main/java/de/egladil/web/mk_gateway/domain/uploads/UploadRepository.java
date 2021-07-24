// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;

/**
 * UploadRepository
 */
public interface UploadRepository {

	/**
	 * @param  teilnahmenummer
	 * @return                 List
	 */
	List<PersistenterUpload> findUploadsWithTeilnahmenummer(String teilnahmenummer);

	/**
	 * @param  uploadIdentifier
	 * @return
	 */
	Optional<PersistenterUpload> findUploadByIdentifier(UploadIdentifier uploadIdentifier);

	/**
	 * @param  upload
	 * @return        PersistenterUpload
	 */
	PersistenterUpload addUploadMetaData(PersistenterUpload upload);

	/**
	 * @param  persistenterUpload
	 * @return                    PersistenterUpload
	 */
	PersistenterUpload updateUpload(PersistenterUpload persistenterUpload);

}
