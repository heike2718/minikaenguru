// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain;

import de.egladil.web.mk_gateway.domain.semantik.ValueObject;

/**
 * DownloadData
 */
@ValueObject
public class DownloadData {

	private final String filename;

	private final byte[] data;

	public DownloadData(final String filename, final byte[] data) {

		this.filename = filename;
		this.data = data;
	}

	public String filename() {

		return filename;
	}

	public byte[] data() {

		return data;
	}

}
