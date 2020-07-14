// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.error;

/**
 * DataInconsistencyException
 */
public class DataInconsistencyException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public DataInconsistencyException(final String message) {

		super(message);

	}

}
