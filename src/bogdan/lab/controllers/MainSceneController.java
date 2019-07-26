package bogdan.lab.controllers;

import bogdan.lab.art.DrawnHumanoid;
import bogdan.lab.humanoid.Humanoid;
import bogdan.lab.humanoid.Lunatic;
import bogdan.lab.humanoid.Shorty;
import bogdan.lab.server.Client;
import bogdan.lab.server.Operation;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

import static bogdan.lab.database.SecurityTools.objectToBytes;

public class MainSceneController implements Initializable {
	private static Stage stage;

	// Tools
	@FXML
	private Button addTool;
	@FXML
	private Button importTool;
	@FXML
	private Button mineTool;
	@FXML
	private Button infoTool;
	@FXML
	private Button removeTool;
	@FXML
	private Button clearTool;
	@FXML
	private Button optionsTool;
	@FXML
	private Button logoutTool;

	// Cross buttons
	@FXML
	private Button closeAdd;
	@FXML
	private Button closeImport;
	@FXML
	private Button closeInfo;
	@FXML
	private Button closeClear;
	@FXML
	private Button closeOptions;
	@FXML
	private Button closeRemove;

	//Tool windows
	@FXML
	private AnchorPane addWindow;
	@FXML
	private AnchorPane clearWindow;
	@FXML
	private AnchorPane infoWindow;
	@FXML
	private AnchorPane importWindow;
	@FXML
	private AnchorPane optionsWindow;
	@FXML
	private AnchorPane interruptText;
	@FXML
	private AnchorPane removeWindow;

	public static boolean minePressed = false;

	//main methods
	@FXML
	private void showWindow(ActionEvent e) throws IOException, InterruptedException {
		disableTools();
		if (!interruptText.isVisible()) {
			if (e.getSource() == addTool) {
				addTool.getStyleClass().remove("tool_button");
				addTool.getStyleClass().add("toggled_button");
				addWindow.setVisible(true);
			}
			if (e.getSource() == infoTool) {
				updateAmounts();
				infoWindow.setVisible(true);
				infoTool.getStyleClass().remove("tool_button");
				infoTool.getStyleClass().add("toggled_button");
			}
			if (e.getSource() == importTool) {
				importWindow.setVisible(true);
				importTool.getStyleClass().remove("tool_button");
				importTool.getStyleClass().add("toggled_button");
			}
			if (e.getSource() == clearTool) {
				clearWindow.setVisible(true);
				clearTool.getStyleClass().remove("tool_button");
				clearTool.getStyleClass().add("toggled_button");
			}
			if (e.getSource() == optionsTool) {
				optionsWindow.setVisible(true);
				optionsTool.getStyleClass().remove("tool_button");
				optionsTool.getStyleClass().add("toggled_button");
			}
			if (e.getSource() == removeTool) {

				removeWindow.setVisible(true);
				removeTool.getStyleClass().remove("tool_button");
				removeTool.getStyleClass().add("toggled_button");
			}
			if (e.getSource() == filterButton) {

				filterWindow.setVisible(true);
			}
		}
	}

	@FXML
	public void selectHumanoid() throws NullPointerException {
		try {
			humanoid = myTable.getSelectionModel().getSelectedItem();
			nameField1.setText(humanoid.getName());
			ageText1.setText(String.valueOf(humanoid.getAge()));
			xField.setText(String.valueOf(humanoid.getPosX()));
			yField.setText(String.valueOf(humanoid.getPosY()));
		} catch (Exception a) {
		}
	}

	public void selectHumanoid(Humanoid humanoid) throws NullPointerException {
		try {
			nameField1.setText(humanoid.getName());
			ageText1.setText(String.valueOf(humanoid.getAge()));
			xField.setText(String.valueOf(humanoid.getPosX()));
			yField.setText(String.valueOf(humanoid.getPosY()));
		} catch (Exception a) {
		}
	}

