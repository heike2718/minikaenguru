// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import de.egladil.web.mk_gateway.domain.event.EventType;

/**
 * LoesungszettelChanged
 */
public class LoesungszettelChanged extends AbstractLoesungszettelEvent {

	/**
	 *
	 */
	public LoesungszettelChanged() {

		super();

	}

	/**
	 * @param triggeringUser
	 */
	public LoesungszettelChanged(final String triggeringUser) {

		super(triggeringUser);

	}

	@Override
	public String typeName() {

		return EventType.LOESUNGSZETTEL_CHANGED.getLabel();
	}

}
