// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * LoesungszettelCreated
 */
public class LoesungszettelCreated extends AbstractLoesungszettelEvent {

	/**
	 *
	 */
	public LoesungszettelCreated() {

		super();
	}

	/**
	 * @param triggeringUser
	 */
	public LoesungszettelCreated(final String triggeringUser) {

		super(triggeringUser);

	}

	@Override
	public String typeName() {

		return EventType.LOESUNGSZETTEL_CREATED.getLabel();
	}

}
