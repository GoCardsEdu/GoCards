#noinspection CucumberUndefinedStep
Feature: Sync the file with the Deck: Update and add cards


  Scenario: SE_UP_AD_01 Add a new card and update cards in the deck.
    Given Add the following cards into the deck:
      | Term           | Definition             | updatedAt |
      | Sample Term 22 | Sample Definition 22   | 0         |
      | Sample Term 11 | Sample Definition 11   | 0         |
    Given Add the following cards into the file updatedAt=1:
      | New card 1     | New card 1           | New 1   |
      | Sample Term 1  | Sample Definition 1  | Other 1 |
      | Sample Term 2  | Sample Definition 2  | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | New card 1     | New card 1           | New 1   |
      | Sample Term 1  | Sample Definition 1  | Other 1 |
      | Sample Term 2  | Sample Definition 2  | Other 2 |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Then Deleted 0 cards.
    Then No exceptions should be logged.