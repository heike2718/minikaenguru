// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;

/**
 * ChangeNewsletterAboService
 */
@ApplicationScoped
public class ChangeNewsletterAboService {

	private static final Logger LOG = LoggerFactory.getLogger(ChangeNewsletterAboService.class);

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	LoggableEventDelegate eventDelegate;

	@Inject
	VeranstalterRepository veranstalterRepository;

	public static ChangeNewsletterAboService createForTest(final VeranstalterRepository veranstalterRepository) {

		ChangeNewsletterAboService result = new ChangeNewsletterAboService();
		result.veranstalterRepository = veranstalterRepository;
		return result;
	}

	/**
	 * @param uuid
	 */
	@Transactional
	public Veranstalter changeStatusNewsletter(final String uuid) {

		Optional<Veranstalter> optVeranstalter = this.veranstalterRepository.ofId(new Identifier(uuid));

		if (optVeranstalter.isEmpty()) {

			String msg = "Versuch, einen nicht existierenden Veranstalter zu ändern: " + uuid;

			eventDelegate.fireSecurityEvent(msg, domainEventHandler);

			LOG.warn(msg);

			throw new BadRequestException(msg);
		}

		Veranstalter veranstalter = optVeranstalter.get();
		veranstalter.toggleAboNewsletter();

		this.veranstalterRepository.changeVeranstalter(veranstalter);

		return veranstalter;
	}
}
