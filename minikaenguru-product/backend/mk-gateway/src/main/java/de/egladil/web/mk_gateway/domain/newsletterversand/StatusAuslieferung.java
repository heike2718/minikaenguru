// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.newsletterversand;

/**
 * StatusAuslieferung Status des Versands eines bestimmten Newsletters an eine Gruppe von Mailempfaengern.
 */
public enum StatusAuslieferung {

	NEW {

		@Override
		public boolean isCompleted() {

			return false;
		}

	},
	WAITING {

		@Override
		public boolean isCompleted() {

			return false;
		}

	},
	IN_PROGRESS {

		@Override
		public boolean isCompleted() {

			return false;
		}

	},
	COMPLETED {

		@Override
		public boolean isCompleted() {

			return true;
		}

	},
	ERRORS {

		@Override
		public boolean isCompleted() {

			return true;
		}

	};

	public abstract boolean isCompleted();

}
