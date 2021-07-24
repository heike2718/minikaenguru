// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.uploads;

import de.egladil.web.mk_gateway.domain.Identifier;

/**
 * UploadRequestPayload enthält alle Informationen, die für einen Upload erforderlich sind.
 */
public class UploadRequestPayload {

	private Identifier veranstalterID;

	private Identifier schuleID;

	private UploadType uploadType;

	private UploadData uploadData;

	private Object context;

	public Identifier getVeranstalterID() {

		return veranstalterID;
	}

	public UploadRequestPayload withVeranstalterID(final Identifier veranstalterID) {

		this.veranstalterID = veranstalterID;
		return this;
	}

	public Identifier getSchuleID() {

		return schuleID;
	}

	public UploadRequestPayload withSchuleID(final Identifier schuleID) {

		this.schuleID = schuleID;
		return this;
	}

	public UploadType getUploadType() {

		return uploadType;
	}

	public UploadRequestPayload withUploadType(final UploadType uploadType) {

		this.uploadType = uploadType;
		return this;
	}

	public UploadData getUploadData() {

		return uploadData;
	}

	public UploadRequestPayload withUploadData(final UploadData uploadData) {

		this.uploadData = uploadData;
		return this;
	}

	public Object getContext() {

		return context;
	}

	public UploadRequestPayload withContext(final Object context) {

		this.context = context;
		return this;
	}

}
