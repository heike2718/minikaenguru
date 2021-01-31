// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.unterlagen;

import java.text.MessageFormat;
import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.mk_gateway.domain.DownloadData;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.error.UnterlagenNichtVerfuegbarException;
import de.egladil.web.mk_gateway.domain.event.LoggableEventDelegate;
import de.egladil.web.mk_gateway.domain.event.SecurityIncidentRegistered;
import de.egladil.web.mk_gateway.domain.fileutils.MkGatewayFileUtils;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.domain.veranstalter.Veranstalter;
import de.egladil.web.mk_gateway.domain.veranstalter.VeranstalterRepository;
import de.egladil.web.mk_gateway.domain.veranstalter.ZugangUnterlagenService;
import de.egladil.web.mk_gateway.domain.wettbewerb.Wettbewerb;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbService;

/**
 * UnterlagenService
 */
@ApplicationScoped
public class UnterlagenService {

	private static final String PATH_SUBDIR_UNTERLAGEN = "/unterlagen/";

	private static final String MESSAGE_FORMAT_PRIVAT_DEUTSCH = "{0}-minikaenguru-deutsch-privat.zip";

	private static final String MESSAGE_FORMAT_PRIVAT_ENGLISCH = "{0}-minikangaru-english-private.zip";

	private static final String MESSAGE_FORMAT_SCHULEN_DEUTSCH = "{0}-minikaenguru-deutsch-schulen.zip";

	private static final String MESSAGE_FORMAT_SCHULEN_ENGLISCH = "{0}-minikangaroo-english-schools.zip";

	private static final Logger LOG = LoggerFactory.getLogger(UnterlagenService.class);

	@ConfigProperty(name = "path.external.files")
	String pathExternalFiles;

	@Inject
	Event<SecurityIncidentRegistered> securityEventRegistered;

	@Inject
	private WettbewerbService wettbewerbService;

	@Inject
	VeranstalterRepository veranstalterRepository;

	@Inject
	ZugangUnterlagenService zugangUnterlagenService;

	private SecurityIncidentRegistered securityIncidentEventPayload;

	public static UnterlagenService createForTest(final WettbewerbService wettbewerbService, final VeranstalterRepository veranstalterRepository, final ZugangUnterlagenService zugangUnterlagenService) {

		UnterlagenService result = new UnterlagenService();
		result.wettbewerbService = wettbewerbService;
		result.veranstalterRepository = veranstalterRepository;
		result.zugangUnterlagenService = zugangUnterlagenService;
		return result;
	}

	/**
	 * Stellt dem Lehrer die Wettbewerbsunterlagen für eine Schule zur Verfügung.
	 *
	 * @param  lehrerID
	 * @param  sprache
	 *                  Sprache
	 * @return          DownloadData
	 */
	public DownloadData getUnterlagenFuerSchule(final Identifier lehrerID, final Sprache sprache) {

		Wettbewerb aktuellerWettbewerb = checkPreconditionsAndGetWettbewerb(lehrerID, Rolle.LEHRER);

		String pattern = sprache == Sprache.de ? MESSAGE_FORMAT_SCHULEN_DEUTSCH : MESSAGE_FORMAT_SCHULEN_ENGLISCH;
		String dateiname = MessageFormat.format(pattern, new Object[] { aktuellerWettbewerb.id().toString() });
		String path = pathExternalFiles + PATH_SUBDIR_UNTERLAGEN + dateiname;

		final byte[] data = MkGatewayFileUtils.readBytesFromFile(path);

		return new DownloadData(dateiname, data);
	}

