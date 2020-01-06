// =====================================================
// Projekt: mk-commons
// (c) Heike Winkelvoß
// =====================================================

package de.egladil.web.mk_commons.domain;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import de.egladil.web.mk_commons.domain.enums.MKVRolle;
import de.egladil.web.mk_commons.domain.impl.Downloaddaten;
import de.egladil.web.mk_commons.domain.impl.Person;
import de.egladil.web.mk_commons.domain.impl.TeilnahmeIdentifier;

/**
 * IKontakt
 */
public interface IMKVKonto {

	Long getId();

	String getUuid();

	Person getPerson();

	void setUuid(String uuid);

	void setPerson(Person kontaktdaten);

	MKVRolle getRole();

	/**
	 * Kuerzel der Kindobjekte: schulzuordnungen bei Lehrerkonten, privatteilnahmen bei Privatkonten.
	 *
	 * @return
	 */
	List<String> getChildKuerzel();

	/**
	 * Gibt die Teilnahme zum gegebenen Jahr zurück.
	 *
	 * @param  jahr
	 *              String darf nicht null sein
	 * @return      ITeilnahme oder null
	 */
	ITeilnahmeIdentifierProvider getTeilnahmeZuJahr(String jahr) throws IllegalArgumentException;

	/**
	 * Gibt die Daten aller Downloads dieses MKV-Kontos zurück.
	 *
	 * @return
	 */
	List<Downloaddaten> alleDownloads();

	/**
	 * Sucht einen Download mit gegebenem jahr und gegebener Dateiart.
	 *
	 * @param  dateiname
	 * @param  jahr
	 * @return           IDownload oder null
	 */
	IDownload findDownload(String dateiname, String jahr);

	void addDownload(IDownload download);

	/**
	 * Erzeugt ein neues IDownload-Objekt.
	 *
	 * @return
	 */
	IDownload createBlankDownload();

	/**
	 * Gibt den TeilnahmeIdentifier zu diesem Jahr zurück.
	 *
	 * @param  jahr
	 *              String jahr
	 * @return      Optional
	 */
	Optional<TeilnahmeIdentifier> getTeilnahmeIdentifier(String jahr);

	Date getLastLogin();
}
