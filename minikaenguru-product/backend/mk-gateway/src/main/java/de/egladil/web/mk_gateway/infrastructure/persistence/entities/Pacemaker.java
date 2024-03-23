// =====================================================
// Projekt: authprovider
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Pacemaker (Herzschrittmacher) wird vom eigengebauten Monitor verwendet, um regelmäßige Zugriffe auf die DB zu machen.
 */
@Entity
@Table(name = "PACEMAKERS")
public class Pacemaker extends ConcurrencySafeEntity {

	private static final long serialVersionUID = 784193470370623531L;

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
		builder.append(getUuid());
		builder.append(", wert=");
		builder.append(wert);
		builder.append(", version=");
		builder.append(version);
		builder.append("]");
		return builder.toString();
	}
}
