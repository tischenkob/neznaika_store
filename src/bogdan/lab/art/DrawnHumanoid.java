package bogdan.lab.art;

import bogdan.lab.controllers.CanvasController;
import bogdan.lab.controllers.MainSceneController;
import bogdan.lab.humanoid.Humanoid;
import bogdan.lab.server.Client;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import static javafx.scene.input.MouseEvent.MOUSE_CLICKED;


public class DrawnHumanoid extends Shape {
	Humanoid humanoid;
	public String owner;

	Circle head;
	Circle leftPupil;
	Circle rightPupil;
	Circle leftIris;
	Circle rightIris;
	Polygon nose;
	Polygon hat;
	Path star;
	Rectangle smile;

	Color[] colors = new Color[]{
		Color.VIOLET,
		Color.BLACK,
		Color.RED,
		Color.GREEN,
		Color.YELLOW
	};

	public ObservableList<Shape> getCompleteHeadList() {
		return completeHeadList;
	}

	ObservableList<Shape> completeHeadList;

	Shape completeHead;

	public DrawnHumanoid(Humanoid humanoid, String owner) {
		this.humanoid = humanoid;
		this.owner = owner;
		setAppearance();
	}

	public void setAppearance() {

		hat = new Polygon();
		nose = new Polygon();
		head = new Circle(humanoid.getPosX() / 1000.0 * 800, humanoid.getPosY() / 1000.0 * 500, humanoid.getAge() + 10);
		star = new Path();

		leftIris = new Circle(head.getCenterX() - (6.0 / 14.0) * head.getRadius(), head.getCenterY() - head.getRadius() / 3.0, head.getRadius() / 3.0);
		rightIris = new Circle(head.getCenterX() + (6.0 / 14.0) * head.getRadius(), head.getCenterY() - head.getRadius() / 3.0, head.getRadius() / 3.0);
		leftIris.setFill(Color.WHITE);
		rightIris.setFill(Color.WHITE);

		nose.getPoints().addAll(head.getCenterX(), head.getCenterY() - head.getRadius() * 0.3,
			head.getCenterX() - head.getRadius() / 7.0, head.getCenterY() + head.getRadius() / 3.0,
			head.getCenterX() + head.getRadius() / 7.0, head.getCenterY() + head.getRadius() / 3.0);

		smile = new Rectangle();
		smile.setHeight(head.getRadius() / 6.0);
		smile.setWidth(head.getRadius() / 2.0);
		smile.setX(head.getCenterX() - smile.getWidth() / 2.0);
		smile.setY(head.getCenterY() + smile.getHeight() * 3);
		smile.setFill(Color.WHITE);

		leftPupil = new Circle(leftIris.getCenterX(), leftIris.getCenterY(), leftIris.getRadius() / 3.5);
		rightPupil = new Circle(rightIris.getCenterX(), rightIris.getCenterY(), rightIris.getRadius() / 3.5);
		leftPupil.setFill(Color.BLACK);
		rightPupil.setFill(Color.BLACK);

		double hatBottomY = head.getCenterY() - head.getRadius();

		if (humanoid.getRace().equalsIgnoreCase("shorty")) {
			head.setFill(Color.valueOf("#ffe4a5"));
			nose.setFill(Color.valueOf("#d67f28"));
			star.setStroke(Color.valueOf("#bf0f09"));
			hat.getPoints().addAll(head.getCenterX() + 12, hatBottomY + 3,
				head.getCenterX() + 16, hatBottomY - 40,
				head.getCenterX() - 16, hatBottomY - 40,
				head.getCenterX() - 12, hatBottomY + 3);
		} else {
			head.setFill(Color.valueOf("#94b0dd"));
			nose.setFill(Color.valueOf("#3149c4"));
			star.setStroke(Color.valueOf("#ffffff"));
			hat.getPoints().addAll(head.getCenterX() + 12, hatBottomY + 3,
				head.getCenterX(), hatBottomY - 40,
				head.getCenterX() - 12, hatBottomY + 3);
		}

		hat.setFill(colors[owner.length() % colors.length]);

		MoveTo moveTo = new MoveTo(head.getCenterX() - 5, hatBottomY - 8); // 108, 71
		LineTo line1 = new LineTo(head.getCenterX() + 5, hatBottomY - 8); //321, 161
		LineTo line2 = new LineTo(head.getCenterX() - 2, hatBottomY - 3); //126, 232
		LineTo line3 = new LineTo(head.getCenterX() + 0, hatBottomY - 10); //232, 52
		LineTo line4 = new LineTo(head.getCenterX() + 2, hatBottomY - 3); //269, 250
		LineTo line5 = new LineTo(head.getCenterX() - 5, hatBottomY - 8); //108, 71

//		star.setFill(Color.valueOf("#ffffff"));
		star.setStrokeWidth(2);
		star.getElements().add(moveTo);
		star.getElements().addAll(line1, line2, line3, line4, line5);

		completeHeadList = FXCollections.observableArrayList(
			hat,
			star,
			head,
			leftIris,
			rightIris,
			leftPupil,
			rightPupil,
			nose,
			smile
		);

	}

