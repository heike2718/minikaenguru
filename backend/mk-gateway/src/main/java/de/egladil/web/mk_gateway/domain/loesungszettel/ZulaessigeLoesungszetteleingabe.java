// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

/**
 * ZulaessigeLoesungszetteleingabe
 */
public enum ZulaessigeLoesungszetteleingabe {

	A,
	B,
	C,
	D,
	E,
	N;

	public static ZulaessigeLoesungszetteleingabe valueOfChar(final char c) {

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
