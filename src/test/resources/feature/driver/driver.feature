Feature: Driver Service

  Scenario: Create the initial state of the database
    Given Two drivers in database

  Scenario: Retrieve all drivers from database
    When I request all drivers from database through service
    Then all drivers request complete with code 200 (OK)
    And first drivers page should be returned
  Scenario: Retrieve driver by id from database
    When I request driver by id = 1 from database through service
    Then find by id request complete with code 200 (OK) for driver
    And returned driver must not be null, with name "First Middle Last", email "example@mail.com" and phone number "987654321"
  Scenario: Retrieve non-existing driver from database by id
    When I request driver by id = 101 from database through service
    Then request complete with code 404(NOT_FOUND) and indicates that specified driver not found
  Scenario: Save new driver into database
    When I save new driver with name "First Middle Last", email "example1@mail.com" and phone number "123456"
    Then saved driver with name "First Middle Last", email "example1@mail.com", phone number "123456" and defined id should be returned
  Scenario: Save new driver into database with duplicate email
    When I save new driver with name "First Middle Last", email "example1@mail.com" and phone number "1234567"
    Then the response should indicate that email already owned by another driver with code 400
  Scenario: Save new driver into database with duplicate phone
    When I save new driver with name "First Middle Last", email "example2@mail.com" and phone number "123456"
    Then the response should indicate that email already owned by another driver with code 400
  Scenario: Update existing driver
    When I try to update driver with id = 1 changing name to "First Middle Last" and email to "example3@mail.com"
    Then updated driver with name "First Middle Last" and email "example3@mail.com" should be returned
  Scenario: Update existing driver with email already defined within database
    When I try to update driver with id = 1 changing email to "example1@mail.com"
    Then the response should indicate that updated email already owned by another driver with code 400
  Scenario: Soft Delete Of Driver
    When I try to delete driver with id = 1
    Then the response should indicate successful delete of driver with code 204(NO_CONTENT)
  Scenario: Soft Delete Of Driver with non-existing id
    When I try to delete driver with id = 2002
    Then request complete with code 404(NOT_FOUND) and indicates that specified for delete driver not found