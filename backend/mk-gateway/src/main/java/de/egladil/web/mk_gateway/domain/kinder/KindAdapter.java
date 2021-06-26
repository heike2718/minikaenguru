// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.kinder;

import de.egladil.web.mk_gateway.domain.Identifier;
import de.egladil.web.mk_gateway.domain.kinder.api.KindRequestData;
import de.egladil.web.mk_gateway.domain.klassenlisten.impl.KindImportDaten;
import de.egladil.web.mk_gateway.domain.teilnahmen.Klassenstufe;

/**
 * KindAdapter
 */
public class KindAdapter {

	/**
	 * Adaptiert das Kind an die KindAdaptable-API.
	 *
	 * @param  kind
	 *              Kind
	 * @return      KindAdaptable
	 */
	public KindAdaptable adaptKind(final Kind kind) {

		return new KindAdaptable() {

			@Override
			public Identifier klasseID() {

				return kind.klasseID();
			}

			@Override
			public boolean isNeu() {

				return kind.identifier() != null;
			}

			@Override
			public Identifier identifier() {

				return kind.identifier();
			}

			@Override
			public String getLowerZusatzNullSafe() {

				return kind.zusatz() == null ? null : kind.zusatz().trim().toLowerCase();
			}

			@Override
			public String getLowerVornameNullSafe() {

				return kind.vorname() == null ? null : kind.vorname().trim().toLowerCase();
			}

			@Override
			public String getLowerNachnameNullSafe() {

				return kind.nachname() == null ? null : kind.nachname().trim().toLowerCase();
			}

			@Override
			public Klassenstufe getKlassenstufe() {

				return kind.klassenstufe();
			}

			@Override
			public String getType() {

				return Kind.class.getName();
			}
		};

	}

	/**
	 * Adaptiert die KindImportDaten an die KindAdaptable-API.
	 *
	 * @param  kind
	 *              Kind
	 * @return      KindAdaptable
	 */
	public KindAdaptable adaptKindImportDaten(final KindImportDaten kindImportDaten) {

		final KindAdaptable adaptedKind = this.adaptKindImportDaten(kindImportDaten.getKindRequestData());

		return new KindAdaptable() {

			@Override
			public Identifier klasseID() {

				return adaptedKind.klasseID();
			}

			@Override
			public boolean isNeu() {

				return adaptedKind.isNeu();
			}

			@Override
			public Identifier identifier() {

				return adaptedKind.identifier();
			}

			@Override
			public String getType() {

				return KindImportDaten.class.getName();
			}

			@Override
			public String getLowerZusatzNullSafe() {

				return adaptedKind.getLowerZusatzNullSafe();
			}

			@Override
			public String getLowerVornameNullSafe() {

				return adaptedKind.getLowerVornameNullSafe();
			}

			@Override
			public String getLowerNachnameNullSafe() {

				return adaptedKind.getLowerNachnameNullSafe();
			}

			@Override
			public Klassenstufe getKlassenstufe() {

				return adaptedKind.getKlassenstufe();
			}
		};
	}

	/**
	 * Adaptiert die KindRequestData an die KindAdaptable-API.
	 *
	 * @param  kind
	 *              Kind
	 * @return      KindAdaptable
	 */
	public KindAdaptable adaptKindImportDaten(final KindRequestData kind) {

		return new KindAdaptable() {

			@Override
			public Identifier klasseID() {

				return new Identifier(kind.klasseUuid());
			}

			@Override
			public boolean isNeu() {

				return KindRequestData.KEINE_UUID.equalsIgnoreCase(kind.uuid());
			}

			@Override
			public Identifier identifier() {

				return new Identifier(kind.uuid());
			}

			@Override
			public String getLowerZusatzNullSafe() {

				return kind.kind().zusatz() == null ? null : kind.kind().zusatz().trim().toLowerCase();
			}

			@Override
			public String getLowerVornameNullSafe() {

				return kind.kind().vorname() == null ? null : kind.kind().vorname().trim().toLowerCase();
			}

			@Override
			public String getLowerNachnameNullSafe() {

				return kind.kind().nachname() == null ? null : kind.kind().nachname().trim().toLowerCase();
			}

			@Override
			public Klassenstufe getKlassenstufe() {

				return kind.kind().klassenstufe().klassenstufe();
			}

			@Override
			public String getType() {

				return KindRequestData.class.getName();
			}
		};
	}

}
