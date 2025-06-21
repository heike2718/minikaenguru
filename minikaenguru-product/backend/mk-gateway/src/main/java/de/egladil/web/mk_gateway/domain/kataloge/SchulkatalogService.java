// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kataloge;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_mailer.DefaultEmailDaten;
import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.apimodel.StringsAPIModel;
import de.egladil.web.mk_gateway.domain.error.DuplicateEntityException;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kataloge.api.LandPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.OrtPayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulePayload;
import de.egladil.web.mk_gateway.domain.kataloge.api.SchulkatalogAntrag;
import de.egladil.web.mk_gateway.domain.kataloge.dto.KatalogItem;
import de.egladil.web.mk_gateway.domain.kataloge.dto.Katalogtyp;
import de.egladil.web.mk_gateway.domain.mail.AdminMailService;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.veranstalter.api.SchuleAPIModel;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbStatus;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.dao.KatalogeRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.dao.SchuleRepository;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Land;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Ort;
import de.egladil.web.mk_gateway.infrastructure.persistence.kataloge.entities.Schule;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.PersistenceException;
import jakarta.validation.constraints.NotBlank;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

/**
 * SchulkatalogService
 */
@ApplicationScoped
public class SchulkatalogService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SchulkatalogService.class);

	@ConfigProperty(name = "email.admin")
	String emailAdmin;

	@Inject
	KatalogeRepository katalogeRepository;

	@Inject
	SchuleRepository schuleRepository;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	AdminMailService mailService;

	@Inject
	LoggableEventDelegate eventDelegate;

	@Inject
	DomainEventHandler domainEventHandler;

	/**
	 * Ermittelt die Daten der Schule mit gegebenem schulkuerzel aus der Katalog-DB. Exceptions werden nur geloggt.
	 *
	 * @param schulkuerzel
	 * @return Optional
	 */
	public Optional<SchuleAPIModel> findSchule(final String schulkuerzel) {

		Optional<Schule> optSchule = katalogeRepository.findSchuleWithKuerzel(schulkuerzel);

		if (optSchule.isEmpty()) {
			return Optional.empty();
		}

		return Optional.of(new SchulkatalogEntitiesMapper().mapSchuleToSchuleAPIModel(optSchule.get()));
	}

	/**
	 * Ermittelt die Daten der Schulen aus der Katalog-DB. Exceptions weren nur geloggt.
	 *
	 * @param schulkuerzel
	 * @return
	 */
	public List<SchuleAPIModel> loadSchulenQuietly(final StringsAPIModel schulkuerzel) {

		List<String> relevanteKuerzel = schulkuerzel.getStrings().stream().filter(k -> !k.isBlank()).map(k -> k.trim())
			.collect(Collectors.toList());

		List<Schule> schulen = katalogeRepository.findSchulenWithKuerzeln(relevanteKuerzel);

		final SchulkatalogEntitiesMapper mapper = new SchulkatalogEntitiesMapper();
		return schulen.stream().map(s -> mapper.mapSchuleToSchuleAPIModel(s)).toList();
	}

	/**
	 * Benennt die gegebene Schule um.
	 *
	 * @param uuid String die UUID eines Admins
	 * @param secret String irgendein Secret
	 * @param payload
	 * @return
	 */
	public ResponsePayload renameSchule(final SchulePayload payload) {

		Schule schule = findSchule(payload);
		Schulteilnahme schulteilnahme = findSchulteilnahme(payload);

		schule.setName(payload.name().trim());
		schuleRepository.updateSchule(schule);

		if (schulteilnahme != null) {
			schulteilnahme.setNameSchule(payload.name().trim());
			teilnahmenRepository.changeTeilnahme(schulteilnahme);
		}

		if (StringUtils.isNotBlank(payload.emailAuftraggeber())) {

			this.mailService.sendSchuleCreatedMailQuietly(payload, emailAdmin);
		}

		return new ResponsePayload(MessagePayload.info("Die Schule wurde erfolgreich geändert."), payload);
	}

	/**
	 * Sucht die Schule zum Umbenennen.
	 *
	 * @param payload SchulePayload
	 * @return Schule
	 */
	Schule findSchule(final SchulePayload payload) {

		Optional<Schule> optSchule = schuleRepository.getSchule(payload.kuerzel());

		if (optSchule.isEmpty()) {
			Response response = Response.status(404)
				.entity(new ResponsePayload(MessagePayload.error("Diese Schule gibt es nicht."), payload)).build();
			throw new NotFoundException(response);
		}

		Schule schule = optSchule.get();

		if (!schule.getOrtKuerzel().equals(payload.kuerzelOrt()) || !schule.getLandKuerzel().equals(payload.kuerzelLand())) {

			String msg = "Umbenennung abgelehnt: Ort oder Land passt nicht.";
			LOGGER.warn(msg + " - " + schule.printForLog() + ", " + payload.toString());
			Response response = Response.status(412).entity(ResponsePayload.messageOnly(MessagePayload.error(msg))).build();
			throw new WebApplicationException(response);
		}

		return schule;
	}

	Schulteilnahme findSchulteilnahme(final SchulePayload payload) {

		Optional<Wettbewerb> optWettbewerb = wettbewerbService.aktuellerWettbewerb();

		if (optWettbewerb.isPresent()) {

			Wettbewerb wettbewerb = optWettbewerb.get();

			if (WettbewerbStatus.ANMELDUNG == wettbewerb.status() || WettbewerbStatus.DOWNLOAD_LEHRER == wettbewerb.status()
				|| WettbewerbStatus.DOWNLOAD_PRIVAT == wettbewerb.status()) {

				TeilnahmeIdentifier teilnahmeIdentifier = new TeilnahmeIdentifier().withTeilnahmeart(Teilnahmeart.SCHULE)
					.withTeilnahmenummer(payload.kuerzel()).withWettbewerbID(wettbewerb.id());

				Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier);

				if (optTeilnahme.isPresent()) {

					Teilnahme teilnahme = optTeilnahme.get();
					return (Schulteilnahme) teilnahme;

				}

			}
		}

		return null;
	}

	/**
	 * Läd die Länder des Schulkatalogs, aber ohne Anzahl Orte.
	 *
	 * @return List
	 */
	public List<KatalogItem> loadLaender() {

		List<Land> laender = katalogeRepository.loadLaender();
		return laender.stream().map(l -> mapLandToKatalogItem(l)).toList();
	}

	KatalogItem mapLandToKatalogItem(Land land) {

		int anzahlOrte = katalogeRepository.countOrteInLand(land.getKuerzel());

		KatalogItem result = KatalogItem.createWithTypKuerzelName(Katalogtyp.LAND, land.getKuerzel(), land.getName(),
			anzahlOrte);

		result.setPfad(land.getName());

		return result;

	}

	/**
	 * Gibt alle Schulen im Ort mit dem Kuerzel ortkuerzel zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param ortkuerzel String
	 * @param suchbegriff String
	 * @return List
	 */
	public List<KatalogItem> findSchulenInOrt(final String ortkuerzel, final String suchbegriff) {

		List<Schule> schulen = katalogeRepository.findSchulenInOrt(ortkuerzel, suchbegriff);
		List<KatalogItem> result = schulen.stream().map(s -> mapSchuleToKatalogItem(s)).toList();
		return result;
	}

	/**
	 * Läd alle Schulen im gegebenen Ort. Nur für Orte mit maximal 25 Schulen. Bei mehr Schulen muss die Suchfunktion
	 * mit dem Namen verwendet werden.
	 *
	 * @param ortkuerzel String
	 * @return List
	 */
	public List<KatalogItem> loadSchulenInOrt(String ortkuerzel) {
		List<Schule> schulen = katalogeRepository.loadSchulenInOrt(ortkuerzel);
		List<KatalogItem> result = schulen.stream().map(s -> mapSchuleToKatalogItem(s)).toList();
		return result;
	}

	KatalogItem mapSchuleToKatalogItem(Schule schule) {

		Optional<Ort> optOrt = katalogeRepository.findOrtWithKuerzel(schule.getOrtKuerzel());
		Optional<Land> optLand = katalogeRepository.findLandWithKuerzel(schule.getLandKuerzel());

		Ort ort = optOrt.get();
		int anzahlSchulen = katalogeRepository.countSchulenInOrt(ort.getKuerzel());
		ort.setAnzahlSchulen(anzahlSchulen);

		Land land = optLand.get();
		int anzahlOrte = katalogeRepository.countOrteInLand(land.getKuerzel());
		land.setAnzahlOrte(anzahlOrte);

		return new SchulkatalogEntitiesMapper().mapSchuleToKatalogItem(schule, ort, land);

	}

	/**
	 * Gibt alle Orte im Land mit dem Kuerzel landkuerzel zurück, deren Name mit suchbegriff beginnt.
	 *
	 * @param landkuerzel String
	 * @param suchbegriff String
	 * @return List
	 */
	public List<KatalogItem> findOrteInLand(final String landkuerzel, final String suchbegriff) {

		List<Ort> orteInLand = katalogeRepository.findOrteInLand(landkuerzel, suchbegriff);
		List<KatalogItem> result = orteInLand.stream().map(o -> mapOrtToKatalogItem(o)).toList();
		return result;
	}

	/**
	 * Läd alle Orte im gegebenen Land. Nur für Länder mit maximal 25 Orten. Bei mehr Orten muss die Suchfunktion mit
	 * dem Namen verwendet werden.
	 *
	 * @param landkuerzel String
	 * @return List
	 */
	public List<KatalogItem> loadOrteInLand(String landkuerzel) {
		List<Ort> orte = katalogeRepository.loadOrteInLand(landkuerzel);
		List<KatalogItem> result = orte.stream().map(o -> mapOrtToKatalogItem(o)).toList();
		return result;
	}

	KatalogItem mapOrtToKatalogItem(Ort ort) {

		Optional<Land> optLand = katalogeRepository.findLandWithKuerzel(ort.getLandKuerzel());

		int anzahlSchulen = katalogeRepository.countSchulenInOrt(ort.getKuerzel());
		ort.setAnzahlSchulen(anzahlSchulen);

		Land land = optLand.get();
		int anzahlOrte = katalogeRepository.countOrteInLand(land.getKuerzel());
		land.setAnzahlOrte(anzahlOrte);

		return new SchulkatalogEntitiesMapper().mapOrtToKatalogItem(ort, land);
	}

	/**
	 * Sendet eine Mail an mich mit einem Schulkatalogantrag.
	 *
	 * @param antrag SchulkatalogAntrag
	 */
	public void sendeSchulkatalogAntrag(SchulkatalogAntrag antrag) {

		if (StringUtils.isNotBlank(antrag.kleber())) {

			String msg = "Honeypot des Schulkatalogantrags war nicht blank: " + antrag.toSecurityLog();
			eventDelegate.fireSecurityEvent(msg, domainEventHandler);
			LOGGER.warn(msg);
			throw new WebApplicationException(400);
		}

		DefaultEmailDaten emailDaten = createMailDatenSchulkatalogAntrag(antrag);
		this.mailService.sendMail(emailDaten);
	}

	private DefaultEmailDaten createMailDatenSchulkatalogAntrag(final SchulkatalogAntrag antrag) {

		DefaultEmailDaten result = new DefaultEmailDaten();
		result.setBetreff("Minikänguru: Schulkatalog");
		result.setText(new KatalogAntragMailtextGenerator().getSchuleKatalogantragText(antrag));
		result.setEmpfaenger(antrag.email());
		result.addHiddenEmpfaenger(emailAdmin);
		return result;

	}

	/**
	 * Globale Suche nach Schulen mit searchTerm im Namen.
	 *
	 * @param searchTerm String
	 * @return List
	 */
	public List<KatalogItem> sucheSchulenMitNameEnthaltend(@NotBlank
	String searchTerm) {
		List<Schule> trefferliste = katalogeRepository.findSchulen(searchTerm);
		List<KatalogItem> list = trefferliste.stream().map(s -> mapSchuleToKatalogItem(s)).toList();
		return list;
	}

	/**
	 * Globale Suche mit Orten, die mit searchTerm beginnen.
	 *
	 * @param searchTerm String
	 * @return List
	 */
	public List<KatalogItem> sucheOrteMitNameBeginnendMit(@NotBlank
	String searchTerm) {
		List<Ort> trefferliste = katalogeRepository.findOrte(searchTerm);
		List<KatalogItem> list = trefferliste.stream().map(o -> mapOrtToKatalogItem(o)).toList();
		return list;
	}

	/**
	 * Globale Suche nach Ländern, die mit searchTerm beginnen.
	 *
	 * @param searchTerm String
	 * @return List
	 */
	public List<KatalogItem> sucheLaenderMitNameBeginnendMit(@NotBlank
	String searchTerm) {
		List<Land> trefferliste = katalogeRepository.findLander(searchTerm);
		List<KatalogItem> list = trefferliste.stream().map(l -> mapLandToKatalogItem(l)).toList();
		return list;
	}

	/**
	 * Bennennt den Ort um und gibt ihn als KatalogItem zuürück.
	 *
	 * @param ortPayload OrtPayload
	 * @return
	 */
	public ResponsePayload ortUmbenennen(final OrtPayload ortPayload) {

		try {

			Optional<Ort> optOrt = schuleRepository.getOrt(ortPayload.kuerzel());

			if (optOrt.isEmpty()) {

				throw new NotFoundException(Response.status(404)
					.entity(new ResponsePayload(MessagePayload.error("Diesen Ort gibt es nicht."), ortPayload)).build());
			}

			Ort ort = optOrt.get();

			if (!ort.getLandKuerzel().equals(ortPayload.kuerzelLand())) {

				String msg = "Umbenennung abgelehnt: Land passt nicht.";
				LOGGER.warn(msg + " - " + ort.printForLog() + ", " + ortPayload.toString());
				throw new WebApplicationException(
					Response.status(412).entity(ResponsePayload.messageOnly(MessagePayload.error(msg))).build());
			}

			List<Ort> orte = schuleRepository.findOrteInLand(ortPayload.kuerzelLand());

			Optional<Ort> optOrtGleichenNamens = orte.stream()
				.filter(o -> !o.getKuerzel().equals(ortPayload.kuerzel()) && ortPayload.name().equalsIgnoreCase(o.getName()))
				.findFirst();

			if (optOrtGleichenNamens.isPresent()) {

				return new ResponsePayload(
					MessagePayload.warn("Umbenennung abgelehnt: Es gibt im gleichen Land bereits einen anderen Ort mit dem Namen "
						+ ortPayload.name() + ". Dieser wurde zurückgegeben."),
					OrtPayload.create(optOrtGleichenNamens.get()));
			}

			List<Schule> schulen = schuleRepository.findSchulenInOrt(ortPayload.kuerzel());

			schulen.forEach(s -> s.setOrtName(ortPayload.name()));

			schuleRepository.replaceSchulen(schulen);

			LOGGER.info("Anzahl geänderter Schulen: {}", schulen.size());

			return new ResponsePayload(
				MessagePayload.info("Der Ort wurde erfolgreich umbenannt. Anzahl geänderter Schulen: " + schulen.size()),
				ortPayload);

		} catch (PersistenceException e) {

			LOGGER.error("Die Schulen zum Ort {} konnten nicht geändert werden: {}", ortPayload, e.getMessage(), e);
			throw new MkGatewayRuntimeException("Der Ort konnte wegen eines Serverfehlers nicht umbenannt werden.");
		}
	}

	public ResponsePayload landUmbenennen(final LandPayload landPayload) {

		try {

			List<Land> laender = schuleRepository.loadLaender();

			Optional<Land> optLand = laender.stream().filter(l -> l.getKuerzel().equals(landPayload.kuerzel())).findFirst();

			if (optLand.isEmpty()) {

				throw new NotFoundException(Response.status(404)
					.entity(new ResponsePayload(MessagePayload.error("Dieses Land gibt es nicht."), landPayload)).build());
			}

			optLand = laender.stream()
				.filter(l -> !l.getKuerzel().equals(landPayload.kuerzel()) && l.getName().equalsIgnoreCase(landPayload.name()))
				.findFirst();

			if (optLand.isPresent()) {

				return new ResponsePayload(
					MessagePayload.warn("Umbenennung abgelehnt: Es gibt bereits ein anderes Land mit dem Namen "
						+ landPayload.name() + ". Dieses wurde zurückgegeben."),
					LandPayload.create(optLand.get()));
			}

			List<Schule> schulen = schuleRepository.findSchulenInLand(landPayload.kuerzel());

			schulen.forEach(s -> {

				s.setLandName(landPayload.name());
				s.setLandKuerzel(s.getLandKuerzel());
			});

			schuleRepository.replaceSchulen(schulen);

			LOGGER.info("Anzahl geänderter Schulen: {}", schulen.size());

			return new ResponsePayload(
				MessagePayload.info("Das Land wurde erfolgreich umbenannt. Anzahl geänderter Schulen: " + schulen.size()),
				landPayload);

		} catch (PersistenceException e) {

			LOGGER.error("Die Schulen mit Land {} konnten nicht geändert werden: {}", landPayload, e.getMessage(), e);
			throw new MkGatewayRuntimeException("Das Land konnte wegen eines Serverfehlers nicht umbenannt werden.");
		}

	}

	/**
	 * Falls es die Schule noch nicht gibt (Gleichheit ortKuerzel und Name), wird sie angelegt und persistiert. Eine
	 * Meil wird an die Adresse des Auftraggebers gesendet, sofern diese angegeben ist. Wenn es den Ort oder das Land
	 * noch nicgt gibt, wird es ebenfalls angelegt
	 *
	 * @param schulePayload
	 * @return ResponsePayload mit SchulePayload als Daten.
	 */
	public ResponsePayload schuleAnlegen(final SchulePayload schulePayload) {

		try {

			List<Schule> schulen = schuleRepository.findSchulenInOrt(schulePayload.kuerzelOrt());
			Optional<Schule> optSchule = schulen.stream()
				.filter(s -> !s.getKuerzel().equals(schulePayload.kuerzel()) && s.getName().equalsIgnoreCase(schulePayload.name()))
				.findFirst();

			if (optSchule.isPresent()) {

				SchulePayload result = SchulePayload.create(optSchule.get());

				if (StringUtils.isNotBlank(schulePayload.emailAuftraggeber())) {

					this.mailService.sendSchuleCreatedMailQuietly(schulePayload, emailAdmin);
				}
				return new ResponsePayload(MessagePayload.warn("Diese Schule gibt es bereits."), result);
			}

			Schule schule = mapFromSchulePayload(schulePayload);

			boolean added = schuleRepository.addSchule(schule);
			LOGGER.debug("Schule added=" + added);

			if (StringUtils.isNotBlank(schulePayload.emailAuftraggeber())) {

				this.mailService.sendSchuleCreatedMailQuietly(schulePayload, emailAdmin);
			} else {

				LOGGER.debug("emailAuftraggeber war blank - keine Mail gesendet.");
			}

			return new ResponsePayload(MessagePayload.info("Die Schule wurde erfolgreich angelegt."), schulePayload);

		} catch (DuplicateEntityException e) {

			String msg = "schuleAnlegen: " + e.getMessage();
			throw new MkGatewayRuntimeException(msg);
		} catch (PersistenceException e) {

			LOGGER.error("Die Schule {} konnte nicht angelegt werden: {}", schulePayload, e.getMessage(), e);
			throw new MkGatewayRuntimeException("Die Schule konnte wegen eines Serverfehlers nicht angelegt werden.");
		}
	}

	Schule mapFromSchulePayload(final SchulePayload schulePayload) {

		Schule result = new Schule();
		result.setImportiertesKuerzel(schulePayload.kuerzel());
		result.setLandKuerzel(schulePayload.kuerzelLand());
		result.setLandName(schulePayload.nameLand());
		result.setName(schulePayload.name());
		result.setOrtKuerzel(schulePayload.kuerzelOrt());
		result.setOrtName(schulePayload.nameOrt());

		return result;
	}

}
