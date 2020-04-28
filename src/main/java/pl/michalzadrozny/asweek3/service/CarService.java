package pl.michalzadrozny.asweek3.service;

import pl.michalzadrozny.asweek3.model.Car;

import java.util.List;
import java.util.Optional;

public interface CarService {

    List<Car> findCarsByColor(String color);
    Optional<Car> findCarById(long id);
    Optional<Car> findEqualCar(Car car);
}
