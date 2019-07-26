package bogdan.lab.server;

import bogdan.lab.art.DrawnHumanoid;
import bogdan.lab.controllers.CanvasController;
import bogdan.lab.humanoid.Humanoid;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import static bogdan.lab.database.SecurityTools.deserializeObject;
import static bogdan.lab.database.SecurityTools.objectToBytes;

public class Client {
	private static volatile int server = -1;
	private static volatile Scanner scanner = new Scanner(System.in);
	private static volatile String input;
	private static volatile String message;
	public static ObservableList<Humanoid> myList = FXCollections.observableArrayList();

	public static ObservableList<Humanoid> getMyList() {
		return myList;
	}

	public static ObservableList<Humanoid> bigList;

	public static ObservableList<Humanoid> getBigList() {
		return bigList;
	}

	public static ObservableMap<String, ArrayList<Humanoid>> map = FXCollections.observableHashMap();

	private static volatile DatagramChannel channel;

	static {
		try {
			channel = DatagramChannel.open();
		} catch (IOException e) {
		}
	}

	private static volatile InetAddress host;

	static {
		try {
			host = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	private static volatile SocketAddress address = new InetSocketAddress(host, 1080);

	public static volatile boolean connected = false;
	public static volatile boolean loggedIn = false;
	private static volatile int responses = 0;
	public static volatile boolean interruptWork = false;

	private static String login = "";

	private static String password = "";

	public static void setPassword(String password) {
		Client.password = password;
	}

	public static void setLogin(String login) {
		Client.login = login;
	}

	public static void main() {
		try {
			Runtime.getRuntime().addShutdownHook(new Thread(() -> System.out.println("Exiting")));

			channel = DatagramChannel.open();

			Thread check = new Thread(() -> {
				while (true) if (connected) try {
					int currentResponses = responses;
					Thread.sleep(2000);
					if (currentResponses == responses) sendOperation("check_again");
					Thread.sleep(1500);
					if (currentResponses == responses) {
						System.out.println("Server is not responding");
						interruptWork = true;
						connected = false;
						loggedIn = false;
					}
				} catch (Exception e) {
				}
			});
			check.setDaemon(true);
			check.start();

			while (true)
				if (channel.isConnected())
					try {

						Operation operation = receive();
						input = operation.getOperation();

						responses++;

						connected = true;
						if (input.equals("remove")) {
							Platform.runLater(
								() -> {
									ArrayList<Humanoid> listOfObject = null;
									try {
										listOfObject = (ArrayList) deserializeObject(operation.getObject());
									} catch (IOException e) {
										e.printStackTrace();
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
									String user = operation.getArgument();
									System.out.println("Trying to remove " + listOfObject);
									ArrayList<Humanoid> removeList = new ArrayList<>();
									ArrayList<Humanoid> list = map.get(user);
									for (Humanoid humanoid : listOfObject) {
										for (Humanoid h : list)
											if (humanoid.equals(h)) removeList.add(h);
									}
									list.removeAll(removeList);
									bigList.removeAll(removeList);
									for (Humanoid h : removeList)
										CanvasController.mapOfHumanoids.remove(h);
								}
							);
						} else if (input.equals("add")) {
							Platform.runLater(
								() -> {
									ArrayList<Humanoid> listOfObject = null;
									try {
										listOfObject = (ArrayList) deserializeObject(operation.getObject());
									} catch (IOException e) {
										e.printStackTrace();
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
									String user = operation.getArgument();
									try {
										bigList.addAll(listOfObject);
										for (Humanoid humanoid : listOfObject) {
											CanvasController.mapOfHumanoids.put(humanoid, new DrawnHumanoid(humanoid, user));
										}
										map.get(user).addAll(listOfObject);
									} catch (NullPointerException n) {
										map.put(user, listOfObject);
									}

								}
							);


						} else if (input.equals("update")) {
							Platform.runLater(
								() -> {
									Humanoid humanoid = null;
									try {
										humanoid = (Humanoid) deserializeObject(operation.getObject());
									} catch (IOException e) {
										e.printStackTrace();
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}
									for (Humanoid h : map.get(operation.getArgument())) {
										if (h.equals(humanoid)) humanoid = h;
									}
									Humanoid new_humanoid = null;
									try {
										new_humanoid = (Humanoid) deserializeObject(operation.getSecondObject());
									} catch (IOException e) {
										e.printStackTrace();
									} catch (ClassNotFoundException e) {
										e.printStackTrace();
									}

									map.get(operation.getArgument()).remove(humanoid);
									map.get(operation.getArgument()).add(new_humanoid);
									CanvasController.mapOfHumanoids.remove(humanoid);
									CanvasController.mapOfHumanoids.put(new_humanoid, new DrawnHumanoid(new_humanoid, operation.getArgument()));
									bigList.remove(humanoid);
									bigList.add(new_humanoid);
								});
						} else if (input.equals("clear")) {
							for (Humanoid h : map.get(operation.getArgument()))
								CanvasController.mapOfHumanoids.remove(h);
							bigList.removeAll(map.get(operation.getArgument()));
							map.get(operation.getArgument()).removeAll(map.get(operation.getArgument()));
						} else if (input.equalsIgnoreCase("true")) {
							loggedIn = true;
						} else if (input.equalsIgnoreCase("false")) {
							loggedIn = false;
						} else if (input.equals("your_map")) {
							try {
								map.putAll((HashMap) deserializeObject(operation.getObject()));
							} catch (IOException e) {
								e.printStackTrace();
							} catch (ClassNotFoundException e) {
								e.printStackTrace();
							}

							try {
								myList = FXCollections.observableList(map.get(login));
							} catch (Exception ignored) {
							}
							bigList = FXCollections.observableArrayList();
							for (Object key : map.keySet()) {
								bigList.addAll(map.get(key));
							}
						} else if (!input.contains("New connection") && !input.equals("")) {
							System.out.println(input);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
		} catch (
			Exception e) {
		}

	}


	public static void operation(String line) throws IOException {
		line = line.trim();
		String op;
		String ar;
		try {
			op = line.split(" ", 2)[0].trim();
		} catch (Exception e) {
			op = "";
		}
		try {
			ar = line.split(" ", 2)[1].trim();
		} catch (Exception e) {
			ar = "";
		}
		Operation operation = createOperation(op, ar);
		switch (operation.getOperation()) {
			case "import":
				if (connected) {
					if (loggedIn) {
						try {
							File file = new File(operation.getArgument());
							FileContent fileContent = new FileContent();
							fileContent.addContent(file);
							try {
								operation.setObject(objectToBytes(fileContent));
							} catch (IOException e) {
								System.out.println("File is too big");
							} catch (Exception e) {
								System.out.println("Couldn't serialize");
							}
							sendOperation(operation);
						} catch (ArrayIndexOutOfBoundsException | NullPointerException a) {
							System.out.println("No argument?");
						} catch (Exception e) {
							System.out.println("There was something wrong with the file");
						}

					} else System.out.println("You are not logged in");
				} else System.out.println("You are not connected");
				break;
			case "connect":
				try {
					connect(operation.getArgument());
				} catch (ArrayIndexOutOfBoundsException a) {
					System.out.println("You forgot to put an argument");
				}
				break;
			case "reset_password":
				sendOperation(operation);
			case "try_password":
				password = operation.getArgument();
				sendOperation("check_again");
				break;
			case "disconnect":
				connected = false;
				loggedIn = false;
				break;
			case "exit":
				System.out.println("Shutting down");
				System.exit(0);
			default:
				if (connected) {
					if (loggedIn || operation.getOperation().contains("reset_password")) {
						sendOperation(operation);
					} else System.out.println("You are not logged in");
				} else if (operation.getOperation().startsWith("co")) {
					System.out.println("You misspelled 'connect'");
				} else if (operation.getOperation().equals("")) {
				} else {
					System.out.println("You are not connected here");
				}
				break;
		}
	}

	public static void refresh() {
		sendOperation("refresh");
	}

	public static void connect(String argument) {
		try {
			// connected = false;

			server = Math.round(Math.abs(Integer.parseInt(argument)));

			// address = new InetSocketAddress(host, server);


			if (!channel.isConnected()) {
				channel = DatagramChannel.open();
				channel.connect(address);
			}

			sendOperation("check");

			Thread.sleep(100);

			if (!connected) {
				System.out.println("Try again");
			}

		} catch (NumberFormatException n) {
			System.out.println("Broken number. Try integer");
		} catch (UnknownHostException u) {
			System.out.println("Unknown host");
		} catch (SocketException s) {
			s.printStackTrace();
			System.out.println("Can't connect");
		} catch (IOException i) {
			System.out.println("Channel not working");
		} catch (Exception t) {
			t.printStackTrace();
			System.out.println("Can't connect");
		}
	}

	public static void send(String operation) throws IOException {

		ByteBuffer out = ByteBuffer.allocate(100000);

		out.put(operation.getBytes());
		out.flip();

		channel.send(out, address);
	}

	public static void sendBytes(byte[] operation) throws IOException {
		ByteBuffer out = ByteBuffer.allocate(65000);
		out.put(operation);
		out.flip();

		channel.send(out, address);
	}

	public static void sendOperation(String command) {
		try {
			sendBytes(objectToBytes(createOperation(command, "")));
		} catch (Exception e) {
			System.out.println("There was an error sending the operation");
		}
	}

	public static void sendOperation(Operation operation) throws IOException {
		sendBytes(objectToBytes(operation));
	}

	public static Operation createOperation(String operation, String argument) {
		return new Operation(operation, argument, login, password);
	}

	public static Operation receive() throws IOException, ClassNotFoundException {

		ByteBuffer in = ByteBuffer.allocate(1000000);

		channel.read(in);

		in.flip();

		return (Operation) deserializeObject(in.array());
	}


	public static void showCat() {
		for (int i = 0; i < 100; i++) {
			if (i % 2 == 0) System.out.print(Art.nyancat0);
			else System.out.print(Art.nyancat1);
			try {
				Thread.sleep(200);
			} catch (Exception ignored) {
			}
			clear();
		}

	}

	private static void help() {
		System.out.println(
			"List of operations:\n" +
				"connect (integer) - connects you to a server\n" +
				"import (string) - sends the contents of YOUR file to the server and creates a collection\n" +
				"load (string) - loads the contents of the SERVER'S file into a collection\n" +
				"help - prints out the list of operations available at the moment\n" +
				"disconnect - disconnects\n" +
				"lk - unpredictable\n" +
				"exit - exits"
		);
	}

	public static void clear() {
		System.out.print("\033[H\033[2J");
	}

	public static String getLogin() {
		return login;
	}

	public static String getPassword() {
		return password;
	}

	public static Operation getOperation() {
		return new Operation(login, password);
	}
}
