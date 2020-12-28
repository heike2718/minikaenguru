// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden;

/**
 * Farbschema
 */
public enum Farbschema {

	BLUE("blau", "/urkunden/overlay_blue.png", "/urkunden/overlay_blue.pdf") {

		@Override
		public Ueberschriftfarbe getUeberschriftfarbe() {

			return Ueberschriftfarbe.BLUE;
		}

	},
	GREEN("grün", "/urkunden/overlay_green.png", "/urkunden/overlay_green.pdf") {

		@Override
		public Ueberschriftfarbe getUeberschriftfarbe() {

			return Ueberschriftfarbe.GREEN;
		}

	},
	ORANGE("orange", "/urkunden/overlay_orange.png", "/urkunden/overlay_orange.pdf") {

		@Override
		public Ueberschriftfarbe getUeberschriftfarbe() {

			return Ueberschriftfarbe.ORANGE;
		}

	};

	private final String label;

	private final String thumbnailClasspathResource;

	private final String backgroundClasspathResource;

	/**
	 * Farbschema
	 */
	private Farbschema(final String label, final String thumbnailClasspathResource, final String backgroundClasspathResource) {

		this.thumbnailClasspathResource = thumbnailClasspathResource;
		this.label = label;
		this.backgroundClasspathResource = backgroundClasspathResource;
	}

	public final String getThumbnailClasspathResource() {

		return thumbnailClasspathResource;
	}

	public final String getLabel() {

		return label;
	}

	public String getBackgroundClasspathResource() {

		return backgroundClasspathResource;
	}

	public abstract Ueberschriftfarbe getUeberschriftfarbe();
}
