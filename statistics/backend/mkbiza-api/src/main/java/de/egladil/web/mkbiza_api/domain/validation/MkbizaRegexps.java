// =====================================================
// Project: mkbiza-api
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkbiza_api.domain.validation;

/**
 * MkbizaRegexps
 */
public interface MkbizaRegexps {

	String VALID_JAHR = "^[\\d]{4}$";

	String MSG_INVALID_JAHR = "jahr ist nicht numerisch oder hat nicht die richtige Länge";

	String VALID_AUFGABENNUMMER = "^[ABC]-\\d$";

	String MSG_INVALID_AUFGABENNUMMER = "nummer enthält ungültige Zeichen: erwarte A-1 bis C-5";

}
