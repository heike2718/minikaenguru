// =====================================================
// Project: minikaenguru-statistik
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.minikaenguru_statistik.domain;

/**
 * Klassenstufe
 */
public enum Klassenstufe {

	IKID {

		@Override
		public int getStartguthaben() {

			return 12;
		}

	},
	EINS {

		@Override
		public int getStartguthaben() {

			return 12;
		}

	},
	ZWEI {

		@Override
		public int getStartguthaben() {

			return 15;
		}

	};

	public abstract int getStartguthaben();
}
