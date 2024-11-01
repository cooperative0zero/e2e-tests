package com.modsen.software.e2e_tests.component.driver.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.software.e2e_tests.driver.DriverRequest;
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

import static com.modsen.software.e2e_tests.util.Constants.driverRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@CucumberContextConfiguration
public class DriverServiceTestSteps extends E2EConfig {

    @Value("${api.host}")
    private String host;

    @Value("${gateway.exposed-port}")
    private String exposedPort;

    @Autowired
    private ObjectMapper mapper;

    private Response response;

    @PostConstruct
    public void setup() throws InterruptedException {
        RestAssured.baseURI = "http://"+host+":"+exposedPort+"/api/v1/drivers";

        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout",10000)
                        .setParam("http.connection.timeout", 10000));
    }

    @SneakyThrows
    @Given("Two drivers in database")
    public void twoDriversInDatabase() {
        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(driverRequest))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(1))
                .body("fullName", is(driverRequest.getFullName()));

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(Constants.anotherDriverRequest))
                .when()
                .post()
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(2))
                .body("fullName", is(Constants.anotherDriverRequest.getFullName()));
    }

    @When("I request all drivers from database through service")
    public void iRequestAllDriversFromDatabaseThroughService() {
        response = given()
                .contentType(ContentType.JSON)
                .param("pageNumber", "0")
                .param("pageSize", "10")
                .param("sortBy", "id")
                .param("sortOrder", "asc")
        .when()
                .get();
    }

    @Then("all drivers request complete with code {int} \\(OK)")
    public void allDriversRequestCompleteWithCodeOK(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @And("first drivers page should be returned")
    public void firstDriversPageShouldBeReturned() {
        response.then()
                .body("items[0].id", is(1))
                .body("items[1].id", is(2))
                .body("items[0].fullName", is(driverRequest.getFullName()))
                .body("items[1].fullName", is(Constants.anotherDriverRequest.getFullName()))
                .body("total", is(2))
                .body("page", is(0))
                .body("size", is(10));
    }

    @When("I request driver by id = {int} from database through service")
    public void iRequestDriverByIdFromDatabaseThroughService(int arg0) {
        response = given()
                .contentType(ContentType.JSON)
        .when()
                .get("/{id}", arg0);
    }

    @Then("find by id request complete with code {int} \\(OK) for driver")
    public void findByIdRequestCompleteWithCodeOKForDriver(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @And("returned driver must not be null, with name {string}, email {string} and phone number {string}")
    public void returnedDriverMustNotBeNullWithNameEmailAndPhoneNumber(String arg0, String arg1, String arg2) {
        response.then()
                .body("fullName", is(arg0))
                .body("email", is(arg1))
                .body("phone", is(arg2));
    }

    @Then("request complete with code {int}\\(NOT_FOUND) and indicates that specified driver not found")
    public void requestCompleteWithCodeNOT_FOUNDAndIndicatesThatSpecifiedDriverNotFound(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @SneakyThrows
    @When("I save new driver with name {string}, email {string} and phone number {string}")
    public void iSaveNewDriverWithNameEmailAndPhoneNumber(String arg0, String arg1, String arg2) {
        DriverRequest driverRequestToSave = driverRequest.toBuilder()
                .fullName(arg0)
                .email(arg1)
                .phone(arg2)
                .build();

        DriverRequest driverRequest = new DriverRequest();
        driverRequest.setFullName(arg0);
        driverRequest.setEmail(arg1);
        driverRequest.setPhone(arg2);
        driverRequest.setGender("MALE");
        driverRequest.setStatus("AVAILABLE");
        driverRequest.setIsDeleted(false);

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(driverRequestToSave))
        .when()
                .post();
    }

    @Then("saved driver with name {string}, email {string}, phone number {string} and defined id should be returned")
    public void savedDriverWithNameEmailPhoneNumberAndDefinedIdShouldBeReturned(String arg0, String arg1, String arg2) {
        response.then()
                .body("fullName", is(arg0))
                .body("email", is(arg1))
                .body("phone", is(arg2));
    }

    @Then("the response should indicate that email already owned by another driver with code {int}")
    public void theResponseShouldIndicateThatEmailAlreadyOwnedByAnotherDriverWithCode(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @SneakyThrows
    @When("I try to update driver with id = {int} changing name to {string} and email to {string}")
    public void iTryToUpdateDriverWithIdChangingNameToAndEmailTo(int arg0, String arg1, String arg2) {
        DriverRequest driverRequestToSave = driverRequest.toBuilder()
                .id((long) arg0)
                .fullName(arg1)
                .email(arg2)
                .build();

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(driverRequestToSave))
        .when()
                .put();
    }

    @Then("updated driver with name {string} and email {string} should be returned")
    public void updatedDriverWithNameAndEmailShouldBeReturned(String arg0, String arg1) {
        response.then()
                .body("fullName", is(arg0))
                .body("email", is(arg1));
    }

    @SneakyThrows
    @When("I try to update driver with id = {int} changing email to {string}")
    public void iTryToUpdateDriverWithIdChangingEmailTo(int arg0, String arg1) {
        DriverRequest driverRequestToUpdate = driverRequest.toBuilder()
            .id((long) arg0)
            .email(arg1)
            .build();

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(driverRequestToUpdate))
        .when()
                .put();
    }

    @Then("the response should indicate that updated email already owned by another driver with code {int}")
    public void theResponseShouldIndicateThatUpdatedEmailAlreadyOwnedByAnotherDriverWithCode(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @When("I try to delete driver with id = {int}")
    public void iTryToDeleteDriverWithId(int arg0) {
        response = given()
                .when()
                .delete("/{id}", arg0);
    }

    @Then("the response should indicate successful delete of driver with code {int}\\(NO_CONTENT)")
    public void theResponseShouldIndicateSuccessfulDeleteOfDriverWithCodeNO_CONTENT(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @Then("request complete with code {int}\\(NOT_FOUND) and indicates that specified for delete driver not found")
    public void requestCompleteWithCodeNOT_FOUNDAndIndicatesThatSpecifiedForDeleteDriverNotFound(int arg0) {
        response.then()
                .statusCode(arg0);
    }
}
