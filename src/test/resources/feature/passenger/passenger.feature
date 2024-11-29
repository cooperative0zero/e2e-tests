Feature: Passenger Service

  Scenario: Create the initial state of the database
    Given Two passengers in database

  Scenario: Retrieve all passengers from database
    When I request all passengers from database through service
    Then first passengers page should be returned
    And all passengers request complete with code 200 (OK)

  Scenario: Retrieve passenger by id from database
    When I request passenger with id = 1 from database through service
    Then find by id request complete with code 200 (OK) for passenger
    And returned passenger must be with name "First Middle Last", email "example@mail.com" and phone number "987654321"

  Scenario: Retrieve non-existing passenger from database by id
    When I request passenger with id = 101 from database through service
    Then request complete with code 404(NOT_FOUND) and indicates that specified passenger not found

  Scenario: Save new passenger into database
    When I save new passenger with name "First Middle Last", email "example123@mail.com" and phone number "9876543210"
    Then saved passenger with name "First Middle Last", email "example123@mail.com", phone number "9876543210" should be returned

  Scenario: Save new passenger into database with duplicate email
    When I save new passenger with name "First Middle Last", email "example@mail.com" and phone number "987654321"
    Then the response should indicate with code 400 that email already owned by another passenger

  Scenario: Save new passenger into database with duplicate phone
    When I save new passenger with name "First Middle Last", email "example@mail.com" and phone number "987654321"
    Then the response should indicate with code 400 that phone already owned by another passenger

  Scenario: Update existing passenger
    When I try to update passenger with id = 1 changing name to "First Middle Last" and email to "exampleNewMail@mail.com"
    Then updated passenger with name "First Middle Last" and email "exampleNewMail@mail.com" should be returned

  Scenario: Update existing passenger with email already defined within database
    When I try to update passenger with id = 1 changing email to "example2@mail.com"
    Then the response should indicate with code 400 that updated email already owned by another passenger

  Scenario: Soft delete of passenger
    When I try to delete passenger with id = 1
    Then the response should indicate successful delete of passenger with code 204(NO_CONTENT)

  Scenario: Soft delete of passenger with non-existing id
    When I try to delete passenger with id = 500
    Then request complete with code 404(NOT_FOUND) and indicates that specified for delete passenger not found