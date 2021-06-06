// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import java.util.Base64;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;

/**
 * UploadData ein Upload
 */
@ValueObject
public class UploadData {

	private final String filename;

	private final byte[] data;

	public UploadData(final String filename, final byte[] data) {

		super();
		this.filename = filename;
		this.data = data;
	}

	public String getFilename() {

		return filename;
	}

	byte[] getData() {

		return data;
	}

	public byte[] getDataBASE64() {

		return Base64.getEncoder().encode(data);

	}

}
