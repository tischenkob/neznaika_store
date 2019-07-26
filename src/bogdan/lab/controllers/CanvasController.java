package bogdan.lab.controllers;

import bogdan.lab.art.DrawnHumanoid;
import bogdan.lab.humanoid.Humanoid;
import bogdan.lab.server.Client;
import javafx.animation.Transition;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Shape;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CanvasController implements Initializable {
	@FXML
	private Canvas canvas;
	@FXML
	private AnchorPane anchorPane;
	public static Humanoid selectedHumanoid;
	public static ObservableMap<Humanoid, DrawnHumanoid> mapOfHumanoids = FXCollections.observableHashMap();
	public static String humanoidOwner;
	public static MainSceneController mainSceneController;
	public static FXMLLoader loader;
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		MapChangeListener listener = change -> {
			if (change.wasAdded()) {
				drawHumanoid((DrawnHumanoid) change.getValueAdded());
				try {
					((DrawnHumanoid) change.getValueAdded()).addClickEvent();
				} catch (IOException e) {
					e.printStackTrace();
				}
				for (Transition transition : ((DrawnHumanoid) change.getValueAdded()).addTransition())
					transition.play();
			}
			if (change.wasRemoved()){
				for (Transition transition : ((DrawnHumanoid) change.getValueRemoved()).removeTransition())
					transition.play();

			}
		};
		mapOfHumanoids.addListener(listener);
		for (String key : Client.map.keySet()) {
			List<Humanoid> list = Client.map.get(key);
			for (Humanoid humanoid : list) {
				DrawnHumanoid drawnHumanoid = new DrawnHumanoid(humanoid, key);
				try {
					drawnHumanoid.addClickEvent();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mapOfHumanoids.put(humanoid, drawnHumanoid);
			}
		}
	}

	public void redraw() {
		anchorPane.getChildren().removeAll(anchorPane.getChildren());
		for (Humanoid key : mapOfHumanoids.keySet()) {
			for (Shape shape : mapOfHumanoids.get(key).getCompleteHeadList())
				anchorPane.getChildren().add(shape);
		}
	}

	public void drawHumanoid(Humanoid humanoid, String owner) {
		DrawnHumanoid drawnHumanoid = new DrawnHumanoid(humanoid, owner);
		mapOfHumanoids.put(humanoid, drawnHumanoid);
		for (Shape shape : drawnHumanoid.getCompleteHeadList())
			anchorPane.getChildren().add(shape);
	}

	public void drawHumanoid(DrawnHumanoid drawnHumanoid) {
		for (Shape shape : drawnHumanoid.getCompleteHeadList())
			anchorPane.getChildren().add(shape);
	}

	public void eraseHumanoid(Humanoid humanoid) {
		DrawnHumanoid drawnHumanoid = mapOfHumanoids.get(humanoid);
		for (Shape shape : drawnHumanoid.getCompleteHeadList())
			anchorPane.getChildren().remove(shape);
	}

	public void eraseHumanoid(DrawnHumanoid drawnHumanoid) {
		for (Shape shape : drawnHumanoid.getCompleteHeadList())
			anchorPane.getChildren().remove(shape);
	}

	public void updateHumanoid(Humanoid oldValue, Humanoid newValue) {
		eraseHumanoid(oldValue);
		drawHumanoid(newValue, mapOfHumanoids.get(oldValue).owner);
	}

	@FXML
	public void removeSelected(){

	}
	@FXML
	public void closeWindow(){

	}
}
