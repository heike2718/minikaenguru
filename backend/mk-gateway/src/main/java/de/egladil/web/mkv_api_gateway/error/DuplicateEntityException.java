// =====================================================
// Projekt: mk-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mkv_api_gateway.error;

/**
 * DuplicateEntityException
 */
public class DuplicateEntityException extends RuntimeException {

	/* serialVersionUID */
	private static final long serialVersionUID = 1L;

	/**
	 * Erzeugt eine Instanz von DuplicateEntityException
	 */
	public DuplicateEntityException(final String arg0, final Throwable arg1) {

		super(arg0, arg1);
	}

	/**
	 * Erzeugt eine Instanz von DuplicateEntityException
	 */
	public DuplicateEntityException(final String arg0) {

		super(arg0);
	}

}
