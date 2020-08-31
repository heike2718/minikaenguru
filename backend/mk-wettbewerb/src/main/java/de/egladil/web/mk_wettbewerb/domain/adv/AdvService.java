// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.domain.adv;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * AdvService
 */
@ApplicationScoped
public class AdvService {

	private static final String PATH_SUBDIR_ADV_TEXTE = "/adv";

	@ConfigProperty(name = "path.external.files")
	String pathAdvTexteDir;

	@Inject
	VertragAuftragsverarbeitungRepository vertragRepository;

}
