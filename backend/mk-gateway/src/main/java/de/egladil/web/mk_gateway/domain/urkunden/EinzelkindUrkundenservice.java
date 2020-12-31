// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden;

import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.AuthorizationService;
import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.MkGatewayRuntimeException;
import de.egladil.web.mk_gateway.domain.event.DataInconsistencyRegistered;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.kinder.Kind;
import de.egladil.web.mk_gateway.domain.kinder.KinderRepository;
import de.egladil.web.mk_gateway.domain.kinder.Klasse;
import de.egladil.web.mk_gateway.domain.kinder.KlassenRepository;
import de.egladil.web.mk_gateway.domain.loesungszettel.Loesungszettel;
import de.egladil.web.mk_gateway.domain.loesungszettel.LoesungszettelRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.Schulteilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahme;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;
import de.egladil.web.mk_gateway.domain.teilnahmen.TeilnahmenRepository;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifier;
import de.egladil.web.mk_gateway.domain.teilnahmen.api.TeilnahmeIdentifierAktuellerWettbewerb;
import de.egladil.web.mk_gateway.domain.urkunden.api.UrkundenauftragEinzelkind;
import de.egladil.web.mk_gateway.domain.urkunden.daten.AbstractDatenUrkunde;
import de.egladil.web.mk_gateway.domain.urkunden.generator.UrkundeGenerator;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.CreateDatenKaengurusprungPrivatStrategie;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.CreateDatenKaengurusprungSchuleStrategie;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.CreateDatenTeilnahmePrivatStrategie;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.CreateDatenTeilnahmeSchuleStrategie;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.CreateDatenUrkundeStrategy;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.FontSizeAndLines;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.SplitSchulnameStrategie;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * EinzelkindUrkundenservice
 */
@ApplicationScoped
public class EinzelkindUrkundenservice {

	private static final Logger LOG = LoggerFactory.getLogger(EinzelkindUrkundenservice.class);

	@Inject
	LoesungszettelRepository loesungezettelRepository;

	@Inject
	KinderRepository kinderRepository;

	@Inject
	AuthorizationService authService;

	@Inject
	TeilnahmenRepository teilnahmenRepository;

	@Inject
	KlassenRepository klassenRepository;

	@Inject
	UrkundenmotivRepository urkundenmotivRepository;

	@Inject
	WettbewerbService wettbewerbService;

	@Inject
	Event<DataInconsistencyRegistered> dataInconsistencyEvent;

	private Wettbewerb aktuellerWettbewerb;

	/**
	 * Generiert eine einzelne Teilnehmerurkunde für das gegebene Privatkind.
	 *
	 * @param  kindID
	 * @param  veranstalterID
	 * @param  farbschema
	 * @return
	 */
	public DownloadData generiereUrkunde(final UrkundenauftragEinzelkind urkundenauftrag, final Identifier veranstalterID) {

		Identifier kindID = new Identifier(urkundenauftrag.kindUuid());

		Optional<Kind> optKind = kinderRepository.ofId(kindID);

		if (optKind.isEmpty()) {

			LOG.warn("Kein Kind mit UUID {} bekannt", kindID.identifier());

			throw new NotFoundException();
		}

		Kind kind = optKind.get();

		TeilnahmeIdentifierAktuellerWettbewerb teilnahmeIdntifier = kind.teilnahmeIdentifier();

		authService.checkPermissionForTeilnahmenummer(veranstalterID,
			new Identifier(teilnahmeIdntifier.teilnahmenummer()),
			"[generiereUrkunde - " + kindID.toString() + "]");

		if (kind.loesungszettelID() == null) {

			LOG.warn("Kind mit UUID {} hat keinen Loesungszettel", kindID.identifier());

			throw new NotFoundException();
		}

		Optional<Loesungszettel> optLoesungszettel = loesungezettelRepository.ofID(kind.loesungszettelID());

		if (optLoesungszettel.isEmpty()) {

			String msg = "Loesungszettel mit UUID " + kind.loesungszettelID() + " existiert nicht, ist aber mit Kind "
				+ kind.identifier() + " verknuepft";
			LOG.warn(msg);
			new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);

			throw new NotFoundException();
		}

