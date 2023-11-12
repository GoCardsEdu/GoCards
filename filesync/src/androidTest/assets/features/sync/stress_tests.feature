#noinspection CucumberUndefinedStep
Feature: Sync the file with the deck: Find similar cards. Stress tests


  @disabled
  Scenario Outline: ST_01 No changes <repeat> cards.
    Given Generate cards <repeat> times into the deck:
      | Sample Term {i} | Sample Definition {i} | 0 |
    Given Generate cards <repeat> times into the file:
      | Sample Term {i} | Sample Definition {i} | Other {i} |
    When Synchronize the file with the deck syncAt=2.
    Then <repeat> cards in the file.
    Then The expected deck with cards:
      | Sample Term 1 | Sample Definition 1 | Other 1 |
      | Sample Term 2 | Sample Definition 2 | Other 2 |
      | Sample Term 3 | Sample Definition 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Examples:
      | repeat |
      | 2000   |


  @disabled
  Scenario Outline: ST_02 Similar <repeat> cards.
    Given Generate cards <repeat> times into the deck:
      | Sample Term deck {i} | Sample Definition deck {i} | 0 |
    Given Generate cards <repeat> times into the file:
      | Sample Term file {i} | Sample Definition file {i} | Other {i} |
    When Synchronize the file with the deck syncAt=2.
    Then <repeat> cards in the deck.
    Then <repeat> cards in the file.
    Then The expected deck with cards:
      | Sample Term file 1 | Sample Definition file 1 | Other 1 |
      | Sample Term file 2 | Sample Definition file 2 | Other 2 |
      | Sample Term file 3 | Sample Definition file 3 | Other 3 |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Examples:
      | repeat |
      | 2000   |


  @disabled
  Scenario Outline: ST_03 Merging new cards.
    Given Generate cards <repeat> times into the deck:
      | Common card   {i} | Common card   {i} | 0 |
      | Deck Deck 1   {i} | Deck Deck 1   {i} | 2 |
      | Deck Deck 2   {i} | Deck Deck 2   {i} | 2 |
      | Deck Deck 3   {i} | Deck Deck 3   {i} | 2 |
    Given Generate cards <repeat> times into the file:
      | Common card   {i} | Common card   {i} | Other {i} |
      | File File 1 {i}   | File File 1 {i}   | Other {i} |
      | File File 2 {i}   | File File 2 {i}   | Other {i} |
      | File File 3 {i}   | File File 3 {i}   | Other {i} |
    When Synchronize the file with the deck syncAt=2.
    Then <expected> cards in the deck.
    Then <expected> cards in the file.
    Then The expected deck with cards:
      | Common card   1 | Common card   1 | Other 1 |
      | Deck Deck 1   1 | Deck Deck 1   1 |         |
      | Deck Deck 2   1 | Deck Deck 2   1 |         |
      | Deck Deck 3   1 | Deck Deck 3   1 |         |
      | File File 1 1   | File File 1 1   | Other 2 |
      | File File 2 1   | File File 2 1   | Other 3 |
      | File File 3 1   | File File 3 1   | Other 4 |
    Then Check the cards in the deck.
    Then Check the cards in the file.
    Examples:
      | repeat | expected |
      | 200    | 1400     |


  @disabled
  Scenario Outline: Check overwriting only changed rows in the exported file.
  If only all records are saved to the file:
  updateFile=00:00:00.325
  If only changed records are saved to the file:
  updateFile=00:00:00.095
    Given Add the following cards into the deck:
      | Swap places 1 | Swap places 1 | 2 |
      | Swap places 2 | Swap places 2 | 2 |
    Given Generate cards <repeat> times into the deck:
      | Sample Term {i} | Sample Definition {i} | 0 |
    Given Add the following cards into the file updatedAt=1:
      | Swap places 2 | Swap places 2 |
      | Swap places 1 | Swap places 1 |
    Given Generate cards <repeat> times into the file:
      | Sample Term {i} | Sample Definition {i} |
    When Synchronize the file with the deck syncAt=2.
    Then <expected> cards in the deck.
    Then <expected> cards in the file.
    Examples:
      | repeat | expected |
      | 1000   | 1002     |