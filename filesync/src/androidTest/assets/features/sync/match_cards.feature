#noinspection CucumberUndefinedStep
Feature: Sync the file with the Deck. Match cards


  Scenario: SE_MA_F_01 Match cards by Term and Definition. The file is newer.
    Given Add the following cards into the deck:
      | Term      | Definition      | updatedAt |
      | Deck Term | Deck Definition | 0         |
    Given Add the following cards into the file updatedAt=1:
      | File Term | File Definition | Other |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File Term | File Definition | Other |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 1 cards in the deck.
    Then Deleted 0 cards.


  Scenario: SE_MA_D_01 Match cards by Term and Definition. The deck is newer.
    Given Add the following cards into the deck:
      | Term      | Definition      | createdAt |
      | Deck Term | Deck Definition | 2         |
    Given Add the following cards into the file updatedAt=1:
      | File Term | File Definition | Other |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File Term | File Definition | Other |
      | Deck Term | Deck Definition |       |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 2 cards in the deck.
    Then Deleted 0 cards.


  Scenario: SE_MA_F_02 Match cards by Term. The file is newer.
    Given Add the following cards into the deck:
      | Term      | Definition      | updatedAt |
      | Deck Term |                 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | File Term | File Definition | Other |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File Term | File Definition | Other |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 1 cards in the deck.
    Then Deleted 0 cards.


  Scenario: SE_MA_D_02 Match cards by Term. The deck is newer.
    Given Add the following cards into the deck:
      | Term       | Definition      | createdAt |
      | Deck Term  |                 | 2         |
    Given Add the following cards into the file updatedAt=1:
      | File Term  | File Definition | Other  |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File Term  | File Definition | Other |
      | Deck Term  |                 |       |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 2 cards in the deck.
    Then Deleted 0 cards.


  Scenario: SE_MA_F_03 Match cards by Definition. The file is newer.
    Given Add the following cards into the deck:
      | Term      | Definition      | updatedAt |
      |           | Deck Definition | 0         |
    Given Add the following cards into the file updatedAt=1:
      | File Term | File Definition | Other |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File Term | File Definition | Other |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 1 cards in the deck.
    Then Deleted 0 cards.


  Scenario: SE_MA_D_03 Match cards by Definition. The deck is newer.
    Given Add the following cards into the deck:
      | Term      | Definition      | createdAt |
      |           | Deck Definition | 2         |
    Given Add the following cards into the file updatedAt=1:
      | File Term | File Definition | Other |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File Term | File Definition | Other |
      |           | Deck Definition |       |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 2 cards in the deck.
    Then Deleted 0 cards.


  Scenario: SE_MA_04 Create a card from the deck and a card from the file if both were created after the last EXPORT.
    Given Add the following cards into the deck:
      | Term      | Definition      |
      | Deck Term | Deck Definition |
    When Export the deck to a file syncAt=1.
    Given Clear file.
    Given Add the following cards into the file updatedAt=2:
      | File Term | File Definition | Other |
    Given Update the following cards in the deck:
      | createdAt |
      | 3         |
    When Synchronize the file with the deck syncAt=4.
    Then The expected deck with cards:
      | File Term | File Definition | Other |
      | Deck Term | Deck Definition |       |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 2 cards in the deck.
    Then Deleted 0 cards.


  Scenario: SE_MA_05 Create a card from the deck and a card from the file if both were created after the last SYNC.
    Given Add the following cards into the deck:
      | Term      | Definition      |
      | Deck Term | Deck Definition |
    Given Add the following cards into the file updatedAt=1:
      | Deck Term | Deck Definition | Other     |
    When Synchronize the file with the deck syncAt=2.
    Given Clear file.
    Given Add the following cards into the file updatedAt=3:
      | File Term | File Definition | Other |
    Given Update the following cards in the deck:
      | createdAt |
      | 4         |
    When Synchronize the file with the deck syncAt=5.
    Then The expected deck with cards:
      | File Term | File Definition | Other |
      | Deck Term | Deck Definition |       |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 2 cards in the deck.
    Then Deleted 0 cards.


  Scenario: SE_MA_F_06 Match cards with SQL escape square brackets. The file is newer.
    Given Add the following cards into the deck:
      | Term   | Definition   | createdAt |
      | []Term | []Definition | 0         |
    Given Add the following cards into the file updatedAt=1:
      | []Term | []Definition | Other     |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | []Term | []Definition | Other |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 1 cards in the deck.
    Then Updated 1 cards.


  @disabled
  Scenario Outline: SE_MA_04 Multithreading, Prevent the same card from being matched multiple times.
    Given The deck of <num> generated cards.
      | Deck Term duplicated | Deck Definition duplicated | 0 |
    Given The deck of <num> generated cards.
      | Deck Term {i}        | Deck Definition {i}        | 0 |
    Given The file of <num> generated cards with last modified 1.
      | File Term duplicated | File Definition duplicated |
    Given The file of <num> generated cards with last modified 1.
      | File Term {i}        | File Definition {i} |
    When Synchronize the file with the deck syncAt=2.
    Then 1002 cards in the deck.
    Then 1002 cards in the file.
    Then Deleted 0 cards.
    Examples:
      | num |
      | 501 |