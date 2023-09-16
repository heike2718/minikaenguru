// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.apimodel;

import java.io.InputStream;

import org.jboss.resteasy.reactive.PartType;

import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.core.MediaType;

/**
 * FileResource
 */
public class FileResource {

	@FormParam("uploadedFile")
	@PartType(MediaType.APPLICATION_OCTET_STREAM)
	public InputStream file;

	@FormParam("fileName")
	@PartType(MediaType.TEXT_PLAIN)
	public String fileName;
}
