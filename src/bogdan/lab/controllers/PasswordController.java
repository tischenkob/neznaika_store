package bogdan.lab.controllers;

import bogdan.lab.server.Client;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class PasswordController {
	@FXML
	private PasswordField passwordTXT;
	@FXML
	private Button forgotButton;
	@FXML
	private Button loginButton;
	@FXML
	private Text wrongText;


	private static Stage stage;

	static void setStage(Stage _stage) {
		stage = _stage;
	}

	@FXML
	private void connectClient() throws InterruptedException, IOException {
		Client.setPassword(passwordTXT.getText());
		Client.connect("1080");
		Thread.sleep(500);
		if (Client.loggedIn) {
			MainSceneController.setStage(stage);
			try {
				Client.myList = FXCollections.observableArrayList(Client.map.get(Client.getLogin()));
			} catch (Exception e) {
			}
			changeScene("/bogdan/lab/fxml/main_scene.fxml");
		} else wrongText.setVisible(true);
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
		CanvasController.mainSceneController = fxmlLoader.getController();
		stage.setScene(new Scene(root, 900, 600));
	}

	@FXML
	private void backToLogin() throws IOException {
		changeScene("/bogdan/lab/fxml/login_scene.fxml");
	}

	public void askToken(ActionEvent actionEvent) throws IOException {
		Client.sendOperation("reset_password");
		ResetController.setStage(stage);
		changeScene("/bogdan/lab/fxml/reset_scene.fxml");
	}
}
