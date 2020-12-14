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
	void handleLehrerChanged(@Observes final LehrerChanged event) {

		List<Identifier> alteSchulen = StringUtils.isBlank(event.alteSchulkuerzel()) ? new ArrayList<>()
			: Arrays.stream(event.alteSchulkuerzel().split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

		List<Identifier> neueSchulen = StringUtils.isBlank(event.neueSchulkuerzel()) ? new ArrayList<>()
			: Arrays.stream(event.neueSchulkuerzel().split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

		final Person derLehrer = event.person();

		this.handleDeregisteredSchulen(derLehrer, alteSchulen, neueSchulen);
		this.handleRegisteredSchulen(derLehrer, alteSchulen, neueSchulen);
		this.handleUnchangedSchulen(derLehrer, alteSchulen, neueSchulen);

	}

	/**
	 * @param Lehrer
	 *               lehrer
	 */
	public void entferneSpurenDesLehrers(final Lehrer lehrer) {

		final Person person = new Person(lehrer.person().uuid(), "ANONYM");

		List<Identifier> alteSchulen = StringUtils.isBlank(lehrer.persistierbareTeilnahmenummern()) ? new ArrayList<>()
			: Arrays.stream(lehrer.persistierbareTeilnahmenummern().split(",")).map(k -> new Identifier(k))
				.collect(Collectors.toList());

		this.handleDeregisteredSchulen(person, alteSchulen, new ArrayList<>());

	}

	private void handleDeregisteredSchulen(final Person derLehrer, final List<Identifier> alteSchulen, final List<Identifier> neueSchulen) {

		List<Identifier> deregisteredFromSchulen = this.getDeregisterList(alteSchulen, neueSchulen);

		for (Identifier schulkuerzel : deregisteredFromSchulen) {

			Optional<Schulkollegium> opt = repository.ofSchulkuerzel(schulkuerzel);

			if (opt.isPresent()) {

				Schulkollegium kollegium = opt.get();
				List<Person> verbleibendeLehrer = getVerbleibendeLehrer(derLehrer, kollegium);

				if (verbleibendeLehrer.isEmpty()) {

					this.repository.deleteKollegium(kollegium);

				} else {

					this.repository.replaceKollegen(new Schulkollegium(schulkuerzel, verbleibendeLehrer.toArray(new Person[0])));
				}
			}
		}
	}

	private void handleRegisteredSchulen(final Person derLehrer, final List<Identifier> alteSchulen, final List<Identifier> neueSchulen) {

		List<Identifier> registeredAtSchulen = this.getRegisterList(alteSchulen, neueSchulen);

		for (Identifier schulkuerzel : registeredAtSchulen) {

			Optional<Schulkollegium> opt = repository.ofSchulkuerzel(schulkuerzel);

			List<Person> alleLehrer = new ArrayList<>();

			if (opt.isPresent()) {

				Schulkollegium schulkollegium = opt.get();
				alleLehrer.addAll(schulkollegium.alleLehrerUnmodifiable());
				alleLehrer.add(derLehrer);
				this.repository.replaceKollegen(new Schulkollegium(schulkuerzel, alleLehrer.toArray(new Person[0])));
			} else {

				alleLehrer.add(derLehrer);
				Schulkollegium neuesSchulkollegium = new Schulkollegium(schulkuerzel, alleLehrer.toArray(new Person[0]));
				this.repository.addKollegium(neuesSchulkollegium);
			}
		}
	}

	private void handleUnchangedSchulen(final Person derLehrer, final List<Identifier> alteSchulen, final List<Identifier> neueSchulen) {

		List<Identifier> unchangedSchulen = this.getUnchangedList(alteSchulen, neueSchulen);

		for (Identifier schulkuerzel : unchangedSchulen) {

			Optional<Schulkollegium> opt = repository.ofSchulkuerzel(schulkuerzel);

			if (opt.isPresent()) {

				Schulkollegium kollegium = opt.get();

				List<Person> verbleibendeLehrer = kollegium.alleLehrerUnmodifiable().stream().filter(p -> !p.equals(derLehrer))
					.collect(Collectors.toList());

				// bei Änderung des Namens!!!
				verbleibendeLehrer.add(derLehrer);

				this.repository.replaceKollegen(new Schulkollegium(schulkuerzel, verbleibendeLehrer.toArray(new Person[0])));
			} else {

				LOG.debug("Schulkollegium mit Schule {} nicht vorhanden", schulkuerzel);
			}

		}
	}

	List<Person> getVerbleibendeLehrer(final Person dieserLehrer, final Schulkollegium kollegium) {

		List<Person> verbleibendeLehrer = kollegium.alleLehrerUnmodifiable().stream().filter(p -> !p.equals(dieserLehrer))
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
