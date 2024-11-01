Feature: Rating Service
  Scenario: Create the initial state of the database
    Given Two ratings in database

  Scenario: Retrieve all rating scores from database
    When I request all rating scores from database through service
    Then all rating scores request complete with code 200 (OK)
    And first rating scores page should be returned
  Scenario: Retrieve all rating scores from database with defined driver id
    When I request all rating scores with driver id = 1 from database through service
    Then all rating scores filtered by driver id request complete with code 200 (OK)
    And first page of filtered by driver id = 1 rating scores should be returned
  Scenario: Retrieve rating score by id from database
    When I request rating score with id = 1 from database through service
    Then find by id request complete with code 200 (OK) of rating scores
    And returned rating score must be with rating = 5 and initiator should be passenger
  Scenario: Retrieve non-existing rating score from database by id
    When I request rating score with id = 101 from database through service
    Then request complete with code 404(NOT_FOUND) and indicates that specified rating score not found
  Scenario: Save new rating score into database
    When I save new rating score with driver id = 1, passenger id = 1 and rating = 5
    Then saved rating score with driver id = 1, passenger id = 1 and rating = 5 should be returned
  Scenario: Update existing rating score
    When I try to update rating score with id = 1 changing rating to 4
    Then updated rating with rating = 4 should be returned