	@FXML
	private void closeWindow(ActionEvent e) {
		if (e.getSource() == closeAdd) {
			addTool.getStyleClass().remove("toggled_button");
			addTool.getStyleClass().add("tool_button");
			addWindow.setVisible(false);
		}
		if (e.getSource() == closeInfo) {
			infoWindow.setVisible(false);
			infoTool.getStyleClass().remove("toggled_button");
			infoTool.getStyleClass().add("tool_button");
		}
		if (e.getSource() == closeImport) {
			importWindow.setVisible(false);
			importTool.getStyleClass().remove("toggled_button");
			importTool.getStyleClass().add("tool_button");
		}
		if (e.getSource() == closeClear) {
			clearWindow.setVisible(false);
			clearTool.getStyleClass().remove("toggled_button");
			clearTool.getStyleClass().add("tool_button");
		}
		if (e.getSource() == closeOptions) {
			optionsWindow.setVisible(false);
			optionsTool.getStyleClass().remove("toggled_button");
			optionsTool.getStyleClass().add("tool_button");
		}
		if (e.getSource() == closeRemove) {
			removeWindow.setVisible(false);
			removeTool.getStyleClass().remove("toggled_button");
			removeTool.getStyleClass().add("tool_button");
		}
		if (e.getSource() == closeFilter) {
			filterWindow.setVisible(false);
		}
		enableTools();
	}

	private void disableTools() throws IOException, InterruptedException {
		addTool.setDisable(true);
		clearTool.setDisable(true);
		infoTool.setDisable(true);
		importTool.setDisable(true);
		removeTool.setDisable(true);
		optionsTool.setDisable(true);
		logoutTool.setDisable(true);
		mineTool.setDisable(true);
		if (Client.interruptWork) {
			Client.interruptWork = false;
			interruptText.setVisible(true);
			Thread.sleep(5000);
			interruptText.setVisible(false);
			if (!Client.connected)
				backToLogin();
		}
	}

	private void enableTools() {
		addTool.setDisable(false);
		clearTool.setDisable(false);
		infoTool.setDisable(false);
		importTool.setDisable(false);
		if (myTable.isVisible()) removeTool.setDisable(false);
		optionsTool.setDisable(false);
		logoutTool.setDisable(false);
		mineTool.setDisable(false);
	}

	static void setStage(Stage _stage) {
		stage = _stage;
	}

	public void changeScene(String fxml) throws IOException {
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(
			getClass().
				getResource(fxml)
		);
		fxmlLoader.setResources(
			ResourceBundle.getBundle(
				"bogdan.lab.bundles.LanguageBundle",
				new Locale("en_UK")
			)
		);
		Parent root = fxmlLoader.load();

		stage.setScene(new Scene(root, 900, 600));
	}

	@FXML
	private void backToLogin() throws IOException {
		Client.setLogin("");
		Client.setPassword("");
		Client.myList.removeAll(Client.getMyList());
		changeScene("/bogdan/lab/fxml/login_scene.fxml");
	}

	// Add window methods
	@FXML
	private ChoiceBox<String> raceBox;
	@FXML
	private TextField nameField;
	@FXML
	private Text ageText;
	@FXML
	private Button minusButton;
	@FXML
	private Button bigMinusButton;
	@FXML
	private Button plusButton;
	@FXML
	private Button bigPlusButton;
	@FXML
	private Button addButton;
	@FXML
	private RadioButton minRadioButton;
	@FXML
	ChoiceBox fillBox;

	@FXML
	private void changeAge(ActionEvent e) {
		int age = Integer.parseInt(ageText.getText());
		if (e.getSource() == bigMinusButton) {
			if (age > 10) age -= 10;
			else age = 1;
		}
		if (e.getSource() == minusButton) {
			if (age > 1) age--;
			else age = 1;
		}
		if (e.getSource() == plusButton) {
			if (age < 99) age++;
			else age = 99;
		}
		if (e.getSource() == bigPlusButton) {
			if (age < 90) age += 10;
			else age = 99;
		}
		ageText.setText(String.valueOf(age));
	}

