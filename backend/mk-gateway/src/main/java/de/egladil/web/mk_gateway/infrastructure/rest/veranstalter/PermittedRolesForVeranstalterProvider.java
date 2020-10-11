// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.rest.veranstalter;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.HttpMethod;

import de.egladil.web.mk_gateway.domain.permissions.PathWithMethod;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * PermittedRolesForVeranstalterProvider
 */
public final class PermittedRolesForVeranstalterProvider {

	private PermittedRolesForVeranstalterProvider() {

	}

	public static Map<PathWithMethod, List<Rolle>> getPathWithMethod2Rollen() {

		Map<PathWithMethod, List<Rolle>> result = new HashMap<>();

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.PRIVAT });
			result.put(new PathWithMethod("/veranstalter/teilnahmen/privat", HttpMethod.GET), rollen);
			result.put(new PathWithMethod("/veranstalter/teilnahmen/privat", HttpMethod.POST), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/veranstalter/teilnahmen/schulen/*", HttpMethod.GET), rollen);
			result.put(new PathWithMethod("/veranstalter/teilnahmen/schule", HttpMethod.POST), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/veranstalter/lehrer/schulen", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/veranstalter/lehrer/schulen/*", HttpMethod.POST), rollen);
			result.put(new PathWithMethod("/veranstalter/lehrer/schulen/*", HttpMethod.DELETE), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/veranstalter/lehrer/schulen/*/details", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER, Rolle.PRIVAT });
			result.put(new PathWithMethod("/veranstalter/newsletter", HttpMethod.PUT), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.PRIVAT });
			result.put(new PathWithMethod("/veranstalter/privat", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/veranstalter/lehrer", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/veranstalter/lehrer", HttpMethod.GET), rollen);
			result.put(new PathWithMethod("/veranstalter/lehrer", HttpMethod.PUT), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/adv/*", HttpMethod.GET), rollen);
			result.put(new PathWithMethod("/adv", HttpMethod.POST), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER, Rolle.PRIVAT });
			result.put((new PathWithMethod("/statistik/*/*/*", HttpMethod.GET)),
				rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER, Rolle.PRIVAT });
			result.put((new PathWithMethod("/teilnahmen/*", HttpMethod.GET)),
				rollen);

		}

		return result;

	}

}
