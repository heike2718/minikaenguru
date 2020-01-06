// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.enums;

/**
 * UploadStatus
 */
public enum UploadStatus {

	NEU(""),
	FERTIG(".done"),
	LEER(".leer"),
	FEHLER(".error"),
	KORRIGIERT(".korr"), // der Original-Upload-Eintrag wird als KORRIGIERT markiert.
	IGNORE(".ignore"); // bereits einmal hochgeladen: nur zum Markieren von Dateien, nichts für DB.

	private final String fileExtension;

	/**
	 * Erzeugt eine Instanz von UploadStatus
	 */
	private UploadStatus(final String fileExtension) {

		this.fileExtension = fileExtension;
	}

	public String getFileExtension() {

		return fileExtension;
	}

	/**
	 * Gibt die Status zurück, die anzeigen, dass die Datei geparsed wurde.
	 *
	 * @return UploadStatus
	 */
	public static UploadStatus[] getVerarbeiteteStatus() {

		return new UploadStatus[] { FERTIG, LEER, FEHLER, KORRIGIERT, IGNORE };
	}

}
