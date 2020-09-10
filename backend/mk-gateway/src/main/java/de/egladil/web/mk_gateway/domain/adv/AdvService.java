// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.adv;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterAuthorizationService;

/**
 * AdvService
 */
@ApplicationScoped
public class AdvService {

	@Inject
	VertragAuftragsverarbeitungRepository vertragRepository;

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

}
