// =====================================================
// Projekt: mkv-api-gateway
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mkv_api_gateway.error;

/**
 * ConcurrentUpdateException
 */
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
