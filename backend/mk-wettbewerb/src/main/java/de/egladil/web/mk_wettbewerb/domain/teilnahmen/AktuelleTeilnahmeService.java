// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.teilnahmen;

import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.error.AccessDeniedException;
import de.egladil.web.mk_wettbewerb.domain.error.MkWettbewerbRuntimeException;
import de.egladil.web.mk_wettbewerb.domain.personen.Privatperson;
import de.egladil.web.mk_wettbewerb.domain.personen.Rolle;
import de.egladil.web.mk_wettbewerb.domain.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.domain.personen.ZugangUnterlagen;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbService;

/**
 * AktuelleTeilnahmeService
 */
@RequestScoped
@DomainService
public class AktuelleTeilnahmeService {

	private static final Logger LOG = LoggerFactory.getLogger(AktuelleTeilnahmeService.class);

	@Inject
	Event<PrivatteilnahmeCreated> privatteilnahmeCreated;

	private PrivatteilnahmeCreated privatteilnahmeCreatedEvent;

	private boolean test = false;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	static AktuelleTeilnahmeService createForTest(final TeilnahmenRepository teilnahmenRepository, final WettbewerbService wettbewerbService, final VeranstalterRepository veranstalterRepository) {

		AktuelleTeilnahmeService result = new AktuelleTeilnahmeService();
		result.teilnahmenRepository = teilnahmenRepository;
		result.wettbewerbService = wettbewerbService;
		result.veranstalterRepository = veranstalterRepository;
		result.test = true;
		return result;
	}

	/**
	 * Sucht die aktuelle Teilnahme mit der gegebenen teilnahmenummer.
	 *
	 * @param  teilnahmenummer
	 * @return                 boolean
	 */
	public Optional<Teilnahme> aktuelleTeilnahme(final String teilnahmenummer) {

		if (StringUtils.isBlank(teilnahmenummer)) {

			throw new IllegalArgumentException("teilnahmenummer darf nicht blank sein.");
		}

		List<Teilnahme> teilnahmen = teilnahmenRepository.ofTeilnahmenummer(teilnahmenummer);

		return this.aktuelleTeilnahme(teilnahmen);
	}

	/**
	 * Wenn man irgendwo her die Teilnahmen bereits hat, ist das einfacher.
	 *
	 * @param  teilnahmen
	 *                    List
	 * @return            Optional
	 */
	public Optional<Teilnahme> aktuelleTeilnahme(final List<Teilnahme> teilnahmen) {

		if (teilnahmen == null) {

			throw new IllegalArgumentException("teilnahmen darf nicht null sein.");
		}

		Optional<Wettbewerb> optLaufend = wettbewerbService.aktuellerWettbewerb();

		if (optLaufend.isEmpty()) {

			return Optional.empty();
		}

		final Wettbewerb laufenderWettbewerb = optLaufend.get();

		Optional<Teilnahme> optAktuelle = teilnahmen.stream().filter(t -> t.wettbewerbID().equals(laufenderWettbewerb.id()))
			.findFirst();

		return optAktuelle;

	}

	/**
	 * Legt eine neue Privatteilnahme an, wenn alle Voraussetzungen erfüllt sind.
	 *
	 * @param  uuid
	 *              String die UUID eines Veranstalters. Dies muss ein Privatmensch sein, der nicht gesperrt ist.
	 * @return      Teilnahme die neue oder einen möglicherweise bereits vorhandene.
	 */
	@Transactional
	public Privatteilnahme privatpersonAnmelden(final String uuid) {

		if (StringUtils.isBlank(uuid)) {

			throw new IllegalArgumentException("uuid darf nicht blank sein.");
		}

		Optional<Wettbewerb> optWettbewerb = wettbewerbService.aktuellerWettbewerb();

		if (optWettbewerb.isEmpty()) {

		}

		Wettbewerb aktuellerWettbewerb = optWettbewerb.get();

		switch (aktuellerWettbewerb.status()) {

		case BEENDET:
			throw new IllegalStateException("Keine Anmeldung möglich. Der Wettbewerb ist beendet.");

		case ERFASST:
			throw new IllegalStateException("Keine Anmeldung möglich. Der Anmeldezeitraum hat noch nicht begonnen.");

		default:
			break;
		}

		final Identifier identifier = new Identifier(uuid);

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(identifier);

		if (optVeranstalter.isEmpty()) {

			throw new MkWettbewerbRuntimeException("keinen Veranstalter mit UUID=" + uuid + " gefunden");
		}

		Veranstalter veranstalter = optVeranstalter.get();

		if (Rolle.LEHRER == veranstalter.rolle()) {

			LOG.info("Lehrer {} versucht, eine Privatteilnahme anzulegen", veranstalter.toString());
			throw new AccessDeniedException("Der Veranstalter ist ein Lehrer. Nur Privatprsonen dürfen diese Funktion aufrufen.");
		}

		if (veranstalter.zugangUnterlagen() == ZugangUnterlagen.ENTZOGEN) {

			LOG.info("Veranstalter {} hat keine Berechtigung zur Anmeldung: Zugang Unterlagen {}", veranstalter.toString(),
				ZugangUnterlagen.ENTZOGEN);
			throw new AccessDeniedException("Dem Veranstalter wurde der Zugang zu den Unterlagen entzogen.");
		}

		Teilnahme vorhandene = this.findVorhandeneTeilnahme((Privatperson) veranstalter, aktuellerWettbewerb);

		if (vorhandene != null) {

			return (Privatteilnahme) vorhandene;
		}

		Privatteilnahme neue = new Privatteilnahme(aktuellerWettbewerb.id(), veranstalter.teilnahmeIdentifier().get(0));

		teilnahmenRepository.addTeilnahme(neue);

		privatteilnahmeCreatedEvent = new PrivatteilnahmeCreated(neue.wettbewerbID().jahr(), neue.teilnahmenummer().identifier(),
			uuid);

		if (!test) {

			privatteilnahmeCreated.fire(privatteilnahmeCreatedEvent);
		}

		return neue;
	}

	private Teilnahme findVorhandeneTeilnahme(final Privatperson veranstalter, final Wettbewerb aktuellerWettbewerb) {

		Teilnahme vorhandene = null;

		for (Identifier id : veranstalter.teilnahmeIdentifier()) {

			Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofTeilnahmenummerArtWettbewerb(id.identifier(),
				veranstalter.teilnahmeart(), aktuellerWettbewerb.id());

			if (optTeilnahme.isPresent()) {

				vorhandene = optTeilnahme.get();
				return vorhandene;
			}
		}

		return null;
	}

	PrivatteilnahmeCreated privatteilnahmeCreatedEvent() {

		return privatteilnahmeCreatedEvent;
	}
}
