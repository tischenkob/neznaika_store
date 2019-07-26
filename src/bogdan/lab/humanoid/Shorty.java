package bogdan.lab.humanoid;

import javafx.beans.InvalidationListener;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

public class Shorty extends Humanoid implements Serializable {
    {
        race = "Shorty";
    }
    public Shorty() {
        this.race = "Shorty";
    }

    Shorty(String name) {
        super(name);
    }

    public Shorty(String name, long age) {
        super(name, age);
    }

    public Shorty(String name, long age, LocalDateTime birthDate, int posX, int posY) {
        super(name, age, birthDate, posX, posY);
    }

    @Override
    public Humanoid clone() {
        Humanoid humanoid = new Shorty();
        humanoid.setName(this.name);
        humanoid.setAge(this.getAge());
        humanoid.setPosX(this.getPosX());
        humanoid.setPosY(this.getPosY());
        humanoid.setBirthDate(this.getBirthDate());
        return humanoid;
    }

}
