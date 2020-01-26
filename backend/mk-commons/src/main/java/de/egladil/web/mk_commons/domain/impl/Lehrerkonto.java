// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain.impl;

import java.util.ArrayList;
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
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
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
 * Lehrerkonto
 */
@Entity
@Table(name = "lehrerkonten")
public class Lehrerkonto implements IMkEntity, IMKVKonto {

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

	@Version
	@Column(name = "VERSION")
	private int version;

	@NotNull
	@ManyToOne(optional = false)
	@JoinColumn(name = "SCHULE", referencedColumnName = "ID")
	private Schule schule;

	@ManyToMany(cascade = { CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE })
	@JoinTable(
		name = "schulteilnahmen_lehrer", joinColumns = { @JoinColumn(name = "LEHRER") }, inverseJoinColumns = {
			@JoinColumn(name = "TEILNAHME") })
	private List<Schulteilnahme> schulteilnahmen = new ArrayList<>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "lehrerkonto", cascade = { CascadeType.ALL })
	private List<Lehrerdownload> downloads = new ArrayList<>();

	/**
	 * Verwende LehrerkontoBuilder, um ein Lehrerkonto zu erzeugen!
	 */
	public Lehrerkonto() {

	}

	public Lehrerkonto(final String uuid, final Person person, final Schule schule, final boolean benachrichtigen) throws MkRuntimeException {

		if (StringUtils.isBlank(uuid)) {

			throw new MkRuntimeException("brauchen eine benutzerUuid fuer das Lehrerkonto");
		}

		if (person == null) {

			throw new MkRuntimeException("brauchen eine person fuer das Lehrerkonto");
		}

		if (schule == null) {

			throw new MkRuntimeException("brauchen eine Schule fuer das Lehrerkonto");
		}
		this.uuid = uuid;
		this.person = person;
		this.schule = schule;
		this.automatischBenachrichtigen = benachrichtigen;
	}

	public void addSchulteilnahme(final Schulteilnahme teilnahme) {

		if (!schulteilnahmen.contains(teilnahme)) {

			schulteilnahmen.add(teilnahme);
			teilnahme.onAddToLehrerkonto(this);
		}
	}

	public void removeSchulteilnahme(final Schulteilnahme teilnahme) {

		schulteilnahmen.remove(teilnahme);
		teilnahme.onRemoveFromLehrerkonto(this);
	}

	public void addLehrerdownload(final Lehrerdownload download) {

		if (downloads == null) {

			downloads = new ArrayList<>();
		}

		if (!downloads.contains(download)) {

			downloads.add(download);
		}
		download.setLehrerkonto(this);
	}

	@Override
	public void addDownload(final IDownload download) {

		this.addLehrerdownload((Lehrerdownload) download);
	}

	@Override
	public List<String> getChildKuerzel() {

		final List<String> result = new ArrayList<>();

		// for (final Lehrerteilnahme z : teilnahmen) {
		// result.add(z.getSchulteilnahme().getKuerzel());
		// }
		for (final Schulteilnahme teilnahme : schulteilnahmen) {

			result.add(teilnahme.getKuerzel());
		}
		return result;
	}

	@Override
	public IDownload findDownload(final String dateiname, final String jahr) {

		for (final Lehrerdownload d : downloads) {

			final Downloaddaten downloaddaten = d.getDownloaddaten();

			if (downloaddaten.getDateiname().equals(dateiname) && downloaddaten.getJahr().equals(jahr)) {

				return d;
			}
		}
		return null;
	}

	@Override
	public IDownload createBlankDownload() {

		return new Lehrerdownload();
	}

	@Override
	public Rolle getRole() {

		return Rolle.LEHRER;
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
		final Lehrerkonto other = (Lehrerkonto) obj;

		if (uuid == null) {

			if (other.uuid != null)
				return false;
		} else if (!uuid.equals(other.uuid))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "Lehrerkonto [uuid=" + uuid + ", person=" + person + ", schule=" + schule + "]";
	}

	@Override
	public Long getId() {

		return id;
	}

	@Override
	public String getUuid() {

		return uuid;
	}

	@Override
	public Person getPerson() {

		return person;
	}

	@Override
	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}

	public void setId(final Long id) {

		this.id = id;
	}

	@Override
	public void setPerson(final Person person) {

		this.person = person;
	}

	@Override
	public ITeilnahmeIdentifierProvider getTeilnahmeZuJahr(final String jahr) throws IllegalArgumentException {

		if (jahr == null) {

			throw new IllegalArgumentException("jahr null");
		}

		for (final Schulteilnahme teilnahme : schulteilnahmen) {

			if (jahr.equals(teilnahme.getJahr())) {

				return teilnahme;
			}
		}
		return null;
	}

	/**
	 * @see de.egladil.mkv.persistence.domain.IMKVKonto#alleDownloads()
	 */
	@Override
	public List<Downloaddaten> alleDownloads() {

		final List<Downloaddaten> result = new ArrayList<>();

		if (this.downloads != null) {

			for (final Lehrerdownload d : this.downloads) {

				result.add(d.getDownloaddaten());
			}
		}
		return result;
	}

	public Schule getSchule() {

		return schule;
	}

	public void setSchule(final Schule schule) {

		this.schule = schule;
	}

	public boolean isAutomatischBenachrichtigen() {

		return automatischBenachrichtigen;
	}

	public void setAutomatischBenachrichtigen(final boolean automatischBenachrichtigen) {

		this.automatischBenachrichtigen = automatischBenachrichtigen;
	}

	@Override
	public Optional<TeilnahmeIdentifier> getTeilnahmeIdentifier(final String jahr) {

		final ITeilnahmeIdentifierProvider iTeilnahmeIdentifierProvider = this.getTeilnahmeZuJahr(jahr);

		if (iTeilnahmeIdentifierProvider != null) {

			return Optional.of(TeilnahmeIdentifier.createFromTeilnahmeIdentifierProvider(iTeilnahmeIdentifierProvider));
		}
		return Optional.empty();
	}

	public List<Schulteilnahme> getSchulteilnahmen() {

		return schulteilnahmen;
	}

	@Override
	public Date getLastLogin() {

		return person.getLastLogin();
	}
}
