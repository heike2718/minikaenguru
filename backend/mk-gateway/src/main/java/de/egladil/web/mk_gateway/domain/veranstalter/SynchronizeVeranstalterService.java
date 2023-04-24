// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_net.exception.SessionExpiredException;
import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MessagingAuthException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.user.UserRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.api.ChangeUserCommand;
import de.egladil.web.mk_gateway.domain.veranstalter.events.LehrerChanged;
import de.egladil.web.mk_gateway.infrastructure.messaging.HandshakeAck;
import de.egladil.web.mk_gateway.infrastructure.messaging.LoescheVeranstalterCommand;
import de.egladil.web.mk_gateway.infrastructure.messaging.SyncHandshake;

/**
 * SynchronizeVeranstalterService
 */
@ApplicationScoped
public class SynchronizeVeranstalterService {

	private static final Logger LOG = LoggerFactory.getLogger(SynchronizeVeranstalterService.class);

	@ConfigProperty(name = "syncToken.validity.period.minutes")
	int syncTokenValidityPeriod;

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	LoggableEventDelegate eventDelegate;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	UserRepository userRepository;

	@Inject
	SchulkollegienService schulkollegienService;

	private Map<String, SyncHandshake> syncSessions = new ConcurrentHashMap<String, SyncHandshake>();

	public HandshakeAck createHandshakeAck(final SyncHandshake handshake) {

		String syncSessionId = UUID.randomUUID().toString();
		long expiresAt = CommonTimeUtils.getInterval(LocalDateTime.now(), syncTokenValidityPeriod, ChronoUnit.MINUTES).getEndTime()
			.getTime();
		handshake.setExpiresAt(expiresAt);

		syncSessions.put(syncSessionId, handshake);
		return new HandshakeAck(syncSessionId, handshake.nonce());
	}

	@Transactional
	public void changeVeranstalterDaten(final ChangeUserCommand command) {

		this.verifySession(command.syncToken());
		this.syncSessions.remove(command.syncToken());

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(new Identifier(command.uuid()));

		if (optVeranstalter.isEmpty()) {

			LOG.info("Veranstalter mit UUID {} unbekannt. Wird ignoriert", command.uuid());

			return;
		}

		Veranstalter veranstalter = optVeranstalter.get();

		Veranstalter merged = veranstalter.merge(new Person(command.uuid(), command.fullName()).withEmail(command.email()));

		veranstalterRepository.changeVeranstalter(merged);

		LOG.info("Daten aus authprovider nach VERANSTALTER uebernommen: {}", command);

		if (Rolle.LEHRER == veranstalter.rolle()) {

			Lehrer lehrer = (Lehrer) merged;

			String schulkuerzel = lehrer.joinedSchulen();

			LehrerChanged lehrerChanged = new LehrerChanged(merged.person(), schulkuerzel, schulkuerzel,
				lehrer.isNewsletterEmpfaenger());

			this.schulkollegienService.handleLehrerChanged(lehrerChanged);

			LOG.info("Daten aus authprovider nach SCHULKOLLEGIEN uebernommen: schulkuerzel={}", schulkuerzel);
		}
	}

	@Transactional
	public void loescheVeranstalter(final LoescheVeranstalterCommand command) {

		this.verifySession(command.syncToken());
		this.syncSessions.remove(command.syncToken());

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(new Identifier(command.uuid()));

		if (optVeranstalter.isEmpty()) {

			LOG.info("Veranstalter mit UUID {} unbekannt. Wird ignoriert", command.uuid());

			return;
		}

		Veranstalter veranstalter = optVeranstalter.get();

		if (veranstalter.rolle() == Rolle.LEHRER) {

			this.schulkollegienService.entferneSpurenDesLehrers((Lehrer) veranstalter);
		}

		veranstalterRepository.removeVeranstalter(veranstalter);
		userRepository.removeUser(command.uuid());
	}

	public void verifySession(final String syncSessionId) {

		SyncHandshake handshake = this.syncSessions.get(syncSessionId);

		if (handshake == null) {

			String msg = "Aufruf sync mit falscher sessionId '" + syncSessionId + "'";
			LOG.warn("{}", msg);
			eventDelegate.fireSecurityEvent(msg, domainEventHandler);
			throw new MessagingAuthException(msg);
		}

		if (System.currentTimeMillis() > handshake.getExpiresAt()) {

			throw new SessionExpiredException("SyncSession ist nicht mehr gültig");
		}

	}

	/**
	 * @param syncToken
	 */
	public void removeSyncToken(final String syncToken) {

		this.syncSessions.remove(syncToken);
	}
}
