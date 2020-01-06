// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mkv_server.pdf;

/**
 * FontTyp
 */
public enum FontTyp {

	BOLD("/fonts/FreeSansBold.ttf"),
	ITALIC("/fonts/FreeSansOblique.ttf"),
	ITALIC_BOLD("/fonts/FreeSansBoldOblique.ttf"),
	NORMAL("/fonts/FreeSans.ttf");

	private final String path;

	FontTyp(final String path) {

		this.path = path;
	}

	public String getResource() {

		return FontProviderWrapper.class.getResource(path).toString();
	}
}
