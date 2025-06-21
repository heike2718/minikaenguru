// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.entities.InMemoryLoesungszettelList;

/**
 * StatistikTestUtils
 */
public class StatistikTestUtils {

	public static List<Loesungszettel> loadTheLoesungszettel() throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		try (InputStream in = StatistikTestUtils.class.getResourceAsStream("/loesungszettelCollection.json")) {

			InMemoryLoesungszettelList liste = objectMapper.readValue(in, InMemoryLoesungszettelList.class);

			return liste.getLoesungszettel();

		}

	}

	public static List<Loesungszettel> loadTheLoesungszettel(final int jahr) throws Exception {

		ObjectMapper objectMapper = new ObjectMapper();

		try (InputStream in = StatistikTestUtils.class.getResourceAsStream("/loesungszettelCollection.json")) {

			InMemoryLoesungszettelList liste = objectMapper.readValue(in, InMemoryLoesungszettelList.class);

			return liste.getLoesungszettel().stream().filter(lo -> lo.teilnahmeIdentifier().jahr() == jahr)
				.collect(Collectors.toList());

		}

	}

	public static void print(final DownloadData data, final boolean deleteOnExit) throws Exception {

		String prefix = data.filename().substring(0, data.filename().length() - 4) + "-";

		File pdf = File.createTempFile(prefix, ".pdf");

		if (deleteOnExit) {

			pdf.deleteOnExit();
		}

		try (FileOutputStream out = new FileOutputStream(pdf)) {

			IOUtils.write(data.data(), out);

			out.flush();

			if (!deleteOnExit) {

				System.out.println("output ist here: " + pdf.getAbsolutePath());
			}
		}
	}

}
