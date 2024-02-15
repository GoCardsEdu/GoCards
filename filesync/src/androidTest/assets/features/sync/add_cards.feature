#noinspection CucumberUndefinedStep
Feature: Sync the file with the deck: Add new cards


  Scenario: SE_AD_01 2 new cards: deck and file
    Given Add the following cards into the deck:
      | Term   | Definition | updatedAt |
      | Deck 1 | Deck 1     | 2         |
    Given Add the following cards into the file updatedAt=1:
      | File 2 | File 2 | File 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File 2 | File 2 | File 2 |
      | Deck 1 | Deck 1 |        |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_02 2 new cards, but only 1 column.
    Given Add the following cards into the deck:
      | Term   | Definition | updatedAt |
      | Deck 1 |            | 2         |
    Given Add the following cards into the file updatedAt=1:
      | File 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File 2 |
      | Deck 1 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_03 2 new cards at the beginning: deck + file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Deck 1        | Deck 1              | 2         |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | File 1        | File 1              | File 1  |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File 1        | File 1              | File 1  |
      | Deck 1        | Deck 1              |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_04 4 new cards at the beginning: deck + file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Deck 1        | Deck 1              | 2         |
      | Deck 2        | Deck 2              | 2         |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
      | Deck 1        | Deck 1              |         |
      | Deck 2        | Deck 2              |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_05 2 new cards in the middle: deck + file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Deck 3        | Deck 3              | 2         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | File 4        | File 4              | File 4  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Deck 3        | Deck 3              |         |
      | File 4        | File 4              | File 4  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.

  Scenario: SE_AD_06 4 new cards in the middle: deck + file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Deck 3        | Deck 3              | 2         |
      | Deck 4        | Deck 4              | 2         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | File 5        | File 5              | File 5  |
      | File 6        | File 6              | File 6  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Deck 3        | Deck 3              |         |
      | Deck 4        | Deck 4              |         |
      | File 5        | File 5              | File 5  |
      | File 6        | File 6              | File 6  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_07 2 new cards at the end: deck + file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Deck 3        | Deck 3              | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | File 4        | File 4              | File 4  |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Deck 3        | Deck 3              |         |
      | File 4        | File 4              | File 4  |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_08 4 new cards at the end: deck + file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Deck 3        | Deck 3              | 2         |
      | Deck 4        | Deck 4              | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | File 5        | File 5              | File 5  |
      | File 6        | File 6              | File 6  |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Deck 3        | Deck 3              |         |
      | Deck 4        | Deck 4              |         |
      | File 5        | File 5              | File 5  |
      | File 6        | File 6              | File 6  |
    Then Check the cards in the deck.
    Then Check the cards in the file.