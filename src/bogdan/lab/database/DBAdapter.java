package bogdan.lab.database;

import java.sql.*;


public class DBAdapter {

	// variables
	String jdbcURL = "jdbc:postgresql://localhost:5432/postgres";
	String username = "postgres";
	String password = "postgres";

	// db variables
	Connection connection = null;
	static Statement statement = null;
	PreparedStatement insertStatement;
	static PreparedStatement deleteStatement;
	PreparedStatement deleteOlderStatement;
	PreparedStatement registerStatement;
	PreparedStatement getUserStatement;
	PreparedStatement checkRegistration;
	PreparedStatement removeAllStatement;
	PreparedStatement clearStatement;
	PreparedStatement updateTokenStatement;
	PreparedStatement updatePasswordStatement;
	PreparedStatement updateHumanoidStatement;
	ResultSet resultSet = null;
	static PreparedStatement getObjectsListStatement;
	PreparedStatement findOwnerStatement;
	PreparedStatement getListOfUsersStatement;

	// constructor
	DBAdapter() {
	}

	// methods

	public boolean connect() {
		try {
			try {
				Class.forName("org.postgresql.Driver");
			} catch (Exception e) {
				System.out.println("Couldn't load the driver");
			}
			connection = DriverManager.getConnection(jdbcURL, username, password);
			System.out.println("Connected to the database");
			statement = connection.createStatement();
			insertStatement = connection.prepareStatement(
				"INSERT INTO humanoids (humanoid_race, humanoid_name,  humanoid_age,  pos_x,  pos_y, birth_date, user_name)" +
					"VALUES (?, ?, ?, ?, ?, ?, ?);");
			deleteStatement = connection.prepareStatement(
				"DELETE FROM humanoids WHERE " +
					"humanoid_name = ? AND" +
					" humanoid_race = ? AND" +
					" humanoid_age = ? AND" +
					" user_name = ?;"
			);
			getObjectsListStatement = connection.prepareStatement(
				"SELECT * FROM humanoids WHERE user_name =?;"
			);
			deleteOlderStatement = connection.prepareStatement(
				"DELETE FROM humanoids WHERE" +
					" humanoid_age > ? AND" +
					" user_name = ?;"
			);
			registerStatement = connection.prepareStatement(
				"INSERT INTO users (login, password) VALUES (?, ?);"
			);
			getUserStatement = connection.prepareStatement(
				"SELECT * FROM users WHERE login = ?;"
			);
			checkRegistration = connection.prepareStatement(
				"SELECT login FROM users WHERE login = ?;"
			);
			removeAllStatement = connection.prepareStatement(
				"DELETE FROM humanoids WHERE user_name = ? AND humanoid_name = ?;"
			);
			clearStatement = connection.prepareStatement(
				"DELETE FROM humanoids WHERE user_name = ?;"
			);
			updateTokenStatement = connection.prepareStatement(
				"UPDATE users SET token = ? WHERE login = ?;"
			);
			updatePasswordStatement = connection.prepareStatement(
				"UPDATE users SET password = ? WHERE login = ?;"
			);
			updateHumanoidStatement = connection.prepareStatement(
				"UPDATE humanoids SET " +
					"humanoid_name = ?," +
					"humanoid_age = ?," +
					"pos_x = ?," +
					"pos_y = ? WHERE " +
					"humanoid_race = ? AND " +
					"humanoid_name = ? AND " +
					"humanoid_age = ? AND " +
					"user_name = ?;"
			);
			findOwnerStatement = connection.prepareStatement(
				"SELECT * FROM humanoids WHERE " +
					"humanoid_race = ? AND " +
					"humanoid_name = ? AND " +
					"humanoid_age = ? AND " +
					"pos_x = ? AND " +
					"pos_y = ?;"
			);
			getListOfUsersStatement = connection.prepareStatement(
				"SELECT * FROM users;"
			);
			return true;
		} catch (SQLException s) {
			System.out.println("Couldn't connect to the database");
		}
		return false;
	}

	public void disconnect() {
		try {
			if (connection != null) connection.close();
			if (statement != null) statement.close();
			if (resultSet != null) resultSet.close();
		} catch (Exception e) {

		}
	}
}
