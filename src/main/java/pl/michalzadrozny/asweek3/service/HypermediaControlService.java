package pl.michalzadrozny.asweek3.service;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Service;
import pl.michalzadrozny.asweek3.controller.CarController;
import pl.michalzadrozny.asweek3.model.Car;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Service
public class HypermediaControlService {

    public void addLink(Car car) {
        car.addIf(!car.hasLinks(), () -> linkTo(CarController.class).slash(car.getId()).withSelfRel());
    }

    public CollectionModel<Car> addMultipleLinksForColors(String color, List<Car> carList) {
        carList.forEach(this::addLink);

        Link link = linkTo(CarController.class).slash("color").slash(color).withSelfRel();
        return new CollectionModel<>(carList, link);
    }


    public CollectionModel<Car> addMultipleLinks(List<Car> carList) {
        carList.forEach(this::addLink);

        Link link = linkTo(CarController.class).withSelfRel();
        return new CollectionModel<>(carList, link);
    }

}
