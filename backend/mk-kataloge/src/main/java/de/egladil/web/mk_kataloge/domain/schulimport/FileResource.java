// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.domain.schulimport;

import java.io.InputStream;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.annotations.providers.multipart.PartType;

/**
 * FileResource
 */
public class FileResource {

	@FormParam("uploadedFile")
	@PartType(MediaType.APPLICATION_OCTET_STREAM)
	private InputStream file;

	@FormParam("fileName")
	@PartType(MediaType.TEXT_PLAIN)
	private String fileName;

	public InputStream file() {

		return file;
	}

	public String fileName() {

		return fileName;
	}
}
