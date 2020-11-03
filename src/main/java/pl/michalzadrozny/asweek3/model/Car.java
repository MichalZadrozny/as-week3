package pl.michalzadrozny.asweek3.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.hateoas.RepresentationModel;

@Data
@AllArgsConstructor
public class Car extends RepresentationModel<Car> {

    private long id;
    private String mark;
    private String model;
    private String color;

    public void modCar(Car car) {
        this.mark = car.mark;
        this.model = car.model;
        this.color = car.color;
    }


}
