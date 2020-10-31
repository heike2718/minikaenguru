// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * LoesungszettelDeleted
 */
public class LoesungszettelDeleted extends AbstractLoesungszettelEvent {

	/**
	 *
	 */
	public LoesungszettelDeleted() {

		super();
	}

	/**
	 * @param triggeringUser
	 */
	public LoesungszettelDeleted(final String triggeringUser) {

		super(triggeringUser);

	}

	@Override
	public String typeName() {

		return EventType.LOESUNGSZETTEL_DELETED.getLabel();
	}

}
