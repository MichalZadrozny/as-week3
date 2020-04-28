package pl.michalzadrozny.asweek3.service;

import lombok.Getter;
import org.springframework.stereotype.Service;
import pl.michalzadrozny.asweek3.model.Car;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Getter
public class CarServiceImpl implements CarService {

    List<Car> listOfCars;

    public CarServiceImpl() {
        listOfCars = new ArrayList<>();

        Car maluch = new Car(1, "Fiat", "126p", "Silver");
        Car mustang = new Car(2, "Ford", "Mustang", "Red");
        Car cytrynka = new Car(3, "Citroen", "Saxo", "Silver");

        listOfCars.add(maluch);
        listOfCars.add(mustang);
        listOfCars.add(cytrynka);
    }

    @Override
    public List<Car> findCarsByColor(String color) {
        return listOfCars.stream().filter(car -> car.getColor().equalsIgnoreCase(color)).collect(Collectors.toList());
    }

    @Override
    public Optional<Car> findCarById(long id) {
        return listOfCars.stream().filter(car -> car.getId() == id).findFirst();
    }

    @Override
    public Optional<Car> findEqualCar(Car newCar) {
        return listOfCars.stream().filter(car -> car.getId() == newCar.getId()).findFirst();
    }
}
