// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

/**
 * UuidGenerator
 */
public class UuidGenerator implements IdentifierGenerator {

	private static final long serialVersionUID = 1L;

	@Override
	public Serializable generate(final SharedSessionContractImplementor session, final Object object) throws HibernateException {

		if (object instanceof ConcurrencySafeEntity) {

			ConcurrencySafeEntity entity = (ConcurrencySafeEntity) object;

			if (entity.getImportierteUuid() != null) {

				return entity.getImportierteUuid();
			}
		}
		return UUID.randomUUID().toString();
	}

}
