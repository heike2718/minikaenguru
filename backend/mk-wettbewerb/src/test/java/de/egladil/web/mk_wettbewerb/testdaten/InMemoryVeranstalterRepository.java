// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.testdaten;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_wettbewerb.domain.Identifier;
import de.egladil.web.mk_wettbewerb.domain.personen.Lehrer;
import de.egladil.web.mk_wettbewerb.domain.personen.Privatperson;
import de.egladil.web.mk_wettbewerb.domain.personen.Rolle;
import de.egladil.web.mk_wettbewerb.domain.personen.Veranstalter;
import de.egladil.web.mk_wettbewerb.domain.personen.VeranstalterRepository;
import de.egladil.web.mk_wettbewerb.testdaten.entities.InMemoryVeranstalter;
import de.egladil.web.mk_wettbewerb.testdaten.entities.InMemoryVeranstalterList;

/**
 * InMemoryVeranstalterRepository
 */
public class InMemoryVeranstalterRepository implements VeranstalterRepository {

	private int countPrivatpersonAdded = 0;

	private int countLehrerAdded = 0;

	private int countPrivatpersonChanged = 0;

	private int countLehrerChanged = 0;

	private final List<Veranstalter> alleVeranstalter = new ArrayList<>();

	public InMemoryVeranstalterRepository() {

		try (InputStream in = getClass().getResourceAsStream("/veranstalter.json")) {

			ObjectMapper objectMapper = new ObjectMapper();

			InMemoryVeranstalterList list = objectMapper.readValue(in, InMemoryVeranstalterList.class);

			list.getVeranstalter().stream().forEach(ver -> {

				Veranstalter veranstalter = mapToVeranstalter(ver);

				if (veranstalter != null) {

					alleVeranstalter.add(veranstalter);
				} else {

					System.err.println("keine korrekte Rolle gefunden: " + ver.getRolle());
				}
			});

		} catch (Exception e) {

			throw new RuntimeException("Kann Testsetting nicht initialisieren: " + e.getMessage(), e);
		}

	}

	private Veranstalter mapToVeranstalter(final InMemoryVeranstalter ver) {

		if (ver.getRolle() == Rolle.PRIVAT) {

			Privatperson privatperson = new Privatperson(ver.getPerson(), ver.getTeilnahmenummern());

			switch (ver.getZugangUnterlagen()) {

			case DEFAULT:
				break;

			case ENTZOGEN:
				privatperson.verwehreZugangUnterlagen();
				break;

			case ERTEILT:
				privatperson.erlaubeZugangUnterlagen();
			default:
				break;
			}
			return privatperson;
		}

		if (ver.getRolle() == Rolle.LEHRER) {

			Lehrer lehrer = new Lehrer(ver.getPerson(), ver.getTeilnahmenummern());

			switch (ver.getZugangUnterlagen()) {

			case DEFAULT:
				break;

			case ENTZOGEN:
				lehrer.verwehreZugangUnterlagen();
				break;

			case ERTEILT:
				lehrer.erlaubeZugangUnterlagen();
			default:
				break;
			}
			return lehrer;
		}

		return null;

	}

	@Override
	public Optional<Veranstalter> ofId(final Identifier identifier) {

		return alleVeranstalter.stream().filter(v -> identifier.toString().equals(v.person().uuid())).findFirst();
	}

	@Override
	public void addVeranstalter(final Veranstalter veranstalter) throws IllegalStateException {

		switch (veranstalter.rolle()) {

		case PRIVAT:
			countPrivatpersonAdded++;
			break;

		case LEHRER:
			countLehrerAdded++;
			break;

		default:
			break;
		}

		this.alleVeranstalter.add(veranstalter);
	}

	@Override
	public void changeVeranstalter(final Veranstalter veranstalter) throws IllegalStateException {

		switch (veranstalter.rolle()) {

		case PRIVAT:
			countPrivatpersonChanged++;
			break;

		case LEHRER:
			countLehrerChanged++;
			break;

		default:
			break;
		}
	}

	public int getCountPrivatpersonAdded() {

		return countPrivatpersonAdded;
	}

	public int getCountLehrerAdded() {

		return countLehrerAdded;
	}

	public int getCountPrivatpersonChanged() {

		return countPrivatpersonChanged;
	}

	public int getCountLehrerChanged() {

		return countLehrerChanged;
	}

}
