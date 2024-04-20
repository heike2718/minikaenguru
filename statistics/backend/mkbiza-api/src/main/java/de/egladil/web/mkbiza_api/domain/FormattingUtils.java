// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * FormattingUtils
 */
public class FormattingUtils {

	public static String doubleAsString(final double value) {

		DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance(Locale.GERMAN);

		DecimalFormatSymbols symbols = df.getDecimalFormatSymbols();
		symbols.setDecimalSeparator(',');
		df.setDecimalFormatSymbols(symbols);

		return df.format(value);
	}
}
