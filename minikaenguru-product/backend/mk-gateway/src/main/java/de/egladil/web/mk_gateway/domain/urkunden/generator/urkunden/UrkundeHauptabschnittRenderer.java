// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden;

import com.itextpdf.text.pdf.PdfContentByte;

import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;

/**
 * UrkundeHauptabschnittRenderer
 */
public interface UrkundeHauptabschnittRenderer {

	/**
	 * Druckt seinen Teil in den content und gibt die finale y-Position zurück.
	 *
	 * @param  content
	 * @param  datenUrkunde
	 * @param  differenceFromTopPositionPoints
	 * @return
	 */
	int printAbschnittAndShiftVerticalPosition(final PdfContentByte content, final AbstractDatenUrkunde datenUrkunde, final int differenceFromTopPositionPoints);

}
