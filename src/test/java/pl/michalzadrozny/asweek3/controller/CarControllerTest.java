package pl.michalzadrozny.asweek3.controller;

import org.hamcrest.core.Is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pl.michalzadrozny.asweek3.model.Car;
import pl.michalzadrozny.asweek3.service.CarServiceImpl;
import pl.michalzadrozny.asweek3.service.HypermediaControlService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@AutoConfigureJsonTesters
@ExtendWith(SpringExtension.class)
@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarServiceImpl carService;

    @MockBean
    private HypermediaControlService hypermediaService;

    @Autowired
    private JacksonTester<Car> jacksonTester;

    private List<Car> prepareMockData() {
        List<Car> carList = new ArrayList<>();

        carList.add(new Car(0, "Fiat", "126p", "Silver"));
        carList.add(new Car(1, "Ford", "Mustang", "Red"));
        carList.add(new Car(2, "Citroen", "Saxo", "Silver"));

        return carList;
    }

    @Test
    void should_returnNotFoundStatus_when_listOfCarsIsEmpty() throws Exception {

//        given
        given(carService.getListOfCars()).willReturn(Collections.emptyList());

//        when
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/cars")).andReturn().getResponse();

//        then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    void should_returnCollectionOfCars_when_listOfCarsIsNotEmpty() throws Exception {

//        given
        List<Car> cars = prepareMockData();
        given(carService.getListOfCars()).willReturn(cars);
        given(hypermediaService.addMultipleLinks(cars)).willCallRealMethod();

//        when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/cars"));

//        then
        assertThat(resultActions.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultActions.andExpect(jsonPath("$._embedded.carList.[0].mark", Is.is("Fiat"))));
        assertThat(resultActions.andExpect(jsonPath("$._embedded.carList.[1].color", Is.is("Red"))));
        assertThat(resultActions.andExpect(jsonPath("$._embedded.carList.[2].model", Is.is("Saxo"))));
    }

    @Test
    void should_returnNotFoundStatus_when_carWithGivenIdDoesNotExist() throws Exception {

//        given
        given(carService.findCarById(0)).willReturn(Optional.empty());

//        when
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/cars/{id}", 0)).andReturn().getResponse();

//        then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    void should_returnCarById() throws Exception {

//        given
        Car car = new Car(0, "Fiat", "126p", "Silver");
        given(carService.findCarById(0)).willReturn(Optional.of(car));

//        when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/cars/{id}", 0));

//        then
        assertThat(resultActions.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultActions.andExpect(jsonPath("$.mark", Is.is("Fiat"))));
    }

    @Test
    void should_returnNotFoundStatus_when_carsWithGivenColorDoNotExist() throws Exception {

//        given
        given(carService.findCarsByColor("Blue")).willReturn(Collections.emptyList());

//        when
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.get("/cars/color/{color}", "Blue")).andReturn().getResponse();

//        then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    void should_returnCollectionOfCarsByColor() throws Exception {

//        given
        List<Car> cars = prepareMockData();
        given(carService.findCarsByColor("Silver")).willReturn(cars);
        given(hypermediaService.addMultipleLinksForColors("Silver", cars)).willCallRealMethod();

//        when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/cars/color/{color}", "Silver"));

