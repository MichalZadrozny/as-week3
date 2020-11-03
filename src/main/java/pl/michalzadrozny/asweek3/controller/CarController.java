package pl.michalzadrozny.asweek3.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.michalzadrozny.asweek3.model.Car;
import pl.michalzadrozny.asweek3.service.CarServiceImpl;
import pl.michalzadrozny.asweek3.service.HypermediaControlService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/cars", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
public class CarController {

    private final CarServiceImpl carService;
    private final HypermediaControlService hypermediaService;


    @Autowired
    public CarController(CarServiceImpl carService, HypermediaControlService hypermediaService) {
        this.carService = carService;
        this.hypermediaService = hypermediaService;
    }

    @GetMapping
    public ResponseEntity<CollectionModel<Car>> getCars() {
        List<Car> carList = carService.getListOfCars();

        if (carList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } else {
            CollectionModel<Car> collectionModel = hypermediaService.addMultipleLinks(carList);
            return ResponseEntity.ok(collectionModel);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarByID(@PathVariable long id) {
        Optional<Car> foundCar = carService.findCarById(id);

        if (foundCar.isPresent()) {
            hypermediaService.addLink(foundCar.get());
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
            return ResponseEntity.ok(hypermediaService.addMultipleLinksForColors(color, foundCars));
        }
    }


    @PostMapping
    public ResponseEntity<Car> addCar(@RequestBody Car newCar) {
        Optional<Car> foundCar = carService.findCarById(newCar.getId());

        if (foundCar.isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(foundCar.get());
        } else {
            carService.getListOfCars().add(newCar);
            hypermediaService.addLink(newCar);
            return ResponseEntity.status(HttpStatus.CREATED).body(newCar);
        }
    }

    @PutMapping
    public ResponseEntity<Car> modCar(@RequestBody Car newCar) {
        Optional<Car> foundCar = carService.findCarById(newCar.getId());

        if (foundCar.isPresent()) {
            foundCar.get().modCar(newCar);
            hypermediaService.addLink(foundCar.get());
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
            hypermediaService.addLink(foundCar.get());
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


}
