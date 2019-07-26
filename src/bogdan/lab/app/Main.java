package bogdan.lab.app;

import bogdan.lab.controllers.LoginController;
import bogdan.lab.server.Client;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
	static Stage primaryStage;
	static String fxml;
	public static  Thread clientThread;
	@Override
	public void start(Stage stage) throws Exception {


		primaryStage = stage;

		clientThread = new Thread(Client::main);
		clientThread.setDaemon(true);
		clientThread.start();


		FXMLLoader fxmlLoader = new FXMLLoader();
		fxmlLoader.setLocation(
			getClass().
				getResource("/bogdan/lab/fxml/login_scene.fxml")
		);

		fxmlLoader.setResources(
			ResourceBundle.getBundle(
				"bogdan.lab.bundles.LanguageBundle",
				new Locale("en","UK")
			)
		);
		Parent root = fxmlLoader.load();
		LoginController.setStage(stage);

		primaryStage.setScene(new Scene(root, 900, 600));
		primaryStage.setTitle(fxmlLoader.getResources().getString("title"));
		primaryStage.getIcons().add(new Image("file:icon.png"));
		primaryStage.setResizable(false);

		primaryStage.show();


	}

	public void changeScene() throws IOException {
		primaryStage.setScene(new Scene(FXMLLoader.load(getClass().getResource(fxml)), 900, 600));
	}

	public static void main(String[] args) {
		launch(args);
	}
}
