#noinspection CucumberUndefinedStep
Feature: Sync the file with the deck: Add new cards and reorder


  Scenario: SE_RO_AD_D_01 The order from the file. A new card was added from the deck at the beginning.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | New card 1    | New card 1          | 2         |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | New card 1    | New card 1          |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_D_02 The order from the file. 2 new cards were added from the deck at the beginning.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | New card 1    | New card 1          | 2         |
      | New card 2    | New card 2          | 2         |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | New card 1    | New card 1          |         |
      | New card 2    | New card 2          |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_D_03 The order from the file. A new card was added from the deck in the middle.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | New card 1    | New card 1          | 2         |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          |         |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_D_04 The order from the file. 2 new cards were added from the deck in the middle.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | New card 1    | New card 1          | 2         |
      | New card 2    | New card 2          | 2         |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          |         |
      | New card 2    | New card 2          |         |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_D_05 The order from the file. A new card was added from the deck at the end.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | New card 1    | New card 1          | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_D_06 The order from the file. 2 new cards were added from the deck in the end.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | New card 1    | New card 1          | 2         |
      | New card 2    | New card 2          | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          |         |
      | New card 2    | New card 2          |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_F_01 The order from the deck. A new card was added from the deck at the beginning.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 2         |
      | Sample Term 2 | Sample Definition 2 | 2         |
      | Sample Term 3 | Sample Definition 3 | 2         |
    Given Add the following cards into the file updatedAt=1:
      | New card 1    | New card 1          | New card 1 |
      | Sample Term 3 | Sample Definition 3 | Other 3    |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | Sample Term 1 | Sample Definition 1 | Other 1    |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | New card 1    | New card 1          | New card 1 |
      | Sample Term 1 | Sample Definition 1 | Other 1    |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | Sample Term 3 | Sample Definition 3 | Other 3    |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_F_02 The order from the deck. 2 new cards were added from the deck at the beginning.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 2         |
      | Sample Term 2 | Sample Definition 2 | 2         |
      | Sample Term 3 | Sample Definition 3 | 2         |
    Given Add the following cards into the file updatedAt=1:
      | New card 1    | New card 1          | New card 1 |
      | New card 2    | New card 2          | New card 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3    |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | Sample Term 1 | Sample Definition 1 | Other 1    |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | New card 1    | New card 1          | New card 1 |
      | New card 2    | New card 2          | New card 2 |
      | Sample Term 1 | Sample Definition 1 | Other 1    |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | Sample Term 3 | Sample Definition 3 | Other 3    |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_F_03 The order from the deck. A new card was added from the deck in the middle.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 2         |
      | Sample Term 2 | Sample Definition 2 | 2         |
      | Sample Term 3 | Sample Definition 3 | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 3 | Sample Definition 3 | Other 3    |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | New card 1    | New card 1          | New card 1 |
      | Sample Term 1 | Sample Definition 1 | Other 1    |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1    |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | New card 1    | New card 1          | New card 1 |
      | Sample Term 3 | Sample Definition 3 | Other 3    |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_F_04 The order from the deck. 2 new cards were added from the deck in the middle.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 2         |
      | Sample Term 2 | Sample Definition 2 | 2         |
      | Sample Term 3 | Sample Definition 3 | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 3 | Sample Definition 3 | Other 3    |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | New card 1    | New card 1          | New card 1 |
      | New card 2    | New card 2          | New card 2 |
      | Sample Term 1 | Sample Definition 1 | Other 1    |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1    |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | New card 1    | New card 1          | New card 1 |
      | New card 2    | New card 2          | New card 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3    |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_F_05 The order from the deck. A new card was added from the deck at the end.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 2         |
      | Sample Term 2 | Sample Definition 2 | 2         |
      | Sample Term 3 | Sample Definition 3 | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 3 | Sample Definition 3 | Other 3    |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | Sample Term 1 | Sample Definition 1 | Other 1    |
      | New card 1    | New card 1          | New card 1 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1    |
      | New card 1    | New card 1          | New card 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | Sample Term 3 | Sample Definition 3 | Other 3    |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_F_06 The order from the deck. 2 new cards were added from the deck in the end.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 1 | Sample Definition 1 | 2         |
      | Sample Term 2 | Sample Definition 2 | 2         |
      | Sample Term 3 | Sample Definition 3 | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 3 | Sample Definition 3 | Other 3    |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | Sample Term 1 | Sample Definition 1 | Other 1    |
      | New card 1    | New card 1          | New card 1 |
      | New card 2    | New card 2          | New card 2 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1    |
      | New card 1    | New card 1          | New card 1 |
      | New card 2    | New card 2          | New card 2 |
      | Sample Term 2 | Sample Definition 2 | Other 2    |
      | Sample Term 3 | Sample Definition 3 | Other 3    |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_01 The order from the file. 2 new cards were added at the beginning.
  A new card from the deck was added.
  A new card from the imported file was added.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Deck 1        | Deck 1              | 2         |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | File 1        | File 1              | File 1  |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File 1        | File 1              | File 1  |
      | Deck 1        | Deck 1              |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_02 The order from the file. 4 new cards were added at the beginning.
  At the beginning, 2 new card from the deck were added.
  At the beginning, 2 new card from the imported file were added.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Deck 1        | Deck 1              | 2         |
      | Deck 2        | Deck 2              | 2         |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
      | Deck 1        | Deck 1              |         |
      | Deck 2        | Deck 2              |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_03 The order from the file. 2 new cards was added in the middle.
  In the middle, a new card from the deck was added.
  In the middle, a new card from the imported file was added.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Deck 1        | Deck 1              | 2         |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | File 1        | File 1              | File 1  |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Deck 1        | Deck 1              |         |
      | File 1        | File 1              | File 1  |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_04 The order from the file. 4 new cards were added in the middle.
  In the middle, 2 new card from the deck were added.
  In the middle, 2 new card from the imported file were added.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Deck 1        | Deck 1              | 2         |
      | Deck 2        | Deck 2              | 2         |
      | Sample Term 1 | Sample Definition 1 | 0         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Deck 1        | Deck 1              |         |
      | Deck 2        | Deck 2              |         |
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_05 The order from the file. 2 new card were added at the end.
  At the end, a new card from the deck was added.
  At the end, a new card from the imported file was added.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Deck 1        | Deck 1              | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | File 1        | File 1              | File 1  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Deck 1        | Deck 1              |         |
      | File 1        | File 1              | File 1  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_06 The order from the file. 4 new cards were added in the end.
  At the end, 2 new card from the deck were added.
  At the end, 2 new card from the imported file were added.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Deck 1        | Deck 1              | 2         |
      | Deck 2        | Deck 2              | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Deck 1        | Deck 1              |         |
      | Deck 2        | Deck 2              |         |
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.

  Scenario: SE_RO_AD_07 The order from the file. 2 new card were added at the end.
  At the end, a new card from the deck was added.
  At the end, a new card from the imported file was added.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Deck 1        | Deck 1              | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | File 1        | File 1              | File 1  |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Deck 1        | Deck 1              |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | File 1        | File 1              | File 1  |
    Then Check the cards in the deck.
    Then Check the cards in the file.


  Scenario: SE_RO_AD_08 The order from the file. 4 new cards were added in the end.
  At the end, 2 new card from the deck were added.
  At the end, 2 new card from the imported file were added.
    Given Add the following cards into the deck:
      | Term          | Definition          | updatedAt |
      | Sample Term 3 | Sample Definition 3 | 0         |
      | Sample Term 2 | Sample Definition 2 | 0         |
      | Sample Term 1 | Sample Definition 1 | 0         |
      | Deck 1        | Deck 1              | 2         |
      | Deck 2        | Deck 2              | 2         |
    Given Add the following cards into the file updatedAt=1:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
    When Synchronize the file with the deck syncAt=2.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Deck 1        | Deck 1              |         |
      | Deck 2        | Deck 2              |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
    Then Check the cards in the deck.
    Then Check the cards in the file.