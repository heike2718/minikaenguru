// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.general;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.auth.signup.SignUpService;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.veranstalter.SynchronizeVeranstalterService;
import de.egladil.web.mk_gateway.domain.veranstalter.api.ChangeUserCommand;
import de.egladil.web.mk_gateway.domain.veranstalter.api.CreateUserCommand;
import de.egladil.web.mk_gateway.domain.veranstalter.events.DeleteVeranstalterFailed;
import de.egladil.web.mk_gateway.domain.veranstalter.events.SynchronizeVeranstalterFailed;
import de.egladil.web.mk_gateway.infrastructure.messaging.HandshakeAck;
import de.egladil.web.mk_gateway.infrastructure.messaging.LoescheVeranstalterCommand;
import de.egladil.web.mk_gateway.infrastructure.messaging.SyncHandshake;

/**
 * SyncVeranstalterDataResource ist eine REST-API, bei Änderungen aus der Benutzerverwaltung aufgerufen wird, um diese an Clients
 * des IdentityProviders zu übermitteln. Solche Änderungen sind
 * <ul>
 * <li>neues Benutzerkonto</li>
 * <li>Änderungen der Benutzerattribute email und fullName</li>
 * <li>Löschung eines Benutzerkontos</li>
 * </ul>
 */
@RequestScoped
@Path(value = "sync")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class SyncVeranstalterDataResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(SyncVeranstalterDataResource.class);

	@ConfigProperty(name = "mkv-app.client-id")
	String clientId;

	@Inject
	DomainEventHandler domainEventHandler;

	@Inject
	SynchronizeVeranstalterService syncService;

	@Inject
	SignUpService signUpService;

	@POST
	@Path("ack")
	public Response getSyncToken(final SyncHandshake data) {

		if (!clientId.equals(data.sendingClientId())) {

			String msg = "Aufruf POST /sync/ack mit falscher ClientID '" + data.sendingClientId() + "'";
			LOGGER.warn("{}: {}", msg, data);
			new LoggableEventDelegate().fireSecurityEvent(msg, domainEventHandler);
			throw new AccessDeniedException(msg);
		}

		HandshakeAck ack = this.syncService.createHandshakeAck(data);

		return Response.ok(new ResponsePayload(MessagePayload.ok(), ack)).build();

	}

	@POST
	@Path("veranstalter")
	public Response createVeranstalter(final CreateUserCommand createUserCommand) {

		if (!clientId.equals(createUserCommand.getClientId())) {

			LOGGER.info("nicht die erwartete ClientId => uninteressant");
			return Response.ok(ResponsePayload.messageOnly(MessagePayload.ok())).build();
		}

		LOGGER.info("nonce=" + createUserCommand.getNonce());

		try {

			this.signUpService.verifySyncTokenAndCreateUser(createUserCommand);

			return Response.ok(ResponsePayload.messageOnly(MessagePayload.ok())).build();
		} catch (PersistenceException e) {

			LOGGER.error("Veranstalter mit UUID {} wurde nicht angelegt: {}", createUserCommand.getUuid(), e.getMessage(), e);

			if (domainEventHandler != null) {

				domainEventHandler.handleEvent(SynchronizeVeranstalterFailed.fromMessagingCommand(createUserCommand));

			}
			throw new MkGatewayRuntimeException("Veranstalter synchronisieren schlug fehl!");
		}
	}

	@PUT
	@Path("veranstalter")
	public Response synchronizeVeranstalter(final ChangeUserCommand data) {

		try {

			this.syncService.changeVeranstalterDaten(data);

			return Response.ok(ResponsePayload.messageOnly(MessagePayload.ok())).build();
		} catch (PersistenceException e) {

			LOGGER.error("Veranstalter mit UUID {} wurde nicht geändert: {}", data.uuid(), e.getMessage(), e);

			if (domainEventHandler != null) {

				domainEventHandler.handleEvent(SynchronizeVeranstalterFailed.fromMessagingCommand(data));

			}
			throw new MkGatewayRuntimeException("Veranstalter synchronisieren schlug fehl!");
		}
	}

	@DELETE
	@Path("veranstalter")
	public Response loescheVeranstalter(final LoescheVeranstalterCommand data) {

		try {

			this.syncService.loescheVeranstalter(data);

			return Response.ok(ResponsePayload.messageOnly(MessagePayload.ok())).build();
		} catch (PersistenceException e) {

			LOGGER.error("Veranstalter mit UUID {} wurde nicht gelöscht: {}", data.uuid(), e.getMessage(), e);

			if (domainEventHandler != null) {

				domainEventHandler.handleEvent(new DeleteVeranstalterFailed(data.uuid()));

			}
			throw new MkGatewayRuntimeException("Veranstalter löschen schlug fehl!");
		}
	}

}
