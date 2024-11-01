package com.modsen.software.e2e_tests.component.car.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.software.e2e_tests.car.CarRequest;
import com.modsen.software.e2e_tests.util.Constants;
import com.modsen.software.e2e_tests.util.E2EConfig;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

import static com.modsen.software.e2e_tests.util.Constants.carRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@CucumberContextConfiguration
public class CarServiceTestSteps extends E2EConfig {

    @Value("${api.host}")
    private String host;

    @Value("${gateway.exposed-port}")
    private String exposedPort;

    @Autowired
    private ObjectMapper mapper;

    private Response response;

    @PostConstruct
    public void setup() throws InterruptedException {
        RestAssured.baseURI = "http://"+host+":"+exposedPort+"/api/v1/cars";

        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout",10000)
                        .setParam("http.connection.timeout", 10000));
    }

    @SneakyThrows
    @Given("Two cars in database")
    public void twoCarsInDatabase() {
        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(carRequest))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(1))
                .body("vehicleNumber", is(carRequest.getVehicleNumber()));

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(Constants.anotherCarRequest))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(2))
                .body("vehicleNumber", is(Constants.anotherCarRequest.getVehicleNumber()));
    }

    @When("I request all cars from database through service")
    public void iRequestAllCarsFromDatabaseThroughService() {
        response = given()
                .contentType(ContentType.JSON)
                .param("pageNumber", "0")
                .param("pageSize", "10")
                .param("sortBy", "id")
                .param("sortOrder", "asc")
        .when()
                .get();
    }

    @Then("all cars request complete with code {int} \\(OK)")
    public void allCarsRequestCompleteWithCodeOK(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @And("first cars page should be returned")
    public void firstCarsPageShouldBeReturned() {
        response.then()
                .body("items[0].id", is(1))
                .body("items[1].id", is(2))
                .body("items[0].vehicleNumber", is(carRequest.getVehicleNumber()))
                .body("items[1].vehicleNumber", is(Constants.anotherCarRequest.getVehicleNumber()))
                .body("total", is(2))
                .body("page", is(0))
                .body("size", is(10));
    }

    @When("I request car by id = {int} from database through service")
    public void iRequestCarByIdFromDatabaseThroughService(int arg0) {
        response = given()
                .contentType(ContentType.JSON)
        .when()
                .get("/{id}", arg0);
    }

    @Then("find by id request complete with code {int} \\(OK) for car")
    public void findByIdRequestCompleteWithCodeOKForCar(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @And("returned car must not be null, with brand {string} and vehicle number {string}")
    public void returnedCarMustNotBeNullWithBrandAndVehicleNumber(String arg0, String arg1) {
        response.then()
                .body("model", is(arg0))
                .body("vehicleNumber", is(arg1));
    }

    @Then("request complete with code {int}\\(NOT_FOUND) and indicates that specified car not found")
    public void requestCompleteWithCodeNOT_FOUNDAndIndicatesThatSpecifiedCarNotFound(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @SneakyThrows
    @When("I save new car with brand {string} and vehicle number {string}")
    public void iSaveNewCarWithBrandAndVehicleNumber(String arg0, String arg1) {
        CarRequest carRequestToSave = carRequest.toBuilder()
                .model(arg0)
                .vehicleNumber(arg1)
                .build();

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(carRequestToSave))
        .when()
                .post();
    }

    @Then("saved car with brand {string}, vehicle number {string}")
    public void savedCarWithBrandVehicleNumber(String arg0, String arg1) {
        response.then()
                .body("model", is(arg0))
                .body("vehicleNumber", is(arg1));
    }

    @Then("the response should indicate that vehicle number already owned by another car with code {int}")
    public void theResponseShouldIndicateThatVehicleNumberAlreadyOwnedByAnotherCarWithCode(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @SneakyThrows
    @When("I try to update car with id = {int} changing brand to {string} and vehicle number to {string}")
    public void iTryToUpdateCarWithIdChangingBrandToAndVehicleNumberTo(int arg0, String arg1, String arg2) {
        CarRequest carRequestToUpdate = carRequest.toBuilder()
                .id(((long) arg0))
                .model(arg1)
                .vehicleNumber(arg2)
                .build();

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(carRequestToUpdate))
        .when()
                .put();
    }

    @Then("updated car with brand {string} and vehicle number to {string} should be returned")
    public void updatedCarWithBrandAndVehicleNumberToShouldBeReturned(String arg0, String arg1) {
        response.then()
                .body("model", is(arg0))
                .body("vehicleNumber", is(arg1));
    }

    @SneakyThrows
    @When("I try to update car with id = {int} changing vehicle number to {string}")
    public void iTryToUpdateCarWithIdChangingVehicleNumberTo(int arg0, String arg1) {
        CarRequest carRequestToUpdate = carRequest.toBuilder()
                .id(((long) arg0))
                .vehicleNumber(arg1)
                .build();

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(carRequestToUpdate))
        .when()
                .put();
    }

    @Then("the response should indicate that updated vehicle number already owned by another car with code {int}")
    public void theResponseShouldIndicateThatUpdatedVehicleNumberAlreadyOwnedByAnotherCarWithCode(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @When("I try to delete car with id = {int}")
    public void iTryToDeleteCarWithId(int arg0) {
        response = given()
                .when()
                .delete("/{id}", arg0);
    }

    @Then("the response should indicate successful delete of car with code {int}\\(NO_CONTENT)")
    public void theResponseShouldIndicateSuccessfulDeleteOfCarWithCodeNO_CONTENT(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @Then("request complete with code {int}\\(NOT_FOUND) and indicates that specified for delete car not found")
    public void requestCompleteWithCodeNOT_FOUNDAndIndicatesThatSpecifiedForDeleteCarNotFound(int arg0) {
        response.then()
                .statusCode(arg0);
    }
}
