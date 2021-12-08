// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.UploadsMonitoringViewItem;

/**
 * UploadRepository
 */
public interface UploadRepository {

	/**
	 * @param  teilnahmenummer
	 * @return                 List
	 */
	List<UploadsMonitoringViewItem> findUploadsWithUploadTypeAndTeilnahmenummer(UploadType uploadRType, String teilnahmenummer);

	/**
	 * Läd die Page.
	 *
	 * @param  limit
	 *                int die Anzahl, die gelesen werden soll
	 * @param  offset
	 *                int der Index, an dem das Lesen beginnen soll.
	 * @return        List
	 */
	List<UploadsMonitoringViewItem> loadUploadsPage(int limit, int offset);

	/**
	 * @return long
	 */
	long countUploads();

	/**
	 * @param  uploadIdentifier
	 * @return
	 */
	Optional<PersistenterUpload> findUploadByIdentifier(UploadIdentifier uploadIdentifier);

	/**
	 * @param  uuid
	 * @return
	 */
	Optional<PersistenterUpload> findByUuid(String uuid);

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

	/**
	 * @param  uploadType
	 * @param  teilnahmenummer
	 * @return                 long
	 */
	long countUploadsWithUploadTypeAndTeilnahmenummer(UploadType uploadType, String teilnahmenummer);

}
