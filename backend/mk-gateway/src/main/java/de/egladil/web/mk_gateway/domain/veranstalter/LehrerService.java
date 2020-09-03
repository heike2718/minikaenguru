// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.veranstalter;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.semantik.DomainService;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * LehrerService
 */
@ApplicationScoped
@DomainService
public class LehrerService {

	private static final Logger LOG = LoggerFactory.getLogger(LehrerService.class);

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	SchulkollegienService schulkollegienService;

	@Inject
	Event<LehrerChanged> lehrerChanged;

	private LehrerChanged lehrerChangedEventPayload;

	public static LehrerService createServiceForTest(final VeranstalterRepository lehrerRepository) {

		LehrerService result = new LehrerService();
		result.veranstalterRepository = lehrerRepository;
		return result;

	}

	@Transactional
	public boolean addLehrer(final CreateOrUpdateLehrerCommand data) {

		Optional<Veranstalter> optLehrer = veranstalterRepository.ofId(new Identifier(data.uuid()));

		if (optLehrer.isPresent()) {

			LOG.info("Es gibt bereits einen Lehrer: {}", optLehrer.get());

			return false;
		}
		String neueSchulkuerzel = data.schulkuerzel();

		List<Identifier> schulen = Arrays.stream(neueSchulkuerzel.split(",")).map(s -> new Identifier(s))
			.collect(Collectors.toList());
		Person person = new Person(data.uuid(), data.fullName());

		Lehrer lehrer = new Lehrer(person, data.newsletterEmpfaenger(), schulen);

		veranstalterRepository.addVeranstalter(lehrer);

		lehrerChangedEventPayload = new LehrerChanged(person, "", neueSchulkuerzel, data.newsletterEmpfaenger());

		if (lehrerChanged != null) {

			lehrerChanged.fire(lehrerChangedEventPayload);
		}

		return true;

	}

	public boolean changeLehrer(final CreateOrUpdateLehrerCommand data) {

		Optional<Veranstalter> optLehrer = veranstalterRepository.ofId(new Identifier(data.uuid()));

		if (optLehrer.isEmpty()) {

			LOG.warn("Versuch, einen nicht existierenden Lehrer zu ändern: {}", data);

			return false;
		}

		Veranstalter veranstalter = optLehrer.get();

		if (Rolle.LEHRER != veranstalter.rolle()) {

			LOG.warn("Versuch, einen Veranstalter zu ändern, der kein Lehrer ist: {} - {}", data, veranstalter);

			return false;
		}

		Lehrer vorhandener = (Lehrer) veranstalter;

		String alteSchulkuerzel = StringUtils.join(vorhandener.schulen(), ",");
		String neueSchulkuerzel = data.schulkuerzel();

		List<Identifier> schulen = Arrays.stream(neueSchulkuerzel.split(",")).map(s -> new Identifier(s))
			.collect(Collectors.toList());

		Lehrer geaenderterLehrer = new Lehrer(new Person(veranstalter.uuid(), data.fullName()), data.newsletterEmpfaenger(),
			schulen);

		veranstalterRepository.changeVeranstalter(geaenderterLehrer);

		lehrerChangedEventPayload = new LehrerChanged(geaenderterLehrer.person(), alteSchulkuerzel, neueSchulkuerzel,
			data.newsletterEmpfaenger());

		if (lehrerChanged != null) {

			lehrerChanged.fire(lehrerChangedEventPayload);
		}

		return true;
	}

	LehrerChanged event() {

		return lehrerChangedEventPayload;
	}

}
