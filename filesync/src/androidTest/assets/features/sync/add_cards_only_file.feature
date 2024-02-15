#noinspection CucumberUndefinedStep
Feature: Sync the file with the deck: Add new cards to a file


  Scenario: SE_AD_F_01 Empty deck with 1 new card: file
    Given Add the following cards into the file updatedAt=1:
      | File 1 | File 1 | File 1 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File 1 | File 1 | File 1 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_F_02 1 new card at the beginning: file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | New card 1    | New card 1          | New 1   |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | New card 1    | New card 1          | New 1   |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_F_02 2 new cards at the beginning: file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_F_03 2 new empty cards at the beginning: file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      |               |                     |         |
      |               |                     |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
    Then Check the cards in the deck.
    Then The expected deck with cards:
      |               |                     |         |
      |               |                     |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
    Then Check the cards in the file.


  Scenario: SE_AD_F_04 1 new card in the middle: file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.

  Scenario: SE_AD_F_05 2 new cards in the middle: file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 4 | Sample Definition 4 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_F_06 3 new cards in the middle: file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
      | New card 3    | New card 3          | New 3   |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
      | New card 3    | New card 3          | New 3   |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.



  Scenario: SE_AD_F_07 2 new empty cards in the middle: file.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      |               |                     |         |
      |               |                     |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      |               |                     |         |
      |               |                     |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_F_08 1 new card at the end: file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_AD_F_09 2 new cards at the end: file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
    Then Check the cards in the deck.
    Then Check the cards in the file.



  Scenario: SE_AD_F_09 Remove the last empty lines in the file
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
      |               |                     |         |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 3 cards in the file.


  Scenario: SE_AD_F_10 If the file has not changed since the last synchronization, do not add a card from a file.
  This means that the card had to be modified or deleted in the deck.
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Given Update the cards in the deck:
      | New Term 4    | New Updated 4       |
      | Sample Term 2 | Sample Definition 2 |
      | Sample Term 3 | Sample Definition 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | New Term 4    | New Updated 4       |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.