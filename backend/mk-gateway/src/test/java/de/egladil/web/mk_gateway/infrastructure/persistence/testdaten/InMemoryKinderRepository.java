// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.testdaten;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.infrastructure.persistence.testdaten.entities.InMemoryKinderList;

/**
 * InMemoryKinderRepository
 */
public class InMemoryKinderRepository implements KinderRepository {

	private Map<Identifier, Kind> alleKinder = new HashMap<>();

	public InMemoryKinderRepository() {

		try (InputStream in = getClass().getResourceAsStream("/kinderCollection.json")) {

			ObjectMapper objectMapper = new ObjectMapper();

			InMemoryKinderList list = objectMapper.readValue(in, InMemoryKinderList.class);

			list.getKinder().stream().forEach(kind -> {

				alleKinder.put(kind.identifier(), kind);
			});

		} catch (Exception e) {

			throw new RuntimeException("Kann Testsetting nicht initialisieren: " + e.getMessage(), e);
		}
	}

	@Override
	public List<Kind> withTeilnahme(final TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier) {

		return alleKinder.values().stream().filter(k -> k.teilnahmeIdentifier().equals(teilnahmeIdentifier))
			.collect(Collectors.toList());
	}

	@Override
	public Optional<Kind> ofId(final Identifier identifier) {

		Kind kind = alleKinder.get(identifier);

		return kind == null ? Optional.empty() : Optional.of(kind);
	}

	@Override
	public Optional<Kind> findKindWithLoesungszettelId(final Identifier loesungszettelID) {

		return alleKinder.values().stream().filter(k -> loesungszettelID.equals(k.loesungszettelID())).findFirst();
	}

	@Override
	public Kind addKind(final Kind kind) {

		Identifier identifier = new Identifier(UUID.randomUUID().toString());

		Kind neues = new Kind(identifier)
			.withKlasseID(kind.klasseID())
			.withKlassenstufe(kind.klassenstufe())
			.withLandkuerzel(kind.landkuerzel())
			.withLoesungszettelID(kind.loesungszettelID())
			.withNachname(kind.nachname())
			.withSprache(kind.sprache())
			.withTeilnahmeIdentifier(kind.teilnahmeIdentifier())
			.withVorname(kind.vorname())
			.withZusatz(kind.zusatz());

		alleKinder.put(identifier, neues);
		return neues;
	}

	@Override
	public boolean changeKind(final Kind kind) {

		Kind vorhandenes = alleKinder.get(kind.identifier());

		if (vorhandenes == null) {

			return false;
		}

		alleKinder.put(kind.identifier(), kind);
		return true;
	}

	@Override
	public boolean removeKind(final Kind kind) {

		Kind vorhandenes = alleKinder.remove(kind.identifier());

		return vorhandenes != null;
	}

	@Override
	public int removeKinder(final List<Kind> kinder) {

		int count = 0;

		for (Kind kind : kinder) {

			boolean removed = this.removeKind(kind);

			if (removed) {

				count++;
			}
		}

		return count;
	}

	@Override
	public long countKinderInKlasse(final Klasse klasse) {

		return alleKinder.values().stream().filter(k -> klasse.identifier().equals(k.klasseID())).count();

	}

	@Override
	public long countLoesungszettelInKlasse(final Klasse klasse) {

		return alleKinder.values().stream().filter(k -> k.loesungszettelID() != null && klasse.identifier().equals(k.klasseID()))
			.count();
	}

	@Override
	public List<Kind> withTeilnahmeHavingLoesungszettel(final TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdentifier) {

		return alleKinder.values().stream()
			.filter(k -> k.teilnahmeIdentifier().equals(teilnahmeIdentifier) && k.loesungszettelID() != null)
			.collect(Collectors.toList());
	}

}
