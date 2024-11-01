Feature: Ride Service
  Scenario: Create the initial state of the database
    Given Two rides in database

  Scenario: Retrieve all rides scores from database
    When I request all rides from database through service
    Then all rides request complete with code 200 (OK)
    And first rides page should be returned
  Scenario: Retrieve all rides from database with defined passenger id
    When I request all rides with passenger id = 1 from database through service
    Then all rides filtered by passenger id request complete with code 200 (OK)
    And first page of filtered by passenger id = 1 rides should be returned
  Scenario: Retrieve all rides from database with defined driver id
    When I request all rides with driver id = 2 from database through service
    Then all rides filtered by driver id request complete with code 200 (OK)
    And first page of filtered by driver id = 2 rides should be returned
  Scenario: Retrieve ride by id from database
    When I request ride with id = 1 from database through service
    Then find by id request complete with code 200 (OK) of rides
    And returned ride must be with status "CREATED"
  Scenario: Retrieve non-existing ride from database by id
    When I request ride with id = 205 from database through service
    Then request complete with code 404(NOT_FOUND) and indicates that specified ride not found
  Scenario: Save new ride into database
    When I save new ride with passenger id = 1, departureAddress = "Departure address 1" and destinationAddress = "Destination address 1"
    Then saved ride with passenger id = 1, departureAddress = "Departure address 1" and destinationAddress = "Destination address 1"
  Scenario: Update existing ride
    When I try to update ride with id = 1 changing departureAddress to "New departure address"
    Then updated ride with departureAddress = "New departure address" should be returned
  Scenario: Change ride status
    When I try to change status of ride with id = 1 to "CANCELLED"
    Then ride with changed status "CANCELLED" should be returned