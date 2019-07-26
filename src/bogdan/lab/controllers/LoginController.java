package bogdan.lab.controllers;

import bogdan.lab.server.Client;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LoginController {
	@FXML
	private TextField emailTXT;
	@FXML
	private Button signupBUTTON;
	@FXML
	private Button nextBUTTON;
	@FXML
	private Text incorrectSign;
	@FXML
	private Text incorrectTip;

	private static Client client;

	private static Stage stage;

	@FXML
	private Text signupText;

	@FXML
	private void buttonPressed(ActionEvent e) throws IOException {
		String email = emailTXT.getText();
		if (!email.matches(".*@.*\\..*")) {
			incorrectSign.setVisible(true);
		} else {
			Client.setLogin(email);
			if (e.getSource() == signupBUTTON) {
				Client.sendOperation("check");
				signupText.setVisible(true);
			} else {
				PasswordController.setStage(stage);
				changeScene("/bogdan/lab/fxml/password_scene.fxml");
			}
		}
	}

	public static void setStage(Stage _stage){
		stage = _stage;
	}

	public static void setClient(Client client) {
		LoginController.client = client;
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
				new Locale("en" ,"UK")
			)
		);
		Parent root = fxmlLoader.load();
		stage.setScene(new Scene(root, 900, 600));
	}

	@FXML
	private void showTip(ActionEvent e) {
		incorrectTip.setVisible(true);
	}

	@FXML
	private void hideTip(ActionEvent e) {
		incorrectTip.setVisible(false);
	}
}
