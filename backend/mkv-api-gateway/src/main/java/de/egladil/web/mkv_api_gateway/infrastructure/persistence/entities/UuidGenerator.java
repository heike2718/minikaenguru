// =====================================================
// Project: mkv-api-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_api_gateway.infrastructure.persistence.entities;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * UuidGenerator
 */
public class UuidGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(final SharedSessionContractImplementor session, final Object object) throws HibernateException {

		if (object instanceof User) {

			ConcurrencySafeEntity entity = (ConcurrencySafeEntity) object;

			if (entity.getImportierteUuid() != null) {

				return entity.getImportierteUuid();
			}
		}
		return UUID.randomUUID().toString().substring(0, 8);
	}

}
