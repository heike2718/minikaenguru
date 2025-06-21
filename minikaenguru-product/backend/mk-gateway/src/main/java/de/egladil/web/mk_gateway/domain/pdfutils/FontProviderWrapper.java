// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.pdfutils;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.pdf.BaseFont;

/**
 * FontProviderWrapper
 */
public class FontProviderWrapper {

	public Font getFont(final FontTyp typ, final int size) {

		FontFactory.register(typ.getResource());
		final Font font = FontFactory.getFont(typ.getResource(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED, size);
		return font;
	}

}
