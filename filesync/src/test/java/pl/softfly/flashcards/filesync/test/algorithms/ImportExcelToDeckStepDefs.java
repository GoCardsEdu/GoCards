package pl.softfly.flashcards.filesync.test.algorithms;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.WorkbookUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import io.cucumber.datatable.DataTable;
import io.cucumber.java.Before;
import io.cucumber.java.DataTableType;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import pl.softfly.flashcards.entity.deck.Card;
import pl.softfly.flashcards.filesync.FileSync;
import pl.softfly.flashcards.filesync.algorithms.importt.ImportExcelToDeck;
import pl.softfly.flashcards.filesync.test.TestCardsHelper;

public class ImportExcelToDeckStepDefs {

    protected static final int ENTITIES_TO_UPDATE_POOL_MAX = 100;

    protected static final String PATH = "build/tests/import_excel_to_deck";

    private ImportExcelToDeck importExcelToDeck;

    private Scenario scenario;

    private int repeat = 1;

    @Captor
    private ArgumentCaptor<List<Card>> cardsListCaptor;

    private TestCardsHelper testCardsHelper = new TestCardsHelper();

    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("Generate {int} rows.")
    public void generate_rows(Integer repeat) {
        this.repeat = repeat;
    }

    @Given("The following Excel file:")
    public void the_following_excel_file(@NonNull DataTable dataTable) throws IOException {
        Workbook wb = new XSSFWorkbook();//SXSSFWorkbook dont use
        CreationHelper createHelper = wb.getCreationHelper();
        Sheet sheet = wb.createSheet(WorkbookUtil.createSafeSheetName(scenario.getName()));

        int rowNum = 0;
        int colNum;
        for (List<String> rowIn : dataTable.asLists()) {
            for (int i = 0; i < repeat; i++) {
                Row rowOut = sheet.createRow(rowNum);
                colNum = 0;
                for (String colIn : rowIn) {
                    Cell cell = rowOut.createCell(colNum);
                    cell.setCellValue(createHelper.createRichTextString(colIn));
                    colNum++;
                }
                rowNum++;
            }
        }
        new File(PATH).mkdirs();
        try (OutputStream fileOut = new FileOutputStream(PATH + "/" + scenario.getName() + ".xlsx")) {
            wb.write(fileOut);
        }
    }

    @When("Import the Excel file into the deck.")
    public void import_the_excel_file_into_the_deck() throws Exception {
        importExcelToDeck = Mockito.spy(ImportExcelToDeck.class);
        doReturn(null).when(importExcelToDeck).getDeckDatabase(any());
        doNothing().when(importExcelToDeck).refreshLastUpdatedAt(any());
        doReturn(scenario.getName()).when(importExcelToDeck).findFreeDeckName(any(), any());
        doNothing().when(importExcelToDeck).insertAll(any());

        InputStream is = new FileInputStream(PATH + "/" + scenario.getName() + ".xlsx");
        importExcelToDeck.importExcelFile("", scenario.getName(), is, FileSync.TYPE_XLSX, 0l);
    }

    @Then("A new deck with the following cards imported.")
    public void a_new_deck_with_the_following_cards_imported(@NonNull DataTable expectedCards) {
        MockitoAnnotations.initMocks(this);
        int times = countTimesUpdatePoolCommitted(expectedCards);
        verify(importExcelToDeck, times(times)).insertAll(cardsListCaptor.capture());
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

    protected boolean empty(@Nullable String str) {
        return str == null || str.isEmpty();
    }

    @DataTableType(replaceWithEmptyString = "[blank]")
    public String stringType(String cell) {
        return cell;
    }
}
