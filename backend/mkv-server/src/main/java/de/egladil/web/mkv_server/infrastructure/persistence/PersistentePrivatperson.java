// =====================================================
// Project: mkv-server
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server.infrastructure.persistence;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * PersistentePrivatperson
 */
@Entity
@Table(name = "PRIVATPERSON")
public class PersistentePrivatperson extends ConcurrencySafeEntity {

	@Column(name = "TEILNAHMEKUERZEL")
	private String teilnahmekuerzel;

}
