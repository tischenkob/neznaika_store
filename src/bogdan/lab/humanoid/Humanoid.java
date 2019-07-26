package bogdan.lab.humanoid;

import javafx.collections.ObservableArray;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public abstract class Humanoid implements Serializable {
	protected String race = "none";
	protected String name;
	private long age;
	private int posX;
	private int posY;
	private LocalDateTime birthDate;

	public LocalDateTime getBirthDate() {
		return birthDate;
	}

	{
		age = 5 + Math.round(Math.random() * 85);
		birthDate = birthDate.now();
		posX = (int) Math.round(Math.random() * 1000);
		posY = (int) Math.round(Math.random() * 1000);
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	Humanoid() {
		this.name = randomName();
	}

	Humanoid(String name) {
		this.name = name;
	}

	Humanoid(String name, long age) {
		this.name = name;
		this.age = age;
	}

	Humanoid(String name, long age, LocalDateTime birthDate, int posX, int posY) {
		this.name = name;
		this.age = age;
		this.birthDate = birthDate;
		this.posX = posX;
		this.posY = posY;
	}

	private static String randomName() {
		String[] name = {"Nicolas", "Marie", "Nilson", "Devon", "Holden", "Carl", "Kevin", "Lolita", "Julie", "Kari", "Bogdan", "Ivan", "Alexander", "Marcus", "Johnathan", "Koyomi", "Porfiriy", "Julie", "Liese", "Gabriel", "Dio", "Izuku", "Eren"};

		Humanoid.RandomMath number = (n) -> (int) Math.floor(Math.random() * n);
		return name[number.getRandNumber(name.length)];
	}

	public String getName() {
		return this.name;
	}

	public String getRace() {
		return this.race;
	}

	public int getAge() {
		return (int) this.age;
	}

	public int hashCode() {
		return Objects.hash(this.race, this.name);
	}

	public boolean equals(Humanoid obj) {
		return obj != null && this.hashCode() == obj.hashCode();
	}

	public String toString() {
		return this.getRace() + " " + this.getName() + " " + this.getAge() + " " + this.birthDate + " " + "X:" + this.posX + " " + "Y:" + this.posY;
	}

	@FunctionalInterface
	interface RandomMath {
		int getRandNumber(int var1);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAge(long age) {
		this.age = age;
	}

	public void setPosX(int posX) {
		this.posX = posX;
	}

	public void setPosY(int posY) {
		this.posY = posY;
	}

	public void setBirthDate(LocalDateTime birthDate) {
		this.birthDate = birthDate;
	}

	@Override
	public abstract Humanoid clone();
}