// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_server_tests.connections;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_gateway.domain.user.Rolle;
import de.egladil.web.mk_gateway.infrastructure.persistence.entities.User;

/**
 * JDBCConnectionTest
 */
public class JDBCConnectionTest {

	@Test
	void should_getAConnection_to_localDatabase() {

		List<User> result = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection(
			"jdbc:mysql://localhost:3306/mk_wettbewerb", "mk_wettbewerb", "hwinkel");

			PreparedStatement preparedStatement = conn.prepareStatement("SELECT UUID, ROLE from USERS")) {

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				String uuid = resultSet.getString("UUID");
				String role = resultSet.getString("ROLE");

				User user = new User();
				user.setUuid(uuid);
				user.setRolle(Rolle.valueOf(role));

				result.add(user);
			}
			result.stream().filter(u -> "dev-db-on-localhost".equals(u.getUuid())).forEach(x -> System.out.println(x));

		} catch (SQLException e) {

			System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	@Test
	void should_getAConnection_to_dockerDatabase() {

		List<User> result = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection(
			"jdbc:mysql://192.168.10.176:4306/mk_wettbewerb", "mk_wettbewerb", "integrationtests");

			PreparedStatement preparedStatement = conn.prepareStatement("SELECT UUID, ROLE from USERS")) {

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				String uuid = resultSet.getString("UUID");
				String role = resultSet.getString("ROLE");

				User user = new User();
				user.setUuid(uuid);
				user.setRolle(Rolle.valueOf(role));

				result.add(user);

			}
			result.stream().filter(u -> "it-db-inside-docker".equals(u.getUuid())).forEach(x -> System.out.println(x));

		} catch (SQLException e) {

			System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
