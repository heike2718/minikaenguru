// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.permissions;

import java.util.Optional;

import de.egladil.web.mk_gateway.domain.semantik.Repository;

/**
 * RestrictedUrlPathRepository
 */
@Repository
public interface RestrictedUrlPathRepository {

	Optional<RestrictedUrlPath> ofPath(String path);

}
