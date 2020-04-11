// =====================================================
// Projekt: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.exception;

/**
 * ConcurrentUpdateException
 */
@Deprecated(forRemoval = true)
public class ConcurrentUpdateException extends RuntimeException {

	/* serialVersionUID */
	private static final long serialVersionUID = 1L;

	public ConcurrentUpdateException(final String arg0) {

		super(arg0);
	}

	public ConcurrentUpdateException(final String arg0, final Throwable arg1) {

		super(arg0, arg1);
	}
}
