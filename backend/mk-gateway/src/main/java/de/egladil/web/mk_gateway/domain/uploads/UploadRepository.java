// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_gateway.domain.statistik.gruppeninfos.Auspraegung;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
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

	/**
	 * Selektiert die Uploads der gegebenen Teilnahme und gruppiert sie nach der gewünschten Spalte.
	 *
	 * @param  teilnahmeIdentifier
	 *                             TeilnahmeIdentifier
	 * @param  columnName
	 *                             String name des group by- Kriteriums.
	 * @return
	 */
	List<Auspraegung> countAuspraegungenForTeilnahmeByColumnName(TeilnahmeIdentifier teilnahmeIdentifier, String columnName);

	/**
	 * Löscht den Upload mit der gegebenen ID.
	 *
	 * @param uuid
	 */
	void deleteUpload(final String uuid);

	/**
	 * @param teilnahmenummer
	 */
	void deleteUploadsKlassenlisten(String teilnahmenummer);

}
