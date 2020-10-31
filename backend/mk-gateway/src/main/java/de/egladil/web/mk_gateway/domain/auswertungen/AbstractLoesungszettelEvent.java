// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.auswertungen;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.commons_net.time.CommonTimeUtils;
import de.egladil.web.mk_gateway.domain.event.MkGatewayDomainEvent;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRohdaten;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;

/**
 * AbstractLoesungszettelEvent
 */
public abstract class AbstractLoesungszettelEvent implements MkGatewayDomainEvent {

	@JsonIgnore
	private final LocalDateTime occuredOn;

	@JsonProperty
	private String uuid;

	@JsonProperty
	private String kindID;

	@JsonProperty
	private TeilnahmeIdentifier teilnahmeIdentifier;

	@JsonProperty
	private Sprache spracheAlt;

	@JsonProperty
	private Sprache spracheNeu;

	@JsonProperty
	private LoesungszettelRohdaten rohdatenAlt;

	@JsonProperty
	private LoesungszettelRohdaten rohdatenNeu;

	@JsonProperty
	private String triggeringUser;

	AbstractLoesungszettelEvent() {

		this.occuredOn = CommonTimeUtils.now();

	}

	public AbstractLoesungszettelEvent(final String triggeringUser) {

		this();
		this.triggeringUser = triggeringUser;
	}

	public AbstractLoesungszettelEvent withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public AbstractLoesungszettelEvent withKindID(final String kindID) {

		this.kindID = kindID;
		return this;
	}

	public AbstractLoesungszettelEvent withTeilnahmeIdentifier(final TeilnahmeIdentifier teilnahmeIdentifier) {

		this.teilnahmeIdentifier = teilnahmeIdentifier;
		return this;
	}

	public AbstractLoesungszettelEvent withRohdatenAlt(final LoesungszettelRohdaten rohdatenAlt) {

		this.rohdatenAlt = rohdatenAlt;
		return this;
	}

	public AbstractLoesungszettelEvent withRohdatenNeu(final LoesungszettelRohdaten rohdatenNeu) {

		this.rohdatenNeu = rohdatenNeu;
		return this;
	}

	public AbstractLoesungszettelEvent withSpracheAlt(final Sprache spracheAlt) {

		this.spracheAlt = spracheAlt;
		return this;
	}

	public AbstractLoesungszettelEvent withSpracheNeu(final Sprache spracheNeu) {

		this.spracheNeu = spracheNeu;
		return this;
	}

	@Override
	public LocalDateTime occuredOn() {

		return null;
	}

}
