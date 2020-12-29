// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.generator;

import java.text.MessageFormat;
import java.util.List;

import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.PdfContentByte;

import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;

/**
 * KaengurusprungurkundeHauptabschnittRendererEnglisch
 */
public class KaengurusprungurkundeHauptabschnittRendererEnglisch implements UrkundeHauptabschnittRenderer {

	private static final String MF_PATTERN_PUNKTE = "with {0} correct answers in a row";

	private final FontCalulator fontCalculator = new FontCalulator();

	@Override
	public int printAbschnittAndShiftVerticalPosition(final PdfContentByte content, final AbstractDatenUrkunde datenUrkunde, final int differenceFromTopPositionPoints) {

		int deltaY = differenceFromTopPositionPoints;

		Font font = UrkundePDFUtils.getFont(UrkundePDFUtils.SIZE_TITLE, datenUrkunde.urkundenmotiv().baseColor());

		UrkundeLinePrinter.printTextCenter(content, "Minikangaroo", font, deltaY);

		String text = datenUrkunde.wettbewerbsjahr();
		deltaY = fontCalculator.berechneDeltaY(text, deltaY, UrkundePDFUtils.SIZE_TITLE);
		UrkundeLinePrinter.printTextCenter(content, text, font, deltaY);

		text = "";
		deltaY += UrkundePDFUtils.POINTS_BETWEEN_PARAGRAPHS;
		deltaY = fontCalculator.berechneDeltaY(text, deltaY, UrkundePDFUtils.SIZE_TEXT_NORMAL);
		font = UrkundePDFUtils.getFontBlack(UrkundePDFUtils.SIZE_TEXT_NORMAL);
		UrkundeLinePrinter.printTextCenter(content, text, font, deltaY);

		deltaY += UrkundePDFUtils.POINTS_BETWEEN_PARAGRAPHS;

		FontSizeAndLines fontSizeAndLines = new SplitNameKindStrategie().getFontSizeAndLines(datenUrkunde.fullName());
		int fontSize = fontSizeAndLines.fontSize();
		font = UrkundePDFUtils.getFontBlack(fontSize);

		List<String> lines = fontSizeAndLines.lines().get();

		for (String line : lines) {

			deltaY = fontCalculator.berechneDeltaY(line, deltaY, fontSize);
			UrkundeLinePrinter.printTextCenter(content, line, font, deltaY);
		}

		font = UrkundePDFUtils.getFontBlack(UrkundePDFUtils.SIZE_TEXT_NORMAL);
		text = datenUrkunde.nameKlasse();
		deltaY = fontCalculator.berechneDeltaY(text, deltaY, UrkundePDFUtils.SIZE_TEXT_NORMAL);
		UrkundeLinePrinter.printTextCenter(content, text, font, deltaY);

		fontSizeAndLines = new SplitSchulnameStrategie().getFontSizeAndLines(datenUrkunde.nameSchule());
		fontSize = fontSizeAndLines.fontSize();
		font = UrkundePDFUtils.getFontBlack(fontSize);

		lines = fontSizeAndLines.lines().get();

		for (String line : lines) {

			deltaY = fontCalculator.berechneDeltaY(line, deltaY, fontSize);
			UrkundeLinePrinter.printTextCenter(content, line, font, deltaY);
		}

		deltaY += UrkundePDFUtils.POINTS_BETWEEN_PARAGRAPHS;

		fontSize = UrkundePDFUtils.SIZE_TEXT_NORMAL;
		text = MessageFormat.format(MF_PATTERN_PUNKTE, new Object[] { datenUrkunde.punktvalue() });

		deltaY = fontCalculator.berechneDeltaY(text, deltaY, fontSize);
		UrkundeLinePrinter.printTextCenter(content, text, UrkundePDFUtils.getFontBlack(fontSize), deltaY);

		deltaY += UrkundePDFUtils.POINTS_BETWEEN_ROWS;
		text = "made the farthest kangaroo jump.";
		UrkundeLinePrinter.printTextCenter(content, text, UrkundePDFUtils.getFontBlack(UrkundePDFUtils.SIZE_TEXT_NORMAL),
			deltaY);

		return deltaY;
	}
}