	public Shape getCompleteHead() {
		Shape leftEye = Shape.union(leftIris, leftPupil);
		Shape rightEye = Shape.union(rightIris, rightPupil);
		Shape eyes = Shape.union(leftEye, rightEye);
		Shape mouthNose = Shape.union(smile, nose);
		Shape facial = Shape.union(mouthNose, eyes);

		Shape completeFace = Shape.union(head, facial);
		Shape completeHat = Shape.union(hat, star);
		completeHead = Shape.union(completeHat, completeFace);
		return completeHead;
	}

	public double get(Shape shape) {
		return Math.sqrt(
			Math.pow(head.getCenterX() - (shape.getLayoutX() - shape.getScaleX() / 2), 2) +
				Math.pow(head.getCenterY() - (shape.getLayoutY() - shape.getScaleY() / 2), 2)
		);
	}

	@Override
	public com.sun.javafx.geom.Shape impl_configShape() {
		return null;
	}

	public void addClickEvent() throws IOException {
		head.addEventFilter(MOUSE_CLICKED, (m) -> {

			for (Humanoid key : CanvasController.mapOfHumanoids.keySet())
				CanvasController.mapOfHumanoids.get(key).head.setEffect(null);

			DropShadow dropShadow = new DropShadow();
			dropShadow.setRadius(15);
			dropShadow.setOffsetX(0);
			dropShadow.setOffsetY(0);
			Color shadowColor = (Client.getLogin().equalsIgnoreCase(this.owner)) ? Color.GREEN : Color.RED;
			dropShadow.setColor(shadowColor);
			head.setEffect(dropShadow);
			CanvasController.selectedHumanoid = this.humanoid;

			CanvasController.mainSceneController.selectHumanoid(this.humanoid);

			if (shadowColor.equals(Color.GREEN)) {
				CanvasController.mainSceneController.enableEdit();
			} else {
				CanvasController.mainSceneController.disableEdit();
			}

			if (MainSceneController.minePressed) MainSceneController.humanoid = this.humanoid;
		});
	}

	public void changeHumanoid(Humanoid new_humanoid) {
		ArrayList<Transition> trans = new ArrayList<>();
		DrawnHumanoid drawnHumanoid = new DrawnHumanoid(new_humanoid, "wat");
		drawnHumanoid.setAppearance();
		int i = 0;
		for (Shape shape : this.getCompleteHeadList()) {
			TranslateTransition transition = new TranslateTransition(Duration.seconds(1), shape);
			transition.setFromX(shape.getLayoutX());
			transition.setToX(drawnHumanoid.getCompleteHeadList().get(i).getLayoutX());
			transition.setFromY(shape.getLayoutY());
			transition.setToY(drawnHumanoid.getCompleteHeadList().get(i).getLayoutY());
			transition.play();
			i++;
		}
		this.humanoid.setName(new_humanoid.getName());
		this.humanoid.setAge(new_humanoid.getAge());
		this.humanoid.setPosX(new_humanoid.getPosX());
		this.humanoid.setPosY(new_humanoid.getPosY());

		setAppearance();
	}

	public List<Transition> addTransition() {
		List<Transition> trans = new ArrayList<>();
		for (Shape shape : completeHeadList) {
			TranslateTransition transition = new TranslateTransition(Duration.seconds(1), shape);
			transition.setFromX(shape.getLayoutX());
			transition.setFromY(900);
			transition.setToX(shape.getLayoutX());
			transition.setToY(shape.getLayoutY());
			trans.add(transition);
		}
		return trans;
	}

	public List<Transition> removeTransition() {
		List<Transition> trans = new ArrayList<>();
		for (Shape shape : completeHeadList) {
			TranslateTransition transition = new TranslateTransition(Duration.seconds(1), shape);
			transition.setFromX(shape.getLayoutX());
			transition.setFromY(shape.getLayoutY());
			transition.setToX(shape.getLayoutX());
			transition.setToY(-900);
			trans.add(transition);
		}
		ScaleTransition scaleTransition = new ScaleTransition(Duration.seconds(0.2), leftPupil);
		ScaleTransition scaleTransition2 = new ScaleTransition(Duration.seconds(0.2), rightPupil);
		scaleTransition.setByX(1.5);
		scaleTransition2.setByX(1.5);
		scaleTransition.setByY(1.5);
		scaleTransition2.setByY(1.5);
		trans.add(scaleTransition);
		trans.add(scaleTransition2);
		return trans;
	}
}
