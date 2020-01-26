// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.enums;

import java.util.ArrayList;
import java.util.List;

/**
 * UploadMimeType
 */
public enum UploadMimeType {

	ODS(".ods"),
	VDN_MSEXCEL(".xls"),
	VDN_OPEN_XML(".xlsx"),
	UNKNOWN(".unknown");

	private final String fileExtension;

	/**
	 * Erzeugt eine Instanz von UploadMimeType
	 */
	private UploadMimeType(final String fileExtension) {

		this.fileExtension = fileExtension;
	}

	/**
	 * die Dateiendung mit vorangestelltem Punkt.
	 *
	 * @return
	 */
	public String getFileExtension() {

		return fileExtension;
	}

	/**
	 * Gibt eine Liste von zulässigen Dateiendungen zurück.
	 *
	 * @return List
	 */
	public static List<String> getAllowedFileExtensions() {

		final List<String> allowedExtensions = new ArrayList<>();

		for (final UploadMimeType t : UploadMimeType.values()) {

			if (t != UNKNOWN) {

				allowedExtensions.add(t.fileExtension);
			}
		}
		return allowedExtensions;
	}
}
