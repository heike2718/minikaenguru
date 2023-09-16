// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.NotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.egladil.web.commons_validation.payload.MessagePayload;
import de.egladil.web.commons_validation.payload.ResponsePayload;
import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.event.DomainEventHandler;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertext;
import de.egladil.web.mk_gateway.domain.mustertexte.MustertexteRepository;
import de.egladil.web.mk_gateway.domain.mustertexte.MustertexteService;
import de.egladil.web.mk_gateway.domain.mustertexte.Mustertextkategorie;
import de.egladil.web.mk_gateway.domain.mustertexte.api.MustertextAPIModel;
import de.egladil.web.mk_gateway.domain.mustertexte.events.MustertextDeletedEvent;
import de.egladil.web.mk_gateway.domain.mustertexte.events.MustertextSavedEvent;

/**
 * MustertextServiceImpl
 */
@ApplicationScoped
public class MustertextServiceImpl implements MustertexteService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MustertextServiceImpl.class);

	private final ResourceBundle applicationMessages = ResourceBundle.getBundle("ApplicationMessages", Locale.GERMAN);

	@Inject
	MustertexteRepository mustertexteRepository;

	@Inject
	DomainEventHandler domainEventHandler;

	@Override
	public ResponsePayload loadMustertexte() {

		List<Mustertext> mustertexte = new ArrayList<>();

		for (Mustertextkategorie k : Mustertextkategorie.values()) {

			mustertexte.addAll(this.mustertexteRepository.loadMustertexteByKategorie(k));
		}

		List<MustertextAPIModel> data = mustertexte.stream().map(mt -> mapFromDomainObject(mt)).collect(Collectors.toList());

		return new ResponsePayload(MessagePayload.ok(), data);
	}

	@Override
	public ResponsePayload loadDetails(final Identifier identifier) {

		Optional<Mustertext> opt = mustertexteRepository.findMustertextByIdentifier(identifier);

		if (opt.isEmpty()) {

			LOGGER.warn("Abfrage Mustertext mit ID={}: not found", identifier.identifier());
			throw new NotFoundException();
		}

		MustertextAPIModel data = mapFromDomainObject(opt.get());
		return new ResponsePayload(MessagePayload.ok(), data);
	}

	@Override
	public ResponsePayload mustertextSpeichern(final MustertextAPIModel apiModel, final String uuidAdmin) {

		Optional<Mustertext> optMustertext = this.findByName(apiModel);

		if (optMustertext.isPresent()) {

			String msg = MessageFormat.format(applicationMessages.getString("mustertexte.save.dublette"),
				new Object[] { apiModel.getKategorie(), apiModel.getName() });

			return ResponsePayload.messageOnly(MessagePayload.warn(msg));
		}

		Mustertext mustertext = mapFromAPIModel(apiModel);

		Mustertext persisted = mustertexteRepository.addOrUpdate(mustertext);

		MustertextAPIModel data = mapFromDomainObject(persisted);

		MustertextSavedEvent event = (MustertextSavedEvent) new MustertextSavedEvent(uuidAdmin).withKategorie(data.getKategorie())
			.withName(data.getName());
		domainEventHandler.handleEvent(event);

		return new ResponsePayload(MessagePayload.info(applicationMessages.getString("mustertexte.save.success")), data);
	}

	Optional<Mustertext> findByName(final MustertextAPIModel apiModel) {

		List<Mustertext> vorhandeneMustertexte = mustertexteRepository.loadMustertexteByKategorie(apiModel.getKategorie());

		Optional<Mustertext> optMustertext = vorhandeneMustertexte.stream()
			.filter(
				m -> !apiModel.getUuid().equals(m.getIdentifier().identifier()) && apiModel.getName().equalsIgnoreCase(m.getName()))
			.findAny();

		return optMustertext;

	}

	@Override
	public ResponsePayload mustertextLoeschen(final Identifier identifier, final String uuidAdmin) {

		boolean deleted = mustertexteRepository.deleteMustertext(identifier);

		if (deleted) {

			MustertextDeletedEvent event = new MustertextDeletedEvent(uuidAdmin);
			domainEventHandler.handleEvent(event);

			String msg = MessageFormat.format(applicationMessages.getString("mustertexte.delete.success"),
				new Object[] { identifier });
			return ResponsePayload.messageOnly(MessagePayload.info(msg));

		}

		String msg = MessageFormat.format(applicationMessages.getString("mustertexte.delete.bereitsGeloescht"),
			new Object[] { identifier });
		return ResponsePayload.messageOnly(MessagePayload.warn(msg));
	}

	/**
	 * @param  apiModel
	 * @return
	 */
	Mustertext mapFromAPIModel(final MustertextAPIModel apiModel) {

		if (MustertextAPIModel.KEINE_UUID.equals(apiModel.getUuid())) {

			return new Mustertext().withKategorie(apiModel.getKategorie()).withName(apiModel.getName())
				.withText(apiModel.getText());
		}

		return new Mustertext(new Identifier(apiModel.getUuid())).withKategorie(apiModel.getKategorie())
			.withName(apiModel.getName())
			.withText(apiModel.getText());
	}

	MustertextAPIModel mapFromDomainObject(final Mustertext mustertext) {

		MustertextAPIModel result = new MustertextAPIModel();
		result.setUuid(mustertext.getIdentifier().identifier());
		result.setKategorie(mustertext.getKategorie());
		result.setName(mustertext.getName());
		result.setText(mustertext.getText());
		return result;
	}

}
