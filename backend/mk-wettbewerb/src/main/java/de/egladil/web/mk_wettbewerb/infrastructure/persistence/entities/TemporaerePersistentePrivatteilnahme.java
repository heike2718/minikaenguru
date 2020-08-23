// =====================================================
// Project: mk-wettbewerb
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_wettbewerb.infrastructure.persistence.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Version;

/**
 * TemporaerePersistentePrivatteilnahme
 */
@Entity
@Table(name = "TEMP_PRIVATTEILNAHMEN")
public class TemporaerePersistentePrivatteilnahme {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "VERANSTALTER_UUID")
	private String veranstalterUuid;

	@Column(name = "TEILNAHMENUMMER_ALT")
	private String teilnahmenummerAlt;

	@Column(name = "TEILNAHMENUMMER_NEU")
	private String teilnahmenummerNeu;

	@Column(name = "WETTBEWERB_UUID")
	private String wettbewerbUuid;

	@Version
	@Column(name = "VERSION")
	private int version;

	public static TemporaerePersistentePrivatteilnahme createForTest(final Long id, final String veranstalterUuid, final String teilnahmenummerAlt, final String wettbewerbUuid) {

		TemporaerePersistentePrivatteilnahme result = new TemporaerePersistentePrivatteilnahme();
		result.id = id;
		result.veranstalterUuid = veranstalterUuid;
		result.teilnahmenummerAlt = teilnahmenummerAlt;
		result.wettbewerbUuid = wettbewerbUuid;
		return result;

	}

	public Long getId() {

		return id;
	}

	public String getVeranstalterUuid() {

		return veranstalterUuid;
	}

	public void setVeranstalterUuid(final String veranstalterUuid) {

		this.veranstalterUuid = veranstalterUuid;
	}

	public String getTeilnahmenummerAlt() {

		return teilnahmenummerAlt;
	}

	public void setTeilnahmenummerAlt(final String teilnahmenummerAlt) {

		this.teilnahmenummerAlt = teilnahmenummerAlt;
	}

	public String getTeilnahmenummerNeu() {

		return teilnahmenummerNeu;
	}

	public void setTeilnahmenummerNeu(final String teilnahmenummerNeu) {

		this.teilnahmenummerNeu = teilnahmenummerNeu;
	}

	public String getWettbewerbUuid() {

		return wettbewerbUuid;
	}

	public void setWettbewerbUuid(final String wettbewerbUuid) {

		this.wettbewerbUuid = wettbewerbUuid;
	}

	@Override
	public String toString() {

		return "TemporaerePersistentePrivatteilnahme [id=" + id + ", veranstalterUuid=" + veranstalterUuid + ", teilnahmenummerAlt="
			+ teilnahmenummerAlt + ", wettbewerbUuid=" + wettbewerbUuid + "]";
	}

	@Override
	public int hashCode() {

		return Objects.hash(id);
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
		TemporaerePersistentePrivatteilnahme other = (TemporaerePersistentePrivatteilnahme) obj;
		return Objects.equals(id, other.id);
	}

}
