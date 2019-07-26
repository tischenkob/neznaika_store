package bogdan.lab.server;

import bogdan.lab.collection.CollectionReader;
import bogdan.lab.database.EmailSender;
import bogdan.lab.database.Humanoid_DB;
import bogdan.lab.humanoid.Humanoid;

import java.io.FileWriter;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.sql.SQLException;
import java.util.*;

import static bogdan.lab.database.SecurityTools.deserializeObject;
import static bogdan.lab.database.SecurityTools.objectToBytes;


public class Server {
	private static int serverPort = -1;
	private static int clientPort = -1;
	private static DatagramSocket serverSocket;
	private static DatagramPacket incomingPacket;
	private static Set<Integer> clients = new LinkedHashSet<>();
	private static Humanoid_DB database = new Humanoid_DB();
	private static String salt = "ASSAULT";

	public static String getSalt() {
		return salt;
	}

	public static Map<String, ArrayList<Humanoid>> map = new HashMap<>();

	public static void main(String[] args) throws SQLException, SocketException {
		Thread threadOne = new Thread(() -> System.out.println("Exiting"));
		Runtime.getRuntime().addShutdownHook(threadOne);

		Scanner scanner = new Scanner(System.in);

		while (!database.connect()) {
			try {
				Thread.sleep(1000);
				System.out.println("Couldn't connect to the database");
			} catch (Exception e) {
			}
		}

		EmailSender.connect();
		serverPort = 1080;
		serverSocket = new DatagramSocket(serverPort);


		System.out.println("Server's running on port " + serverPort);

		ArrayList<String> listOfUsers = database.getListOfUsers();

		for (String login : listOfUsers) {
			try {
				ArrayList<Humanoid> usersList = Humanoid_DB.getUsersObjectList(login);
				map.put(login, usersList);
			} catch (Exception ignored) {
				ignored.printStackTrace();
			}
		}
		new Thread(() -> {
			while (true) try {

				Operation operation = update();
				ServerUser user = new ServerUser(clientPort, operation);
				new Thread(() -> handleClient(user)).start();

			} catch (Exception e) {
				System.out.println("There was an error handling a client");
			}
		}).start();
	}

	public static Operation update() {
		incomingPacket = receive();
		clientPort = incomingPacket.getPort();
		Operation operation = null;
		try {
			operation = (Operation) deserializeObject(incomingPacket.getData());
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Error receiving an operation");
		}
		return operation;
	}


	public static void handleClient(ServerUser user) {
		Operation operation = user.getOperation();

		String input = operation.getOperation();
		if (!input.equals("")) {
			try {
				execute(user);
			} catch (Exception e) {
				System.out.println("There was an error");
			}
		}
	}

	public static void send(String message, int userPort) {
		try {
			message = message.trim();
			Operation operation = new Operation("server", "server");
			operation.setOperation(message);
			byte[] byteOperation = objectToBytes(operation);
			DatagramPacket packet = null;
			packet = new DatagramPacket(byteOperation, byteOperation.length, InetAddress.getLocalHost(), userPort);
			serverSocket.send(packet);
		} catch (Exception e) {
			System.out.println("Could't send");
		}
	}

	public static void send(Operation operation, int userPort) {
		try {
			byte[] byteOperation = objectToBytes(operation);
			DatagramPacket packet = null;
			packet = new DatagramPacket(byteOperation, byteOperation.length, InetAddress.getLocalHost(), userPort);
			serverSocket.send(packet);
		} catch (Exception e) {
			System.out.println("Could't send");
		}
	}

	public static void sendToAllUsers(Operation message) {
		for (Integer client : clients) {
			send(message, client);
		}
	}

	public static void sendToAllButOne(Operation message, Integer clientPort) {
		for (Integer client : clients) {
			if (!client.equals(clientPort))
				send(message, client);
		}
	}

