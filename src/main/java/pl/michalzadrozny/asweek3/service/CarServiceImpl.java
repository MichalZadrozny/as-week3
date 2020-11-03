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

    private final List<Car> listOfCars;

    public CarServiceImpl() {
        listOfCars = new ArrayList<>();

        listOfCars.add(new Car(0, "Fiat", "126p", "Silver"));
        listOfCars.add(new Car(1, "Ford", "Mustang", "Red"));
        listOfCars.add(new Car(2, "Citroen", "Saxo", "Silver"));
    }

    @Override
    public List<Car> findCarsByColor(String color) {
        return listOfCars.stream().filter(car -> car.getColor().equalsIgnoreCase(color)).collect(Collectors.toList());
    }

    @Override
    public Optional<Car> findCarById(long id) {
        return listOfCars.stream().filter(car -> car.getId() == id).findFirst();
    }
}
