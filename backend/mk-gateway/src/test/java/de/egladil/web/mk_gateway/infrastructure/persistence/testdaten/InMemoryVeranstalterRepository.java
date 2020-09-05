// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.Lehrer;
import de.egladil.web.mk_gateway.domain.veranstalter.Privatveranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.entities.InMemoryVeranstalter;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.entities.InMemoryVeranstalterList;

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

			Privatveranstalter privatveranstalter = new Privatveranstalter(ver.getPerson(), true, ver.getTeilnahmenummern());

			switch (ver.getZugangUnterlagen()) {

			case DEFAULT:
				break;

			case ENTZOGEN:
				privatveranstalter.verwehreZugangUnterlagen();
				break;

			case ERTEILT:
				privatveranstalter.erlaubeZugangUnterlagen();
			default:
				break;
			}
			return privatveranstalter;
		}

		if (ver.getRolle() == Rolle.LEHRER) {

			Lehrer lehrer = new Lehrer(ver.getPerson(), true, ver.getTeilnahmenummern());

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

	@Override
	public List<Veranstalter> loadPrivatveranstalter() {

		return null;
	}

}
