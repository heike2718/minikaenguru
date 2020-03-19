// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.exceptions;

/**
 * DuplicateEntityException
 */
public class DuplicateEntityException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	/**
	 *
	 */
	public DuplicateEntityException() {

	}

	/**
	 * @param message
	 */
	public DuplicateEntityException(final String message) {

		super(message);

	}

}
