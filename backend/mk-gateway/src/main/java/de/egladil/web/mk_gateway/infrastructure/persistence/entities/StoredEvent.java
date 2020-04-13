// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * StoredEvent
 */
@Entity
@Table(name = "EVENTS")
public class StoredEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "TIME_OCCURED")
	private LocalDateTime occuredOn;

	@Column(name = "NAME")
	private String name;

	@Column(name = "BODY")
	private String body;

	@Version
	@Column(name = "VERSION")
	private int version;

	public static StoredEvent createEvent(final LocalDateTime occuredOn, final String name, final String body) {

		StoredEvent result = new StoredEvent();
		result.body = body;
		result.name = name;
		result.occuredOn = occuredOn;

		return result;

	}

	/**
	 *
	 */
	StoredEvent() {

	}

	public Long getId() {

		return id;
	}

	public LocalDateTime getOccuredOn() {

		return occuredOn;
	}

	public String getName() {

		return name;
	}

	public String getBody() {

		return body;
	}
}
