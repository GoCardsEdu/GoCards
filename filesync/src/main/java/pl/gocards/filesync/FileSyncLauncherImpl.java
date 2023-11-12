package pl.gocards.filesync;

import static pl.gocards.filesync.sheet.WorkbookFactory.FILE_EXTENSION_CSV;
import static pl.gocards.filesync.sheet.WorkbookFactory.FILE_EXTENSION_XLSX;
import static pl.gocards.filesync.sheet.WorkbookFactory.MIME_TYPE_CSV;
import static pl.gocards.filesync.sheet.WorkbookFactory.MIME_TYPE_XLSX;
import static pl.gocards.filesync.sheet.WorkbookFactory.SUPPORTED_MIME_TYPES;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import pl.gocards.db.deck.AppDeckDbUtil;

/**
 * @author Grzegorz Ziemski
 */
@Keep
public class FileSyncLauncherImpl extends FileSyncBean implements FileSyncLauncher {

    @NotNull
    protected final ActivityResultLauncher<String[]> importFileLauncher;

    @NotNull
    protected final ActivityResultLauncher<String> exportExcelLauncher;

    @NotNull
    protected final ActivityResultLauncher<String> exportCsvLauncher;

    @NotNull
    protected final AppCompatActivity activity;

    @NotNull
    private final CompositeDisposable disposable;

    @Nullable
    protected String deckDbPath;

    @SuppressWarnings("unused")
    public FileSyncLauncherImpl(
            @NonNull AppCompatActivity activity,
            @NonNull CompositeDisposable disposable
    ) {
        this.activity = activity;
        this.disposable = disposable;
        importFileLauncher = initImportFileLauncher();
        exportExcelLauncher = initExportFileLauncher(MIME_TYPE_XLSX);
        exportCsvLauncher = initExportFileLauncher(MIME_TYPE_CSV);
    }

    @NotNull
    protected ActivityResultLauncher<String[]> initImportFileLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                importedFileUri -> {
                    if (importedFileUri != null) {
                        importFile(activity, Objects.requireNonNull(deckDbPath), importedFileUri);
                        this.deckDbPath = null;
                    }
                }
        );
    }

    @NotNull
    protected ActivityResultLauncher<String> initExportFileLauncher(String mimeType) {
        return activity.registerForActivityResult(
                new ActivityResultContracts.CreateDocument(mimeType),
                exportedFileUri -> {
                    if (exportedFileUri != null) {
                        exportFile(activity, Objects.requireNonNull(deckDbPath), exportedFileUri, disposable);
                        this.deckDbPath = null;
                    }
                }
        );
    }

    /**
     * FS_I Import the file as a new deck.
     */
    public void launchImportFile(@NonNull String importToFolder) {
        this.deckDbPath = importToFolder;
        importFileLauncher.launch(SUPPORTED_MIME_TYPES);
    }

    /**
     * FS_E Export the deck to to a new file.
     */
    public void launchExportToExcel(@NonNull String deckDbPath) {
        this.deckDbPath = deckDbPath;
        exportExcelLauncher.launch(getDeckName(deckDbPath) + "." + FILE_EXTENSION_XLSX);
    }

    /**
     * FS_E Export the deck to to a new file.
     */
    public void launchExportToCsv(@NonNull String deckDbPath) {
        this.deckDbPath = deckDbPath;
        exportCsvLauncher.launch(getDeckName(deckDbPath) + "." + FILE_EXTENSION_CSV);
    }

    @NonNull
    protected String getDeckName(@NonNull String dbPath) {
        return AppDeckDbUtil.getDeckName(dbPath);
    }
}