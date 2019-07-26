package bogdan.lab.collection;

import bogdan.lab.humanoid.Humanoid;
import bogdan.lab.server.LittleParser;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;


public class CollectionReader implements Serializable {
	public CollectionReader() { }

	public int find(String name, List<Humanoid> list) {
		for (int i = 0; i < list.size(); ++i) {
			if (list.get(i).getName().trim().equals(name.trim())) {
				return i;
			}
		}
		return -1;
	}

	public static CollectionManager createCollectionFromFile(String filePath) {
		CollectionManager collection = new CollectionManager();
		boolean overallFailure = true;
		boolean initialFailure = false;

		do try {
			File file;
			if (initialFailure) {
				String line = (new Scanner(System.in)).nextLine();
				file = new File(line);
			} else {
				file = new File(filePath);
			}

			if (file.createNewFile()) {
				System.out.println("Made a new file.");
			} else {
				collection.checkFile(file.getPath());
			}

			overallFailure = false;
			Scanner input = new Scanner(file);
			collection.setFilePath(filePath);

			while (input.hasNextLine()) {
				try {
					String line = input.nextLine();
					collection.list.add(LittleParser.fromJson(line));
				} catch (Exception var8) {
				}
			}
		} catch (IOException var9) {
			System.out.println("Couldn't get access to the file. Try entering the path once more.");
			initialFailure = true;
		} catch (SecurityException var10) {
			System.out.println("The file couldn't be read or written into or is a directory. Try again:");
			initialFailure = true;
		} while (overallFailure);

		return collection;
	}

	String readFromConsole() {
		try {
			Scanner jsonReader = new Scanner(System.in);
			StringBuilder jsonParameters = new StringBuilder();
			String line = jsonReader.nextLine();
			int emptyLinesCounter = 0;
			if (line.contains("}")) {
				return line;
			}
			do {
				jsonParameters.append(line.trim());
				line = jsonReader.nextLine();
				emptyLinesCounter = line.trim().equals("") ? emptyLinesCounter + 1 : 0;
				if (emptyLinesCounter == 2) {
					System.out.println("One more blank line and I'll stop reading this crap.");
				}
			} while (emptyLinesCounter != 3 && !line.contains("}"));

			jsonParameters.append(line.trim());
			return jsonParameters.toString();
		} catch (NoSuchElementException var5) {
			System.out.println("Input error, try again: \n{");
			return this.readFromConsole();
		}
	}

	public Humanoid readHumanoid(String parameter) throws Exception {
		boolean failure = true;
		byte var3 = 0;
		do {
			try {
				int tries = var3 + 1;
				if (tries <= 2) {
					String parameters = parameter + this.readFromConsole().trim();
					parameters = parameters.replaceAll("\\s", "");
					if (!parameters.contains("{")) parameters = "{" + parameters;
					int age = Integer.parseInt(LittleParser.get(parameters, "age").trim());
					if (LittleParser.get(parameters, "name").equals("")) {
						throw new CollectionReader.EmptyNameException();
					}
					if ((age < 1) || (age > 99)) {
						throw new CollectionReader.UnacceptableAgeException();
					}
					return LittleParser.fromJson(parameters);
				}
				break;
			} catch (NullPointerException var11) {
				System.out.println("Wrong input.");
			}
			System.out.println("Try again:\n{");
		} while (failure);
		return null;
	}

	public static class WrongRaceException extends Exception {
		WrongRaceException() {
			System.out.println("Wrong race.");
		}
	}

	private class UnacceptableAgeException extends Exception {
		UnacceptableAgeException() {
			System.out.println("Age is a natural number that is less than a hundred.");
		}
	}

	private class EmptyNameException extends Exception {
		EmptyNameException() {
			System.out.println("Name can't be empty.");
		}
	}
}
