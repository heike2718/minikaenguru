// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.admin;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_kataloge.domain.SchuleRepository;
import de.egladil.web.mk_kataloge.domain.apimodel.LandPayload;
import de.egladil.web.mk_kataloge.domain.error.KatalogAPIException;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Land;
import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

/**
 * RenameLandService
 */
@ApplicationScoped
public class RenameLandService {

	private final static Logger LOG = LoggerFactory.getLogger(RenameLandService.class);

	@Inject
	SchuleRepository schuleRepository;

	static RenameLandService createForTest(final SchuleRepository schuleRepository) {

		RenameLandService result = new RenameLandService();
		result.schuleRepository = schuleRepository;
		return result;
	}

	public ResponsePayload updateLand(final LandPayload landPayload) {

		try {

			List<Land> laender = schuleRepository.loadLaender();

			Optional<Land> optLand = laender.stream().filter(l -> l.getKuerzel().equals(landPayload.kuerzel())).findFirst();

			if (optLand.isEmpty()) {

				throw new NotFoundException(Response.status(404)
					.entity(new ResponsePayload(MessagePayload.error("Dieses Land gibt es nicht."), landPayload)).build());
			}

			optLand = laender.stream()
				.filter(l -> !l.getKuerzel().equals(landPayload.kuerzel()) && l.getName().equalsIgnoreCase(landPayload.name()))
				.findFirst();

			if (optLand.isPresent()) {

				return new ResponsePayload(MessagePayload.warn(
					"Umbenennung abgelehnt: Es gibt bereits ein anderes Land mit dem Namen " + landPayload.name()
						+ ". Dieses wurde zurückgegeben."),
					LandPayload.create(optLand.get()));
			}

			List<Schule> schulen = schuleRepository.findSchulenInLand(landPayload.kuerzel());

			schulen.forEach(s -> {

				s.setLandName(landPayload.name());
				s.setLandKuerzel(s.getLandKuerzel());
			});

			schuleRepository.replaceSchulen(schulen);

			LOG.info("Anzahl geänderter Schulen: {}", schulen.size());

			return new ResponsePayload(MessagePayload.info(
				"Das Land wurde erfolgreich umbenannt. Anzahl geänderter Schulen: " + schulen.size()),
				landPayload);

		} catch (PersistenceException e) {

			LOG.error("Die Schulen mit Land {} konnten nicht geändert werden: {}", landPayload, e.getMessage(), e);
			throw new KatalogAPIException("Das Land konnte wegen eines Serverfehlers nicht umbenannt werden.");
		}

	}

}
