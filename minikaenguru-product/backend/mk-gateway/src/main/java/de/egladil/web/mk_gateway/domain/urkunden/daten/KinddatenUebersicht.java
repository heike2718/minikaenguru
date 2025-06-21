// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.urkunden.daten;

import java.util.Objects;

import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * KinddatenUebersicht
 */
public class KinddatenUebersicht {

	private String uuid;

	private String fullName;

	private String nameKlasse;

	private Klassenstufe klassenstufe;

	private int punkte;

	private int laengeKaengurusprung;

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
		KinddatenUebersicht other = (KinddatenUebersicht) obj;
		return Objects.equals(uuid, other.uuid);
	}

	public String uuid() {

		return uuid;
	}

	public KinddatenUebersicht withUuid(final String uuid) {

		this.uuid = uuid;
		return this;
	}

	public String fullName() {

		return fullName;
	}

	public KinddatenUebersicht withFullName(final String fullName) {

		this.fullName = fullName;
		return this;
	}

	public String nameKlasse() {

		return nameKlasse;
	}

	public KinddatenUebersicht withNameKlasse(final String nameKlasse) {

		this.nameKlasse = nameKlasse;
		return this;
	}

	public Klassenstufe klassenstufe() {

		return klassenstufe;
	}

	public KinddatenUebersicht withKlassenstufe(final Klassenstufe klassenstufe) {

		this.klassenstufe = klassenstufe;
		return this;
	}

	public int punkte() {

		return punkte;
	}

	public KinddatenUebersicht withPunkte(final int punkte) {

		this.punkte = punkte;
		return this;
	}

	public int laengeKaengurusprung() {

		return laengeKaengurusprung;
	}

	public KinddatenUebersicht withLaengeKaengurusprung(final int laengeKaengurusprung) {

		this.laengeKaengurusprung = laengeKaengurusprung;
		return this;
	}

}
