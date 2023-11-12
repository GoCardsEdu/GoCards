#noinspection CucumberUndefinedStep
Feature: Sync the file with the deck: others


  Scenario: Check overwriting only changed rows in the file.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Swap places 1 | Swap places 1       | 2         |
      | Swap places 2 | Swap places 2       | 2         |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 4 | Sample Definition 4 | 0         |
      | Sample Term 5 | Sample Definition 5 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Swap places 2 | Swap places 2       |
      | Swap places 1 | Swap places 1       |
      | Sample Term 3 | Sample Definition 3 |
      | Sample Term 4 | Sample Definition 4 |
      | Sample Term 5 | Sample Definition 5 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Swap places 1 | Swap places 1       |
      | Swap places 2 | Swap places 2       |
      | Sample Term 3 | Sample Definition 3 |
      | Sample Term 4 | Sample Definition 4 |
      | Sample Term 5 | Sample Definition 5 |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then Updated 2 rows in the file.


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