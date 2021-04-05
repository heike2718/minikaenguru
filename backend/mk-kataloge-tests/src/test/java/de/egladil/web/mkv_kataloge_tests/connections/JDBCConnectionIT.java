// =====================================================
// Project: mk-wettbewerb-tests
// (c) Heike Winkelvoß
// =====================================================
package de.egladil.web.mkv_kataloge_tests.connections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import de.egladil.web.mk_kataloge.infrastructure.persistence.entities.Schule;

/**
 * JDBCConnectionTest
 */
public class JDBCConnectionIT {

	@Test
	void should_getAConnection_to_localDatabase() {

		List<Schule> result = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection(
			"jdbc:mysql://localhost:3306/mk_kataloge", "mk_kataloge", "hwinkel");

			PreparedStatement preparedStatement = conn.prepareStatement("SELECT KUERZEL, NAME from SCHULEN")) {

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				String kuerzel = resultSet.getString("KUERZEL");
				String name = resultSet.getString("NAME");

				Schule schule = new Schule();
				schule.setKuerzel(kuerzel);
				schule.setName(name);

				result.add(schule);
			}
			Optional<Schule> optTestschule = result.stream().filter(s -> "VX1IF3HS".equals(s.getKuerzel())).findFirst();

			assertTrue(optTestschule.isPresent());
			Schule testschule = optTestschule.get();
			assertEquals("dev-db-Exporttest-1-Drei", testschule.getName());
			System.out.println(testschule);

		} catch (SQLException e) {

			System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	@Test
	void should_getAConnection_to_dockerDatabase() {

		List<Schule> result = new ArrayList<>();

		try (Connection conn = DriverManager.getConnection(
			"jdbc:mysql://192.168.10.176:5306/mk_kataloge", "mk_kataloge", "integrationtests");

			PreparedStatement preparedStatement = conn.prepareStatement("SELECT KUERZEL, NAME from SCHULEN")) {

			ResultSet resultSet = preparedStatement.executeQuery();

			while (resultSet.next()) {

				String kuerzel = resultSet.getString("KUERZEL");
				String name = resultSet.getString("NAME");

				Schule schule = new Schule();
				schule.setKuerzel(kuerzel);
				schule.setName(name);

				result.add(schule);
			}
			Optional<Schule> optTestschule = result.stream().filter(s -> "VX1IF3HS".equals(s.getKuerzel())).findFirst();

			assertTrue(optTestschule.isPresent());
			Schule testschule = optTestschule.get();
			assertEquals("it-db-Exporttest-1-Drei", testschule.getName());
			System.out.println(testschule);

		} catch (SQLException e) {

			System.err.format("SQL State: %s\n%s", e.getSQLState(), e.getMessage());
		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
