#noinspection CucumberUndefinedStep
Feature: Sync the file with the Deck. Find similar cards

  Scenario: SE_SI_01 Update cards in the file.
    Given Add the following cards into the deck:
      | Term          | Definition            | updatedAt |
      | Sample Term 1 | Sample Definition 1   | 0         |
      | Sample Term 2 | Sample Definition 2   | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 11 | Sample Definition 11 | Other 1 |
      | Sample Term 22 | Sample Definition 22 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 11 | Sample Definition 11 | Other 1 |
      | Sample Term 22 | Sample Definition 22 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_SI_02 Update cards in the deck.
    Given Add the following cards into the deck:
      | Term           | Definition             | updatedAt |
      | Sample Term 11 | Sample Definition 11   | 2         |
      | Sample Term 22 | Sample Definition 22   | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1  | Sample Definition 1  | Other 1 |
      | Sample Term 2  | Sample Definition 2  | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 11 | Sample Definition 11 | Other 1 |
      | Sample Term 22 | Sample Definition 22 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_SI_03 Update deck cards with blank term/definition from file.
    Given Add the following cards into the deck:
      | Term          | Definition            | updatedAt |
      | Sample Term 1 | Sample Definition 1   | 0         |
      | Sample Term 2 | Sample Definition 2   | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 11 |                      | Other 1 |
      |                | Sample Definition 22 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 11 |                      | Other 1 |
      |                | Sample Definition 22 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_SI_04 Update deck cards with blank term/definition from deck.
    Given Add the following cards into the deck:
      | Term          | Definition            | updatedAt |
      | Sample Term 1 |                       | 0         |
      |               | Sample Definition 2   | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 11 | Sample Definition 22 | Other 1 |
      | Sample Term 22 | Sample Definition 22 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 11 | Sample Definition 22 | Other 1 |
      | Sample Term 22 | Sample Definition 22 | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  @disabled
  Scenario Outline: SE_SI_05 Multithreading, Prevent the same card from being matched multiple times.
    Given The deck of <num> generated cards.
      | Sample Term deck duplicated | Sample Definition deck duplicated | 0 |
    Given The deck of <num> generated cards.
      | Sample Term deck {i} | Sample Definition deck {i} | 0 |
    Given The file of <num> generated cards with last modified 1.
      | Sample Term file duplicated | Sample Definition file duplicated |
    Given The file of <num> generated cards with last modified 1.
      | Sample Term file {i} | Sample Definition file {i} |
    When Synchronize the file with the deck syncAt=2.
    Then 1002 cards in the deck.
    Then 1002 cards in the file.
    Examples:
      | num |
      | 501 |