	public static synchronized DatagramPacket receive() {

		byte[] buffer = new byte[10000];
		DatagramPacket packet = new DatagramPacket(buffer, 0, buffer.length);

		try {
			serverSocket.receive(packet);
		} catch (IOException e) {
			System.out.println("Couldn't receive");
		}

		return packet;
	}

	public static void execute(ServerUser user) {
		String login = user.operation.getLogin();
		Operation operation = user.getOperation();
		String cmd = operation.getOperation();
		String argument = operation.getArgument();
		int userPort = user.getPort();
		try {
			if ("import".equals(cmd)) {
				String file;
				try {
					file = argument;
				} catch (ArrayIndexOutOfBoundsException ar) {
					System.out.println("No argument passed");
					file = "default.json";
				}
				try {

					FileContent fileContent = (FileContent) deserializeObject(operation.getObject());

					FileWriter fileWriter = new FileWriter(file);
					fileWriter.write(fileContent.getContent());
					fileWriter.close();

					ArrayList<Humanoid> list = (CollectionReader.createCollectionFromFile(file).list);

					Operation response = new Operation("server", "server");
					response.setOperation("add");
					response.setArgument(operation.getLogin());
					response.setObject(objectToBytes(list));
					sendToAllUsers(response);
					database.insertList(list, operation.getLogin());
					map.get(operation.getLogin()).addAll(list);
				} catch (Exception e) {
					System.out.println("Couldn't create collection out of that");
				}
			} else if ("add".equals(cmd)) {

				ArrayList<Humanoid> list = (ArrayList) deserializeObject(operation.getObject());

				try{
					map.get(operation.getLogin()).addAll(list);
				} catch (NullPointerException n){
					map.put(operation.getLogin(), list);
				}

				database.insertList(list, operation.getLogin());

				Operation response = new Operation("server", "server");

				response.setOperation("add");
				response.setArgument(operation.getLogin());
				response.setObject(operation.getObject());

				sendToAllButOne(response, userPort);
			} else if ("clear".equals(cmd)) {
				List<Humanoid> list = map.get(operation.getLogin());
				Operation response = new Operation("server", "server");
				response.setOperation("clear");
				response.setArgument(operation.getLogin());
				response.setObject(objectToBytes(list));
				sendToAllButOne(response, userPort);
				map.get(operation.getLogin()).removeAll(list);
				database.clear(operation.getLogin());
			} else if ("remove".equals(cmd)) {
				try {
					Humanoid humanoid = (Humanoid) deserializeObject(operation.getObject());
					String race = humanoid.getRace();
					String name = humanoid.getName();
					int age = humanoid.getAge();
					Operation response = new Operation("server", "server");
					List<Humanoid> list = new ArrayList<>();
					list.add(humanoid);
					response.setOperation("remove");
					response.setArgument(operation.getLogin());
					response.setObject(objectToBytes(list));
					sendToAllButOne(response, user.getPort());
					database.deleteHumanoid(name, race, age, operation.getLogin());
					for (Humanoid h : map.get(operation.getLogin())){
						if (h.equals(humanoid)) humanoid = h;
					}
					map.get(operation.getLogin()).remove(humanoid);
				} catch (NullPointerException | ArrayIndexOutOfBoundsException exception) {
					exception.printStackTrace();
				}
			} else if ("remove_greater".equals(cmd)) {
				ArrayList<Humanoid> listOfHumanoids = (ArrayList<Humanoid>) deserializeObject(operation.getObject());

				Operation response = new Operation("server", "server");


				response.setOperation("remove");
				response.setArgument(operation.getLogin());
				response.setObject(objectToBytes(listOfHumanoids));

				sendToAllButOne(response, user.getPort());
				ArrayList<Humanoid> removeList = new ArrayList<>();
				ArrayList<Humanoid> list = map.get(operation.getLogin());
				for (Humanoid humanoid : listOfHumanoids) {
					for (Humanoid h : list)
						if (humanoid.equals(h)) removeList.add(h);
				}
				list.removeAll(removeList);
				map.get(operation.getLogin()).removeAll(removeList);
				database.deleteOlder(Integer.parseInt(operation.getArgument()), operation.getLogin());
			} else if ("remove_all".equals(cmd)) {
				List<Humanoid> listOfHumanoids = (ArrayList<Humanoid>) deserializeObject(operation.getObject());

				Operation response = new Operation("server", "server");


				response.setOperation("remove");
				response.setArgument(operation.getLogin());
				response.setObject(objectToBytes(listOfHumanoids));

				sendToAllButOne(response, user.getPort());

				ArrayList<Humanoid> removeList = new ArrayList<>();
				ArrayList<Humanoid> list = map.get(operation.getLogin());
				for (Humanoid humanoid : listOfHumanoids) {
					for (Humanoid h : list)
						if (humanoid.equals(h)) removeList.add(h);
				}
				list.removeAll(removeList);
				map.get(operation.getLogin()).removeAll(removeList);
				database.removeAll(operation.getArgument(), operation.getLogin());

			} else
				if ("check".equals(cmd)) {
				if (clients.add(userPort))
					System.err.println("New connection: " + userPort);
				send("", userPort);
				if (database.isRegistered(operation.getLogin()) && database.checkPassword(operation.getLogin(), operation.getPassword(), salt)) {
					send("true", userPort);
					Operation listOperation = new Operation("server", "server");
					listOperation.setOperation("your_map");
					listOperation.setObject(objectToBytes(map));
					send(listOperation, userPort);
				} else if (!database.isRegistered(operation.getLogin())) {
					database.register(operation.getLogin(), salt);
					send("You are now registered. Try password that's on your email", userPort);
				} else {
					send("false", userPort);
				}
			} else
				if ("check_again".equals(cmd)) {
				if (database.isRegistered(operation.getLogin()) && database.checkPassword(operation.getLogin(), operation.getPassword(), salt)) {
					send("true", userPort);
				} else {
					send("false", userPort);
				}
			} else
				if ("update".equals(cmd)) {
				try {
					Humanoid humanoid = (Humanoid) deserializeObject(operation.getObject());
					Humanoid new_humanoid = (Humanoid) deserializeObject(operation.getSecondObject());

					Operation response = new Operation("server", "server");

					response.setOperation("update");
					response.setArgument(login);
					response.setObject(operation.getObject());
					response.setSecondObject(operation.getSecondObject());
					sendToAllButOne(response, userPort);
					Humanoid_DB.deleteHumanoid(humanoid.getName(), humanoid.getRace(), humanoid.getAge(), operation.getLogin());
					database.insertHumanoid(new_humanoid, operation.getLogin());
					for (Humanoid h : map.get(operation.getLogin())){
						if (h.equals(humanoid)) humanoid = h;
					}
					map.get(operation.getLogin()).remove(humanoid);
					map.get(operation.getLogin()).add(new_humanoid);

				} catch (NullPointerException | ArrayIndexOutOfBoundsException exception) {
				}
			} else
				if ("get_owner".equals(cmd)) {
				Humanoid object = (Humanoid) deserializeObject(operation.getObject());
				String owner = database.getOwner(object);
				Operation response = new Operation("server", "server");

				response.setOperation("humanoid_owner");
				response.setArgument(owner);
				response.setObject(objectToBytes(object));

				send(response, userPort);
			} else
				if ("reset_password".equals(cmd)) {
				try {
					if (argument.isEmpty()) throw new NullPointerException();
					String token = argument.split(" ", 2)[0];
					String password = argument.split(" ", 2)[1];
					if (password.isEmpty()) throw new ArrayIndexOutOfBoundsException();

					if (database.checkToken(operation.getLogin(), token))
						database.setPassword(operation.getLogin(), password, salt);

				} catch (NullPointerException ar) {
					database.setToken(operation.getLogin());
				} catch (ArrayIndexOutOfBoundsException ar) {
					System.out.println("(token) (new_password) - only a child would mess up");
				}
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException v) {
			v.printStackTrace();
		} catch (Exception var9) {
			var9.printStackTrace();
		}
	}

}

