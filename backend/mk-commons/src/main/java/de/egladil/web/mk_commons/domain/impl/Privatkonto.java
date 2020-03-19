// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;

import de.egladil.web.commons_validation.annotations.UuidString;
import de.egladil.web.mk_commons.domain.IDownload;
import de.egladil.web.mk_commons.domain.IMKVKonto;
import de.egladil.web.mk_commons.domain.IMkEntity;
import de.egladil.web.mk_commons.domain.ITeilnahmeIdentifierProvider;
import de.egladil.web.mk_commons.domain.enums.Rolle;
import de.egladil.web.mk_commons.exception.MkRuntimeException;

/**
 * Privatkontakt
 */
@Entity
@Table(name = "privatkonten")
public class Privatkonto implements IMkEntity, IMKVKonto {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@UuidString
	@NotNull
	@Size(min = 1, max = 40)
	@Column(name = "UUID")
	private String uuid;

	@Column(name = "MAILNACHRICHT")
	private boolean automatischBenachrichtigen;

	@NotNull
	@Embedded
	private Person person;

	@OneToMany(fetch = FetchType.LAZY, cascade = { CascadeType.ALL })
	@JoinColumn(name = "KONTAKT", referencedColumnName = "ID", nullable = false)
	private List<Privatteilnahme> teilnahmen = new ArrayList<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "privatkonto", cascade = { CascadeType.ALL })
	private List<Privatdownload> downloads = new ArrayList<>();

	@Version
	@Column(name = "VERSION")
	private int version;

	/**
	 * Erzeugt eine Instanz von Privatkonto
	 */
	public Privatkonto() {

	}

	/**
	 * Erzeugt eine Instanz von Privatkonto
	 */
	public Privatkonto(final String uuid, final Person person, final boolean automatischBenachrichtigen) {

		if (StringUtils.isBlank(uuid)) {

			throw new MkRuntimeException("brauchen eine uuid fuer das Privatkonto");
		}

		if (person == null) {

			throw new MkRuntimeException("brauchen eine person fuer das Privatkonto");
		}
		this.uuid = uuid;
		this.person = person;
		this.automatischBenachrichtigen = automatischBenachrichtigen;
	}

	/**
	 * Erzeugt eine Instanz von Privatkonto
	 */
	public Privatkonto(final String uuid, final Person person, final Privatteilnahme teilnahme, final boolean automatischBenachrichtigen) {

		if (StringUtils.isBlank(uuid)) {

			throw new MkRuntimeException("brauchen eine uuid fuer das Privatkonto");
		}

		if (person == null) {

			throw new MkRuntimeException("brauchen eine person fuer das Privatkonto");
		}

		if (teilnahme == null) {

			throw new MkRuntimeException("brauchen eine teilnahme fuer das Privatkonto");
		}
		this.uuid = uuid;
		this.person = person;
		this.teilnahmen.add(teilnahme);
		this.automatischBenachrichtigen = automatischBenachrichtigen;
	}

	public void addTeilnahme(final Privatteilnahme teilnahme) {

		if (teilnahmen == null) {

			teilnahmen = new ArrayList<>();
		}

		if (!teilnahmen.contains(teilnahme)) {

			teilnahmen.add(teilnahme);
			// teilnahme.setKontakt(this);
		}
	}

	public void addPrivatdownload(final Privatdownload download) {

		if (!downloads.contains(download)) {

			downloads.add(download);
		}
		download.setPrivatkonto(this);
	}

	/**
	 * @see de.egladil.mkv.persistence.domain.IMKVKonto#addDownload(de.egladil.mkv.persistence.domain.teilnahmen.IDownload)
	 */
	@Override
	public void addDownload(final IDownload download) {

		this.addPrivatdownload((Privatdownload) download);
	}

	@Override
	public IDownload findDownload(final String dateiname, final String jahr) {

		for (final Privatdownload d : downloads) {

			final Downloaddaten downloaddaten = d.getDownloaddaten();

			if (downloaddaten.getDateiname().equals(dateiname) && downloaddaten.getJahr().equals(jahr)) {

				return d;
			}
		}
		return null;
	}

	/**
	 * @see de.egladil.mkv.persistence.domain.IMKVKonto#createBlankDownload()
	 */
	@Override
	public IDownload createBlankDownload() {

		return new Privatdownload();
	}

	/**
	 * @param teilnahme
	 */
	public void removeTeilnahme(final Privatteilnahme teilnahme) {

		if (teilnahmen != null && teilnahmen.remove(teilnahme)) {

			// teilnahme.setKontakt(null);
		}
	}

	/**
	 * @see de.egladil.mkv.persistence.domain.IMKVKonto#getChildKuerzel()
	 */
	@Override
	public List<String> getChildKuerzel() {

		final List<String> result = new ArrayList<>();

		for (final Privatteilnahme t : teilnahmen) {

			result.add(t.getKuerzel());
		}
		return result;
	}

	@Override
	public Rolle getRole() {

		return Rolle.PRIVAT;
	}

	@Override
	public String toString() {

		return "Privatkontakt [kontaktdaten=" + person == null ? "null" : person.toString() + "]";
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
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
		final Privatkonto other = (Privatkonto) obj;

		if (uuid == null) {

			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	public Long getId() {

		return id;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	@Override
	public Person getPerson() {

		return person;
	}

	@Override
	public void setPerson(final Person kontaktdaten) {

		this.person = kontaktdaten;
	}

	/**
	 * unmodifiable!!!
	 *
	 * @return die Membervariable teilnahmen
	 */
	public List<Privatteilnahme> getTeilnahmen() {

		return Collections.unmodifiableList(teilnahmen);
	}

	public void setTeilnahmen(final List<Privatteilnahme> teilnahmen) {

		this.teilnahmen = teilnahmen;
	}

	@Override
	public String getUuid() {

		return uuid;
	}

	@Override
	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}

	private Privatteilnahme getPrivatTeilnahmeZuJahr(final String jahr) {

		if (this.teilnahmen == null) {

			return null;
		}

		for (final Privatteilnahme t : this.teilnahmen) {

			if (t.getJahr().equals(jahr)) {

				return t;
			}
		}
		return null;
	}

	@Override
	public ITeilnahmeIdentifierProvider getTeilnahmeZuJahr(final String jahr) throws IllegalArgumentException {

		return getPrivatTeilnahmeZuJahr(jahr);
	}

	@Override
	public List<Downloaddaten> alleDownloads() {

		final List<Downloaddaten> result = new ArrayList<>();

		for (final Privatdownload d : this.downloads) {

			result.add(d.getDownloaddaten());
		}
		return result;
	}

	public boolean isAutomatischBenachrichtigen() {

		return automatischBenachrichtigen;
	}

	public void setAutomatischBenachrichtigen(final boolean automatischBenachrichtigen) {

		this.automatischBenachrichtigen = automatischBenachrichtigen;
	}

	@Override
	public Optional<TeilnahmeIdentifier> getTeilnahmeIdentifier(final String jahr) {

		final Privatteilnahme teilnahme = this.getPrivatTeilnahmeZuJahr(jahr);

		if (teilnahme != null) {

			return Optional.of(TeilnahmeIdentifier.createFromTeilnahmeIdentifierProvider(teilnahme));
		}
		return Optional.empty();
	}

	@Override
	public final Date getLastLogin() {

		return person.getLastLogin();
	}
}
