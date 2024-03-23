// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.error;

/**
 * EntityConcurrentlyModifiedException
 */
public class EntityConcurrentlyModifiedException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private final ConcurrentModificationType modificationType;

	private final Object actualEntity;

	public EntityConcurrentlyModifiedException(final ConcurrentModificationType modificationType, final Object actualEntity) {

		this.modificationType = modificationType;
		this.actualEntity = actualEntity;
	}

	public ConcurrentModificationType getModificationType() {

		return modificationType;
	}

	public Object getActualEntity() {

		return actualEntity;
	}

}
