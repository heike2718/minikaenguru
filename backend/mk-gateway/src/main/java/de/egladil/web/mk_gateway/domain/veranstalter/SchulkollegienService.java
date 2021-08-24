// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.IdentifierComparator;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.veranstalter.events.LehrerChanged;

/**
 * SchulkollegienService
 */
@ApplicationScoped
@DomainService
public class SchulkollegienService {

	private static final Logger LOG = LoggerFactory.getLogger(SchulkollegienService.class);

	@Inject
	SchulkollegienRepository repository;

	static SchulkollegienService createForTest(final SchulkollegienRepository repository) {

		SchulkollegienService result = new SchulkollegienService();
		result.repository = repository;
		return result;
	}

	@Transactional
	public void handleLehrerChanged(@Observes final LehrerChanged event) {

		List<Identifier> alteSchulen = StringUtils.isBlank(event.alteSchulkuerzel()) ? new ArrayList<>()
			: Arrays.stream(event.alteSchulkuerzel().split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

		List<Identifier> neueSchulen = StringUtils.isBlank(event.neueSchulkuerzel()) ? new ArrayList<>()
			: Arrays.stream(event.neueSchulkuerzel().split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

		final Kollege derKollege = Kollege.fromPerson(event.person());

		this.handleDeregisteredSchulen(derKollege, alteSchulen, neueSchulen);
		this.handleRegisteredSchulen(derKollege, alteSchulen, neueSchulen);
		this.handleUnchangedSchulen(derKollege, alteSchulen, neueSchulen);

	}

	/**
	 * @param Lehrer
	 *               lehrer
	 */
	public void entferneSpurenDesLehrers(final Lehrer lehrer) {

		final Person person = new Person(lehrer.person().uuid(), "ANONYM");
		final Kollege kollege = Kollege.fromPerson(person);

		List<Identifier> alteSchulen = StringUtils.isBlank(lehrer.persistierbareTeilnahmenummern()) ? new ArrayList<>()
			: Arrays.stream(lehrer.persistierbareTeilnahmenummern().split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

		this.handleDeregisteredSchulen(kollege, alteSchulen, new ArrayList<>());

	}

	private void handleDeregisteredSchulen(final Kollege derLehrer, final List<Identifier> alteSchulen, final List<Identifier> neueSchulen) {

		List<Identifier> deregisteredFromSchulen = this.getDeregisterList(alteSchulen, neueSchulen);

		for (Identifier schulkuerzel : deregisteredFromSchulen) {

			Optional<Schulkollegium> opt = repository.ofSchulkuerzel(schulkuerzel);

			if (opt.isPresent()) {

				Schulkollegium kollegium = opt.get();
				List<Kollege> verbleibendeLehrer = getVerbleibendeLehrer(derLehrer, kollegium);

				if (verbleibendeLehrer.isEmpty()) {

					this.repository.deleteKollegium(kollegium);

				} else {

					this.repository.replaceKollegen(new Schulkollegium(schulkuerzel, verbleibendeLehrer.toArray(new Kollege[0])));
				}
			}
		}
	}

	private void handleRegisteredSchulen(final Kollege derKollege, final List<Identifier> alteSchulen, final List<Identifier> neueSchulen) {

		List<Identifier> registeredAtSchulen = this.getRegisterList(alteSchulen, neueSchulen);

		for (Identifier schulkuerzel : registeredAtSchulen) {

			Optional<Schulkollegium> opt = repository.ofSchulkuerzel(schulkuerzel);

			List<Kollege> alleLehrer = new ArrayList<>();

			if (opt.isPresent()) {

				Schulkollegium schulkollegium = opt.get();
				alleLehrer.addAll(schulkollegium.alleLehrerUnmodifiable());
				alleLehrer.add(derKollege);
				this.repository.replaceKollegen(new Schulkollegium(schulkuerzel, alleLehrer.toArray(new Kollege[0])));
			} else {

				alleLehrer.add(derKollege);
				Schulkollegium neuesSchulkollegium = new Schulkollegium(schulkuerzel, alleLehrer.toArray(new Kollege[0]));
				this.repository.addKollegium(neuesSchulkollegium);
			}
		}
	}

	private void handleUnchangedSchulen(final Kollege derKollege, final List<Identifier> alteSchulen, final List<Identifier> neueSchulen) {

		List<Identifier> unchangedSchulen = this.getUnchangedList(alteSchulen, neueSchulen);

		for (Identifier schulkuerzel : unchangedSchulen) {

			Optional<Schulkollegium> opt = repository.ofSchulkuerzel(schulkuerzel);

			if (opt.isPresent()) {

				Schulkollegium kollegium = opt.get();

				List<Kollege> verbleibendeLehrer = kollegium.alleLehrerUnmodifiable().stream().filter(p -> !p.equals(derKollege))
					.collect(Collectors.toList());

				// bei Änderung des Namens!!!
				verbleibendeLehrer.add(derKollege);

				this.repository.replaceKollegen(new Schulkollegium(schulkuerzel, verbleibendeLehrer.toArray(new Kollege[0])));
			} else {

				LOG.debug("Schulkollegium mit Schule {} nicht vorhanden", schulkuerzel);
			}

		}
	}

	List<Kollege> getVerbleibendeLehrer(final Kollege dieserLehrer, final Schulkollegium kollegium) {

		List<Kollege> verbleibendeLehrer = kollegium.alleLehrerUnmodifiable().stream().filter(p -> !p.equals(dieserLehrer))
			.collect(Collectors.toList());
		return verbleibendeLehrer;
	}

	List<Identifier> getDeregisterList(final List<Identifier> alteSchulen, final List<Identifier> neueSchulen) {

		List<Identifier> result = alteSchulen.stream().filter(s -> !neueSchulen.contains(s))
			.collect(Collectors.toList());

		result.sort(new IdentifierComparator());
		return result;
	}

	List<Identifier> getRegisterList(final List<Identifier> alteSchulen, final List<Identifier> neueSchulen) {

		return getDeregisterList(neueSchulen, alteSchulen);
	}

	List<Identifier> getUnchangedList(final List<Identifier> alteSchulen, final List<Identifier> neueSchulen) {

		List<Identifier> result = neueSchulen.stream().filter(s -> alteSchulen.contains(s)).collect(Collectors.toList());
		result.sort(new IdentifierComparator());
		return result;
	}
}
