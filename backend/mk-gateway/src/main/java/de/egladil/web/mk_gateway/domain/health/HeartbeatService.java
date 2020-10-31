// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.health;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.kataloge.MkKatalogeResourceAdapter;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.Pacemaker;

/**
 * HeartbeatService
 */
@ApplicationScoped
public class HeartbeatService {

	public static final String MK_GATEWAY_PACEMAKER_ID = "mk-gateway-wettbewerb-database";

	private static final String MK_KATALOGE_PACEMAKER_ID = "mk-kataloge-database";

	@ConfigProperty(name = "heartbeat.id")
	String expectedHeartbeatId;

	@Inject
	PacemakerRepository pacemakerRepository;

	@Inject
	MkKatalogeResourceAdapter katalogeAdapter;

	public ResponsePayload updatePacemaker() {

		ResponsePayload ownDatabaseResult = checkOwnDatabase();

		if (!ownDatabaseResult.isOk()) {

			return ownDatabaseResult;
		}

		ResponsePayload katalogeResult = checkKataloge();

		if (!katalogeResult.isOk()) {

			return katalogeResult;
		}

		return ResponsePayload
			.messageOnly(MessagePayload.info(MK_GATEWAY_PACEMAKER_ID + " und " + MK_KATALOGE_PACEMAKER_ID + " leben"));
	}

	@Transactional
	ResponsePayload checkOwnDatabase() {

		Pacemaker pacemaker = pacemakerRepository.findById(MK_GATEWAY_PACEMAKER_ID);

		if (pacemaker == null) {

			throw new MkGatewayRuntimeException("Konnte keinen Pacemaker mit uuid=" + MK_GATEWAY_PACEMAKER_ID + " finden :/");
		}

		pacemaker.setWert("wert-" + System.currentTimeMillis());
		pacemakerRepository.change(pacemaker);

		return ResponsePayload.messageOnly(MessagePayload.info(MK_GATEWAY_PACEMAKER_ID + " lebt"));
	}

	ResponsePayload checkKataloge() {

		Response katalogeResponse = katalogeAdapter.getHeartbeat(expectedHeartbeatId);

		ResponsePayload responsePayload = katalogeResponse.readEntity(ResponsePayload.class);

		return responsePayload;

	}
}