//        then
        assertThat(resultActions.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultActions.andExpect(jsonPath("$._embedded.carList.[0].mark", Is.is("Fiat"))));
        assertThat(resultActions.andExpect(jsonPath("$._embedded.carList.[1].color", Is.is("Red"))));
        assertThat(resultActions.andExpect(jsonPath("$._embedded.carList.[2].model", Is.is("Saxo"))));
    }

    @Test
    void should_returnConflictStatusAndCar_when_addingCarThatAlreadyExist() throws Exception {

//        given
        Car car = new Car(0, "Fiat", "126p", "Silver");
        given(carService.findCarById(0)).willReturn(Optional.of(car));

//        when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/cars").contentType(MediaType.APPLICATION_JSON).content(jacksonTester.write(car).getJson()));

//        then
        assertThat(resultActions.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(resultActions.andExpect(jsonPath("$.mark", Is.is("Fiat"))));
    }

    @Test
    void should_returnCreatedStatusAndCar_when_addingNewCar() throws Exception {

//        given
        Car car = new Car(0, "Fiat", "126p", "Silver");
        given(carService.findCarById(0)).willReturn(Optional.empty());

//        when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/cars").contentType(MediaType.APPLICATION_JSON).content(jacksonTester.write(car).getJson()));

//        then
        assertThat(resultActions.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.CREATED.value());
        assertThat(resultActions.andExpect(jsonPath("$.mark", Is.is("Fiat"))));
    }

    @Test
    void should_returnNotFoundStatus_when_editingCarThatDoesNotExist() throws Exception {

//        given
        Car car = new Car(0, "Fiat", "126p", "Silver");
        given(carService.findCarById(car.getId())).willReturn(Optional.empty());

//        when
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.put("/cars").contentType(MediaType.APPLICATION_JSON).content(jacksonTester.write(car).getJson())).andReturn().getResponse();

//        then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    void should_returnModdedCar_when_editingCar() throws Exception {

//        given
        Car car = new Car(0, "Fiat", "126p", "Silver");
        Car car2 = new Car(0, "Fiat2", "126p2", "Silver2");
        given(carService.findCarById(car2.getId())).willReturn(Optional.of(car));

//        when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.put("/cars").contentType(MediaType.APPLICATION_JSON).content(jacksonTester.write(car2).getJson()));

//        then
        assertThat(resultActions.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultActions.andExpect(jsonPath("$.mark", Is.is("Fiat2"))));
        assertThat(resultActions.andExpect(jsonPath("$.model", Is.is("126p2"))));
        assertThat(resultActions.andExpect(jsonPath("$.color", Is.is("Silver2"))));
    }

    @Test
    void should_returnNotFoundStatus_when_changingColorOfCarThatDoesNotExist() throws Exception {

//        given
        given(carService.findCarById(0)).willReturn(Optional.empty());

//        when
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.patch("/cars/{id}", 0).param("color", "Blue")).andReturn().getResponse();

//        then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();
    }

    @Test
    void should_returnCarWithChangedColor_when_changingColor() throws Exception {

//        given
        Car car = new Car(0, "Fiat", "126p", "Silver");
        given(carService.findCarById(0)).willReturn(Optional.of(car));

//        when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.patch("/cars/{id}", 0).param("color", "Blue"));

//        then
        assertThat(resultActions.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultActions.andExpect(jsonPath("$.mark", Is.is("Fiat"))));
        assertThat(resultActions.andExpect(jsonPath("$.model", Is.is("126p"))));
        assertThat(resultActions.andExpect(jsonPath("$.color", Is.is("Blue"))));
    }

    @Test
    void should_returnNotFoundStatus_when_removingCarThatDoesNotExist() throws Exception {

//        given
        given(carService.findCarById(0)).willReturn(Optional.empty());

//        when
        MockHttpServletResponse response = mockMvc.perform(MockMvcRequestBuilders.delete("/cars/{id}", 0)).andReturn().getResponse();

//        then
        assertThat(response.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(response.getContentAsString()).isEmpty();
    }


    @Test
    void should_returnOkStatusAndRemovedCar_when_removingCar() throws Exception {

//        given
        Car car = new Car(0, "Fiat", "126p", "Silver");
        given(carService.findCarById(0)).willReturn(Optional.of(car));

//        when
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.delete("/cars/{id}", 0));

//        then
        assertThat(resultActions.andReturn().getResponse().getStatus()).isEqualTo(HttpStatus.OK.value());
        assertThat(resultActions.andExpect(jsonPath("$.mark", Is.is("Fiat"))));
        assertThat(resultActions.andExpect(jsonPath("$.model", Is.is("126p"))));
        assertThat(resultActions.andExpect(jsonPath("$.color", Is.is("Silver"))));
    }

}