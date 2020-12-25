// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.loesungszettel;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import de.egladil.web.mk_gateway.domain.loesungszettel.api.LoesungszettelZeileAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * LoesungszettelRohdatenAPIZeileMapper
 */
public class LoesungszettelRohdatenAPIZeileMapper implements Function<LoesungszettelRohdaten, List<LoesungszettelZeileAPIModel>> {

	private final Klassenstufe klassenstufe;

	public LoesungszettelRohdatenAPIZeileMapper(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
	}

	@Override
	public List<LoesungszettelZeileAPIModel> apply(final LoesungszettelRohdaten rohdaten) {

		String antwortcode = rohdaten.antwortcode();

		List<LoesungszettelZeileAPIModel> result = new ArrayList<>(antwortcode.length());

		for (int i = 0; i < antwortcode.length(); i++) {

			char code = antwortcode.charAt(i);

			LoesungszettelZeileAPIModel zeile = new LoesungszettelZeileAPIModel()
				.withAnzahlSpalten(klassenstufe.getAnzahlSpalten())
				.withEingabe(ZulaessigeLoesungszetteleingabe.valueOfChar(code))
				.withIndex(i)
				.withName(getNameZeile(i));

			result.add(zeile);
		}

		return result;
	}

	String getNameZeile(final int index) {

		switch (index) {

		case 0:

			return "A-1";

		case 1:
			return "A-2";

		case 2: {

			switch (klassenstufe) {

			case IKID:

				return "B-1";

			default:
				return "A-3";
			}
		}

		case 3: {

			switch (klassenstufe) {

			case IKID:

				return "B-2";

			default:
				return "A-4";
			}
		}

		case 4: {

			switch (klassenstufe) {

			case IKID:

				return "C-1";

			case EINS:
				return "B-1";

			default:
				return "A-5";
			}
		}

		case 5: {

			switch (klassenstufe) {

			case IKID:

				return "C-2";

			case EINS:
				return "B-2";

			default:
				return "B-1";
			}
		}

		case 6: {

			switch (klassenstufe) {

			case EINS:
				return "B-3";

			default:
				return "B-2";
			}
		}

		case 7: {

			switch (klassenstufe) {

			case EINS:
				return "B-4";

			default:
				return "B-3";
			}
		}

		case 8: {

			switch (klassenstufe) {

			case EINS:
				return "C-1";

			default:
				return "B-4";
			}
		}

		case 9: {

			switch (klassenstufe) {

			case EINS:
				return "C-2";

			default:
				return "B-5";
			}
		}

		case 10: {

			switch (klassenstufe) {

			case EINS:
				return "C-2";

			default:
				return "C-1";
			}
		}

		case 11: {

			switch (klassenstufe) {

			case EINS:
				return "C-3";

			default:
				return "C-2";
			}
		}

		case 12: {

			switch (klassenstufe) {

			case EINS:
				return "C-4";

			default:
				return "C-3";
			}
		}

		case 13: {

			return "C-4";
		}

		case 14: {

			return "C-5";
		}

		default:
			break;
		}

		return null;
	}

}
