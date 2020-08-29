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
import javax.ws.rs.HttpMethod;

import de.egladil.web.mk_gateway.domain.permissions.PathWildcardSum;
import de.egladil.web.mk_gateway.domain.permissions.RestrictedUrlPath;
import de.egladil.web.mk_gateway.domain.permissions.RestrictedUrlPathRepository;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.infrastructure.rest.admin.KatalogAdminResource;
import de.egladil.web.mk_gateway.infrastructure.rest.admin.WettbewerbAdminResource;
import de.egladil.web.mk_gateway.infrastructure.rest.wettbewerb.WettbewerbResource;

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

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/statistik/schulen/*/*",
				Arrays.asList(new Rolle[] { Rolle.ADMIN, Rolle.LEHRER }), Arrays.asList(new String[] { HttpMethod.GET }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/statistik/privat/*/*",
				Arrays.asList(new Rolle[] { Rolle.ADMIN, Rolle.PRIVAT }), Arrays.asList(new String[] { HttpMethod.GET }));
			paths.add(restrictedPath);
		}

	}

	private void addPathInfosForMkWettbewerb() {

		paths.addAll(WettbewerbResource.getRestrictedPathInfos());

	}

	private void addPathInfosForMkWettbewerbAdmin() {

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/statistik/laender/*/*",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }), Arrays.asList(new String[] { HttpMethod.GET }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/wb-admin/upload/schulen/csv",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }), Arrays.asList(new String[] { HttpMethod.POST }));
			paths.add(restrictedPath);
		}

		{

			RestrictedUrlPath restrictedPath = new RestrictedUrlPath("/meldungen/admin/aktuelle-meldung",
				Arrays.asList(new Rolle[] { Rolle.ADMIN }), Arrays.asList(new String[] { HttpMethod.POST }));
			paths.add(restrictedPath);
		}

		paths.addAll(WettbewerbAdminResource.getRestrictedPathInfos());
		paths.addAll(KatalogAdminResource.getRestrictedPathInfos());

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
