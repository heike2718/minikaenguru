// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import de.egladil.web.mk_commons.domain.IMkEntity;

/**
 * Ereignis
 */
@Entity
@Table(name = "ereignisse")
public class Ereignis implements IMkEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Version
	@Column(name = "VERSION")
	private int version;

	@NotBlank
	@Size(min = 1, max = 40)
	@Column(name = "WER", length = 40)
	private String wer;

	@Size(min = 1, max = 200)
	@Column(name = "WAS", length = 200)
	private String was;

	@Size(min = 1, max = 10)
	@Column(name = "EREIGNISART", length = 10)
	private String ereignisart;

	/**
	 * Erzeugt eine Instanz von Ereignis
	 */
	protected Ereignis() {

		super();
	}

	/**
	 * Ereignis
	 */
	public Ereignis(final String ereignisart, final String wer, final String was) {

		this.wer = wer;
		this.was = was;
		this.ereignisart = ereignisart;
	}

	public Long getId() {

		return id;
	}

	@Override
	public String toString() {

		return "Ereignis [ereignisart=" + ereignisart + ", wer=" + wer + ", was=" + was + "]";
	}

	public String getWer() {

		return wer;
	}

	public String getWas() {

		return was;
	}

	public String getEreignisart() {

		return ereignisart;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((ereignisart == null) ? 0 : ereignisart.hashCode());
		result = prime * result + ((was == null) ? 0 : was.hashCode());
		result = prime * result + ((wer == null) ? 0 : wer.hashCode());
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
		final Ereignis other = (Ereignis) obj;

		if (ereignisart == null) {

			if (other.ereignisart != null)
				return false;
		} else if (!ereignisart.equals(other.ereignisart))
			return false;

		if (was == null) {

			if (other.was != null)
				return false;
		} else if (!was.equals(other.was))
			return false;

		if (wer == null) {

			if (other.wer != null)
				return false;
		} else if (!wer.equals(other.wer))
			return false;
		return true;
	}
}
