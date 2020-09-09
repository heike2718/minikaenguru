// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.statistik;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.core.SecurityContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.AnonymisierteTeilnahmeAPIModel;
import de.egladil.web.mk_gateway.domain.apimodel.teilnahmen.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.auswertungen.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.error.AccessDeniedException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;

/**
 * AnonymisierteTeilnahmenService
 */
@ApplicationScoped
public class AnonymisierteTeilnahmenService {

	private static final Logger LOG = LoggerFactory.getLogger(AnonymisierteTeilnahmenService.class);

	private SecurityIncidentRegistered securityIncidentRegistered;

	@Inject
	Event<SecurityIncidentRegistered> securityIncidentEvent;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	LoesungszettelRepository loesungszettelRepository;

	public List<AnonymisierteTeilnahmeAPIModel> loadAnonymisierteTeilnahmen(final String teilnahmenummer, final SecurityContext securityContext) {

		if (securityContext.isUserInRole(Rolle.ADMIN.toString())) {

			// dann einfach alle zurückgeben
		} else {

			checkPermission(teilnahmenummer, securityContext.getUserPrincipal().getName());
		}

		List<Teilnahme> alleTeilnahmen = teilnahmenRepository.ofTeilnahmenummer(teilnahmenummer);

		List<AnonymisierteTeilnahmeAPIModel> result = new ArrayList<>();

		alleTeilnahmen.forEach(teilnahme -> {

			TeilnahmeIdentifier identifier = TeilnahmeIdentifier.createFromTeilnahme(teilnahme);
			final int anzahlLoesungszettel = loesungszettelRepository.anzahlLoesungszettel(identifier);

			result.add(AnonymisierteTeilnahmeAPIModel.create(identifier).withAnzahlKinder(anzahlLoesungszettel));

		});

		Collections.sort(result, new AnonymisierteTeilnahmenDescendingComparator());

		return result;
	}

	/**
	 * @param teilnahmenummer
	 */
	private void checkPermission(final String teilnahmenummer, final String principalName) {

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(new Identifier(principalName));

		if (optVeranstalter.isEmpty()) {

			String msg = "Unzulässiger Zugriff auf anonymisierte Teilnahmen durch UUID " + principalName
				+ ": es gibt keinen Veranstalter mit dieser UUID.";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);

			throw new AccessDeniedException(msg);
		}

		Veranstalter veranstalter = optVeranstalter.get();

		String[] teilnahmenummern = veranstalter.persistierbareTeilnahmenummern().split(",");
		Optional<String> optTeilnahmenummer = Arrays.stream(teilnahmenummern).filter(n -> teilnahmenummer.equals(n)).findFirst();

		if (optTeilnahmenummer.isEmpty()) {

			String msg = "Unzulässiger Zugriff auf anonymisierte Teilnahme mit Nummer " + teilnahmenummer + " durch UUID "
				+ principalName
				+ ": Veranstalter besitzt diese Teilnahmenummer nicht.";

			LOG.warn(msg);

			this.securityIncidentRegistered = new LoggableEventDelegate().fireSecurityEvent(msg, securityIncidentEvent);

			throw new AccessDeniedException(msg);

		}
	}

	public SecurityIncidentRegistered getSecurityIncidentRegistered() {

		return securityIncidentRegistered;
	}

}
