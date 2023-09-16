// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mail;

import java.util.Objects;

import jakarta.mail.Address;
import jakarta.mail.SendFailedException;

/**
 * SendFailedExceptionAdapter
 */
public class SendFailedExceptionAdapter extends SendFailedException {

	private static final long serialVersionUID = 1L;

	class AddressAdapter extends Address {

		/**
		 *
		 */
		private static final long serialVersionUID = 1L;

		private final String email;

		AddressAdapter(final String email) {

			this.email = email;
		}

		@Override
		public int hashCode() {

			final int prime = 31;
			int result = 1;
			result = prime * result + getEnclosingInstance().hashCode();
			result = prime * result + Objects.hash(email);
			return result;
		}

		@Override
		public boolean equals(final Object obj) {

			if (this == obj) {

				return true;
			}

			if (obj == null) {

				return false;
			}

			if (getClass() != obj.getClass()) {

				return false;
			}
			AddressAdapter other = (AddressAdapter) obj;

			if (!getEnclosingInstance().equals(other.getEnclosingInstance())) {

				return false;
			}
			return Objects.equals(email, other.email);
		}

		private SendFailedExceptionAdapter getEnclosingInstance() {

			return SendFailedExceptionAdapter.this;
		}

		@Override
		public String getType() {

			return "Mail";
		}

		@Override
		public String toString() {

			return email;
		}

	}

	@Override
	public Address[] getValidSentAddresses() {

		return new Address[] { new AddressAdapter("hdwinkel@egladil.de") };
	}

	@Override
	public Address[] getValidUnsentAddresses() {

		return new Address[] { new AddressAdapter("info@egladil.de") };
	}

	@Override
	public Address[] getInvalidAddresses() {

		return new Address[] { new AddressAdapter("hdwinkel@egla.de") };
	}

}
