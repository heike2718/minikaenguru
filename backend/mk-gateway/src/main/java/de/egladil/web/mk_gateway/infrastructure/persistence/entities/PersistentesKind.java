// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;
import de.egladil.web.mk_gateway.domain.teilnahmen.Sprache;
import de.egladil.web.mk_gateway.domain.teilnahmen.Teilnahmeart;

/**
 * PersistentesKind
 */
@Entity
@Table(name = "KINDER")
@NamedQueries({
	@NamedQuery(
		name = "PersistentesKind.FIND_WITH_LOESUNGSZETTEL",
		query = "select k from PersistentesKind k where k.loesungszettelUUID = :loesungszettelUUID"),
	@NamedQuery(
		name = "PersistentesKind.FIND_BY_TEILNAHME",
		query = "select k from PersistentesKind k where k.teilnahmenummer = :teilnahmenummer and k.teilnahmeart = :teilnahmeart order by k.uuid"),
	@NamedQuery(
		name = "PersistentesKind.FIND_BY_TEILNAHME_WITH_NON_NULL_LOESUNGSZETTEL",
		query = "select k from PersistentesKind k where k.teilnahmenummer = :teilnahmenummer and k.teilnahmeart = :teilnahmeart and k.loesungszettelUUID IS NOT NULL order by k.uuid")
})
public class PersistentesKind extends ConcurrencySafeEntity {

	private static final long serialVersionUID = -2115088245418466350L;

	public static final String FIND_WITH_LOESUNGSZETTEL = "PersistentesKind.FIND_WITH_LOESUNGSZETTEL";

	public static final String FIND_BY_TEILNAHME = "PersistentesKind.FIND_BY_TEILNAHME";

	public static final String FIND_BY_TEILNAHME_WITH_NON_NULL_LOESUNGSZETTEL = "PersistentesKind.FIND_BY_TEILNAHME_WITH_NON_NULL_LOESUNGSZETTEL";

	@Column(name = "KLASSENSTUFE")
	@Enumerated(EnumType.STRING)
	private Klassenstufe klassenstufe;

	@Column(name = "TEILNAHMEART")
	@Enumerated(EnumType.STRING)
	private Teilnahmeart teilnahmeart;

	@Column
	private String teilnahmenummer;

	@Column(name = "SPRACHE")
	@Enumerated(EnumType.STRING)
	private Sprache sprache;

	@Column(name = "VORNAME")
	private String vorname;

	@Column(name = "NACHNAME")
	private String nachname;

	@Column(name = "ZUSATZ")
	private String zusatz;

	@Column(name = "KLASSE_UUID")
	private String klasseUUID;

	@Column(name = "LOESUNGSZETTEL_UUID")
	private String loesungszettelUUID;

	@Column(name = "LANDKUERZEL")
	private String landkuerzel;

	public Klassenstufe getKlassenstufe() {

		return klassenstufe;
	}

	public void setKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
	}

	public Teilnahmeart getTeilnahmeart() {

		return teilnahmeart;
	}

	public void setTeilnahmeart(final Teilnahmeart teilnahmeart) {

		this.teilnahmeart = teilnahmeart;
	}

	public String getTeilnahmenummer() {

		return teilnahmenummer;
	}

	public void setTeilnahmenummer(final String teilnahmenummer) {

		this.teilnahmenummer = teilnahmenummer;
	}

	public Sprache getSprache() {

		return sprache;
	}

	public void setSprache(final Sprache sprache) {

		this.sprache = sprache;
	}

	public String getVorname() {

		return vorname;
	}

	public void setVorname(final String vorname) {

		this.vorname = vorname;
	}

	public String getNachname() {

		return nachname;
	}

	public void setNachname(final String nachname) {

		this.nachname = nachname;
	}

	public String getZusatz() {

		return zusatz;
	}

	public void setZusatz(final String zusatz) {

		this.zusatz = zusatz;
	}

	public String getKlasseUUID() {

		return klasseUUID;
	}

	public void setKlasseUUID(final String klasseUUID) {

		this.klasseUUID = klasseUUID;
	}

	public String getLoesungszettelUUID() {

		return loesungszettelUUID;
	}

	public void setLoesungszettelUUID(final String loesungszettelUUID) {

		this.loesungszettelUUID = loesungszettelUUID;
	}

	public String getLandkuerzel() {

		return landkuerzel;
	}

	public void setLandkuerzel(final String landkuerzel) {

		this.landkuerzel = landkuerzel;
	}

}
