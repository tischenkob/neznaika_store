package bogdan.lab.controllers;

import bogdan.lab.server.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class ResetController {
	@FXML
	private PasswordField passwordTXT;
	@FXML
	private PasswordField tokenTXT;
	@FXML
	private Text wrongText;

	private static Stage stage;

	static void setStage(Stage _stage){
		stage = _stage;
	}

	@FXML
	private void backToPassword() throws IOException {
		changeScene("/bogdan/lab/fxml/password_scene.fxml");
	}

	public void changeScene(String fxml) throws IOException {
		stage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml)), 1000, 600));
	}

	@FXML
	private void changePassword(ActionEvent e) throws IOException, InterruptedException {
		Client.operation(String.format("reset_password %s %s", tokenTXT.getText(), passwordTXT.getText()));
		Thread.sleep(500);
		Client.operation("try_password " + passwordTXT.getText());
		Thread.sleep(750);
		if (Client.loggedIn) {
			MainSceneController.setStage(stage);
			changeScene("/bogdan/lab/fxml/main_scene.fxml");
		} else wrongText.setVisible(true);
	}
}
