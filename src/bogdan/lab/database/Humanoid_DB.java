package bogdan.lab.database;

import bogdan.lab.collection.CollectionManager;
import bogdan.lab.humanoid.Humanoid;
import bogdan.lab.humanoid.Lunatic;
import bogdan.lab.humanoid.Shorty;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Humanoid_DB extends DBAdapter {

	public void createTable(CollectionManager collectionManager) {
		String sqlClients = "CREATE TABLE IF NOT EXISTS users(" +
			"login VARCHAR UNIQUE," +
			"password VARCHAR);";

		String sqlHumanoids = "CREATE TABLE IF NOT EXISTS humanoids(" +
			"humanoid_race VARCHAR(15)," +
			"humanoid_name VARCHAR(30)," +
			"humanoid_age INT," +
			"pos_x INT," +
			"pos_y INT," +
			"birth_date VARCHAR(100)," +
			"user_name VARCHAR references users(login)" +
			");";

		try {
			statement.execute(sqlClients);
			statement.execute(sqlHumanoids);

			ResultSet resultSet = statement.executeQuery("SELECT * FROM humanoids;");

			collectionManager.list = createListFromResultSet(resultSet);
		} catch (SQLException s) {
			System.out.println("Couldn't create tables");
		}
	}

	public static ArrayList<Humanoid> createListFromResultSet(ResultSet resultSet) throws SQLException {
		ArrayList<Humanoid> list = new ArrayList<>();
		while (resultSet.next()) {
			String race = resultSet.getString("humanoid_race");
			String name = resultSet.getString("humanoid_name");
			long age = (long) resultSet.getInt("humanoid_age");
			int posX = resultSet.getInt("pos_x");
			int posY = resultSet.getInt("pos_y");
			LocalDateTime birthDate;

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			birthDate = LocalDateTime.parse(resultSet.getString("birth_date").replaceAll("'", ""), formatter);

			Humanoid human;
			if (race.equalsIgnoreCase("lunatic")) {
				human = new Lunatic(name, age, birthDate, posX, posY);
			} else human = new Shorty(name, age, birthDate, posX, posY);

			list.add(human);
		}
		return list;
	}

	public void insertList(List<Humanoid> list, String login) {
		for (Humanoid humanoid : list) {
			insertHumanoid(humanoid, login);
		}
	}

	public static void show() {
		try {
			ResultSet resultSet = statement.executeQuery("SELECT * FROM humanoids");
			int i = 0;
			System.out.println();
			while (resultSet.next()) {
				i++;
				System.out.printf("%9s | %10s | %2d | %20s | {%d:%d} \n",
					resultSet.getString("humanoid_race"),
					resultSet.getString("humanoid_name"),
					resultSet.getInt("humanoid_age"),
					resultSet.getString("birth_date"),
					resultSet.getInt("pos_x"),
					resultSet.getInt("pos_y"));
			}
			if (i == 0) System.out.println("Empty");

		} catch (SQLException s) {
			System.out.println("Couldn't show");
		}
	}

	public static void show_mine(String login) {
		int i = 0;
		try {

			ResultSet resultSet = statement.executeQuery("SELECT * FROM humanoids WHERE user_name = '" + login + "'");
			while (resultSet.next()) {
				i++;
				System.out.print(String.format("%9s | %10s | %2d | %20s | {%d:%d:%d} \n",
					resultSet.getString("humanoid_race"),
					resultSet.getString("humanoid_name"),
					resultSet.getInt("humanoid_age"),
					resultSet.getString("birth_date"),
					resultSet.getInt("pos_x"),
					resultSet.getInt("pos_y")));
			}
			if (i == 0) System.out.println("None");
		} catch (SQLException s) {
			System.out.println("Database error. Couldn't show");
		} catch (Exception e) {
			System.out.println("Some error");
		}
	}

	public static ArrayList getUsersObjectList(String login) throws SQLException {
		int i = 0;
		ResultSet resultSet = null;
		try {
			getObjectsListStatement.setString(1, login);
			resultSet = getObjectsListStatement.executeQuery();

		} catch (SQLException s) {
			System.out.println("Database error. Couldn't show");
		} catch (Exception e) {
			System.out.println("Some error");
		}
		return createListFromResultSet(resultSet);
	}

	public void insertHumanoid(Humanoid humanoid, String login) {
		try {

			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			String formattedDateTime = humanoid.getBirthDate().format(formatter);

			insertStatement.setString(1, humanoid.getRace().toLowerCase());
			insertStatement.setString(2, humanoid.getName());
			insertStatement.setInt(3, (int) humanoid.getAge());
			insertStatement.setInt(4, humanoid.getPosX());
			insertStatement.setInt(5, humanoid.getPosY());
			insertStatement.setString(6, formattedDateTime);
			insertStatement.setString(7, login);

			insertStatement.executeUpdate();
		} catch (SQLException s) {
			System.out.println("Couldn't insert a humanoid");
		}
	}

	public static void deleteHumanoid(String name, String race, int age, String login) {
		try {
			deleteStatement.setString(1, name);
			deleteStatement.setString(2, race.toLowerCase());
			deleteStatement.setInt(3, age);
			deleteStatement.setString(4, login);

			int affectedRows = deleteStatement.executeUpdate();

			if (affectedRows == 0) System.out.println("There's no such humanoid");
			else System.out.println("You deleted " + name);
		} catch (SQLException ignored) {
		}
	}

	public void deleteOlder(int age, String login) {
		try {
			deleteOlderStatement.setInt(1, age);
			deleteOlderStatement.setString(2, login);

			int affectedRows = deleteOlderStatement.executeUpdate();

			if (affectedRows == 0) System.out.println("There's no such humanoid");
			else System.out.println("You deleted " + affectedRows + " entries");
		} catch (SQLException ignored) {
		}
	}

	public boolean isRegistered(String login) {
		try {
			checkRegistration.setString(1, login);
			int i = 0;
			ResultSet resultSet = checkRegistration.executeQuery();
			while (resultSet.next()) {
				i++;
			}
			return (i != 0);
		} catch (Exception e) {
			System.out.println("Couldn't check if registered");
		}
		return false;
	}

	public boolean checkPassword(String login, String password, String salt) {
		try {
			getUserStatement.setString(1, login);
			ResultSet resultSet = getUserStatement.executeQuery();
			resultSet.next();
			return resultSet.getString("password").equals(SecurityTools.encryptPassword(password, salt));
		} catch (Exception e) {
			System.out.println("Couldn't check password");
		}
		return false;
	}

	public boolean checkToken(String login, String token) {
		try {
			getUserStatement.setString(1, login);
			ResultSet resultSet = getUserStatement.executeQuery();
			resultSet.next();
			return resultSet.getString("token").equals(token);
		} catch (Exception e) {
			System.out.println("Couldn't check token");
		}
		return false;
	}

	public void register(String login, String salt) {
		String password = SecurityTools.generateString(10);
		try {
			registerStatement.setString(1, login);
			registerStatement.setString(2, SecurityTools.encryptPassword(password, salt));
			registerStatement.executeUpdate();
		} catch (Exception e) {
			System.out.println("There was an error trying to register a user");
		}
		EmailSender.connect();
		EmailSender.sendPassword(password, login);
	}

	public void clear(String login) {
		try {
			clearStatement.setString(1, login);
			clearStatement.execute();
		} catch (Exception e) {

		}
	}

	public void setToken(String login) {
		String token = SecurityTools.generateString(6);
		try {
			updateTokenStatement.setString(1, token);
			updateTokenStatement.setString(2, login);
			updateTokenStatement.execute();
			EmailSender.connect();
			EmailSender.sendToken(token, login);
		} catch (Exception e) {
			System.out.println("There was an error trying to add and send token");
		}
	}

	public void setPassword(String login, String password, String salt) {
		String new_password = SecurityTools.encryptPassword(password, salt);
		try {
			updatePasswordStatement.setString(1, new_password);
			updatePasswordStatement.setString(2, login);
			updatePasswordStatement.execute();
			System.out.println("Password changed successfully. Use try_password");
		} catch (Exception e) {
			System.out.println("There was an error trying to add and send token");
		}
	}

	public void removeAll(String argument, String login) {
		try {
			removeAllStatement.setString(1, login);
			removeAllStatement.setString(2, argument);
			removeAllStatement.execute();
		} catch (Exception e) {
			System.out.println("Couldn't remove");
		}
	}

	public void updateHumanoid(String name, String race, int age, Humanoid humanoid, String login) throws SQLException {
		updateHumanoidStatement.setString(1, humanoid.getName());
		updateHumanoidStatement.setInt(2, humanoid.getAge());
		updateHumanoidStatement.setInt(3, humanoid.getPosX());
		updateHumanoidStatement.setInt(4, humanoid.getPosY());
		updateHumanoidStatement.setString(5, race);
		updateHumanoidStatement.setString(6, name);
		updateHumanoidStatement.setInt(7, age);
		updateHumanoidStatement.setString(8, login);

		updateHumanoidStatement.execute();
	}

	public String getOwner(Humanoid humanoid) {
		try {
			findOwnerStatement.setString(1, humanoid.getRace());
			findOwnerStatement.setString(2, humanoid.getName());
			findOwnerStatement.setInt(3, humanoid.getAge());
			findOwnerStatement.setInt(4, humanoid.getPosX());
			findOwnerStatement.setInt(5, humanoid.getPosY());

			ResultSet resultSet = getUserStatement.executeQuery();
			resultSet.next();
			return resultSet.getString("user");
		} catch (Exception e) {
			System.out.println("Couldn't find user");
		}
		return null;
	}

	public ArrayList<String> getListOfUsers() throws SQLException {
		ResultSet resultSet = getListOfUsersStatement.executeQuery();
		ArrayList<String> list = new ArrayList();
		while (resultSet.next()) {
			list.add(resultSet.getString("login"));
		}
		return list;
	}

}

