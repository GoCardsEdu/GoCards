#noinspection CucumberUndefinedStep
Feature: Sync the file with the deck: Delete cards and reorder


  Scenario: SE_RO_DE_D_01 The order from the file. The card was deleted by the deck at the beginning.
    Given Add the following cards into the deck:
      | Term          | Definition          | deletedAt |
      | Delete card 1 | Delete card 1       | 1         |
      | Sample Term 3 | Sample Definition 3 |           |
      | Sample Term 2 | Sample Definition 2 |           |
      | Sample Term 1 | Sample Definition 1 |           |
    Given Add the following cards into the file updatedAt=1:
      | Delete card 1 | Delete card 1       | Other 4 |
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


  Scenario: SE_RO_DE_D_02 The order from the file. The card was deleted by the deck in the middle.
    Given Add the following cards into the deck:
      | Term          | Definition          | deletedAt |
      | Sample Term 3 | Sample Definition 3 |           |
      | Sample Term 2 | Sample Definition 2 |           |
      | Delete card 1 | Delete card 1       | 1         |
      | Sample Term 1 | Sample Definition 1 |           |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Delete card 1 | Delete card 1       | Other 4 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_DE_F_01 The order from the deck. The card was deleted by the file at the beginning.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Delete card 1 | Delete card 1       | 0         |
      | Sample Term 1 | Sample Definition 1 | 2         |
      | Sample Term 2 | Sample Definition 2 | 2         |
      | Sample Term 3 | Sample Definition 3 | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_RE_F_02 The order from the deck. The card was deleted by the file in the middle.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 2         |
      | Delete card 1 | Delete card 1       | 0         |
      | Sample Term 2 | Sample Definition 2 | 2         |
      | Sample Term 3 | Sample Definition 3 | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_DE_01 The order from the file. 2 cards were deleted at the beginning.
  The card was deleted by the deck.
  The card was deleted by the file.
    Given Add the following cards into the deck:
      | Term          | Definition          | deletedAt |
      | Delete deck 1 | Delete deck 1       |           |
      | Delete deck 1 | Delete deck 1       |           |
      | Delete file 1 | Delete file 1       | 1         |
      | Sample Term 3 | Sample Definition 3 |           |
      | Sample Term 2 | Sample Definition 2 |           |
      | Sample Term 1 | Sample Definition 1 |           |
    Given Add the following cards into the file updatedAt=1:
      | Delete file 1 | Delete file 1       | Other 4 |
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


  Scenario: SE_RO_DE_02 The order from the file. 2 cards were deleted in the middle.
  In the middle, the card was deleted by the deck.
  In the middle, the card was deleted by the file.
    Given Add the following cards into the deck:
      | Term          | Definition          | deletedAt |
      | Sample Term 3 | Sample Definition 3 |           |
      | Sample Term 2 | Sample Definition 2 |           |
      | Delete deck 1 | Delete deck 1       | 1         |
      | Sample Term 1 | Sample Definition 1 |           |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Delete file 1 | Delete file 1       | Other 4 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.