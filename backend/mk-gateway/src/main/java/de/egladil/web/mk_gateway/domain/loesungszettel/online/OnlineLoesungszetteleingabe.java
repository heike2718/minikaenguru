// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel.online;

/**
 * OnlineLoesungszetteleingabe
 */
public enum OnlineLoesungszetteleingabe {

	A,
	B,
	C,
	D,
	E,
	N;

	public static OnlineLoesungszetteleingabe valueOfChar(final char c) {

		switch (c) {

		case 'A':
			return A;

		case 'B':
			return B;

		case 'C':
			return C;

		case 'D':
			return D;

		case 'E':
			return E;

		default:
			return N;
		}

	}

}