	@FXML
	private void enableAdd(KeyEvent e) {
		if (nameField.getText().isEmpty()) addButton.setDisable(true);
		else addButton.setDisable(false);
	}

	@FXML
	private void addPressed(ActionEvent e) throws IOException {
		String race = raceBox.getValue();
		String name = nameField.getText();
		long age = Integer.parseInt(ageText.getText());
		nameField.setText("");
		ageText.setText("50");
		Humanoid humanoid =
			(race.equalsIgnoreCase("lunatic"))
				? new Lunatic(name, age)
				: new Shorty(name, age);
		long amount = Client.getMyList().stream().filter(h -> h.getAge() < age).count();
		if (!minRadioButton.isSelected() || amount != 0) {
			Operation operation = new Operation(Client.getLogin(), Client.getPassword());
			operation.setOperation("add");
			ArrayList<Humanoid> list = new ArrayList<>();
			list.add(humanoid);
			CanvasController.mapOfHumanoids.put(humanoid, new DrawnHumanoid(humanoid, Client.getLogin()));
			operation.setObject(objectToBytes(list));
			Client.getMyList().add(humanoid);
			Client.getBigList().add(humanoid);
			Client.sendOperation(operation);
		}
	}

	@FXML
	private void fillPressed(ActionEvent e) throws IOException {
		Operation operation = Client.getOperation();
		operation.setOperation("add");
		Integer amount = (Integer) fillBox.getValue();
		ArrayList<Humanoid> list = new ArrayList<>();
		for (int i = 0; i < amount; i++) {
			Humanoid humanoid = Math.random() < 0.5 ? new Lunatic() : new Shorty();
			list.add(humanoid);
			CanvasController.mapOfHumanoids.put(humanoid, new DrawnHumanoid(humanoid, Client.getLogin()));
		}
		Client.getMyList().addAll(list);
		Client.getBigList().addAll(list);
		operation.setObject(objectToBytes(list));
		Client.sendOperation(operation);
	}

	// Import window methods

	@FXML
	private Button importButton;
	@FXML
	private Button browseButton;
	@FXML
	private TextField importField;

	@FXML
	private void importPressed() throws IOException {
		String filePath = importField.getText();
		Client.operation("import " + filePath);
	}