		Urkundenart urkundenart = urkundenauftrag.urkundenart();

		CreateDatenUrkundeStrategy datenStrategie = this.getDatenUrkundeStrategie(kind, urkundenart);

		Urkundenmotiv urkundenmotiv = this.urkundenmotivRepository.findByFarbschema(urkundenauftrag.farbschema());

		Loesungszettel loesungszettel = optLoesungszettel.get();

		AbstractDatenUrkunde datenUrkunde = datenStrategie.createDatenUrkunde(kind, loesungszettel, urkundenauftrag.dateString(),
			urkundenmotiv);

		UrkundeGenerator generator = UrkundeGenerator.create(urkundenart, loesungszettel.sprache());

		byte[] daten = generator.generiereUrkunde(datenUrkunde);

		String dateiname = dateiname(urkundenart);

		return new DownloadData(dateiname, daten);
	}

	private String dateiname(final Urkundenart urkundenart) {

		String uuid = UUID.randomUUID().toString().substring(0, 8);

		return urkundenart.praefixDateiname() + "_" + uuid + ".pdf";

	}

	CreateDatenUrkundeStrategy getDatenUrkundeStrategie(final Kind kind, final Urkundenart urkundenart) {

		if (kind.teilnahmeIdentifier().teilnahmeart() == Teilnahmeart.PRIVAT) {

			switch (urkundenart) {

			case KAENGURUSPRUNG:

				return new CreateDatenKaengurusprungPrivatStrategie();

			case TEILNAHME:
				return new CreateDatenTeilnahmePrivatStrategie();

			default:
				throw new IllegalArgumentException("Privatteilnahme unzulaessige Urkundenart " + urkundenart);
			}

		}

		if (kind.klasseID() == null) {

			String msg = "Kind aus Schulteilnahme " + kind.identifier() + ", " + kind.teilnahmeIdentifier()
				+ " ist keiner Klasse zugeordnet.";
			LOG.error(msg);
			new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);
			throw new NotFoundException();
		}

		TeilnahmeIdentifier teilnahmeIdentifier = TeilnahmeIdentifier
			.createFromTeilnahmeIdentifierAktuellesJahr(kind.teilnahmeIdentifier()).withWettbewerbID(getWettbewerb().id());

		Optional<Teilnahme> optTeilnahme = teilnahmenRepository.ofTeilnahmeIdentifier(teilnahmeIdentifier);

		if (optTeilnahme.isEmpty()) {

			String msg = "Das sollte hier nicht mehr passieren: konnte keine Teilnahme mit Identifier " + teilnahmeIdentifier
				+ " finden";
			LOG.error(msg);
			new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);
			throw new MkGatewayRuntimeException(msg);
		}

		Schulteilnahme schulteilnahme = (Schulteilnahme) optTeilnahme.get();

		Optional<Klasse> optKlasse = klassenRepository.ofIdentifier(kind.klasseID());

		if (optKlasse.isEmpty()) {

			String msg = "Klasse mit UUID " + kind.klasseID() + " nicht gefunden";
			LOG.error(msg);
			new LoggableEventDelegate().fireDataInconsistencyEvent(msg, dataInconsistencyEvent);
			throw new MkGatewayRuntimeException(msg);
		}

		Klasse klasse = optKlasse.get();

		FontSizeAndLines fontSizeAndLinesSchulname = new SplitSchulnameStrategie()
			.getFontSizeAndLines(schulteilnahme.nameSchule());

		switch (urkundenart) {

		case KAENGURUSPRUNG:

			return new CreateDatenKaengurusprungSchuleStrategie(fontSizeAndLinesSchulname, klasse);

		case TEILNAHME:
			return new CreateDatenTeilnahmeSchuleStrategie(fontSizeAndLinesSchulname, klasse);

		default:
			throw new IllegalArgumentException("Schulteilnahme: unzulaessige Urkundenart " + urkundenart);
		}
	}

	private Wettbewerb getWettbewerb() {

		if (aktuellerWettbewerb == null) {

			this.aktuellerWettbewerb = this.wettbewerbService.aktuellerWettbewerb().get();
		}

		return aktuellerWettbewerb;
	}
}
