// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.signup;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * SignUpResourceOwner ist ein Value-Objekt, das die Werte enthält, die für das Anlegen eines Benutzers (Lehrer, Privatmensch) in
 * der Minikänguru-Anwendung erforderlich sind.
 */
public class SignUpResourceOwner {

	private final String uuid;

	private final String fullName;

	private Rolle rolle;

	private String schulkuerzel;

	private boolean newsletterEmpfaenger;

	/**
	 * @param uuid
	 * @param fullName
	 */
	public SignUpResourceOwner(final String uuid, final String fullName, final String nonce) {

		if (StringUtils.isBlank(uuid)) {

			throw new IllegalArgumentException("uuid darf nicht blank sein");
		}

		if (StringUtils.isBlank(fullName)) {

			throw new IllegalArgumentException("fullName darf nicht blank sein");
		}

		this.uuid = uuid;
		this.fullName = fullName;
		this.checkAndInit(nonce);
	}

	/**
	 * @param String
	 */
	private void checkAndInit(final String theNonce) {

		if (StringUtils.isBlank(theNonce)) {

			throw new IllegalArgumentException("nonce darf nicht blank sein");
		}

		if (!theNonce.contains(Rolle.LEHRER.name()) && !theNonce.contains(Rolle.PRIVAT.name())) {

			throw new MkGatewayRuntimeException(
				"Fehler in der Integration mit AuthProvider: das nonce muss entweder den String 'PRIVAT' oder den String 'LEHRER' enthalten");
		}

		String newsletterToken = null;

		if (theNonce.contains(Rolle.LEHRER.name())) {

			this.rolle = Rolle.LEHRER;
			String[] tokens = theNonce.split("-");

			if (tokens.length != 2 && tokens.length != 3) {

				throw new MkGatewayRuntimeException(
					"Das nonce für ein Lehrerkonto muss die Form 'LEHRER-SCHULKUERZEL' oder 'LEHRER-SCHULKUERZEL-TRUE' haben, war aber "
						+ theNonce);
			}
			this.schulkuerzel = tokens[1];

			if (tokens.length == 3) {

				newsletterToken = tokens[2];
			}

		} else {

			String[] tokens = theNonce.split("-");

			if (tokens.length != 1 && tokens.length != 2) {

				throw new MkGatewayRuntimeException(
					"Das nonce für ein Lehrerkonto muss die Form 'PRIVAT' oder 'PRIVAT-TRUE' haben, war aber "
						+ theNonce);
			}

			this.rolle = Rolle.PRIVAT;

			if (tokens.length == 2) {

				newsletterToken = tokens[1];
			}
		}

		if ("TRUE".equalsIgnoreCase(newsletterToken)) {

			this.newsletterEmpfaenger = true;
		}
	}

	public Rolle rolle() {

		return this.rolle;

	}

	/**
	 * Gibt, falls vorhanden, das Schulkürzel zurück.
	 *
	 * @return String oder null.
	 */
	public String schulkuerzel() {

		return this.schulkuerzel;

	}

	public String uuid() {

		return uuid;
	}

	public String fullName() {

		return fullName;
	}

	@Override
	public int hashCode() {

		return Objects.hash(uuid);
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
		SignUpResourceOwner other = (SignUpResourceOwner) obj;
		return Objects.equals(uuid, other.uuid);
	}

	public boolean isNewsletterEmpfaenger() {

		return newsletterEmpfaenger;
	}

}
