#noinspection CucumberUndefinedStep
Feature: Sync the file with the Deck. Update matched cards.


  Scenario: SE_UPD_F_01 Update cards matched by Term and Definition with newer versions from the file.
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


  Scenario: SE_UPD_D_01 Update cards matched by Term and Definition with newer versions from the deck.
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


  Scenario: SE_UPD_F_02 Update cards matched by Term with newer versions from the file.
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


  Scenario: SE_UPD_D_02 Update cards matched by Term with newer versions from the deck.
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


  Scenario: SE_UPD_F_03 Update cards matched by Term with newer versions from the file.
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


  Scenario: SE_UPD_D_03 Update cards matched by Definition with newer versions from the deck.
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


  Scenario: SE_UPD_04 Create cards from both the deck and the file if they were added after the last sync.
    Given Add the following cards into the file updatedAt=1:
      | Deck Term | Deck Definition | Other |
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


  Scenario: SE_UPD_05 Create cards from both the deck and the file if they were added after the last export.
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


  # Square brackets are special regex characters used to define character classes, e.g., [A-Z0-9].
  Scenario: SE_UPD_F_06 Match cards with SQL escape square brackets. The file is newer.
    Given Add the following cards into the deck:
      | Term   | Definition   |
      | []Term | []Definition |
    Given Add the following cards into the file updatedAt=1:
      | []Term | []Definition | Other     |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | []Term | []Definition | Other |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 1 cards in the deck.
    Then Updated 0 cards.


  Scenario Outline: SE_UPD_F_07 Update disable=<FILE> with newer versions from the file.
    Given Add the following cards into the deck:
      | Term        | Definition        | Disabled |
      | Sample Term | Sample Definition | <DECK>   |
    Given Add the following cards into the file updatedAt=1:
      | Term        | Definition        | Disabled |
      | Sample Term | Sample Definition | <FILE>   |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Term        | Definition        | Disabled |
      | Sample Term | Sample Definition | <FILE>   |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 1 cards in the deck.
    Then Updated 1 cards.
    Examples:
      | DECK  | FILE  |
      | TRUE  | FALSE |
      | FALSE | TRUE  |


  Scenario Outline: SE_UPD_F_08 Update disable=<DECK> with newer versions from the deck.
    Given Add the following cards into the deck:
      | Term        | Definition        | Disabled | createdAt |
      | Sample Term | Sample Definition | <DECK>   | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Term        | Definition        | Disabled |
      | Sample Term | Sample Definition | <FILE>   |
    When Synchronize the file with the deck syncAt=3.
    Then The expected deck with cards:
      | Term        | Definition        | Disabled |
      | Sample Term | Sample Definition | <DECK>   |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then 1 cards in the deck.
    Then Updated 1 cards.
    Examples:
      | DECK  | FILE  |
      | TRUE  | FALSE |
      | FALSE | TRUE  |


  @disabled
  Scenario Outline: SE_UPD_04 Multithreading, Prevent the same card from being matched multiple times.
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