	/**
	 * Stellt dem Privatveranstalter die Wettbewerbsunterlagen für eine Schule zur Verfügung.
	 *
	 * @param  privatveranstalterID
	 * @param  sprache
	 *                              Sprache
	 * @return                      DownloadData
	 */
	public DownloadData getUnterlagenFuerPrivatanmeldung(final Identifier privatveranstalterID, final Sprache sprache) {

		Wettbewerb aktuellerWettbewerb = checkPreconditionsAndGetWettbewerb(privatveranstalterID, Rolle.PRIVAT);

		String pattern = sprache == Sprache.de ? MESSAGE_FORMAT_PRIVAT_DEUTSCH : MESSAGE_FORMAT_PRIVAT_ENGLISCH;
		String dateiname = MessageFormat.format(pattern, new Object[] { aktuellerWettbewerb.id().toString() });
		String path = pathExternalFiles + PATH_SUBDIR_UNTERLAGEN + dateiname;

		final byte[] data = MkGatewayFileUtils.readBytesFromFile(path);

		return new DownloadData(dateiname, data);
	}

	private Wettbewerb checkPreconditionsAndGetWettbewerb(final Identifier veranstalterID, final Rolle rolle) {

		Optional<Veranstalter> optVeranstalter = checkVeranstalterExists(veranstalterID);

		Veranstalter veranstalter = optVeranstalter.get();

		checkRolle(veranstalter, rolle);

		boolean hatZugang = zugangUnterlagenService.hatZugang(veranstalter.uuid());

		Wettbewerb aktuellerWettbewerb = getWettbewerb();

		checkZugangZuUnterlagen(veranstalter, hatZugang, aktuellerWettbewerb);

		return aktuellerWettbewerb;

	}

	/**
	 * @param lehrerID
	 * @param veranstalter
	 * @param hatZugang
	 * @param aktuellerWettbewerb
	 */
	private void checkZugangZuUnterlagen(final Veranstalter veranstalter, final boolean hatZugang, final Wettbewerb aktuellerWettbewerb) {

		if (!hatZugang) {

			String msg = "Veranstalter UUID=" + veranstalter.uuid() + ", Zugang Unterlagen=" + veranstalter.zugangUnterlagen()
				+ ", Wettbewerbsstatus=" + aktuellerWettbewerb.status()
				+ " versucht, Wettbewerbsunterlagen herunterzuladen.";
			LOG.warn(msg);

			this.securityIncidentEventPayload = new LoggableEventDelegate().fireSecurityEvent(msg, securityEventRegistered);
			throw new UnterlagenNichtVerfuegbarException();
		}
	}

	/**
	 * @param veranstalterID
	 * @param veranstalter
	 */
	private void checkRolle(final Veranstalter veranstalter, final Rolle rolle) {

		if (veranstalter.rolle() != rolle) {

			String msg = "Veranstalter mit UUID=" + veranstalter.uuid() + " und Rolle " + veranstalter.rolle()
				+ " versucht, Wettbewerbsunterlagen über die falsche URL herunterzuladen.";
			LOG.warn(msg);

			this.securityIncidentEventPayload = new LoggableEventDelegate().fireSecurityEvent(msg, securityEventRegistered);
			throw new NotFoundException();
		}
	}

	/**
	 * @param  lehrerID
	 * @return
	 */
	private Optional<Veranstalter> checkVeranstalterExists(final Identifier lehrerID) {

		Optional<Veranstalter> optVeranstalter = veranstalterRepository.ofId(lehrerID);

		if (optVeranstalter.isEmpty()) {

			String msg = "Unbekannter Veranstalter mit UUID=" + lehrerID + " versucht, Wettbewerbsunterlagen herunterzuladen.";
			LOG.warn(msg);

			this.securityIncidentEventPayload = new LoggableEventDelegate().fireSecurityEvent(msg, securityEventRegistered);
			throw new NotFoundException();

		}
		return optVeranstalter;
	}

	SecurityIncidentRegistered securityIncidentEventPayload() {

		return securityIncidentEventPayload;
	}

	private Wettbewerb getWettbewerb() {

		return this.wettbewerbService.aktuellerWettbewerb().get();
	}

	void setPathExternalFiles(final String pathExternalFiles) {

		this.pathExternalFiles = pathExternalFiles;
	}

}
