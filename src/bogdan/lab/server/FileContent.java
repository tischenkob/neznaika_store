package bogdan.lab.server;

import bogdan.lab.collection.CollectionManager;

import java.io.*;
import java.util.Scanner;

public class FileContent implements Serializable {
	String content = "";

	public void addContent(File file) throws FileNotFoundException, IOException {
		file.createNewFile();
		new CollectionManager().checkFile(file.getPath());
		FileReader fileReader = new FileReader(file);
		Scanner fileScanner = new Scanner(fileReader);
		while (fileScanner.hasNextLine()){
			content += fileScanner.nextLine() + "\n";
		}
		fileScanner.close();
		fileReader.close();
	}

	public String getContent() {
		return content;
	}
}
