// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import de.egladil.web.mk_gateway.domain.permissions.PathWildcardSum;
import de.egladil.web.mk_gateway.domain.permissions.RestrictedUrlPath;
import de.egladil.web.mk_gateway.domain.permissions.RestrictedUrlPathRepository;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * RestrictedUrlPathInMemoryRepository
 */
@ApplicationScoped
public class RestrictedUrlPathInMemoryRepository implements RestrictedUrlPathRepository {

	private Set<RestrictedUrlPath> paths = new HashSet<>();

	public RestrictedUrlPathInMemoryRepository() {

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/anmeldungen/anmeldung",
				Arrays.asList(new Rolle[] { Rolle.LEHRER, Rolle.PRIVAT }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/teilnahmen/teilnahmenummern",
				Arrays.asList(new Rolle[] { Rolle.LEHRER, Rolle.PRIVAT }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/schulen/*/details",
				Arrays.asList(new Rolle[] { Rolle.LEHRER }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/statistik/laender/*/*",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/statistik/schulen/*/*",
				Arrays.asList(new Rolle[] { Rolle.ADMIN, Rolle.LEHRER }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/statistik/privat/*/*",
				Arrays.asList(new Rolle[] { Rolle.ADMIN, Rolle.PRIVAT }));
			paths.add(restrictedPath);
		}

	}

	@Override
	public Optional<RestrictedUrlPath> ofPath(final String path) {

		Optional<RestrictedUrlPath> opt = this.paths.stream().filter(key -> key.path().equalsIgnoreCase(path)).findFirst();

		if (opt.isPresent()) {

			return opt;
		}

		String[] tokens = path.toLowerCase().split("/");

		List<RestrictedUrlPath> trefferliste = this.paths.stream().filter(key -> key.tokens().length == tokens.length)
			.collect(Collectors.toList());

		if (trefferliste.isEmpty()) {

			return Optional.empty();
		}

		List<RestrictedUrlPath> kandidaten = new ArrayList<>();

		for (RestrictedUrlPath urlPath : trefferliste) {

			String sum = new PathWildcardSum().apply(urlPath, tokens);

			if (sum == null) {

				return Optional.empty();
			}

			if (sum.equalsIgnoreCase(path)) {

				kandidaten.add(urlPath);
			}
		}

		String thePath = path.toLowerCase();

		for (RestrictedUrlPath up : kandidaten) {

			if (thePath.startsWith(up.nonWildcardPrefix())) {

				return Optional.of(up);
			}
		}

		return Optional.empty();
	}

}
