// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.personen;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.apimodel.PrivatteilnahmeAPIModel;
import de.egladil.web.mk_wettbewerb.domain.apimodel.PrivatveranstalterAPIModel;
import de.egladil.web.mk_wettbewerb.domain.error.MkWettbewerbRuntimeException;
import de.egladil.web.mk_wettbewerb.domain.semantik.DomainService;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Privatteilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_wettbewerb.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_wettbewerb.domain.wettbewerb.WettbewerbService;

/**
 * PrivatpersonService
 */
@RequestScoped
@DomainService
public class PrivatpersonService {

	private static final Logger LOG = LoggerFactory.getLogger(PrivatpersonService.class);

	@Inject
	VeranstalterRepository repository;

	@Inject
	ZugangUnterlagenService zugangUnterlagenService;

	@Inject
	WettbewerbService wettbewerbSerivice;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	public static PrivatpersonService createForTest(final VeranstalterRepository veranstalterRepository) {

		PrivatpersonService result = new PrivatpersonService();
		result.repository = veranstalterRepository;
		return result;
	}

	public PrivatveranstalterAPIModel findPrivatperson(final String uuid) {

		if (StringUtils.isBlank(uuid)) {

			throw new IllegalArgumentException("uuid darf nicht blank sein!");
		}

		Optional<Veranstalter> optVeranstalter = this.repository.ofId(new Identifier(uuid));

		if (optVeranstalter.isEmpty()) {

			throw new NotFoundException("Kennen keinen Veranstalter mit dieser ID");
		}

		Veranstalter veranstalter = optVeranstalter.get();

		if (veranstalter.rolle() != Rolle.PRIVAT) {

			throw new NotFoundException("Kennen keinen Privatveranstalter mit dieser ID");
		}

		Optional<Wettbewerb> optWettbewerb = wettbewerbSerivice.aktuellerWettbewerb();

		Privatperson privatperson = (Privatperson) veranstalter;
		boolean hatZugang = false;

		if (optWettbewerb.isPresent()) {

			hatZugang = zugangUnterlagenService.hatZugang(privatperson, optWettbewerb.get());
		}

		PrivatveranstalterAPIModel result = PrivatveranstalterAPIModel.create(hatZugang);

		if (optWettbewerb.isPresent()) {

			List<Identifier> teilnahmenummern = privatperson.teilnahmeIdentifier();

			if (teilnahmenummern.isEmpty() || teilnahmenummern.size() > 1) {

				LOG.warn(
					"Bei der Migration der Privatkonten ist etwas schiefgegangen: Privatperson {} hat keine oder mehr als eine Teilnahmenummer.",
					privatperson);
				throw new MkWettbewerbRuntimeException("Kann aktuelle Teilnahme nicht ermitteln");
			}

			Identifier teilnahmenummer = teilnahmenummern.get(0);

			Optional<Teilnahme> optAktuelle = teilnahmenRepository.ofTeilnahmenummerArtWettbewerb(teilnahmenummer.identifier(),
				Teilnahmeart.PRIVAT,
				optWettbewerb.get().id());

			if (optAktuelle.isPresent()) {

				Teilnahme aktuelle = optAktuelle.get();
				result.withTeilnahme(PrivatteilnahmeAPIModel.createFromPrivatteilnahme((Privatteilnahme) aktuelle));

			}
		}

		return result;

	}

	/**
	 * Persistiert eine neue Privatperson.
	 *
	 * @param data
	 *             CreateOrUpdatePrivatpersonCommand
	 */
	@Transactional
	public void addPrivatperson(final CreateOrUpdatePrivatpersonCommand data) {

		Optional<Veranstalter> optPrivatperson = repository.ofId(new Identifier(data.uuid()));

		if (optPrivatperson.isPresent()) {

			return;
		}

		// Issue minikaenguru#18
		String teilnahmenummer = data.uuid().substring(27).toUpperCase();

		Privatperson privatperson = new Privatperson(new Person(data.uuid(), data.fullName()),
			Arrays.asList(new Identifier(teilnahmenummer)));

		repository.addVeranstalter(privatperson);
	}
}
