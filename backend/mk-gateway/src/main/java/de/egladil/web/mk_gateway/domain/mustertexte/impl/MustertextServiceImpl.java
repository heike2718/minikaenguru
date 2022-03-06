// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.mustertexte.impl;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

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
	public ResponsePayload getMustertexteByKategorie(final Mustertextkategorie kategorie) {

		List<Mustertext> mustertexte = mustertexteRepository.loadMustertexteByKategorie(kategorie);

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

		Mustertext mustertext = mapFromAPIModel(apiModel);

		Mustertext persisted = mustertexteRepository.addOrUpdate(mustertext);

		MustertextAPIModel data = mapFromDomainObject(persisted);

		MustertextSavedEvent event = (MustertextSavedEvent) new MustertextSavedEvent(uuidAdmin).withKategorie(data.getKategorie())
			.withName(data.getName());
		domainEventHandler.handleEvent(event);

		return new ResponsePayload(MessagePayload.info(applicationMessages.getString("mustertexte.save.success")), data);
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
