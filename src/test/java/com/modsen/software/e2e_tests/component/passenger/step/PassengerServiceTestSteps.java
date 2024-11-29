package com.modsen.software.e2e_tests.component.passenger.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.software.e2e_tests.passenger.PassengerRequest;
import com.modsen.software.e2e_tests.util.Constants;
import com.modsen.software.e2e_tests.util.E2EConfig;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;

import io.restassured.RestAssured;

import static com.modsen.software.e2e_tests.util.Constants.passengerRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import jakarta.annotation.PostConstruct;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;

@CucumberContextConfiguration
public class PassengerServiceTestSteps extends E2EConfig {

    @Value("${api.host}")
    private String host;

    @Value("${gateway.exposed-port}")
    private String exposedPort;

    @Autowired
    private ObjectMapper mapper;

    private Response response;

    @PostConstruct
    public void setup() throws InterruptedException {
        RestAssured.baseURI = "http://"+host+":"+exposedPort+"/api/v1/passengers";

        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout",10000)
                        .setParam("http.connection.timeout", 10000));
    }

    @SneakyThrows
    @Given("Two passengers in database")
    public void twoPassengersInDatabase() {
        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(Constants.passengerRequest))
        .when()
                .post()
        .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(1))
                .body("fullName", is(Constants.passengerRequest.getFullName()));

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(Constants.anotherPassengerRequest))
        .when()
                .post()
        .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(2))
                .body("fullName", is(Constants.anotherPassengerRequest.getFullName()));
    }

    @SneakyThrows
    @When("I request all passengers from database through service")
    public void iRequestAllPassengersFromDatabaseThroughService() {
         response = given()
                 .contentType(ContentType.JSON)
                 .param("pageNumber", "0")
                 .param("pageSize", "10")
                 .param("sortBy", "id")
                 .param("sortOrder", "asc")
        .when()
                .get();
    }

    @Then("first passengers page should be returned")
    public void firstPassengersPageShouldBeReturned() {
        response.then()
                .body("items[0].id", is(1))
                .body("items[1].id", is(2))
                .body("items[0].fullName", is(Constants.passengerRequest.getFullName()))
                .body("items[1].fullName", is(Constants.anotherPassengerRequest.getFullName()))
                .body("total", is(2))
                .body("page", is(0))
                .body("size", is(10));
    }

    @And("all passengers request complete with code {int} \\(OK)")
    public void allPassengersRequestCompleteWithCodeOK(int status) {
        response.then()
                .statusCode(status);
    }

    @When("I request passenger with id = {int} from database through service")
    public void iRequestPassengerWithIdFromDatabaseThroughService(int id) {
        response = given()
                .contentType(ContentType.JSON)
        .when()
                .get("/{id}", id);
    }

    @Then("find by id request complete with code {int} \\(OK) for passenger")
    public void findByIdRequestCompleteWithCodeOKForPassenger(int status) {
        response.then()
                .statusCode(status);
    }

    @And("returned passenger must be with name {string}, email {string} and phone number {string}")
    public void returnedPassengerMustBeWithNameEmailAndPhoneNumber(String fullName, String email, String phone) {
        response.then()
                .body("fullName", is(fullName))
                .body("email", is(email))
                .body("phone", is(phone));
    }

    @Then("request complete with code {int}\\(NOT_FOUND) and indicates that specified passenger not found")
    public void requestCompleteWithCodeNOT_FOUNDAndIndicatesThatSpecifiedPassengerNotFound(int status) {
        response.then()
                .statusCode(status);
    }

    @SneakyThrows
    @When("I save new passenger with name {string}, email {string} and phone number {string}")
    public void iSaveNewPassengerWithNameEmailAndPhoneNumber(String fullName, String email, String phone) {
        PassengerRequest passengerRequestToSave = passengerRequest.toBuilder()
                .fullName(fullName)
                .email(email)
                .phone(phone)
                .build();

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(passengerRequestToSave))
            .when()
                .post();
    }

    @Then("saved passenger with name {string}, email {string}, phone number {string} should be returned")
    public void savedPassengerWithNameEmailPhoneNumberShouldBeReturned(String name, String email, String phone) {
        response.then()
                .body("fullName", is(name))
                .body("email", is(email))
                .body("phone", is(phone));
    }

    @Then("the response should indicate with code {int} that email already owned by another passenger")
    public void theResponseShouldIndicateWithCodeThatEmailAlreadyOwnedByAnotherPassenger(int status) {
        response.then()
                .statusCode(status);
    }

    @Then("the response should indicate with code {int} that phone already owned by another passenger")
    public void theResponseShouldIndicateWithCodeThatPhoneAlreadyOwnedByAnotherPassenger(int status) {
        response.then()
                .statusCode(status);
    }

    @SneakyThrows
    @When("I try to update passenger with id = {int} changing name to {string} and email to {string}")
    public void iTryToUpdatePassengerWithIdChangingNameToAndEmailTo(int id, String fullName, String email) {
        PassengerRequest passengerRequestToUpdate = passengerRequest.toBuilder()
                .id(((long) id))
                .fullName(fullName)
                .email(email)
                .build();

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(passengerRequestToUpdate))
            .when()
                .put();
    }

    @Then("updated passenger with name {string} and email {string} should be returned")
    public void updatedPassengerWithNameAndEmailShouldBeReturned(String fullName, String email) {
        response.then()
                .body("fullName", is(fullName))
                .body("email", is(email));
    }

    @SneakyThrows
    @When("I try to update passenger with id = {int} changing email to {string}")
    public void iTryToUpdatePassengerWithIdChangingEmailTo(int id, String email) {
        PassengerRequest passengerRequestToUpdate = passengerRequest.toBuilder()
                .id(((long) id))
                .email(email)
                .build();

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(passengerRequestToUpdate))
            .when()
                .put();
    }

    @Then("the response should indicate with code {int} that updated email already owned by another passenger")
    public void theResponseShouldIndicateWithCodeThatUpdatedEmailAlreadyOwnedByAnotherPassenger(int status) {
        response.then()
                .statusCode(status);
    }

    @When("I try to delete passenger with id = {int}")
    public void iTryToDeletePassengerWithId(int id) {
        response = given()
                .when()
                    .delete("/{id}", id);
    }

    @Then("the response should indicate successful delete of passenger with code {int}\\(NO_CONTENT)")
    public void theResponseShouldIndicateSuccessfulDeleteOfPassengerWithCodeNO_CONTENT(int status) {
        response.then()
                .statusCode(status);
    }

    @Then("request complete with code {int}\\(NOT_FOUND) and indicates that specified for delete passenger not found")
    public void requestCompleteWithCodeNOT_FOUNDAndIndicatesThatSpecifiedForDeletePassengerNotFound(int status) {
        response.then()
                .statusCode(status);
    }
}
