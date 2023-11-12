#noinspection CucumberUndefinedStep
Feature: Sync the file with the deck: Remove cards


  Scenario: SE_RE_D_01 Remove all cards in the file.
    Given Add the following cards into the deck:
      | Term          | Definition    | deletedAt |
      | Delete card 1 | Delete card 1 | 1         |
      | Delete card 2 | Delete card 2 | 1         |
    Given Add the following cards into the file updatedAt=1:
      | Delete card 1 | Delete card 1 | Other 1 |
      | Delete card 2 | Delete card 2 | Other 2 |
    When Synchronize the file with the deck syncAt=2.
    Then 0 cards in the file.


  Scenario: SE_RE_D_02 Remove the same card from the deck and the file.
    Given Add the following cards into the deck:
      | Term          | Definition          | deletedAt |
      | Sample Term 1 | Sample Definition 1 |           |
      | Delete card 2 | Delete card 2       | 1         |
      | Sample Term 3 | Sample Definition 3 |           |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RE_D_03 Remove the card after modifying the file.
    Given Add the following cards into the deck:
      | Term          | Definition          | deletedAt |
      | Delete card 1 | Delete card       1 | 3         |
      | Sample Term 2 | Sample Definition 2 |           |
      | Sample Term 3 | Sample Definition 3 |           |
    Given Add the following cards into the file updatedAt=1:
      | Delete card 1 | Delete card 1       | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RE_D_04 Do not delete the same card if it has been added to the same file twice.
    Given Add the following cards into the deck:
      | Term          | Definition          | deletedAt |
      | Sample Term 1 | Sample Definition 1 |           |
      | Delete card 2 | Delete card 2       | 1         |
      | Delete card 3 | Delete card 3       | 1         |
      | Sample Term 4 | Sample Definition 4 |           |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Delete card 2 | Delete card 2       | Other 2 |
      | Delete card 3 | Delete card 3       | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    When Synchronize the file with the deck syncAt=2.
    Then 2 cards in the file.
    Given Clear file.
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Delete card 2 | Delete card 2       | Other 2 |
      | Delete card 3 | Delete card 3       | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    When Synchronize the file with the deck syncAt=2.
    Then 4 cards in the file.


  Scenario: SE_RE_D_05 Delete from 2 different synced files.
    Given Add the following cards into the deck:
      | Term          | Definition          | deletedAt |
      | Sample Term 1 | Sample Definition 1 |           |
      | Delete card 2 | Delete card 2       | 1         |
      | Delete card 3 | Delete card 3       | 1         |
      | Sample Term 4 | Sample Definition 4 |           |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Delete card 2 | Delete card 2       | Other 2 |
      | Delete card 3 | Delete card 3       | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    When Synchronize the file with the deck syncAt=2.
    Then 2 cards in the file.
    Given Create a new 2nd file.
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Delete card 2 | Delete card 2       | Other 2 |
      | Delete card 3 | Delete card 3       | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    When Synchronize the file with the deck syncAt=2.
    Then 2 cards in the file.