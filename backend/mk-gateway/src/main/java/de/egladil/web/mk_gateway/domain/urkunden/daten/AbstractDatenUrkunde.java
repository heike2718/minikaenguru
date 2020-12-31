// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import java.util.Objects;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;
import de.egladil.web.mk_gateway.domain.urkunden.generator.urkunden.FontSizeAndLines;

/**
 * AbstractDatenUrkunde
 */
public abstract class AbstractDatenUrkunde {

	private String uuid;

	private Klassenstufe klassenstufe;

	private String klasseUUID;

	private Sprache sprache;

	private String fullName;

	private String nameKlasse;

	private String datum;

	private String wettbewerbsjahr;

	private Urkundenmotiv urkundenmotiv;

	private FontSizeAndLines fontSizeAndLinesSchulname;

	@Override
	public int hashCode() {

		return Objects.hash(uuid);
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj) {

			return true;
		}

		if (obj == null) {

			return false;
		}

		if (getClass() != obj.getClass()) {

			return false;
		}
		AbstractDatenUrkunde other = (AbstractDatenUrkunde) obj;
		return Objects.equals(uuid, other.uuid);
	}

	public Klassenstufe klassenstufe() {

		return klassenstufe;
	}

	protected void setKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
	}

	public String klasseUUID() {

		return klasseUUID;
	}

	protected void setKlasseUUID(final String klasseUUID) {

		this.klasseUUID = klasseUUID;
	}

	public Sprache sprache() {

		return sprache;
	}

	protected void setSprache(final Sprache sprache) {

		this.sprache = sprache;
	}

	public String fullName() {

		return fullName;
	}

	protected void setFullName(final String fullName) {

		this.fullName = fullName;
	}

	public String nameKlasse() {

		return nameKlasse;
	}

	protected void setNameKlasse(final String nameKlasse) {

		this.nameKlasse = nameKlasse;
	}

	public String datum() {

		return datum;
	}

	protected void setDatum(final String datum) {

		this.datum = datum;
	}

	public abstract String punktvalue();

	public Urkundenmotiv urkundenmotiv() {

		return urkundenmotiv;
	}

	protected void setUrkundenmotiv(final Urkundenmotiv urkundenmotiv) {

		this.urkundenmotiv = urkundenmotiv;
	}

	public String wettbewerbsjahr() {

		return wettbewerbsjahr;
	}

	protected void setWettbewerbsjahr(final String wettbewerbsjahr) {

		this.wettbewerbsjahr = wettbewerbsjahr;
	}

	public FontSizeAndLines fontSizeAndLinesSchulname() {

		return fontSizeAndLinesSchulname;
	}

	protected void setFontSizeAndLinesSchulname(final FontSizeAndLines fontsizeAndLinesSchulname) {

		this.fontSizeAndLinesSchulname = fontsizeAndLinesSchulname;
	}

	public String uuid() {

		return uuid;
	}

	protected void setUuid(final String uuid) {

		this.uuid = uuid;
	}
}
