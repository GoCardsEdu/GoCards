## [FS_PRO_S](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S) Synchronize the deck with a file.
### Main flow
UI:
- [FS_PRO_S.1.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.1.) Check that deck editing is not locked by another export/import/sync process.
- [FS_PRO_S.2.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.2.) If synchronization failed last time, show the Resume Synchronization dialog. \
  If "Start new synchronization" was clicked, continue.
  If "Resume previous synchronization" was clicked FS_PRO_S.AF1. \
  If "Save backup file to disk" was clicked FS_PRO_S.AF2.
- [FS_PRO_S.3.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.3.) Show a warning dialog to always keep a backup of the synced file as the file may be corrupted.
- [FS_PRO_S.4.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.4.) Select a file to sync.
- [FS_PRO_S.5.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.5.) Show a dialog asking if the deck should be auto-synced.
- [FS_PRO_S.6.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.6.) Lock the deck editing.

Worker:
- [FS_PRO_S.7.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.7.) Auto-Sync: Check access to the synced file.
  Otherwise FS_PRO_S.AF3
- [FS_PRO_S.8.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.8.) Check if synchronization is needed: \
  A) If the file has changed since the last sync with this file. \
  B) If the deck has changed since the last sync with this file. \
  Otherwise FS_PRO_S.AF3
- [FS_PRO_S.9.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.9.) Save a backup of the synced file.
- [FS_PRO_S.10.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.10.) Purge the temporary sync entities in the database before starting.
- [FS_PRO_S.11.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.11.) Import cards {@link CardImported} and merge to the exact same deck card {@link Card}.
- [FS_PRO_S.12.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.12.) Match similar cards. Recognize cards which have been updated by the synced file or deck.
- [FS_PRO_S.13.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.) Recognize which cards have been added or removed by the synced file or deck:
- [FS_PRO_S.14.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.14.) Determine a new order of the cards after merging the cards from both sides.
- [FS_PRO_S.15.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.15.) Check if any cards have been added/updated/removed. \
  If not, skip FS_PRO_S.16-17
- [FS_PRO_S.16.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.16.) Apply changes to the file.
- [FS_PRO_S.17.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.17.) Apply changes to the deck.
- [FS_PRO_S.18.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.18.) Show success notification with statistics how many cards have been added/updated/deleted.
- [FS_PRO_S.19.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.19.) Delete the backup of the synced file as not to store a large file.
- [FS_PRO_S.20.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.20.) Unlock the deck editing.

#### [FS_PRO_S.13.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.) Recognize which cards have been added or removed by the synced file or deck:
- [FS_PRO_S.13.1.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.1.) Remove cards from the file already removed by the deck.
  {@link CardImported#STATUS_DELETE_BY_DECK}
- [FS_PRO_S.13.2.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.2.) Find new or removed cards by iterating cards in the deck.
- [FS_PRO_S.13.2.1.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.2.1.) Create {@link CardImported} for missing cards in the synced file.
- [FS_PRO_S.13.2.2.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.2.2.) If the card is newer than the synced file, \
  add card to the synced file {@link CardImported#STATUS_INSERT_BY_DECK}
- [FS_PRO_S.13.2.3.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.2.3.) If the synced file is newer than the card, \
  delete the card to the deck {@link CardImported#STATUS_DELETE_BY_FILE}
- [FS_PRO_S.13.2.4.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.2.4.) Enrich {@link CardImported#nextDeckCardId} and {@link CardImported#previousDeckCardId}. \
  It will be required for {@link #checkIfPositionUnchanged}

- [FS_PRO_S.13.3.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.3.) Find new or removed cards by iterating the new cards in the file.
  {@link CardImported#cardId} is null
- [FS_PRO_S.13.3.1.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.3.1.) If the file has not been modified since the last sync,
  delete the card from the file {@link CardImported#STATUS_DELETE_BY_DECK}
- [FS_PRO_S.13.3.2.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.3.2.) If the file has been modified after the last sync,
  add card to the deck {@link CardImported#STATUS_INSERT_BY_FILE}

- [FS_PRO_S.13.4.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.4.) Iterate through all the cards in the file.
- [FS_PRO_S.13.4.1.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.4.1.) Enrich {@link CardImported#nextFileCardId} and {@link CardImported#previousFileCardId}.
  It is required for {@link #checkIfPositionUnchanged}
- [FS_PRO_S.13.4.2.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.13.4.2.) If the card is in the same place in the deck and file,
  set {@link CardImported#POSITION_STATUS_UNCHANGED}

#### [FS_PRO_S.16.](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.16.) Apply changes to the file.
- [FS_PRO_S.16.1](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.16.1) Update the cards order that already exist in the file to not lose data in other columns not related to the app.
- [FS_PRO_S.16.2](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.16.2) Overwrite only updated cells.
- [FS_PRO_S.16.3](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.16.3) Remove empty lines at the end of the file.

### Alternative Flow
#### [FS_PRO_S.AF1](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.AF1) Resume previous synchronization
If "Resume previous synchronization" was clicked FS_PRO_S.AF1, skip:
- FS_PRO_S.1.3-5
- FS_PRO_S.2.2.
- FS_PRO_S.AF1.1. Restore the synced file backup.
- FS_PRO_S.2.4-10

#### [FS_PRO_S.AF2](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.AF2) Save backup file to disk
If "Save backup file to disk" was clicked FS_PRO_S.AF2.

#### [FS_PRO_S.AF3](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.AF3) No access to the synced file.
When: FS_PRO_S.7.
- FS_PRO_S.AF3.1. Show dialog with exception.

#### [FS_PRO_S.AF4](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_S.AF4) Synchronization is not needed
After: FS_PRO_S.2.1.
- FS_PRO_S.AF3.1. Show notification: nothing to sync.

## [FS_PRO_A](https://github.com/search?q=org%3AGoCardsEdu%20FS_PRO_A) Automatically sync with Excel file when deck is opened or closed.
Same as FS_PRO_S, only skip FS_PRO_S.1.2-5
