package com.modsen.software.e2e_tests.component.rating.step;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modsen.software.e2e_tests.component.driver.step.DriverServiceTestSteps;
import com.modsen.software.e2e_tests.component.passenger.step.PassengerServiceTestSteps;
import com.modsen.software.e2e_tests.rating.RatingRequest;
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

import static com.modsen.software.e2e_tests.util.Constants.ratingRequest;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.is;

@CucumberContextConfiguration
public class RatingServiceTestSteps extends E2EConfig {

    @Value("${api.host}")
    private String host;

    @Value("${gateway.exposed-port}")
    private String exposedPort;

    @Autowired
    private ObjectMapper mapper;

    private Response response;

    @PostConstruct
    public void setup() throws InterruptedException {
        RestAssured.baseURI = "http://"+host+":"+exposedPort+"/api/v1/ratings";

        RestAssured.config = RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout",10000)
                        .setParam("http.connection.timeout", 10000));
    }

    @SneakyThrows
    @Given("Two ratings in database")
    public void twoRatingsInDatabase() {
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
                .body(mapper.writeValueAsBytes(Constants.driverRequest))
                .when()
                .post("/drivers")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(1))
                .body("fullName", is(Constants.driverRequest.getFullName()));

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(Constants.anotherDriverRequest))
                .when()
                .post("/drivers")
                .then()
                .statusCode(HttpStatus.CREATED.value())
                .body("id", is(2))
                .body("fullName", is(Constants.anotherDriverRequest.getFullName()));

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(ratingRequest))
        .when()
                .post("/ratings")
        .then()
                .statusCode(HttpStatus.CREATED.value());

        given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(Constants.anotherRatingRequest))
        .when()
                .post("/ratings")
        .then()
                .statusCode(HttpStatus.CREATED.value());
    }

    @When("I request all rating scores from database through service")
    public void iRequestAllRatingScoresFromDatabaseThroughService() {
        response = given()
                .contentType(ContentType.JSON)
                .param("pageNumber", "0")
                .param("pageSize", "10")
                .param("sortBy", "id")
                .param("sortOrder", "asc")
        .when()
                .get();
    }

    @Then("all rating scores request complete with code {int} \\(OK)")
    public void allRatingScoresRequestCompleteWithCodeOK(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @And("first rating scores page should be returned")
    public void firstRatingScoresPageShouldBeReturned() {
        response.then()
                .body("items[0].id", is(1))
                .body("items[1].id", is(2))
                .body("items[0].passengerId", is(ratingRequest.getPassengerId().intValue()))
                .body("items[1].passengerId", is(Constants.anotherRatingRequest.getPassengerId().intValue()))
                .body("items[0].driverId", is(ratingRequest.getDriverId().intValue()))
                .body("items[1].driverId", is(Constants.anotherRatingRequest.getDriverId().intValue()))
                .body("total", is(2))
                .body("page", is(0))
                .body("size", is(10));
    }

    @When("I request all rating scores with driver id = {int} from database through service")
    public void iRequestAllRatingScoresWithDriverIdFromDatabaseThroughService(int arg0) {
        response = given()
                .contentType(ContentType.JSON)
        .when()
                .get("/drivers/{id}", arg0);
    }

    @Then("all rating scores filtered by driver id request complete with code {int} \\(OK)")
    public void allRatingScoresFilteredByDriverIdRequestCompleteWithCodeOK(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @And("first page of filtered by driver id = {int} rating scores should be returned")
    public void firstPageOfFilteredByDriverIdRatingScoresShouldBeReturned(int arg0) {
        response.then()
                .body("items[0].id", is(1))
                .body("items[0].passengerId", is(ratingRequest.getPassengerId().intValue()))
                .body("items[0].driverId", is(arg0))
                .body("total", is(1))
                .body("page", is(0))
                .body("size", is(10));
    }

    @When("I request rating score with id = {int} from database through service")
    public void iRequestRatingScoreWithIdFromDatabaseThroughService(int arg0) {
        response = given()
                .contentType(ContentType.JSON)
        .when()
                .get("/{id}", arg0);
    }

    @Then("find by id request complete with code {int} \\(OK) of rating scores")
    public void findByIdRequestCompleteWithCodeOKOfRatingScores(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @And("returned rating score must be with rating = {int} and initiator should be passenger")
    public void returnedRatingScoreMustBeWithRatingAndInitiatorShouldBePassenger(int arg0) {
        response.then()
                .body("id", is(1))
                .body("passengerId", is(ratingRequest.getPassengerId().intValue()))
                .body("driverId", is(ratingRequest.getDriverId().intValue()))
                .body("isByPassenger", is(true))
                .body("rating", is(arg0))
                .body("comment", is(ratingRequest.getComment()));
    }

    @Then("request complete with code {int}\\(NOT_FOUND) and indicates that specified rating score not found")
    public void requestCompleteWithCodeNOT_FOUNDAndIndicatesThatSpecifiedRatingScoreNotFound(int arg0) {
        response.then()
                .statusCode(arg0);
    }

    @SneakyThrows
    @When("I save new rating score with driver id = {int}, passenger id = {int} and rating = {int}")
    public void iSaveNewRatingScoreWithDriverIdPassengerIdAndRating(int arg0, int arg1, int arg2) {
        RatingRequest ratingRequestToSave = ratingRequest.toBuilder()
                        .driverId(((long) arg0))
                        .passengerId(((long) arg1))
                        .rating(((byte) arg2))
                        .build();

        response = given()
                .contentType(ContentType.JSON)
                .body(mapper.writeValueAsBytes(ratingRequestToSave))
        .when()
                .post();
    }

    @Then("saved rating score with driver id = {int}, passenger id = {int} and rating = {int} should be returned")
    public void savedRatingScoreWithDriverIdPassengerIdAndRatingShouldBeReturned(int arg0, int arg1, int arg2) {
        response.then()
                .body("rating", is(arg2))
                .body("passengerId", is(arg1))
                .body("driverId", is(arg0));
    }

    @When("I try to update rating score with id = {int} changing rating to {int}")
    public void iTryToUpdateRatingScoreWithIdChangingRatingTo(int arg0, int arg1) {
        response = given()
                .contentType(ContentType.JSON)
        .when()
                .patch("/{id}/rating/{rating}", arg0, arg1);
    }

    @Then("updated rating with rating = {int} should be returned")
    public void updatedRatingWithRatingShouldBeReturned(int arg0) {
        response.then()
                .body("rating", is(arg0));
    }
}
