// =====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mk_gateway.domain.permissions;

import java.util.List;

import de.egladil.web.mk_gateway.domain.semantik.Repository;
import de.egladil.web.mk_gateway.domain.user.Rolle;

/**
 * PermittedRolesRepository
 */
@Repository
public interface PermittedRolesRepository {

	List<Rolle> permittedRollen(String path, String method);

}
