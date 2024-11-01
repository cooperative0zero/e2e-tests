package com.modsen.software.e2e_tests.component.ride.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.software.e2e_tests.ride.RideRequest;
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

import java.time.OffsetDateTime;

import static com.modsen.software.e2e_tests.util.Constants.rideRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@CucumberContextConfiguration
public class RideServiceTestSteps extends E2EConfig {

    @Value("${api.host}")
    private String host;

    @Value("${gateway.exposed-port}")
    private String exposedPort;

    @Autowired
    private ObjectMapper mapper;

    private Response response;

    @PostConstruct
    public void setup() throws InterruptedException {
        RestAssured.baseURI = "http://"+host+":"+exposedPort+"/api/v1/rides";

        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout",10000)
                        .setParam("http.connection.timeout", 10000));
    }

    @SneakyThrows
    @Given("Two rides in database")
    public void twoRidesInDatabase() {
        RestAssured.baseURI = "http://"+host+":"+exposedPort+"/api/v1/";

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(Constants.passengerRequest))
                .when()
                .post("/passengers")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(1))
                .body("fullName", is(Constants.passengerRequest.getFullName()));

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(Constants.anotherPassengerRequest))
                .when()
                .post("/passengers")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(2))
                .body("fullName", is(Constants.anotherPassengerRequest.getFullName()));

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(rideRequest))
        .when()
                .post("/rides")
        .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(Constants.anotherRideRequest))
        .when()
                .post("/rides")
        .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @When("I request all rides from database through service")
    public void iRequestAllRidesFromDatabaseThroughService() {
        response = given()
                .contentType(ContentType.JSON)
                .param("pageNumber", "0")
                .param("pageSize", "10")
                .param("sortBy", "id")
                .param("sortOrder", "asc")
        .when()
                .get();
    }

    @Then("all rides request complete with code {int} \\(OK)")
    public void allRidesRequestCompleteWithCodeOK(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @And("first rides page should be returned")
    public void firstRidesPageShouldBeReturned() {
        response.then()
                .body("items[0].id", is(1))
                .body("items[1].id", is(2))
                .body("items[0].passengerId", is(rideRequest.getPassengerId().intValue()))
                .body("items[1].passengerId", is(Constants.anotherRideRequest.getPassengerId().intValue()))
                .body("total", is(2))
                .body("page", is(0))
                .body("size", is(10));
    }

    @When("I request all rides with passenger id = {int} from database through service")
    public void iRequestAllRidesWithPassengerIdFromDatabaseThroughService(int arg0) {
        response = given()
                .contentType(ContentType.JSON)
        .when()
                .get("/passengers/{id}", arg0);
    }

    @Then("all rides filtered by passenger id request complete with code {int} \\(OK)")
    public void allRidesFilteredByPassengerIdRequestCompleteWithCodeOK(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @And("first page of filtered by passenger id = {int} rides should be returned")
    public void firstPageOfFilteredByPassengerIdRidesShouldBeReturned(int arg0) {
        response.then()
                .body("items[0].id", is(1))
                .body("items[0].passengerId", is(arg0))
                .body("total", is(1))
                .body("page", is(0))
                .body("size", is(10));
    }

    @When("I request all rides with driver id = {int} from database through service")
    public void iRequestAllRidesWithDriverIdFromDatabaseThroughService(int arg0) {
        response = given()
                .contentType(ContentType.JSON)
        .when()
                .get("/drivers/{id}", arg0);
    }

    @Then("all rides filtered by driver id request complete with code {int} \\(OK)")
    public void allRidesFilteredByDriverIdRequestCompleteWithCodeOK(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @And("first page of filtered by driver id = {int} rides should be returned")
    public void firstPageOfFilteredByDriverIdRidesShouldBeReturned(int arg0) {
        response.then()
                .body("total", is(0))
                .body("page", is(0))
                .body("size", is(10));
    }

    @When("I request ride with id = {int} from database through service")
    public void iRequestRideWithIdFromDatabaseThroughService(int arg0) {
        response = given()
                .contentType(ContentType.JSON)
        .when()
                .get("/{id}", arg0);
    }

    @Then("find by id request complete with code {int} \\(OK) of rides")
    public void findByIdRequestCompleteWithCodeOKOfRides(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @And("returned ride must be with status {string}")
    public void returnedRideMustBeWithStatus(String arg0) {
        response.then()
                .body("id", is(1))
                .body("passengerId", is(rideRequest.getPassengerId().intValue()))
                .body("rideStatus", is(arg0));
    }

    @Then("request complete with code {int}\\(NOT_FOUND) and indicates that specified ride not found")
    public void requestCompleteWithCodeNOT_FOUNDAndIndicatesThatSpecifiedRideNotFound(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @SneakyThrows
    @When("I save new ride with passenger id = {int}, departureAddress = {string} and destinationAddress = {string}")
    public void iSaveNewRideWithPassengerIdDepartureAddressAndDestinationAddress(int arg0, String arg1, String arg2) {
        RideRequest rideRequestToSave = rideRequest.toBuilder()
                .passengerId(((long) arg0))
                .departureAddress(arg1)
                .destinationAddress(arg2)
                .build();

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(rideRequestToSave))
        .when()
                .post();
    }

    @Then("saved ride with passenger id = {int}, departureAddress = {string} and destinationAddress = {string}")
    public void savedRideWithPassengerIdDepartureAddressAndDestinationAddress(int arg0, String arg1, String arg2) {
        response.then()
                .body("passengerId", is(arg0))
                .body("departureAddress", is(arg1))
                .body("destinationAddress", is(arg2));
    }

    @SneakyThrows
    @When("I try to update ride with id = {int} changing departureAddress to {string}")
    public void iTryToUpdateRideWithIdChangingDepartureAddressTo(int arg0, String arg1) {
        RideRequest rideRequestToUpdate = rideRequest.toBuilder()
                    .id(((long) arg0))
                    .departureAddress(arg1)
                    .build();

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(rideRequestToUpdate))
        .when()
                .put();
    }

    @Then("updated ride with departureAddress = {string} should be returned")
    public void updatedRideWithDepartureAddressShouldBeReturned(String arg0) {
        response.then()
                .body("id", is(1))
                .body("departureAddress", is(arg0));
    }

    @When("I try to change status of ride with id = {int} to {string}")
    public void iTryToChangeStatusOfRideWithIdTo(int arg0, String arg1) {
        response = given()
                .contentType(ContentType.JSON)
                .param("userId", "1")
                .param("status", arg1)
        .when()
                .patch("/{id}", arg0);
    }

    @Then("ride with changed status {string} should be returned")
    public void rideWithChangedStatusShouldBeReturned(String arg0) {
        response.then()
                .body("rideStatus", is(arg0));
    }
}
