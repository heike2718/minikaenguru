// =====================================================
// Projekt: authprovider
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.exception;

/**
 * DuplicateEntityException
 */
@Deprecated(forRemoval = true)
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
