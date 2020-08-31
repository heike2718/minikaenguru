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

/**
 * PathWithMethod
 */
@ValueObject
public class PathWithMethod implements TokenizablePath {

	private final String path;

	private final String method;

	public PathWithMethod(final String path, final String method) {

		if (StringUtils.isBlank(path)) {

			throw new IllegalArgumentException("path darf nicht blank sein.");
		}

		if (StringUtils.isBlank(method)) {

			throw new IllegalArgumentException("method darf nicht blank sein.");
		}

		this.path = path;
		this.method = method;
	}

	@Override
	public String path() {

		return path;
	}

	public String method() {

		return method;
	}

	@Override
	public String[] tokens() {

		return this.path.split("/");
	}

	@Override
	public String toString() {

		return method + " " + path;
	}

	@Override
	public int hashCode() {

		return Objects.hash(method, path);
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
		PathWithMethod other = (PathWithMethod) obj;
		return Objects.equals(method, other.method) && Objects.equals(path, other.path);
	}

	@Override
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
