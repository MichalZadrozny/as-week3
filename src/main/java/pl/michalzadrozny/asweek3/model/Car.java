package pl.michalzadrozny.asweek3.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.hateoas.RepresentationModel;

@Data
public class Car extends RepresentationModel<Car> {

    private long id;
    private String mark;
    private String model;
    private String color;

    public Car() {
    }

    public Car(int id, String mark, String model, String color) {
        this.id = id;
        this.mark = mark;
        this.model = model;
        this.color = color;
    }
}
