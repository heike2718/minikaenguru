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

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.PRIVAT, Rolle.LEHRER });
			result.put(new PathWithMethod("/loesungszettel/*", HttpMethod.GET), rollen);
			result.put(new PathWithMethod("/loesungszettel", HttpMethod.POST), rollen);
			result.put(new PathWithMethod("/loesungszettel", HttpMethod.PUT), rollen);
			result.put(new PathWithMethod("/loesungszettel/*", HttpMethod.DELETE), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.PRIVAT });
			result.put(new PathWithMethod("/teilnahmen/privat", HttpMethod.GET), rollen);
			result.put(new PathWithMethod("/teilnahmen/privat", HttpMethod.POST), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/teilnahmen/schulen/*", HttpMethod.GET), rollen);
			result.put(new PathWithMethod("/teilnahmen/schule", HttpMethod.POST), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/lehrer/schulen", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/lehrer/schulen/*", HttpMethod.POST), rollen);
			result.put(new PathWithMethod("/lehrer/schulen/*", HttpMethod.DELETE), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/lehrer/schulen/*/details", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/lehrer/schulen/*/klassen", HttpMethod.DELETE), rollen);

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
			result.put(new PathWithMethod("/lehrer", HttpMethod.GET), rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/lehrer", HttpMethod.GET), rollen);
			result.put(new PathWithMethod("/lehrer", HttpMethod.PUT), rollen);

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
			result.put((new PathWithMethod("/teilnahmen/veranstalter/*", HttpMethod.GET)),
				rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER, Rolle.PRIVAT });

			result.put((new PathWithMethod("/kinder/*", HttpMethod.GET)),
				rollen);

			result.put((new PathWithMethod("/kinder", HttpMethod.POST)),
				rollen);

			result.put((new PathWithMethod("/kinder/duplikate", HttpMethod.POST)),
				rollen);

			result.put((new PathWithMethod("/kinder", HttpMethod.PUT)),
				rollen);

			result.put((new PathWithMethod("/kinder/*", HttpMethod.DELETE)),
				rollen);
		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });

			result.put((new PathWithMethod("/klassen/*", HttpMethod.GET)),
				rollen);

			result.put((new PathWithMethod("/klassen", HttpMethod.POST)),
				rollen);

			result.put((new PathWithMethod("/klassen/duplikate", HttpMethod.POST)),
				rollen);

			result.put((new PathWithMethod("/klassen", HttpMethod.PUT)),
				rollen);

			result.put((new PathWithMethod("/klassen/*", HttpMethod.DELETE)),
				rollen);
		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER, Rolle.PRIVAT });
			result.put(new PathWithMethod("/urkunden/urkunde", HttpMethod.POST), rollen);
		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/urkunden/schule", HttpMethod.POST), rollen);
		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put((new PathWithMethod("/unterlagen/schulen/*", HttpMethod.GET)),
				rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.PRIVAT });
			result.put((new PathWithMethod("/unterlagen/privat/*", HttpMethod.GET)),
				rollen);

		}

		{

			List<Rolle> rollen = Arrays.asList(new Rolle[] { Rolle.LEHRER });
			result.put(new PathWithMethod("/uploads/klassenliste/*/*/*", HttpMethod.POST), rollen);
		}

		return result;

	}

}
