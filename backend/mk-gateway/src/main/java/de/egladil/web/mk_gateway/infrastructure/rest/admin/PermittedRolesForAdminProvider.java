// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.admin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import de.egladil.web.mk_gateway.domain.permissions.PathWithMethod;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * PermittedRolesForAdminProvider
 */
public final class PermittedRolesForAdminProvider {

	private PermittedRolesForAdminProvider() {

	}

	public static Map<PathWithMethod, List<Rolle>> getPathWithMethod2Rollen() {

		Map<PathWithMethod, List<Rolle>> result = new HashMap<>();

		addPathsAndMethodsForWettbewerb(result);
		addPathsAndMethodsForKataloge(result);
		addPathsAndMethodsForVeranstalter(result);
		addPathsAndMethodsForSchulen(result);
		addPathsAndMethodsForPrivatteilnahmen(result);
		addPathsAndMethodsForStatistik(result);

		return result;

	}

	/**
	 * @param result
	 */
	private static void addPathsAndMethodsForKataloge(final Map<PathWithMethod, List<Rolle>> result) {

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/kataloge/laender", HttpMethod.GET), rollen);
			result.put(new PathWithMethod("/admin/kataloge/laender", HttpMethod.PUT), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/kataloge/orte", HttpMethod.PUT), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/kataloge/schulen", HttpMethod.POST), rollen);
			result.put(new PathWithMethod("/admin/kataloge/schulen", HttpMethod.PUT), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/kataloge/laender/*/orte", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/kataloge/orte/*/schulen", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/kataloge/suche/global/*", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/kataloge/suche/global/*/search", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/kataloge/kuerzel", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/upload/schulen/csv", HttpMethod.POST), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/events/*", HttpMethod.GET), rollen);

		}

	}

	/**
	 * @param result
	 */
	private static void addPathsAndMethodsForWettbewerb(final Map<PathWithMethod, List<Rolle>> result) {

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/wettbewerbe", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/wettbewerbe/wettbewerb", HttpMethod.POST), rollen);
			result.put(new PathWithMethod("/admin/wettbewerbe/wettbewerb", HttpMethod.PUT), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/wettbewerbe/wettbewerb/status", HttpMethod.PUT), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/wettbewerbe/wettbewerb/*", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/meldungen/aktuelle-meldung", HttpMethod.POST), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/meldungen/aktuelle-meldung", HttpMethod.DELETE), rollen);

		}
	}

	/**
	 * @param result
	 */
	private static void addPathsAndMethodsForVeranstalter(final Map<PathWithMethod, List<Rolle>> result) {

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/veranstalter/suche", HttpMethod.POST), rollen);

		}
	}

	/**
	 * @param result
	 */
	private static void addPathsAndMethodsForSchulen(final Map<PathWithMethod, List<Rolle>> result) {

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/schulen/*", HttpMethod.GET), rollen);

		}

	}

	/**
	 * @param result
	 */
	private static void addPathsAndMethodsForPrivatteilnahmen(final Map<PathWithMethod, List<Rolle>> result) {

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put(new PathWithMethod("/admin/privatteilnahmen/*", HttpMethod.GET), rollen);

		}

	}

	private static void addPathsAndMethodsForStatistik(final Map<PathWithMethod, List<Rolle>> result) {

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.ADMIN });
			result.put((new PathWithMethod("/admin/statistik/*/*/*", HttpMethod.GET)),
				rollen);

		}

	}
}
