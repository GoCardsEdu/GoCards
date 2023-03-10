Feature: Sync the Excel file with the Deck. Add new cards.

  Scenario: SE_AD_01 2 new cards.
  A new card from the deck was added.
  A new card from the imported file was added.
    Given Add the following cards into the deck:
      | Deck 1 | Deck 1 | 2 |
    Given Add the following cards into the file:
      | File 2 | File 2 | File 2 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | File 2 | File 2 | File 2 |
      | Deck 1 | Deck 1 |        |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_02 2 new cards at the beginning.
  At the beginning, a new card from the deck was added.
  At the beginning, a new card from the imported file was added.
    Given Add the following cards into the deck:
      | Deck 1        | Deck 1              | 2 |
      | Sample Term 1 | Sample Definition 1 | 0 |
      | Sample Term 2 | Sample Definition 2 | 0 |
    Given Add the following cards into the file:
      | File 1        | File 1              | File 1  |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | File 1        | File 1              | File 1  |
      | Deck 1        | Deck 1              |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_03 4 new cards at the beginning.
  At the beginning, 2 new card from the deck were added.
  At the beginning, 2 new card from the imported file were added.
    Given Add the following cards into the deck:
      | Deck 1        | Deck 1              | 2 |
      | Deck 2        | Deck 2              | 2 |
      | Sample Term 1 | Sample Definition 1 | 0 |
      | Sample Term 2 | Sample Definition 2 | 0 |
    Given Add the following cards into the file:
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | File 1        | File 1              | File 1  |
      | File 2        | File 2              | File 2  |
      | Deck 1        | Deck 1              |         |
      | Deck 2        | Deck 2              |         |
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_04 2 new cards in the middle.
  In the middle, a new card from the deck was added.
  In the middle, a new card from the imported file was added.
    Given Add the following cards into the deck:
      | Sample Term 1 | Sample Definition 1 | 0 |
      | Deck 3        | Deck 3              | 2 |
      | Sample Term 2 | Sample Definition 2 | 0 |
    Given Add the following cards into the file:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | File 4        | File 4              | File 4  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Deck 3        | Deck 3              |         |
      | File 4        | File 4              | File 4  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the deck with cards.
    Then Check the Excel file.

  Scenario: SE_AD_05 4 new cards in the middle.
  In the middle, 2 new card from the deck were added.
  In the middle, 2 new card from the imported file were added.
    Given Add the following cards into the deck:
      | Sample Term 1 | Sample Definition 1 | 0 |
      | Deck 3        | Deck 3              | 2 |
      | Deck 4        | Deck 4              | 2 |
      | Sample Term 2 | Sample Definition 2 | 0 |
    Given Add the following cards into the file:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | File 5        | File 5              | File 5  |
      | File 6        | File 6              | File 6  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Deck 3        | Deck 3              |         |
      | Deck 4        | Deck 4              |         |
      | File 5        | File 5              | File 5  |
      | File 6        | File 6              | File 6  |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_06 2 new cards at the end.
  At the end, a new card from the deck was added.
  At the end, a new card from the imported file was added.
    Given Add the following cards into the deck:
      | Sample Term 1 | Sample Definition 1 | 0 |
      | Sample Term 2 | Sample Definition 2 | 0 |
      | Deck 3        | Deck 3              | 2 |
    Given Add the following cards into the file:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | File 4        | File 4              | File 4  |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Deck 3        | Deck 3              |         |
      | File 4        | File 4              | File 4  |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_07 4 new cards at the end.
  At the end, 2 new card from the deck were added.
  At the end, 2 new card from the imported file were added.
    Given Add the following cards into the deck:
      | Sample Term 1 | Sample Definition 1 | 0 |
      | Sample Term 2 | Sample Definition 2 | 0 |
      | Deck 3        | Deck 3              | 2 |
      | Deck 4        | Deck 4              | 2 |
    Given Add the following cards into the file:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | File 5        | File 5              | File 5  |
      | File 6        | File 6              | File 6  |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Deck 3        | Deck 3              |         |
      | Deck 4        | Deck 4              |         |
      | File 5        | File 5              | File 5  |
      | File 6        | File 6              | File 6  |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_F_01 Empty deck. A new card was added from the imported file.
    Given Add the following cards into the file:
      | File 1 | File 1 | File 1 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | File 1 | File 1 | File 1 |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_F_02 A new card was added from the imported file.
    Given Add the following cards into the deck:
      | Sample Term 1 | Sample Definition 1 | 0 |
      | Sample Term 2 | Sample Definition 2 | 0 |
      | Sample Term 3 | Sample Definition 3 | 0 |
      | Sample Term 4 | Sample Definition 4 | 0 |
    Given Add the following cards into the file:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          | New 1   |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          | New 1   |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_F_03 2 new cards were added from the Excel file.
    Given Add the following cards into the deck:
      | Sample Term 1 | Sample Definition 1 | 0 |
      | Sample Term 2 | Sample Definition 2 | 0 |
      | Sample Term 3 | Sample Definition 3 | 0 |
      | Sample Term 4 | Sample Definition 4 | 0 |
    Given Add the following cards into the file:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_F_04 3 new cards were added from the Excel file.
    Given Add the following cards into the deck:
      | Sample Term 1 | Sample Definition 1 | 0 |
      | Sample Term 2 | Sample Definition 2 | 0 |
    Given Add the following cards into the file:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
      | New card 3    | New card 3          | New 3   |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          | New 1   |
      | New card 2    | New card 2          | New 2   |
      | New card 3    | New card 3          | New 3   |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_D_01 A new card was added from the deck.
    Given Add the following cards into the deck:
      | Sample Term 1 | Sample Definition 1 | 0 |
      | Sample Term 2 | Sample Definition 2 | 0 |
      | New card 1    | New card 1          | 2 |
      | Sample Term 3 | Sample Definition 3 | 0 |
      | Sample Term 4 | Sample Definition 4 | 0 |
    Given Add the following cards into the file:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          |         |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_D_02 2 new cards were added from the deck.
    Given Add the following cards into the deck:
      | Sample Term 1 | Sample Definition 1 | 0 |
      | Sample Term 2 | Sample Definition 2 | 0 |
      | New card 1    | New card 1          | 2 |
      | New card 2    | New card 2          | 2 |
      | Sample Term 3 | Sample Definition 3 | 0 |
      | Sample Term 4 | Sample Definition 4 | 0 |
    Given Add the following cards into the file:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | New card 1    | New card 1          |         |
      | New card 2    | New card 2          |         |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
      | Sample Term 4 | Sample Definition 4 | Other 4 |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_D_03 3 new cards were added from the deck.
    Given Add the following cards into the deck:
      | Sample Term 1 | Sample Definition 1 | 0 |
      | New card 1    | New card 1          | 2 |
      | New card 2    | New card 2          | 2 |
      | New card 3    | New card 3          | 2 |
      | Sample Term 2 | Sample Definition 2 | 0 |
    Given Add the following cards into the file:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | New card 1    | New card 1          |         |
      | New card 2    | New card 2          |         |
      | New card 3    | New card 3          |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
    Then Check the deck with cards.
    Then Check the Excel file.


  Scenario: SE_AD_D_04 Add and delete a card in the deck after the first sync.
  If the file has not changed since the last synchronization, do not add a card from a file.
  This means that the card had to be modified or deleted in the deck.
    Given Add the following cards into the file:
      | Sample Term 1 | Sample Definition 1 |
      | Sample Term 2 | Sample Definition 2 |
      | Sample Term 3 | Sample Definition 3 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the deck with cards.
    Then Check the Excel file.
    Given Update the cards in the deck:
      | New Term 4    | New Updated 4       |
      | Sample Term 2 | Sample Definition 2 |
      | Sample Term 3 | Sample Definition 3 |
    When Synchronize the Excel file with the deck.
    Then The expected deck with cards:
      | New Term 4    | New Updated 4       |         |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the deck with cards.
    Then Check the Excel file.