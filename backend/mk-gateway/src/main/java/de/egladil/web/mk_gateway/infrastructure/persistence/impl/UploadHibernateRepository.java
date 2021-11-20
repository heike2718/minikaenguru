// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.uploads.UploadIdentifier;
import de.egladil.web.mk_gateway.domain.uploads.UploadRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.PersistenterUpload;

/**
 * UploadHibernateRepository
 */
@RequestScoped
public class UploadHibernateRepository implements UploadRepository {

	private static final Logger LOGGER = LoggerFactory.getLogger(UploadHibernateRepository.class);

	@Inject
	EntityManager entityManager;

	public static UploadHibernateRepository createForIntegrationTests(final EntityManager em) {

		UploadHibernateRepository result = new UploadHibernateRepository();
		result.entityManager = em;
		return result;
	}

	@Override
	public List<PersistenterUpload> findUploadsWithTeilnahmenummer(final String teilnahmenummer) {

		if (teilnahmenummer == null) {

			LOGGER.error("Abfrage mit teilnahmenummer null");
			return new ArrayList<>();
		}

		return entityManager.createNamedQuery(PersistenterUpload.FIND_BY_TEILNAHMENUMMER, PersistenterUpload.class)
			.setParameter("teilnahmenummer", teilnahmenummer).getResultList();
	}

	@Override
	public Optional<PersistenterUpload> findUploadByIdentifier(final UploadIdentifier uploadIdentifier) {

		if (uploadIdentifier == null) {

			throw new NullPointerException("uploadIdentifier");
		}

		List<PersistenterUpload> trefferliste = entityManager
			.createNamedQuery(PersistenterUpload.FIND_BY_IDENTIFIER, PersistenterUpload.class)
			.setParameter("teilnahmenummer", uploadIdentifier.getTeilnahmenummer())
			.setParameter("checksumme", uploadIdentifier.getChecksumme()).getResultList();

		if (trefferliste.size() > 1) {

			String veranstalterUuids = StringUtils
				.join(trefferliste.stream().map(pu -> pu.getBenutzerUuid()).collect(Collectors.toList()), ",");

			LOGGER.error("Mehr als ein Upload mit UploadIdentifier={} gefunden: hochgeladen durch {}", uploadIdentifier,
				veranstalterUuids);
			throw new MkGatewayRuntimeException("Datenmischmasch");
		}

		return trefferliste.isEmpty() ? Optional.empty() : Optional.of(trefferliste.get(0));
	}

	@Override
	public Optional<PersistenterUpload> findByUuid(final String uuid) {

		PersistenterUpload result = entityManager.find(PersistenterUpload.class, uuid);

		return result != null ? Optional.of(result) : Optional.empty();
	}

	@Override
	public PersistenterUpload addUploadMetaData(final PersistenterUpload upload) {

		entityManager.persist(upload);
		return upload;
	}

	@Override
	@Transactional
	public PersistenterUpload updateUpload(final PersistenterUpload persistenterUpload) {

		return entityManager.merge(persistenterUpload);
	}
}
