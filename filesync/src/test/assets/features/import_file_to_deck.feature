#noinspection CucumberUndefinedStep
Feature: Import the file and create a new deck.


  Scenario: IE_01 Found 1 column, no header columns.
    Given The following file:
      |               |
      | Sample Term 1 |
      | Sample Term 2 |
    When Import the file into the deck.
    Then New deck with the following cards:
      | Sample Term 1 |  |
      | Sample Term 2 |  |


  Scenario: IE_02 Found 1 column with only the Term header.
    Given The following file:
      |               |
      | Term          |
      | Sample Term 1 |
      | Sample Term 2 |
    When Import the file into the deck.
    Then New deck with the following cards:
      | Sample Term 1 |  |
      | Sample Term 2 |  |


  Scenario: IE_03 Found 1 column with only the Definition header.
    Given The following file:
      |                     |
      | Definition          |
      | Sample Definition 1 |
      | Sample Definition 2 |
    When Import the file into the deck.
    Then New deck with the following cards:
      |  | Sample Definition 1 |
      |  | Sample Definition 2 |


  Scenario: IE_04 Found 2 header columns: Term and Definition
    Given The following file:
      |               |                     |    |
      | Term          | Definition          | NU |
      | Sample Term 1 | Sample Definition 1 | NU |
      | Sample Term 2 | Sample Definition 2 | NU |
    When Import the file into the deck.
    Then New deck with the following cards:
      | Sample Term 1 | Sample Definition 1 |
      | Sample Term 2 | Sample Definition 2 |


  Scenario: IE_04 The spreadsheet includes the header in the middle with the Term and Definition.
    Given The following file:
      | Sample Term 1 | Sample Definition 1 |
      | Term          | Definition          |
      | Sample Term 2 | Sample Definition 2 |
    When Import the file into the deck.
    Then New deck with the following cards:
      | Sample Term 1 | Sample Definition 1 |
      | Term          | Definition          |
      | Sample Term 2 | Sample Definition 2 |


  Scenario: IE_06 Found 2 columns, no header columns.
    Given The following file:
      |  |               |  |                     |
      |  | Sample Term 1 |  | Sample Definition 1 |
      |  |               |  |                     |
      |  | Sample Term 2 |  | Sample Definition 2 |
      |  |               |  |                     |
    When Import the file into the deck.
    Then New deck with the following cards:
      | Sample Term 1 | Sample Definition 1 |
      |               |                     |
      | Sample Term 2 | Sample Definition 2 |
      |               |                     |


  Scenario: IE_07 Found 2 columns, no header columns.
    Given The following file:
      |               |                     |
      | Sample Term 1 |                     |
      |               | Sample Definition 2 |
    When Import the file into the deck.
    Then New deck with the following cards:
      | Sample Term 1 |                     |
      |               | Sample Definition 2 |


  Scenario: IE_08 Found 2 columns with only the Term header.
    Given The following file:
      |                     |          |               |
      |                     | Not used | Terms         |
      |                     | Not used | Sample Term 1 |
      | Sample Definition 2 | Not used | Sample Term 2 |
      | Sample Definition 3 | Not used | Sample Term 3 |
    When Import the file into the deck.
    Then New deck with the following cards:
      | Sample Term 1 |                     |
      | Sample Term 2 | Sample Definition 2 |
      | Sample Term 3 | Sample Definition 3 |


  Scenario: IE_09 Found 2 columns with only the Definition header.
    Given The following file:
      |               |          |                     |
      |               | Not used | Definitions         |
      |               | Not used | Sample Definition 1 |
      | Sample Term 2 | Not used | Sample Definition 2 |
      | Sample Term 3 | Not used | Sample Definition 3 |
    When Import the file into the deck.
    Then New deck with the following cards:
      |               | Sample Definition 1 |
      | Sample Term 2 | Sample Definition 2 |
      | Sample Term 3 | Sample Definition 3 |


  Scenario: IE_10 Found 3 header columns: Term, Definition, Disabled
    Given The following file:
      |          |                     |    |               |    |          |
      | Terms    | DefiNItions ab      | NU | TeRms ab      | NU | Disabled |
      | Not used | Sample Definition 1 | NU | Sample Term 1 | NU | TRUE     |
      | Not used | Sample Definition 2 | NU | Sample Term 2 | NU | FALSE    |
      | Not used | Sample Definition 3 | NU | Sample Term 3 | NU |          |
    When Import the file into the deck.
    Then New deck with the following cards:
      | Sample Term 1 | Sample Definition 1 | TRUE  |
      | Sample Term 2 | Sample Definition 2 | FALSE |
      | Sample Term 3 | Sample Definition 3 | FALSE |
  

#  Scenario Outline: Stress test. Import a lot of cards.
#    Given Generate <repeat> rows.
#    Given The following file:
#      | Sample Definition {$i} | Sample Term {$i} |
#    When Import the file into the deck.
#
#    Examples:
#      | repeat  |
#      | 100000 |