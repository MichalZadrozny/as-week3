package pl.michalzadrozny.asweek3.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalzadrozny.asweek3.model.Car;
import pl.michalzadrozny.asweek3.service.CarServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@RestController
@RequestMapping(value = "/cars", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class CarController {

    CarServiceImpl carService;

    @Autowired
    public CarController(CarServiceImpl carService) {
        this.carService = carService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<Car>> getCars() {
        if (carService.getListOfCars().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            List<Car> carList = carService.getListOfCars();

            carList.forEach(this::addLink);

            Link link = linkTo(CarController.class).withSelfRel();
            CollectionModel<Car> collectionModel = new CollectionModel<>(carList, link);

            return ResponseEntity.ok(collectionModel);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarByID(@PathVariable long id) {
        Optional<Car> foundCar = carService.findCarById(id);

        if (foundCar.isPresent()) {
            if (!foundCar.get().hasLinks()) {
                addLink(foundCar.get());
            }
            return ResponseEntity.ok(foundCar.get());
        } else {
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/color/{color}")
    public ResponseEntity<CollectionModel<Car>> getCarsByColor(@PathVariable String color) {
        List<Car> foundCars = carService.findCarsByColor(color);

        if (foundCars.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            foundCars.forEach(this::addLink);

            Link link = linkTo(CarController.class).slash("color").slash(color).withSelfRel();
            CollectionModel<Car> collectionModel = new CollectionModel<>(foundCars, link);

            return ResponseEntity.ok(collectionModel);
        }

    }

    @PostMapping
    public ResponseEntity<Car> addCar(@RequestBody Car newCar) {
        Optional<Car> foundCar = carService.findCarById(newCar.getId());

        if (foundCar.isPresent()) {
            return  ResponseEntity.status(HttpStatus.CONFLICT).body(foundCar.get());
        } else {
            carService.getListOfCars().add(newCar);
            addLink(newCar);
            return  ResponseEntity.status(HttpStatus.CREATED).body(newCar);
        }
    }

    @PutMapping
    public ResponseEntity<Car> modCar(@RequestBody Car newCar) {
        Optional<Car> foundCar = carService.findEqualCar(newCar);

        if (foundCar.isPresent()) {
            foundCar.get().setColor(newCar.getColor());
            foundCar.get().setMark(newCar.getMark());
            foundCar.get().setModel(newCar.getModel());
            addLink(foundCar.get());

            return ResponseEntity.ok(foundCar.get());

        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Car> changeCarColor(@PathVariable long id, @RequestParam String color) {
        Optional<Car> foundCar = carService.findCarById(id);

        if (foundCar.isPresent()) {
            foundCar.get().setColor(color);
            addLink(foundCar.get());
            return new ResponseEntity<>(foundCar.get(), HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Car> removeCarByID(@PathVariable long id) {
        Optional<Car> foundCar = carService.findCarById(id);

        if (foundCar.isPresent()) {
            carService.getListOfCars().remove(foundCar.get());
            return ResponseEntity.ok(foundCar.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private void addLink(Car car) {
        car.addIf(!car.hasLinks(), () -> linkTo(CarController.class).slash(car.getId()).withSelfRel());
    }
}
