// =====================================================
// Project: mkadmin-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkadmin_server.domain;

import java.io.Serializable;

import de.egladil.web.commons_validation.payload.HateoasPayload;

/**
 * MkadminEntity
 */
public interface MkadminEntity extends Serializable {

	Long getId();

	/**
	 * Für HATEOAS..
	 *
	 * @param hateoasPayload
	 */
	void setHateoasPayload(HateoasPayload hateoasPayload);

	/**
	 * Zu Testzwecken kann man das abfragen.
	 *
	 * @return HateoasPayload
	 */
	HateoasPayload getHateoasPayload();
}
