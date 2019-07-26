package bogdan.lab.collection;

import bogdan.lab.humanoid.Humanoid;
import bogdan.lab.humanoid.Lunatic;
import bogdan.lab.humanoid.Shorty;
import bogdan.lab.server.LittleParser;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;


public class CollectionManager implements Serializable {
	public ArrayList<Humanoid> list = new ArrayList<>();
	protected CollectionReader reader = new CollectionReader();
	protected Date date = new Date();
	public String filePath;

	public CollectionManager() {
		this.filePath = "";
	}

	CollectionManager(String filePath) {
		this.filePath = filePath;
	}

	public boolean addIfMin(Humanoid human) {
		try {
			this.sort();
			if (human.getAge() < (this.list.get(0)).getAge()) {
				list.add(human);
				System.out.println("New entry!");
				return true;
			} else {
				System.out.println("Age not least for this collection.");
				return false;
			}
		} catch (IndexOutOfBoundsException | NullPointerException var2) {
			System.out.println("Not added");
		} catch (Exception var3) {
			System.out.println("You're not good at working with this program.");
		}
		return false;
	}

	public void help() {
		System.out.println("Following operations are at your disposal:\n" +
			"add (json) - adds an element\n" +
			"add_if_min (json) - adds an element if its age is the least for the collection\n" +
			"fill (int) - fills the collection with a certain amount of randomly generated elements \n" +
			"remove (race, name, age)- removes an element by its name\n" +
			"clear - removes all elements from the collection\n" +
			"remove_all (name) - removes all elements with the given name\n" +
			"remove_greater (age) - removes all elements that are older than given age\n" +
			"info - prints out the info on the collection\n" +
			"show - prints out the collection's content\n" +
			"show_mine - shows your elements\n" +
			"help - prints out the information on supported operations\n" +
			"save ~filename~ - saves progress into a given file (same if not given)\n" +
			"anime - unpredictable behaviour\n" +
			"exit - terminates");
	}

	public void remove(String line) {
		try {
			this.list.remove(reader.find(line, this.list));
			System.out.println("Successfully removed.");
		} catch (Exception var4) {
			System.out.println("No such element.");
		}
	}

	public CollectionManager fill(int quantity) {
		CollectionManager list = new CollectionManager();
		quantity = Math.round(Math.abs(quantity));
		if (quantity > 100) {
			System.out.println("You can't add more than 100 elements");
			quantity = 100;
		}
		if (list.list.size() + quantity > 500) {
			System.out.println("You can't have more than a thousand elements");
			quantity = 1000 - list.list.size();
		}
		for (int i = 0; i < quantity; i++) {
			if (Math.random() > 0.5) {
				list.add(new Lunatic());
			} else {
				list.add(new Shorty());
			}
		}
		if (quantity == 1) System.out.println("An element has been added");
		else System.out.println(quantity + " elements have been added.");
		return list;
	}

	public void removeGreater(long age) {
		try {
			list.stream().filter(h -> h.getAge() > age).forEach(list::remove);
			System.out.println("No such elements");
		} catch (Exception var5) {
			System.out.println("All elements that are older than " + age + " years have been removed.");
		}
	}

	public boolean add(String line) {
		try {
			if (line.contains("}")) {
				this.addFromJson(line);
			} else {
				this.list.add(this.reader.readHumanoid(line));
			}
			return true;
		} catch (Exception var3) {
			System.out.println("Broken format. Type in 'rules' to see further detail.");
		}
		return false;
	}

	public boolean add(Humanoid h) {
		list.add(h);
		return true;
	}

	public void rules() {
		System.out.println("Proper values: \nrace: Shorty, Lunatic, Коротышка, Лунатик \nname: any that's not empty \nage: integers from 1 through 99");
	}

	public void info() {
		System.out.println("File: " + this.filePath);
		System.out.println("Type: Humanoid's heirs");
		System.out.println("Size: " + this.list.size());
		System.out.println("Initialization date:" + this.date.toString());
	}

	public void toShow(Humanoid h) {
		System.out.println(list.indexOf(h) + ") " + h.toString());
	}

	public void show() {
		if (this.list.isEmpty()) {
			System.out.println("The collection is empty.");
		} else {
			System.out.println("The collection's contents:");
			list.forEach(this::toShow);
		}
	}

	public void removeAll(String line) {
		try {
			list.stream()
				.filter(h -> h.getName().toLowerCase().equals(line.trim().toLowerCase()))
				.forEach(list::remove);
			System.out.println("No such elements");
		} catch (Exception e) {
			System.out.printf("All %s elements have been removed \n", line);
		}
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public void sort() {
		this.list.sort((o1, o2) -> (int) (o1.getAge() - o2.getAge()));
	}

	public void sortName() {
		this.list.sort(Comparator.comparing(Humanoid::getName));
		System.out.println("Sorted by name");
	}

	public void checkFile(String file) throws IOException {
		if (!(new File(file)).isFile()) {
			System.out.println("Not a file");
			// throw new IOException();
		}

		if (!(new File(file)).canWrite()) {
			System.out.println("Can't be written into");
			//throw new IOException();
		}

		if (!(new File(file)).exists()) {
			System.out.println("Doesn't exist");
			//throw new IOException();
		}
	}

	public void save(String file) {
		try {
			checkFile(file);
			FileWriter output = new FileWriter(file);
			Gson json = new Gson();
			for (Humanoid humanoid : this.list) {
				output.write(json.toJson(humanoid) + "\n");
			}
			output.close();
			System.out.println("Collection has been saved to " + file);
		} catch (IOException var5) {
			System.out.println("File managing error. Collection has not been saved");
		}
	}

	public void addFromJson(String jsonParameters) throws CollectionReader.WrongRaceException {
		this.list.add(new LittleParser().fromJson(jsonParameters));
	}

	public void execute() {
		boolean exit = false;
		this.sort();
		do try {
			System.out.print("Enter your command: ");
			String[] command = (new Scanner(System.in)).nextLine().split(" ", 2);
			String cmd = command[0].trim();
			switch (cmd) {
				case "remove":
					this.remove(command[1]);
					break;
				case "add":
					this.add(command[1]);
					this.sort();
					break;
				case "exit":
					System.out.println("The program terminates here.");
					exit = true;
					break;
				case "fill":
					if (command[1].trim().equals("")) {
						throw new NullPointerException();
					}
					this.fill(Math.abs(Math.round(Float.parseFloat(command[1]))));
					this.sort();
					break;
				case "help":
					this.help();
					break;
				case "info":
					this.info();
					break;
				case "save":
					try {
						if (command[1].trim().isEmpty()) {
							this.save(filePath);
						} else {
							this.save(command[1]);
						}
					} catch (ArrayIndexOutOfBoundsException var7) {
						this.save(filePath);
					}
					break;
				case "show":
					this.show();
					break;
				case "sort":
					this.sort();
					break;
				case "sort_name":
					this.sortName();
					this.show();
					break;
				case "rules":
					this.rules();
					break;
				case "remove_greater":
					if (command[1].trim().equals("")) {
						throw new NullPointerException();
					}
					this.removeGreater((long) Math.abs(Math.round(Float.parseFloat(command[1]))));
					break;
				case "remove_all":
					this.removeAll(command[1]);
					break;
				case "add_if_min":
					//this.addIfMin();
					this.sort();
					break;
				default:
					System.out.println("Such a command doesn't exist.");
					break;
			}
		} catch (NullPointerException | ArrayIndexOutOfBoundsException var8) {
			System.out.println("You forgot to put an argument.");
		} catch (Exception var9) {
			System.out.println("You broke the format.");
		} while (!exit);

	}
}
