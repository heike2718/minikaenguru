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

		addPathInfosForMkWettbewerb();
		addPathInfosForMkWettbewerbAdmin();

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

	private void addPathInfosForMkWettbewerb() {

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/teilnahmen/privat",
				Arrays.asList(new Rolle[] { Rolle.PRIVAT }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/teilnahmen/schule",
				Arrays.asList(new Rolle[] { Rolle.LEHRER }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/teilnahmen/schulen/*",
				Arrays.asList(new Rolle[] { Rolle.LEHRER }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/lehrer/schulen",
				Arrays.asList(new Rolle[] { Rolle.LEHRER }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/lehrer/schulen/*/details",
				Arrays.asList(new Rolle[] { Rolle.LEHRER }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/veranstalter/zugangsstatus",
				Arrays.asList(new Rolle[] { Rolle.LEHRER, Rolle.PRIVAT }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wettbewerb/veranstalter/privat",
				Arrays.asList(new Rolle[] { Rolle.PRIVAT }));
			paths.add(restrictedPath);
		}
	}

	private void addPathInfosForMkWettbewerbAdmin() {

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wb-admin/wettbewerbe",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wb-admin/wettbewerbe/wettbewerb",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wb-admin/wettbewerbe/wettbewerb/status",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wb-admin/wettbewerbe/wettbewerb/*",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }));
			paths.add(restrictedPath);
		}
	}

	@Override
	public Optional<RestrictedUrlPath> ofPath(final String path) {

		Optional<RestrictedUrlPath> opt = this.paths.stream().filter(key -> key.path().equalsIgnoreCase(path)).findFirst();

		if (opt.isPresent()) {

			return opt;
		}

		String pathWithMappedQueryParameters = path;

		if (path.contains("?")) {

			pathWithMappedQueryParameters = pathWithMappedQueryParameters.replace("?", "/");
		}

		String[] tokens = pathWithMappedQueryParameters.toLowerCase().split("/");

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

			if (sum.equalsIgnoreCase(pathWithMappedQueryParameters)) {

				kandidaten.add(urlPath);
			}
		}

		String thePath = pathWithMappedQueryParameters.toLowerCase();

		for (RestrictedUrlPath up : kandidaten) {

			if (thePath.startsWith(up.nonWildcardPrefix())) {

				return Optional.of(up);
			}
		}

		return Optional.empty();
	}

}
