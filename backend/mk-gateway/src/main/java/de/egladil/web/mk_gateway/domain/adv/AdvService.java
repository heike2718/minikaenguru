// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.veranstalter.VertragAdvAPIModel;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.domain.veranstalter.SchuleKatalogResponseMapper;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterAuthorizationService;

/**
 * AdvService
 */
@ApplicationScoped
public class AdvService {

	private static final Logger LOG = LoggerFactory.getLogger(AdvService.class);

	private static final String PATH_SUBDIR_ADV_TEXTE = "/adv/";

	@ConfigProperty(name = "path.external.files")
	String pathAdvTexteDir;

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	@Inject
	MkKatalogeResourceAdapter katalogeResourceAdapter;

	@Inject
	VertragAuftragsverarbeitungRepository vertragRepository;

	@Inject
	VertragstextRepository vertragstextRepository;

	@Inject
	VeranstalterAuthorizationService authorizationService;

	@Inject
	VertragAuftragsverarbeitungPdfGenerator pdfGenerator;

	/**
	 * Falls die Schule einen Vertrag abgeschlossen hat und der gegebene Lehrer dieser Schule angehört, wird das PDF generiert und
	 * zurückgegeben.
	 *
	 * @param  schulkuerzel
	 * @param  lehrerUuid
	 * @return
	 */
	public DownloadData getVertragAuftragsdatenverarbeitung(final String schulkuerzel, final String lehrerUuid) {

		Identifier schuleIdentifier = new Identifier(schulkuerzel);
		authorizationService.checkPermissionForTeilnahmenummer(new Identifier(lehrerUuid),
			schuleIdentifier);

		Optional<VertragAuftragsdatenverarbeitung> optVertrag = vertragRepository.findVertragForSchule(schuleIdentifier);

		if (optVertrag.isEmpty()) {

			throw new NotFoundException();
		}

		VertragAuftragsdatenverarbeitung vertrag = optVertrag.get();

		byte[] data = pdfGenerator.generatePdf(vertrag);

		return new DownloadData(vertrag.filename(), data);

	}

	/**
	 * Stellt den aktuellen Vertragstext als PDF zur Verfügung.
	 *
	 * @return DownloadData
	 */
	public DownloadData getAktuellenVertragstextAlsPdf() {

		Vertragstext vertragstext = this.getAktuellenVertragstext();

		String path = pathAdvTexteDir + PATH_SUBDIR_ADV_TEXTE + vertragstext.dateiname();

		final byte[] pdfAllgemein = MkGatewayFileUtils.readBytesFromFile(path);

		return new DownloadData(vertragstext.dateiname(), pdfAllgemein);
	}

	/**
	 * Erzeugt einen Vertrag zur Auftragsdatenverarbeitung mit dem aktuellen Vertragstext.
	 *
	 * @param  daten
	 * @param  lehrerUuid
	 * @return
	 */
	public String createVertragAuftragsdatenverarbeitung(final VertragAdvAPIModel daten, final String lehrerUuid) {

		Identifier schuleIdentifier = new Identifier(daten.schulkuerzel());

		authorizationService.checkPermissionForTeilnahmenummer(new Identifier(lehrerUuid),
			schuleIdentifier);

		Optional<VertragAuftragsdatenverarbeitung> optVertrag = vertragRepository.findVertragForSchule(schuleIdentifier);

		if (optVertrag.isPresent()) {

			return optVertrag.get().uuid();
		}

		Optional<SchuleAPIModel> optSchule = this.findSchuleQuietly(daten.schulkuerzel());

		PostleitzahlLand plzLand = new PostleitzahlLand(daten.plz(), optSchule);

		Vertragstext vertragstext = this.getAktuellenVertragstext();

		String unterzeichnetAm = CommonTimeUtils.format(CommonTimeUtils.now());

		VertragAuftragsdatenverarbeitung vertrag = VertragAuftragsdatenverarbeitung.createFromPayload(daten, plzLand)
			.withSchulkuerzel(schuleIdentifier)
			.withVertragstext(vertragstext)
			.withUnterzeichnenderLehrer(new Identifier(lehrerUuid)).withUnterzeichnetAm(unterzeichnetAm);

		Identifier identifierVertrag = vertragRepository.addVertrag(vertrag);

		return identifierVertrag.identifier();
	}

	private Optional<SchuleAPIModel> findSchuleQuietly(final String schulkuerzel) {

		try {

			Response katalogeResponse = katalogeResourceAdapter.findSchulen(schulkuerzel);

			List<SchuleAPIModel> trefferliste = new SchuleKatalogResponseMapper().getSchulenFromKatalogeAPI(katalogeResponse);

			return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));

		} catch (MkGatewayRuntimeException e) {

			LOG.warn("Können Schule nicht ermitteln: {}", e.getMessage());
			return Optional.empty();
		}
	}

	private Vertragstext getAktuellenVertragstext() {

		List<Vertragstext> vertragstexte = vertragstextRepository.loadVertragstexte();

		if (vertragstexte.isEmpty()) {

			String msg = "Es gibt keinen Vertragstext";

			LOG.error(msg);
			new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);

			throw new MkGatewayRuntimeException(msg);
		}

		Collections.sort(vertragstexte, new VertragstextVersionComparator());

		return vertragstexte.get(vertragstexte.size() - 1);
	}
}
