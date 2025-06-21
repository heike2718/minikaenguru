//=====================================================
// Project: mk-gateway
// (c) Heike Winkelvoß
//=====================================================
package de.egladil.web.mk_gateway.infrastructure.persistence.wettbewerb.entities;

import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.SqlResultSetMapping;

/**
 *
 */
@SqlResultSetMapping(
	  name = NativeQueriesEntity.KALENDERWOCHEN_INTERVALL_MAPPING,
	  classes = @ConstructorResult(
	    targetClass = KalenderwochenIntervall.class,
	    columns = {
	      @ColumnResult(name = "MIN_KW", type = Integer.class),
	      @ColumnResult(name = "MAX_KW", type = Integer.class)
	    }
	  )
	)
	@NamedNativeQuery(
	  name = NativeQueriesEntity.SELECT_KALENDERWOCHEN_INTERVALL_LOESUNGSZETTEL,
	  query = "SELECT MIN(WEEKOFYEAR(DATE_MODIFIED)) AS MIN_KW, MAX(WEEKOFYEAR(DATE_MODIFIED)) AS MAX_KW FROM LOESUNGSZETTEL WHERE YEAR(DATE_MODIFIED) >= :jahr",
	  resultSetMapping = NativeQueriesEntity.KALENDERWOCHEN_INTERVALL_MAPPING
	)
@Entity
public class NativeQueriesEntity {

	public static final String KALENDERWOCHEN_INTERVALL_MAPPING = "NativeQueriesEntity.KALENDERWOCHEN_INTERVALL_MAPPING";
	public static final String SELECT_KALENDERWOCHEN_INTERVALL_LOESUNGSZETTEL = "NativeQueriesEntity.SELECT_KALENDERWOCHEN_INTERVALL_LOESUNGSZETTEL";

	@Id
	private long id;

}
