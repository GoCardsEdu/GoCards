package pl.gocards.filesync.tests.steps;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import androidx.annotation.NonNull;

import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pl.gocards.filesync.algorithms.import_file.MockImportFileToDeck;
import pl.gocards.filesync.sheet.BigWorkbookFactory;
import pl.gocards.filesync.sheet.Cell;
import pl.gocards.filesync.sheet.Row;
import pl.gocards.filesync.sheet.Sheet;
import pl.gocards.filesync.sheet.Workbook;
import pl.gocards.filesync.tests.TestCardsHelper;
import pl.gocards.room.entity.deck.Card;

public class ImportFileToDeckStepDefinitions {

    protected static final int ENTITIES_TO_UPDATE_POOL_MAX = 100;

    protected static final String PATH = "build/tests/import";

    @NonNull
    private final String mimeType;

    @Spy
    private MockImportFileToDeck importFileToDeck;

    private Scenario scenario;

    private int repeat = 1;

    @Captor
    private ArgumentCaptor<List<Card>> cardsListCaptor;

    @NonNull
    private final TestCardsHelper testCardsHelper = new TestCardsHelper();

    @NonNull
    private final BigWorkbookFactory workbookFactory = new BigWorkbookFactory();

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private AutoCloseable mockitoCloseable;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Workbook workbook;

    @NonNull
    @SuppressWarnings("NotNullFieldNotInitialized")
    private Sheet sheet;

    public ImportFileToDeckStepDefinitions(@NonNull String mimeType) {
        this.mimeType = mimeType;
    }

    @Before
    public void before(Scenario scenario) {
        mockitoCloseable = MockitoAnnotations.openMocks(this);
        this.scenario = scenario;
        createFile();
    }

    protected void createFile() {
        workbook = Objects.requireNonNull(workbookFactory.createWorkbook(getFileMimeType()));
        sheet = workbook.createSheet(scenario.getName());
    }

    @After
    @SuppressWarnings("unused")
    public void closeService() throws Exception {
        mockitoCloseable.close();
    }

    @Given("Generate {int} rows.")
    public void generate_rows(Integer repeat) {
        this.repeat = repeat;
    }

    @Given("The following file:")
    public void the_following_file(@NonNull DataTable dataTable) {
        int rowNum = 0;
        for (List<String> cellsTest : dataTable.asLists()) {
            for (int i = 0; i < repeat; i++) {
                createRow(cellsTest, rowNum);
                rowNum++;
            }
        }
    }

    protected void createRow(@NonNull List<String> cellsTest, int rowNum) {
        Row rowFile = sheet.createRow(rowNum);
        createCells(cellsTest, rowFile);
    }

    protected void createCells(@NonNull List<String> cellsTest, @NonNull Row rowFile) {
        int colNum = 0;
        for (String value : cellsTest) {
            Cell cell = rowFile.createCell(colNum);
            cell.setCellValue(value);
            colNum++;
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @When("Import the file into the deck.")
    public void import_the_file_into_the_deck(String fileExtension) throws Exception {
        doReturn(null).when(importFileToDeck).createDeckDatabase(any());
        doReturn(scenario.getName()).when(importFileToDeck).findFreeDeckName(any(), any());
        doNothing().when(importFileToDeck).insertAll(any());
        doNothing().when(importFileToDeck).removeLastEmptyCards();

        new File(PATH).mkdirs();
        try (OutputStream fileOut = Files.newOutputStream(getFilePath(fileExtension))) {
            workbook.write(fileOut);
        }

        InputStream is = Files.newInputStream(getFilePath(fileExtension));
        importFileToDeck.importFile("", scenario.getName(), is, getFileMimeType(), 0L);
    }

    protected Path getFilePath(String fileExtension) {
        String fileName = scenario.getName()
                .replace(":", "")
                .replaceAll(".$", "");
        return Paths.get(PATH + "/" + fileName + "." + fileExtension);
    }

    @Then("New deck with the following cards:")
    public void new_deck_with_the_following_cards_imported(@NonNull DataTable expectedCards) {
        int times = countTimesUpdatePoolCommitted(expectedCards);
        verify(importFileToDeck, times(times)).insertAll(cardsListCaptor.capture());
        List<Card> actualCardList = cardsListCaptor
                .getAllValues()
                .stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());

        try {
            testCardsHelper.assertCards(actualCardList, expectedCards);
        } catch (@NonNull AssertionError e) {
            throw new AssertionError(testCardsHelper.printDbCards(actualCardList), e);
        }
    }

    protected int countTimesUpdatePoolCommitted(@NonNull DataTable expectedCards) {
        int cardsSize = expectedCards.asLists().size();
        int times = cardsSize / ENTITIES_TO_UPDATE_POOL_MAX;

        // committing of the remains in the pool
        if (cardsSize % ENTITIES_TO_UPDATE_POOL_MAX != 0) times++;
        return times;
    }

    @DataTableType(replaceWithEmptyString = "[blank]")
    @SuppressWarnings("unused")
    public String stringType(String cell) {
        return cell;
    }

    @NonNull
    protected String getFileMimeType() {
        return mimeType;
    }
}
