// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import de.egladil.web.mk_gateway.domain.urkunden.Urkundenmotiv;

/**
 * AbstractDatenUrkunde
 */
public abstract class AbstractDatenUrkunde {

	private String fullName;

	private String nameKlasse;

	private String nameSchule;

	private String datum;

	private String wettbewerbsjahr;

	private Urkundenmotiv urkundenmotiv;

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

	public String nameSchule() {

		return nameSchule;
	}

	protected void setNameSchule(final String nameSchule) {

		this.nameSchule = nameSchule;
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
}
