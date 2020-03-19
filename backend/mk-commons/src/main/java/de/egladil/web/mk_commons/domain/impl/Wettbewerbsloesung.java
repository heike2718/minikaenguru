// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.enums.Klassenstufe;
import de.egladil.web.mk_commons.validation.annotations.Loesungscode;

/**
 * Wettbewerbsloesung
 */
@Entity
@Table(name = "loesungsbuchstaben")
public class Wettbewerbsloesung implements IMkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@NotNull
	@Column(name = "JAHR", length = 4)
	private String jahr;

	@NotNull
	@Column(name = "KLASSE")
	@Enumerated(EnumType.STRING)
	private Klassenstufe klassenstufe;

	/** Aneinanderreihung von 12 bzw. 15 Lösungsbuchstaben (Beispiel 'EDDADECCCCABABD') */
	@Size(max = 15)
	@Column(name = "LOESUNGSCODE")
	@Loesungscode
	private String loesungscode;

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	public String getJahr() {

		return jahr;
	}

	public void setJahr(final String jahr) {

		this.jahr = jahr;
	}

	public Klassenstufe getKlasse() {

		return klassenstufe;
	}

	public void setKlasse(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
	}

	public String getLoesungscode() {

		return loesungscode;
	}

	public void setLoesungscode(final String loesungscode) {

		this.loesungscode = loesungscode;
	}

	@Override
	public String toString() {

		return "Wettbewerbsloesung [jahr=" + jahr + ", klassenstufe=" + klassenstufe + ", loesungscode=" + loesungscode + "]";
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((jahr == null) ? 0 : jahr.hashCode());
		result = prime * result + ((klassenstufe == null) ? 0 : klassenstufe.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {

		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Wettbewerbsloesung other = (Wettbewerbsloesung) obj;

		if (jahr == null) {

			if (other.jahr != null)
				return false;
		} else if (!jahr.equals(other.jahr))
			return false;
		if (klassenstufe != other.klassenstufe)
			return false;
		return true;
	}

}
