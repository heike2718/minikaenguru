// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.unterlagen;

import com.fasterxml.jackson.annotation.JsonProperty;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.wettbewerb.WettbewerbID;

/**
 * Download
 */
public class Download {

	@JsonProperty
	private Identifier identifier;

	@JsonProperty
	private Identifier veranstalterID;

	@JsonProperty
	private WettbewerbID wettbewerbID;

	@JsonProperty
	private DownloadType downloadTyp;

	@JsonProperty
	private int anzahl;

	@JsonProperty
	private long sortnumber;

	public static Download createInstance() {

		Download result = new Download();
		return result;
	}

	public static Download createInstance(final Identifier identifier) {

		Download result = new Download();
		result.identifier = identifier;
		return result;
	}

	/**
	 *
	 */
	Download() {

	}

	public String printUniqueKey() {

		return "Download=[veranstalterID=" + veranstalterID.identifier() + ", jahr=" + wettbewerbID.jahr() + ", downloadType="
			+ downloadTyp + "]";

	}

	public Identifier getIdentifier() {

		return identifier;
	}

	public DownloadType getDownloadTyp() {

		return downloadTyp;
	}

	public Download withDownloadTyp(final DownloadType downloadTyp) {

		this.downloadTyp = downloadTyp;
		return this;
	}

	public int getAnzahl() {

		return anzahl;
	}

	public Download withAnzahl(final int anzahl) {

		this.anzahl = anzahl;
		return this;
	}

	public Identifier getVeranstalterID() {

		return veranstalterID;
	}

	public Download withVeranstalterID(final Identifier veranstalterID) {

		this.veranstalterID = veranstalterID;
		return this;
	}

	public WettbewerbID getWettbewerbID() {

		return wettbewerbID;

	}

	public Download withWettbewerbID(final WettbewerbID wettbewerbID) {

		this.wettbewerbID = wettbewerbID;
		return this;
	}

	public long getSortnumber() {

		return sortnumber;
	}

	public Download withSortnumber(final long sortnumber) {

		this.sortnumber = sortnumber;
		return this;
	}

}
