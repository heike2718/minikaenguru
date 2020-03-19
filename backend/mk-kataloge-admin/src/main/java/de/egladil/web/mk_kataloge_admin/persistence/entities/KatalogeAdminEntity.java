// =====================================================
// Project: mk-kataloge-admin
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge_admin.persistence.entities;

/**
 * KatalogeAdminEntity
 */
public interface KatalogeAdminEntity {

	/**
	 * @return the technical Id.
	 */
	Long getId();

	/**
	 * @return the kuerzel
	 */
	String getKuerzel();

	/**
	 * @return the name
	 */
	String getName();
}
