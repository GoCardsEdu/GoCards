package pl.gocards.filesync.tests.csv;

import static pl.gocards.filesync.sheet.WorkbookFactory.FILE_EXTENSION_CSV;
import static pl.gocards.filesync.sheet.WorkbookFactory.MIME_TYPE_CSV;

import androidx.annotation.NonNull;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pl.gocards.filesync.tests.steps.ImportFileToDeckStepDefinitions;

public class CsvImportFileToDeckStepDefinitions {

    private final ImportFileToDeckStepDefinitions stepDefinitions = new ImportFileToDeckStepDefinitions(MIME_TYPE_CSV);

    @Before
    public void before(Scenario scenario) {
        stepDefinitions.before(scenario);
    }

    @After
    @SuppressWarnings("unused")
    void closeService() throws Exception {
        stepDefinitions.closeService();
    }

    @Given("Generate {int} rows.")
    public void generate_rows(Integer repeat) {
        stepDefinitions.generate_rows(repeat);
    }

    @Given("The following file:")
    public void the_following_file(@NonNull DataTable dataTable) {
        stepDefinitions.the_following_file(dataTable);
    }

    @When("Import the file into the deck.")
    public void import_the_file_into_the_deck() throws Exception {
        stepDefinitions.import_the_file_into_the_deck(FILE_EXTENSION_CSV);
    }

    @Then("New deck with the following cards:")
    public void new_deck_with_the_following_cards_imported(@NonNull DataTable expectedCards) {
        stepDefinitions.new_deck_with_the_following_cards_imported(expectedCards);
    }
}
