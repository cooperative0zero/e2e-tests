Feature: Car Service

  Scenario: Create the initial state of the database
    Given Two cars in database

  Scenario: Retrieve all cars from database
    When I request all cars from database through service
    Then all cars request complete with code 200 (OK)
    And first cars page should be returned
  Scenario: Retrieve car by id from database
    When I request car by id = 1 from database through service
    Then find by id request complete with code 200 (OK) for car
    And returned car must not be null, with brand "model1" and vehicle number "AH09823"
  Scenario: Retrieve non-existing car from database by id
    When I request car by id = 101 from database through service
    Then request complete with code 404(NOT_FOUND) and indicates that specified car not found
  Scenario: Save new car into database
    When I save new car with brand "model" and vehicle number "AH0123"
    Then saved car with brand "model", vehicle number "AH0123"
  Scenario: Save new car into database with duplicate vehicle number
    When I save new car with brand "model2" and vehicle number "AH09823"
    Then the response should indicate that vehicle number already owned by another car with code 400
  Scenario: Update existing car
    When I try to update car with id = 1 changing brand to "model2" and vehicle number to "AH09824"
    Then updated car with brand "model2" and vehicle number to "AH09824" should be returned
  Scenario: Update existing car with vehicle number already defined within database
    When  I try to update car with id = 2 changing vehicle number to "AH0123"
    Then the response should indicate that updated vehicle number already owned by another car with code 400
  Scenario: Soft delete of car
    When I try to delete car with id = 1
    Then the response should indicate successful delete of car with code 204(NO_CONTENT)
  Scenario: Soft delete of car with non-existing id
    When I try to delete car with id = 205
    Then request complete with code 404(NOT_FOUND) and indicates that specified for delete car not found