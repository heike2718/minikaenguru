// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import de.egladil.web.mk_gateway.domain.AbstractDomainServiceTest;
import de.egladil.web.mk_gateway.domain.adv.VertragAuftragsverarbeitungRepository;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.statistik.AuswertungsmodusInfoService;
import de.egladil.web.mk_gateway.domain.teilnahmen.AktuelleTeilnahmeService;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchuleDetailsService;
import de.egladil.web.mk_gateway.domain.teilnahmen.SchulenOverviewService;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;

/**
 * SchulenAnmeldeinfoServiceWithInMemoryDatabaseTest
 */
public class SchulenAnmeldeinfoServiceWithInMemoryDatabaseTest extends AbstractDomainServiceTest {

	private SchulenAnmeldeinfoService service;

	private MkKatalogeResourceAdapter katalogeAdapter;

	private VertragAuftragsverarbeitungRepository advRepository;

	private AuswertungsmodusInfoService auswertungsmodusInfoService;

	@Override
	@BeforeEach
	protected void setUp() {

		super.setUp();
		AktuelleTeilnahmeService teilnahmenService = AktuelleTeilnahmeService.createForTest(getTeilnahmenRepository(),
			getWettbewerbService(), getVeranstalterRepository());

		katalogeAdapter = Mockito.mock(MkKatalogeResourceAdapter.class);
		advRepository = Mockito.mock(VertragAuftragsverarbeitungRepository.class);

		auswertungsmodusInfoService = Mockito.mock(AuswertungsmodusInfoService.class);

		SchulenOverviewService schulenOverviewService = SchulenOverviewService.createForTest(getVeranstalterRepository(),
			teilnahmenService, auswertungsmodusInfoService);
		SchuleDetailsService schuleDetailsService = SchuleDetailsService.createForTest(teilnahmenService,
			getSchulkollegienRepository(), getTeilnahmenRepository(), getVeranstalterRepository(), advRepository);
		service = SchulenAnmeldeinfoService.createForTest(katalogeAdapter, schulenOverviewService, schuleDetailsService,
			Mockito.mock(AuswertungsmodusInfoService.class),
			Mockito.mock(AktuelleTeilnahmeService.class));

	}

	@Test
	void should_FindSchulenMitAnmeldeinfo_work_when_lehrerOhneSchule() {

		// Act
		List<SchuleAPIModel> result = service.findSchulenMitAnmeldeinfo(UUID_LEHRER_OHNE_SCHULE);

		// Assert
		assertEquals(0, result.size());
	}

}
