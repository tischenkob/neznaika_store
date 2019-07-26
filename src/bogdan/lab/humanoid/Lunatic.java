package bogdan.lab.humanoid;
import java.io.Serializable;
import java.time.LocalDateTime;

public class Lunatic extends Humanoid implements Serializable {
	{
		race = "Lunatic";
	}

	public Lunatic() {
		super();
	}

	Lunatic(String name) {
		super(name);
	}

	public Lunatic(String name, long age) {
		super(name, age);
	}

	public Lunatic(String name, long age, LocalDateTime birthDate, int posX, int posY) {
		super(name, age, birthDate, posX, posY);
	}

	@Override
	public Humanoid clone() {
		Humanoid humanoid = new Lunatic();
		humanoid.setName(this.name);
		humanoid.setAge(this.getAge());
		humanoid.setPosX(this.getPosX());
		humanoid.setPosY(this.getPosY());
		humanoid.setBirthDate(this.getBirthDate());
		return humanoid;
	}
}

