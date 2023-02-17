Feature: Import an Excel file and create a new deck.

  Scenario: IE_01 Found 2 header columns: Term and Definition
    Given The following Excel file:
      |          |                     |    |               |    |
      | Not used | DefiNItions ab      | NU | TeRms ab      | NU |
      | Not used | Sample definition 1 | NU | Sample term 1 | NU |
      | Not used | Sample definition 2 | NU | Sample term 2 | NU |
    When Import the Excel file into the deck.
    Then A new deck with the following cards imported.
      | Sample term 1 | Sample definition 1 |
      | Sample term 2 | Sample definition 2 |


  Scenario: IE_02 Found 2 columns with only the Term header.
    Given The following Excel file:
      |                     |          |               |
      |                     | Not used | Terms         |
      |                     | Not used | Sample term 1 |
      | Sample definition 2 | Not used | Sample term 2 |
      | Sample definition 3 | Not used | Sample term 3 |
    When Import the Excel file into the deck.
    Then A new deck with the following cards imported.
      | Sample term 1 |                     |
      | Sample term 2 | Sample definition 2 |
      | Sample term 3 | Sample definition 3 |


  Scenario: IE_03 Found 2 columns with only the Definition header.
    Given The following Excel file:
      |               |          |                     |
      |               | Not used | Definitions         |
      |               | Not used | Sample definition 1 |
      | Sample term 2 | Not used | Sample definition 2 |
      | Sample term 3 | Not used | Sample definition 3 |
    When Import the Excel file into the deck.
    Then A new deck with the following cards imported.
      |               | Sample definition 1 |
      | Sample term 2 | Sample definition 2 |
      | Sample term 3 | Sample definition 3 |


  Scenario: IE_04 Found 1 column with only the Term header.
    Given The following Excel file:
      |               |
      | Term          |
      | Sample term 1 |
      | Sample term 2 |
    When Import the Excel file into the deck.
    Then A new deck with the following cards imported.
      | Sample term 1 |  |
      | Sample term 2 |  |


  Scenario: IE_05 Found 1 column with only the Definition header.
    Given The following Excel file:
      |                     |
      | Definition          |
      | Sample definition 1 |
      | Sample definition 2 |
    When Import the Excel file into the deck.
    Then A new deck with the following cards imported.
      |  | Sample definition 1 |
      |  | Sample definition 2 |


  Scenario: IE_06 Found 2 columns, no header columns.
    Given The following Excel file:
      |  |               |  |                     |
      |  | Sample term 1 |  | Sample definition 1 |
      |  |               |  |                     |
      |  | Sample term 2 |  | Sample definition 2 |
      |  |               |  |                     |
    When Import the Excel file into the deck.
    Then A new deck with the following cards imported.
      | Sample term 1 | Sample definition 1 |
      | Sample term 2 | Sample definition 2 |


  Scenario: IE_06 Found 2 columns, no header columns.
    Given The following Excel file:
      |               |                     |
      | Sample term 1 |                     |
      |               | Sample definition 2 |
    When Import the Excel file into the deck.
    Then A new deck with the following cards imported.
      | Sample term 1 |                     |
      |               | Sample definition 2 |


  Scenario: IE_07 Found 1 column, no header columns.
    Given The following Excel file:
      |               |
      | Sample term 1 |
      | Sample term 2 |
    When Import the Excel file into the deck.
    Then A new deck with the following cards imported.
      | Sample term 1 |  |
      | Sample term 2 |  |


  Scenario: IE_08 The spreadsheet includes the header in the middle with the Term and Definition.
    Given The following Excel file:
      | Sample term 1 | Sample definition 1 |
      | Term          | Definition          |
      | Sample term 2 | Sample definition 2 |
    When Import the Excel file into the deck.
    Then A new deck with the following cards imported.
      | Sample term 1 | Sample definition 1 |
      | Term          | Definition          |
      | Sample term 2 | Sample definition 2 |


#  Scenario Outline: Stress test. Import a lot of cards.
#    Given Generate <repeat> rows.
#    Given The following Excel file:
#      | Sample definition {$i} | Sample term {$i} |
#    When Import the Excel file into the deck.
#
#    Examples:
#      | repeat  |
#      | 100000 |