// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.entities;

import java.io.Serializable;
import java.util.Objects;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;

import de.egladil.web.commons_validation.annotations.UuidString;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * ConcurrencySafeEntity
 */
@MappedSuperclass
public abstract class ConcurrencySafeEntity implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "uuid_generator")
	@GenericGenerator(
		name = "uuid_generator", type = UuidGenerator.class)
	@UuidString
	@NotNull
	@Size(min = 1, max = 40)
	@Column(name = "UUID")
	private String uuid;

	@Version
	@Column(name = "VERSION")
	@JsonIgnore
	private int version;

	@Transient
	private String importierteUuid;

	public void setImportierteUuid(final String importierteUuid) {

		this.importierteUuid = importierteUuid;
	}

	public String getImportierteUuid() {

		return importierteUuid;
	}

	public String getUuid() {

		return uuid;
	}

	public void setUuid(final String uuid) {

		this.uuid = uuid;
	}

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
		ConcurrencySafeEntity other = (ConcurrencySafeEntity) obj;
		return Objects.equals(uuid, other.uuid);
	}

	public int getVersion() {

		return version;
	}

}
