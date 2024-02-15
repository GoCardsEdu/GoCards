#noinspection CucumberUndefinedStep
Feature: Sync the file with the deck: Add new cards to a deck


  Scenario: SE_AD_D_01 1 new card at the beginning: deck
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | New card 1    | New card 1          | 2         |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | New card 1    | New card 1          |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_D_02 2 new cards at the beginning: deck
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | New card 1    | New card 1          | 2         |
      | New card 2    | New card 2          | 2         |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | New card 1    | New card 1          |         |
      | New card 2    | New card 2          |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_D_03 2 new empty cards at the beginning: deck
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      |               |                     | 2         |
      |               |                     | 2         |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      |               |                     |         |
      |               |                     |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_D_04 1 new card in the middle: deck
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | New card 1    | New card 1          | 2         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_D_05 2 new cards in the middle: deck
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | New card 1    | New card 1          | 2         |
      | New card 2    | New card 2          | 2         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          |         |
      | New card 2    | New card 2          |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_D_06 3 new cards in the middle: deck
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | New card 1    | New card 1          | 2         |
      | New card 2    | New card 2          | 2         |
      | New card 3    | New card 3          | 2         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          |         |
      | New card 2    | New card 2          |         |
      | New card 3    | New card 3          |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_D_07 2 new empty cards in the middle: deck
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      |               |                     | 2         |
      |               |                     | 2         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      |               |                     |         |
      |               |                     |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_D_08 1 new card at the end: deck
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | New card 1    | New card 1          | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          |         |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_D_09 2 new card at the end: deck
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | New card 1    | New card 1          | 2         |
      | New card 2    | New card 2          | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          |         |
      | New card 2    | New card 2          |         |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_D_10 2 new empty cards at the end: deck
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      |               |                     | 2         |
      |               |                     | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      |               |                     |         |
      |               |                     |         |
    Then Check the cards in the deck.
    Then Check the cards in the file.