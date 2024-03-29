// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploadmonitoring.api;

import java.util.List;

import de.egladil.web.mk_gateway.domain.DownloadData;

/**
 * UploadKlassenlisteMonitoringService
 */
public interface UploadMonitoringService {

	/**
	 * Sucht die Uploadinfos zu Klassenlisteuploads einer gegebenen teilnahmenummer.
	 *
	 * @param  teilnahmenummer
	 * @return                 List
	 */
	List<UploadMonitoringInfo> findUploadsKlassenlisteWithTeilnahmenummer(String teilnahmenummer);

	/**
	 * Läd eine Page der UploadInfos.
	 *
	 * @param  limit
	 * @param  offset
	 * @return        List
	 */
	List<UploadMonitoringInfo> loadUploads(int limit, int offset);

	/**
	 * @return Anzahl Uploads insgesamt.
	 */
	Long countUploads();

	/**
	 * Sammelt die Datei von der Festplatte.
	 *
	 * @param  uploadUuid
	 * @return            DownloadData
	 */
	DownloadData getUploadedFile(String uploadUuid);

	/**
	 * @param  uuid
	 * @return
	 */
	DownloadData getFehlerReport(String uuid);

}
