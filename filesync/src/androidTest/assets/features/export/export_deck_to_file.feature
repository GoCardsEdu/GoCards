#noinspection CucumberUndefinedStep
Feature: Export the deck to a file


  Scenario: EX_01 Export the deck to a file.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 2         |
      | Sample Term 2 | Sample Definition 2 | 2         |
      | Sample Term 3 | Sample Definition 3 | 2         |
    When Export the deck to a file.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 |
      | Sample Term 2 | Sample Definition 2 |
      | Sample Term 3 | Sample Definition 3 |
    Then Check the cards in the deck.
    Then The expected deck with cards:
      | Term          | Definition          | Disabled |
      | Sample Term 1 | Sample Definition 1 | FALSE    |
      | Sample Term 2 | Sample Definition 2 | FALSE    |
      | Sample Term 3 | Sample Definition 3 | FALSE    |
    Then Check the cards in the file.