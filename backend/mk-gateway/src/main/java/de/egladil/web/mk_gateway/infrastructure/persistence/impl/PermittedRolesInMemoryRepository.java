// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.HttpMethod;

import de.egladil.web.mk_gateway.domain.permissions.PathWildcardSum;
import de.egladil.web.mk_gateway.domain.permissions.PathWithMethod;
import de.egladil.web.mk_gateway.domain.permissions.PermittedRolesRepository;
import de.egladil.web.mk_gateway.domain.permissions.TokenizablePath;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.infrastructure.rest.admin.PermittedRolesForAdminProvider;
import de.egladil.web.mk_gateway.infrastructure.rest.veranstalter.PermittedRolesForVeranstalterProvider;

/**
 * PermittedRolesInMemoryRepository
 */
@ApplicationScoped
public class PermittedRolesInMemoryRepository implements PermittedRolesRepository {

	private Map<PathWithMethod, List<Rolle>> pathWithMethods2Rollen = new HashMap<>();

	public PermittedRolesInMemoryRepository() {

		addPathInfosForVeranstalter();
		addPathInfosForAdmin();

		// TODO: das muss man sich noch überlegen
		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN, Rolle.LEHRER });
			pathWithMethods2Rollen.put(new PathWithMethod("/statistik/schulen/*/*", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN, Rolle.PRIVAT });
			pathWithMethods2Rollen.put(new PathWithMethod("/statistik/privat/*/*", HttpMethod.GET), rollen);

		}

	}

	private void addPathInfosForVeranstalter() {

		final Map<PathWithMethod, List<Rolle>> map = PermittedRolesForVeranstalterProvider.getPathWithMethod2Rollen();

		map.keySet().forEach(key -> {

			List<Rolle> rollen = map.get(key);
			pathWithMethods2Rollen.put(key, rollen);
		});

	}

	private void addPathInfosForAdmin() {

		{

			final Map<PathWithMethod, List<Rolle>> map = PermittedRolesForAdminProvider.getPathWithMethod2Rollen();

			map.keySet().forEach(key -> {

				List<Rolle> rollen = map.get(key);
				pathWithMethods2Rollen.put(key, rollen);
			});
		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			pathWithMethods2Rollen.put(new PathWithMethod("/statistik/laender/*/*", HttpMethod.GET), rollen);

		}

	}

	@Override
	public List<Rolle> permittedRollen(final String path, final String method) {

		List<PathWithMethod> trefferliste = this.ofPath(path, pathWithMethods2Rollen.keySet());

		Optional<PathWithMethod> optPathWithMethod = trefferliste.stream().filter(pwm -> method.equalsIgnoreCase(pwm.method()))
			.findFirst();

		if (optPathWithMethod.isEmpty()) {

			return new ArrayList<>();
		}

		return pathWithMethods2Rollen.getOrDefault(optPathWithMethod.get(), new ArrayList<>());
	}

	public <T extends TokenizablePath> List<T> ofPath(final String path, final Set<T> set) {

		String pathWithMappedQueryParameters = path;

		if (path.contains("?")) {

			pathWithMappedQueryParameters = pathWithMappedQueryParameters.replace("?", "/");
		}

		String[] tokens = pathWithMappedQueryParameters.toLowerCase().split("/");

		List<T> trefferliste = set.stream().filter(key -> key.tokens().length == tokens.length)
			.collect(Collectors.toList());

		if (trefferliste.isEmpty()) {

			return new ArrayList<>();
		}

		List<T> kandidaten = new ArrayList<>();

		for (T urlPath : trefferliste) {

			String sum = new PathWildcardSum().apply(urlPath, tokens);

			if (sum == null) {

				return new ArrayList<>();
			}

			if (sum.equalsIgnoreCase(pathWithMappedQueryParameters)) {

				kandidaten.add(urlPath);
			}
		}

		String thePath = pathWithMappedQueryParameters.toLowerCase();

		List<T> result = new ArrayList<>();

		for (T up : kandidaten) {

			if (thePath.startsWith(up.nonWildcardPrefix())) {

				result.add(up);
			}
		}

		return result;
	}

}
