// =====================================================
// Projekt: authprovider
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_kataloge.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * Pacemaker (Herzschrittmacher) wird vom eigengebauten Monitor verwendet, um regelmäßige Zugriffe auf die DB zu machen.
 */
@Entity
@Table(name = "PACEMAKERS")
public class Pacemaker {

	@Id
	@Column(name = "UUID")
	private String uuid;

	@Column(name = "WERT")
	@NotBlank
	@Size(max = 36)
	/** wert wird bei jedem Zugriff geändert */
	private String wert;

	@Version
	@Column(name = "VERSION")
	private int version;

	public String getWert() {

		return wert;
	}

	public void setWert(final String wert) {

		this.wert = wert;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("Pacemaker [uuid=");
		builder.append(uuid);
		builder.append(", wert=");
		builder.append(wert);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}
}
