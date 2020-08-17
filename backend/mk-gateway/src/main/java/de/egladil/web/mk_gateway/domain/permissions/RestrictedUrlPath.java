// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.permissions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * RestrictedUrlPath ist der Path-Anteil einer aufgerufenen Url(requestContext.getUriInfo().getPath()), der eine Zugriffskontrolle
 * hat.
 */
@ValueObject
public class RestrictedUrlPath {

	private final String path;

	private final List<Rolle> erlaubteRollen;

	private final List<String> erlaubteHttpVerben;

	/**
	 * Erkaubt alle RestVerbs.
	 *
	 * @param path
	 *                       String
	 * @param erlaubteRollen
	 *                       List
	 */
	public RestrictedUrlPath(final String path, final List<Rolle> erlaubteRollen, final List<String> erlaubteHttpVerben) {

		if (StringUtils.isBlank(path)) {

			throw new IllegalArgumentException("path darf nicht blank sein.");
		}

		if (erlaubteRollen == null || erlaubteRollen.isEmpty()) {

			throw new IllegalArgumentException("erlaubteRollen darf nicht null oder leer sein.");
		}

		if (erlaubteHttpVerben == null || erlaubteHttpVerben.isEmpty()) {

			throw new IllegalArgumentException("erlaubteHttpVerben darf nicht null oder leer sein.");
		}
		this.path = path;
		this.erlaubteRollen = erlaubteRollen;
		this.erlaubteHttpVerben = erlaubteHttpVerben;
	}

	public boolean isAllowedForRolle(final Rolle rolle) {

		if (rolle == null) {

			throw new IllegalArgumentException("rolle muss bekannt sein");
		}
		return this.erlaubteRollen.stream().filter(r -> r == rolle).findFirst().isPresent();
	}

	public boolean isRestrictedForMethod(final String method) {

		if (method == null) {

			throw new IllegalArgumentException("method muss bekannt sein");
		}
		return this.erlaubteHttpVerben.stream().filter(v -> v == method).findFirst().isPresent();
	}

	@Override
	public int hashCode() {

		return Objects.hash(path);
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
		RestrictedUrlPath other = (RestrictedUrlPath) obj;
		return Objects.equals(path, other.path);
	}

	public String path() {

		return path;
	}

	public String[] tokens() {

		return this.path.split("/");
	}

	public String nonWildcardPrefix() {

		String[] tokens = this.tokens();
		List<String> nonWildcards = new ArrayList<>();

		for (String token : tokens) {

			if (!"*".equals(token)) {

				nonWildcards.add(token);
			} else {

				break;
			}
		}

		return StringUtils.join(nonWildcards.toArray(new String[0]), "/");

	}
}
