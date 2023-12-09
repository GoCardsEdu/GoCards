package pl.gocards.filesync.tests.csv;

import org.junit.runner.RunWith;

import io.cucumber.junit.Cucumber;
import io.cucumber.junit.CucumberOptions;

@RunWith(Cucumber.class)
@CucumberOptions(
        features = "src/test/assets",
        glue = "pl.gocards.filesync.tests.csv",
        // tags="@single"
        tags = "not @disabled"
)
public class RunCsvFileSyncUnitTest {
}