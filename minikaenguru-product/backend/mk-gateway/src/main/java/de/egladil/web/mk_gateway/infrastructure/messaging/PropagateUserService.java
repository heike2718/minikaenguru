// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.messaging;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import de.egladil.web.mk_gateway.domain.auth.events.LehrerCreated;
import de.egladil.web.mk_gateway.domain.auth.events.PrivatveranstalterCreated;
import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.semantik.InfrastructureService;
import de.egladil.web.mk_gateway.domain.veranstalter.CreateOrUpdateLehrerCommand;
import de.egladil.web.mk_gateway.domain.veranstalter.CreateOrUpdatePrivatveranstalterCommand;
import de.egladil.web.mk_gateway.domain.veranstalter.LehrerService;
import de.egladil.web.mk_gateway.domain.veranstalter.PrivatveranstalterService;

/**
 * PropagateUserService
 */
@InfrastructureService
@ApplicationScoped
public class PropagateUserService {

	@Inject
	LehrerService lehrerService;

	@Inject
	PrivatveranstalterService privatveranstalterService;

	public void handleDomainEvent(@Observes final MkGatewayDomainEvent event) {

		if (LehrerCreated.class.getSimpleName().equals(event.typeName())) {

			LehrerCreated lehrerCreated = (LehrerCreated) event;

			this.propagate(lehrerCreated);
			return;
		}

		if (PrivatveranstalterCreated.class.getSimpleName().equals(event.typeName())) {

			PrivatveranstalterCreated privatveranstalterCreated = (PrivatveranstalterCreated) event;

			this.propagate(privatveranstalterCreated);
			return;
		}

	}

	/**
	 * @param privatveranstalterCreated
	 */
	private void propagate(final PrivatveranstalterCreated privatveranstalterCreated) {

		CreateOrUpdatePrivatveranstalterCommand command = CreateOrUpdatePrivatveranstalterCommand.create(privatveranstalterCreated);

		// TODO: ist nicht klar, ob es nicht auch ein update ist
		this.privatveranstalterService.addPrivatperson(command);
	}

	/**
	 * @param lehrerCreated
	 */
	private void propagate(final LehrerCreated lehrerCreated) {

		CreateOrUpdateLehrerCommand command = CreateOrUpdateLehrerCommand.create(lehrerCreated);

		// TODO: ist nicht klar, ob es nicht auch ein update ist
		this.lehrerService.addLehrer(command);
	}

}
