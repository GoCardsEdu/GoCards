#noinspection CucumberUndefinedStep
Feature: Sync the file with the deck: others



  Scenario: A header and a empty row at the beginning
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Deck 1        | Deck 1              | 2         |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      |               |                     |         |
      | Term          | Definition          | Other   |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Deck 1        | Deck 1              |
      | Sample Term 1 | Sample Definition 1 |
    Then Check the cards in the deck.
    Then The expected deck with cards:
      |               |                     |         |
      | Term          | Definition          | Other   |
      | Deck 1        | Deck 1              |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
    Then Check the cards in the file.
    Then No exceptions should be logged.