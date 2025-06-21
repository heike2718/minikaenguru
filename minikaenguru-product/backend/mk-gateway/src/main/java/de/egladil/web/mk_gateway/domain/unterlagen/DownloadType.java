// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.unterlagen;

/**
 * DownloadType
 */
public enum DownloadType {

	PRIVAT_DEUTSCH {

		@Override
		public String getMessageFormatPatternFileName() {

			return "{0}-minikaenguru-deutsch-privat.zip";
		}

	},
	PRIVAT_ENGLISCH {

		@Override
		public String getMessageFormatPatternFileName() {

			return "{0}-minikangaroo-english-private.zip"; // Achtung: hier war ein Typo => File im uploads-Verzeichnis umbenennen.
		}

	},
	SCHULE_DEUTSCH {

		@Override
		public String getMessageFormatPatternFileName() {

			return "{0}-minikaenguru-deutsch-schulen.zip";
		}

	},
	SCHULE_ENGLISCH {

		@Override
		public String getMessageFormatPatternFileName() {

			return "{0}-minikangaroo-english-schools.zip";
		}

	};

	public abstract String getMessageFormatPatternFileName();

}
