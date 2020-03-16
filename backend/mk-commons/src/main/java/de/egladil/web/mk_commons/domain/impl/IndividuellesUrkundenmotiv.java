// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Lob;
import javax.validation.constraints.NotNull;

import de.egladil.web.mk_commons.domain.enums.Ueberschriftfarbe;

/**
 * IndividuellesUrkundenmotiv ist die Kombination eines Schul- oder Klassenspezifischen Hintergrundbildes mit einer Farbe für die
 * Überschrift.
 */
@Embeddable
public class IndividuellesUrkundenmotiv {

	@NotNull
	@Column(name = "OVERLAY")
	@Lob
	private byte[] overlay;

	@NotNull
	@Column(name = "UEBERSCHRIFTFARBE")
	@Enumerated(EnumType.STRING)
	private Ueberschriftfarbe uberschiftfarbe;

	/**
	 * IndividuellesUrkundenmotiv
	 */
	public IndividuellesUrkundenmotiv() {

	}

	/**
	 * Schule
	 * IndividuellesUrkundenmotiv
	 */
	public IndividuellesUrkundenmotiv(final Ueberschriftfarbe uberschiftfarbe, final byte[] overlay) {

		this.uberschiftfarbe = uberschiftfarbe;
		this.overlay = overlay;
	}

	public final byte[] getOverlay() {

		return overlay;
	}

	public final void setOverlay(final byte[] overlay) {

		this.overlay = overlay;
	}

	public final Ueberschriftfarbe getUberschiftfarbe() {

		return uberschiftfarbe;
	}

	public final void setUberschiftfarbe(final Ueberschriftfarbe uberschiftfarbe) {

		this.uberschiftfarbe = uberschiftfarbe;
	}

}
