// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.statistik;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.statistik.StatistikWettbewerbService;
import de.egladil.web.mk_gateway.domain.statistik.api.AnmeldungenAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.api.AnmeldungsitemAPIModel;
import de.egladil.web.mk_gateway.domain.statistik.api.MedianAPIModel;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mkv_server_tests.AbstractIntegrationTest;

/**
 * StatistikWettbewerbServiceIT
 */
public class StatistikWettbewerbServiceIT extends AbstractIntegrationTest {

	private StatistikWettbewerbService service;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();

		try (InputStream in = getClass().getResourceAsStream("/kataloge/schulenAPIModel.json");
			StringWriter sw = new StringWriter()) {

			IOUtils.copy(in, sw, Charset.forName("UTF-8"));

			service = StatistikWettbewerbService.createForIntegrationTest(entityManager, sw.toString());

		} catch (Exception e) {

			fail("kann Schulen nicht laden");
		}

	}

	@Test
	void should_calculateStatistikWithMediane_when_jahr2018() {

		// Arrange
		Integer jahr = Integer.valueOf(2018);

		// Act
		ResponsePayload result = service.getBeteiligungen(jahr);

		// Assert
		assertNotNull(result.getData());
		AnmeldungenAPIModel data = (AnmeldungenAPIModel) result.getData();

		{

			AnmeldungsitemAPIModel anmeldungsitemAPIModel = data.getPrivatanmeldungen();
			assertEquals(20, anmeldungsitemAPIModel.getAnzahlAnmeldungen());
			assertEquals(67, anmeldungsitemAPIModel.getAnzahlLoesungszettel());
			List<MedianAPIModel> mediane = anmeldungsitemAPIModel.getMediane();

			assertEquals(3, mediane.size());

			{

				MedianAPIModel medianModel = mediane.get(0);
				assertEquals(Klassenstufe.IKID, medianModel.getKlassenstufe());
				assertEquals("12,0", medianModel.getMedian());
				assertEquals(2, medianModel.getAnzahlLoesungszettel());
			}

			{

				MedianAPIModel medianModel = mediane.get(1);
				assertEquals(Klassenstufe.EINS, medianModel.getKlassenstufe());
				assertEquals("17,125", medianModel.getMedian());
				assertEquals(46, medianModel.getAnzahlLoesungszettel());
			}

			{

				MedianAPIModel medianModel = mediane.get(2);
				assertEquals(Klassenstufe.ZWEI, medianModel.getKlassenstufe());
				assertEquals("37,25", medianModel.getMedian());
				assertEquals(19, medianModel.getAnzahlLoesungszettel());
			}

			// mediane.forEach(m -> System.out.println(m.toString()));

		}

		{

			AnmeldungsitemAPIModel anmeldungsitemAPIModel = data.getSchulanmeldungen();
			assertEquals(20, anmeldungsitemAPIModel.getAnzahlAnmeldungen());
			assertEquals(163, anmeldungsitemAPIModel.getAnzahlLoesungszettel());
			List<MedianAPIModel> mediane = anmeldungsitemAPIModel.getMediane();

			assertEquals(3, mediane.size());

			// mediane.forEach(m -> System.out.println(m.toString()));

			{

				MedianAPIModel medianModel = mediane.get(0);
				assertEquals(Klassenstufe.IKID, medianModel.getKlassenstufe());
				assertEquals("18,0", medianModel.getMedian());
				assertEquals(7, medianModel.getAnzahlLoesungszettel());
			}

			{

				MedianAPIModel medianModel = mediane.get(1);
				assertEquals(Klassenstufe.EINS, medianModel.getKlassenstufe());
				assertEquals("26,25", medianModel.getMedian());
				assertEquals(79, medianModel.getAnzahlLoesungszettel());
			}

			{

				MedianAPIModel medianModel = mediane.get(2);
				assertEquals(Klassenstufe.ZWEI, medianModel.getKlassenstufe());
				assertEquals("34,25", medianModel.getMedian());
				assertEquals(77, medianModel.getAnzahlLoesungszettel());
			}

		}

		List<AnmeldungsitemAPIModel> laender = data.getLaender();

		assertEquals(8, laender.size());

		{

			Optional<AnmeldungsitemAPIModel> opt = laender.stream().filter(l -> "Nordrhein-Westfalen".equals(l.getName()))
				.findFirst();
			assertTrue(opt.isPresent());

			AnmeldungsitemAPIModel anmeldungsitemAPIModel = opt.get();
			List<MedianAPIModel> mediane = anmeldungsitemAPIModel.getMediane();
			assertEquals(1, mediane.size());

		}

		{

			Optional<AnmeldungsitemAPIModel> opt = laender.stream().filter(l -> "Mecklenburg-Vorpommern".equals(l.getName()))
				.findFirst();
			assertTrue(opt.isPresent());

			AnmeldungsitemAPIModel anmeldungsitemAPIModel = opt.get();
			List<MedianAPIModel> mediane = anmeldungsitemAPIModel.getMediane();
			assertEquals(1, mediane.size());

			// mediane.forEach(m -> System.out.println(m.toString()));
		}

		{

			// Brandenburg
			Optional<AnmeldungsitemAPIModel> opt = laender.stream().filter(l -> "Brandenburg".equals(l.getName()))
				.findFirst();
			assertTrue(opt.isPresent());

			AnmeldungsitemAPIModel anmeldungsitemAPIModel = opt.get();
			List<MedianAPIModel> mediane = anmeldungsitemAPIModel.getMediane();
			assertEquals(3, mediane.size());

			mediane.forEach(m -> System.out.println(m.toString()));
		}

	}

}