	@FXML
	private void browsePressed() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON", "*.json"));
		File file = fileChooser.showOpenDialog(stage);
		try {
			String filePath = file.getAbsolutePath();
			importField.setText(filePath);
		} catch (Exception ignored) {
		}
	}

	// Edit window methods

	@FXML
	private TextField xField;
	@FXML
	private TextField yField;
	@FXML
	private RadioButton sameRadio;
	@FXML
	private TextField nameField1;
	@FXML
	private Text ageText1;
	@FXML
	private Button minusButton1;
	@FXML
	private Button bigMinusButton1;
	@FXML
	private Button plusButton1;
	@FXML
	private Button bigPlusButton1;

	public void deletePressed(ActionEvent actionEvent) throws InterruptedException, IOException, NullPointerException {
		Operation operation = Client.getOperation();
		if (!sameRadio.isSelected()) {

			operation.setOperation("remove");
			operation.setArgument(humanoid.getRace() + " " + humanoid.getName() + " " + humanoid.getAge());

			Client.getBigList().remove(humanoid);
			Client.getMyList().remove(humanoid);
			CanvasController.mapOfHumanoids.remove(humanoid);
			operation.setObject(objectToBytes(humanoid));
		} else {
			operation.setOperation("remove_all");
			operation.setArgument(humanoid.getName());

			ObservableList<Humanoid> list = Client.getMyList();
			ArrayList<Humanoid> removeList = new ArrayList<>();

			list.stream().filter(h -> h.getName().contains(humanoid.getName())).forEach(removeList::add);
			System.out.println(removeList);
			list.removeAll(removeList);
			Client.bigList.removeAll(removeList);
			for (Humanoid humanoid : removeList) {
				CanvasController.mapOfHumanoids.remove(humanoid);
			}
			operation.setObject(objectToBytes(removeList));
		}
		Client.sendOperation(operation);
	}

	@FXML
	private void updateHumanoid(ActionEvent actionEvent) throws IOException, InterruptedException {

		Operation operation = new Operation(Client.getLogin(), Client.getPassword());

		Humanoid new_humanoid = humanoid.clone();
		operation.setOperation("update");
		operation.setObject(objectToBytes(humanoid));
		String user = CanvasController.mapOfHumanoids.get(humanoid).owner;
		Client.getMyList().remove(humanoid);
		Client.getBigList().remove(humanoid);
		CanvasController.mapOfHumanoids.remove(humanoid);
		for (Humanoid newHumanoid : Arrays.asList(humanoid, new_humanoid)) {
			newHumanoid.setName(nameField1.getText());
			newHumanoid.setAge(Integer.parseInt(ageText1.getText()));
			newHumanoid.setPosX(correctPos(xField.getText()));
			newHumanoid.setPosY(correctPos(yField.getText()));
		}
		Client.getMyList().add(new_humanoid);
		Client.getBigList().add(new_humanoid);
		CanvasController.mapOfHumanoids.put(new_humanoid, new DrawnHumanoid(new_humanoid, user));
		operation.setSecondObject(objectToBytes(new_humanoid));

		Client.sendOperation(operation);

		updateTables();
	}

	@FXML
	private Button deleteButton;
	@FXML
	private Button saveButton;

	public void enableEdit() {
		deleteButton.setDisable(false);
		saveButton.setDisable(false);
	}

	public void disableEdit() {
		deleteButton.setDisable(true);
		saveButton.setDisable(true);
	}

	public int correctPos(String pos) {
		try {
			int position = Integer.parseInt(pos);
			position = Math.abs(position);
			if (position > 999) position = 999;
			return position;
		} catch (Exception e) {
		}
		return (int) Math.random() * 1000;
	}

	@FXML
	private void changeAge1(ActionEvent e) {
		int age = Integer.parseInt(ageText1.getText());
		if (e.getSource() == bigMinusButton1) {
			if (age > 10) age -= 10;
			else age = 1;
		}
		if (e.getSource() == minusButton1) {
			if (age > 1) age--;
			else age = 1;
		}
		if (e.getSource() == plusButton1) {
			if (age < 99) age++;
			else age = 99;
		}
		if (e.getSource() == bigPlusButton1) {
			if (age < 90) age += 10;
			else age = 99;
		}
		ageText1.setText(String.valueOf(age));
	}

	// Clear window methods
	@FXML
	private ChoiceBox olderBox;

	@FXML
	private void deleteOlder(ActionEvent e) throws IOException, InterruptedException {
		List<Humanoid> list = new ArrayList<>();

		Client.getMyList().stream().filter(h -> h.getAge() > (Integer) olderBox.getValue()).forEach(list::add);
		for (Humanoid key : list) {
			CanvasController.mapOfHumanoids.remove(key);
		}
		Client.getMyList().removeAll(list);
		Client.getBigList().removeAll(list);

		Operation operation = Client.getOperation();
		operation.setOperation("remove_greater");
		operation.setArgument(String.valueOf(olderBox.getValue()));
		operation.setObject(objectToBytes(list));

		Client.sendOperation(operation);
	}

	@FXML
	private void clear(ActionEvent e) throws IOException, InterruptedException {
		for (Humanoid key : Client.getMyList()) {
			CanvasController.mapOfHumanoids.remove(key);
		}
		Operation operation = Client.getOperation();
		operation.setOperation("remove");
		Client.getBigList().removeAll(Client.getMyList());
		Client.getMyList().removeAll(Client.getMyList());
		Client.sendOperation("clear");
	}

	//Mine thing
	@FXML
	private void toggleMine(ActionEvent e) {
		if (!myTable.isVisible()) {
			myTable.setVisible(true);
			mineTool.getStyleClass().remove("darkstar");
			mineTool.getStyleClass().add("whitestar");
			minePressed = true;
			bigTable.setVisible(false);
			removeTool.setDisable(false);
		} else {
			myTable.setVisible(false);
			mineTool.getStyleClass().remove("whitestar");
			mineTool.getStyleClass().add("darkstar");
			minePressed = false;
			bigTable.setVisible(true);
			removeTool.setDisable(true);
		}
	}

	// Info window methods
	@FXML
	private Text yourEmailText;
	@FXML
	private Text myAmount;
	@FXML
	private Text totalAmount;

	// Options window methods
	@FXML
	private ChoiceBox languageBox;

	@FXML
	private void changeLanguage(ActionEvent e) throws IOException {

		String lang = "";
		String country = "";

		if (languageBox.getValue() == languageBox.getItems().get(0)) {
			lang = "en";
			country = "UK";
		}
		if (languageBox.getValue() == languageBox.getItems().get(1)) {
			lang = "et";
			country = "EE";
		}
		if (languageBox.getValue() == languageBox.getItems().get(2)) {
			lang = "bg";
			country = "BG";
		}
		if (languageBox.getValue() == languageBox.getItems().get(3)) {
			lang = "ru";
			country = "RU";
		}
		languageBox.setValue(languageBox.getValue());
		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(
			getClass().
				getResource("/bogdan/lab/fxml/main_scene.fxml")
		);
		fxmlLoader.setResources(
			ResourceBundle.getBundle(
				"bogdan.lab.bundles.LanguageBundle",
				new Locale(lang, country)
			)
		);
		if (lang.equals("en"))
			for (Humanoid humanoid : Client.getBigList())
				humanoid.setBirthDate(humanoid.getBirthDate().minusHours(2));
		else if (language.equals("en"))
			for (Humanoid humanoid : Client.getBigList())
				humanoid.setBirthDate(humanoid.getBirthDate().plusHours(2));
		language = lang;
		Parent root = fxmlLoader.load();
		stage.setScene(new Scene(root, 900, 600));
	}


	/*
	VERY IMPORTANT STUFF
	 */
	public static Humanoid humanoid;
	@FXML
	private TableView<Humanoid> bigTable;
	@FXML
	private TableColumn<Humanoid, String> raceColumn, nameColumn, birthDateColumn;
	@FXML
	private TableColumn<Humanoid, Integer> ageColumn, xColumn, yColumn;

	@FXML
	private TableView<Humanoid> myTable;
	@FXML
	private TableColumn<Humanoid, String> myRaceColumn, myNameColumn, myBirthDateColumn;
	@FXML
	private TableColumn<Humanoid, Integer> myAgeColumn, myXColumn, myYColumn;
	ResourceBundle resourceBundle;
	String language = "en";
	@FXML
	private TextField filterField;

	@FXML
	private ChoiceBox filterBox;


	// FILTER
	@FXML
	private TextField filterRace;
	@FXML
	private TextField filterName;
	@FXML
	private TextField filterAge;
	@FXML
	private TextField filterBirth;
	@FXML
	private TextField filterX;
	@FXML
	private TextField filterY;
	@FXML
	private AnchorPane filterWindow;
	@FXML
	private Button filterButton;
	@FXML
	private Button closeFilter;
	// / FILTER

	@FXML
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		Locale locale = new Locale("en", "UK");
		Locale.setDefault(locale);
		resourceBundle = resources;
		removeTool.setDisable(true);
		raceBox.getItems().addAll(resourceBundle.getString("shorty"), resourceBundle.getString("lunatic"));
		raceBox.setValue(resourceBundle.getString("shorty"));

		fillBox.getItems().addAll(1, 5, 10, 25, 50);
		fillBox.setValue(1);

		olderBox.getItems().addAll(10, 20, 30, 40, 50, 60, 70, 80, 90);
		olderBox.setValue(50);

		filterBox.getItems().addAll(
			resourceBundle.getString("race"),
			resourceBundle.getString("name"),
			resourceBundle.getString("age"),
			resourceBundle.getString("birthdate"),
			"x",
			"y"
		);
		filterBox.setValue(filterBox.getItems().get(0));

		yourEmailText.setText(Client.getLogin());

		languageBox.getItems().addAll(
			resourceBundle.getString("english"),
			resourceBundle.getString("estonian"),
			resourceBundle.getString("bulgarian"),
			resourceBundle.getString("russian")
		);
		languageBox.setValue(languageBox.getItems().get(0));

		fillTables();
		// 1. Wrap the ObservableList in a FilteredList (initially display all data).
		FilteredList<Humanoid> filteredData = new FilteredList<>(Client.getBigList(), p -> true);
		FilteredList<Humanoid> filteredMyData = new FilteredList<>(Client.getMyList(), p -> true);

		// 2. Set the filter Predicate whenever the filter changes.

		filterRace.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredData.setPredicate(person -> {
				if (person.getRace().contains(filterRace.getText())) {
					return true;
				}
				return false;
			});
		}));
		filterName.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredData.setPredicate(person -> {
				if (person.getName().contains(filterName.getText())) {
					return true;
				}
				return false;
			});
		}));
		filterAge.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredData.setPredicate(person -> {
				try {
					if (person.getAge() == Integer.parseInt(filterAge.getText())) {
						return true;
					}
				} catch (Exception i) {}
				return false;
			});
		}));
		filterBirth.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredData.setPredicate(person -> {
				if (person.getBirthDate().toString().contains(filterBirth.getText())) {
					return true;
				}
				return false;
			});
		}));
		filterX.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredData.setPredicate(person -> {
				try {
					if (person.getPosX() == Integer.parseInt(filterX.getText())) {
						return true;
					}
				} catch (Exception i) {}
				return false;
			});
		}));
		filterY.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredData.setPredicate(person -> {
				try {
					if (person.getPosY() == Integer.parseInt(filterY.getText())) {
						return true;
					}
				} catch (Exception i) {}
				return false;
			});
		}));


		filterRace.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredMyData.setPredicate(person -> {
				if (person.getRace().contains(filterRace.getText())) {
					return true;
				}
				return false;
			});
		}));
		filterName.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredMyData.setPredicate(person -> {
				if (person.getName().contains(filterName.getText())) {
					return true;
				}
				return false;
			});
		}));
		filterAge.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredMyData.setPredicate(person -> {
				try {
					if (person.getAge() == Integer.parseInt(filterAge.getText())) {
						return true;
					}
				} catch (Exception i) {}
				return false;
			});
		}));
		filterBirth.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredMyData.setPredicate(person -> {
				if (person.getBirthDate().toString().contains(filterBirth.getText())) {
					return true;
				}
				return false;
			});
		}));
		filterX.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredMyData.setPredicate(person -> {
				try {
					if (person.getPosX() == Integer.parseInt(filterX.getText())) {
						return true;
					}
				} catch (Exception i) {}
				return false;
			});
		}));
		filterY.textProperty().addListener(((observable, oldValue, newValue) -> {
			filteredMyData.setPredicate(person -> {
				try {
					if (person.getPosY() == Integer.parseInt(filterY.getText())) {
						return true;
					}
				} catch (Exception i) {}
				return false;
			});
		}));


		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredData.setPredicate(person -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}
				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				int ageValue;
				try {
					ageValue = Integer.parseInt(newValue);
					if (ageValue == person.getAge() && filterBox.getValue() == filterBox.getItems().get(2) && !filterWindow.isVisible()) {
						return true;
					}
					if (ageValue == person.getPosX() && filterBox.getValue() == filterBox.getItems().get(4) && !filterWindow.isVisible()) {
						return true;
					}
					if (ageValue == person.getPosY() && filterBox.getValue() == filterBox.getItems().get(5) && !filterWindow.isVisible()) {
						return true;
					}
					if (person.getBirthDate().toString().contains(lowerCaseFilter) && filterBox.getValue() == filterBox.getItems().get(3) && !filterWindow.isVisible()) {
						return true;
					}
				} catch (NumberFormatException e) {
					if (person.getRace().toLowerCase().contains(lowerCaseFilter) && filterBox.getValue() == filterBox.getItems().get(0) && !filterWindow.isVisible()) {
						return true; // Filter matches first name.
					} else if (person.getName().toLowerCase().contains(lowerCaseFilter) && filterBox.getValue() == filterBox.getItems().get(1) && !filterWindow.isVisible()) {
						return true; // Filter matches last name.
					} else if (person.getBirthDate().toString().contains(lowerCaseFilter) && filterBox.getValue() == filterBox.getItems().get(3) && !filterWindow.isVisible()) {
						return true;
					}
				}

				return false; // Does not match.
			});
		});
		filterField.textProperty().addListener((observable, oldValue, newValue) -> {
			filteredMyData.setPredicate(person -> {
				// If filter text is empty, display all persons.
				if (newValue == null || newValue.isEmpty()) {
					return true;
				}

				// Compare first name and last name of every person with filter text.
				String lowerCaseFilter = newValue.toLowerCase();
				int number;

				try {
					number = Integer.parseInt(newValue);
					if (number == person.getAge() && filterBox.getValue() == filterBox.getItems().get(2) && !filterWindow.isVisible()) {
						return true;
					} else if (number == person.getPosX() && filterBox.getValue() == filterBox.getItems().get(4) && !filterWindow.isVisible()) {
						return true;
					} else if (number == person.getPosY() && filterBox.getValue() == filterBox.getItems().get(5) && !filterWindow.isVisible()) {
						return true;
					} else if (person.getBirthDate().toString().contains(lowerCaseFilter) && filterBox.getValue() == filterBox.getItems().get(3) && !filterWindow.isVisible()) {
						return true;
					}
				} catch (NumberFormatException e) {
					if (person.getRace().toLowerCase().contains(lowerCaseFilter) && filterBox.getValue() == filterBox.getItems().get(0) && !filterWindow.isVisible()) {
						return true; // Filter matches first name.
					} else if (person.getName().toLowerCase().contains(lowerCaseFilter) && filterBox.getValue() == filterBox.getItems().get(1) && !filterWindow.isVisible()) {
						return true; // Filter matches last name.
					} else if (person.getBirthDate().toString().contains(lowerCaseFilter) && filterBox.getValue() == filterBox.getItems().get(3) && !filterWindow.isVisible()) {
						return true;
					}
				}

				return false; // Does not match.
			});
		});
		// 3. Wrap the FilteredList in a SortedList.
		SortedList<Humanoid> sortedData = new SortedList<>(filteredData);
		SortedList<Humanoid> sortedMyData = new SortedList<>(filteredMyData);

		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedData.comparatorProperty().bind(bigTable.comparatorProperty());
		sortedMyData.comparatorProperty().bind(myTable.comparatorProperty());
		bigTable.setItems(sortedData);
		myTable.setItems(sortedMyData);
		//updateTables();
	}

	public void fillTables() {
		// устанавливаем тип и значение которое должно храниться в колонке
		raceColumn.setCellValueFactory(new PropertyValueFactory<>("race"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		ageColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
		birthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		xColumn.setCellValueFactory(new PropertyValueFactory<>("posX"));
		yColumn.setCellValueFactory(new PropertyValueFactory<>("posY"));


		myRaceColumn.setCellValueFactory(new PropertyValueFactory<>("race"));
		myNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
		myAgeColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
		myBirthDateColumn.setCellValueFactory(new PropertyValueFactory<>("birthDate"));
		myXColumn.setCellValueFactory(new PropertyValueFactory<>("posX"));
		myYColumn.setCellValueFactory(new PropertyValueFactory<>("posY"));

	}

	public void updateAmounts() {
		myAmount.setText(String.valueOf(Client.getMyList().size()));
		totalAmount.setText(String.valueOf(Client.getBigList().size()));
	}

	public void updateTables() {
		bigTable.setItems(Client.getBigList());
		myTable.setItems(Client.getMyList());
	}
}
