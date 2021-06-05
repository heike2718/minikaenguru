// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.upload;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * UploadTransformService
 */
@ApplicationScoped
public class UploadTransformService {

	@ConfigProperty(name = "upload.folder.path")
	String pathUploadDir;

}
