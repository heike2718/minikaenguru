// =====================================================
// Project: mk-kataloge
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_kataloge.persistence.impl;

import java.io.Serializable;
import java.util.UUID;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import de.egladil.web.mk_kataloge.persistence.impl.entities.Schule;

/**
 * SchuleIdGenerator
 */
public class SchuleIdGenerator implements IdentifierGenerator {

	@Override
	public Serializable generate(final SharedSessionContractImplementor session, final Object object) throws HibernateException {

		if (object instanceof Schule) {

			Schule schule = (Schule) object;
			return schule.getImportiertesKuerzel();
		}

		return UUID.randomUUID().toString().substring(0, 8);
	}

}
