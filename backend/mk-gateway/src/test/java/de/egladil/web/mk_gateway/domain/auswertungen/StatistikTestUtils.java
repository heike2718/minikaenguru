// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import java.io.InputStream;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * StatistikTestUtils
 */
public class StatistikTestUtils {

	public static List<Loesungszettel> loadTheLoesungszettel() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		try (InputStream in = StatistikTestUtils.class.getResourceAsStream("/loesungszettelCollection.json")) {

			LoesungszettelList liste = objectMapper.readValue(in, LoesungszettelList.class);

			return liste.getLoesungszettel();

		}

	}

}
