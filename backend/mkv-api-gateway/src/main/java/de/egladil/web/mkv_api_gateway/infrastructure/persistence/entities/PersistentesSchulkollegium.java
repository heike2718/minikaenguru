// =====================================================
// Project: mkv-api-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.infrastructure.persistence.entities;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * PersistentesSchulkollegium
 */
@Entity
@Table(name = "SCHULKOLLEGIUM")
public class PersistentesSchulkollegium extends ConcurrencySafeEntity {

}
