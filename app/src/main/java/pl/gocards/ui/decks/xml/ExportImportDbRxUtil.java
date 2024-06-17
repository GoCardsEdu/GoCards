package pl.gocards.ui.decks.xml;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.net.Uri;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.Unit;
import kotlin.jvm.functions.Function0;
import kotlin.jvm.functions.Function1;
import pl.gocards.ui.decks.kt.decks.service.ExportImportDbUtil;
import pl.gocards.util.ExceptionHandler;

/**
 * @author Grzegorz Ziemski
 */
public class ExportImportDbRxUtil extends ExportImportDbUtil {

    private static final String TAG = "ExportImportDbUtil";

    @NonNull
    protected final AppCompatActivity activity;

    @NonNull
    protected final ActivityResultLauncher<String> exportDbLauncher;

    @NonNull
    protected final ActivityResultLauncher<String[]> importDbLauncher;

    public ExportImportDbRxUtil(
            @NonNull AppCompatActivity activity,
            @NonNull Function0<Unit> onSuccess
    ) {
        super(activity);
        this.activity = activity;
        exportDbLauncher = initExportDbLauncher();
        importDbLauncher = initImportDbLauncher(onSuccess);
    }

    /**
     * D_R_12 Export database
     */
    @NonNull
    protected ActivityResultLauncher<String> initExportDbLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.CreateDocument("application/vnd.sqlite3"),
                exportedDbUri -> {
                    if (exportedDbUri != null) exportDb(exportedDbUri, path);
                }
        );
    }

    /**
     * D_C_13 Import database
     */
    @NonNull
    protected ActivityResultLauncher<String[]> initImportDbLauncher(
            @NonNull Function0<Unit> onSuccess
    ) {
        return activity.registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                importedDbUri -> {
                    if (importedDbUri != null)
                        importDb(path, importedDbUri, s -> {
                            onSuccess.invoke();
                            return null;
                        });
                }
        );
    }

    /**
     * D_R_12 Export database
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    protected void exportDb(@NonNull Uri exportToUri, @NonNull String dbPath) {
        Completable.fromRunnable(() -> super.exportDb(exportToUri, dbPath))
                .subscribeOn(Schedulers.io())
                .subscribe(EMPTY_ACTION, this::onErrorExportDb);
    }

    protected void onErrorExportDb(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, activity,
                this.getClass().getSimpleName(),
                "Error while exporting DB."
        );
    }

    /**
     * D_C_13 Import database
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint({"Range", "CheckResult"})
    protected void importDb(
            @NonNull String importToFolder,
            @NonNull Uri importedDbUri,
            @NonNull Function1<? super String, Unit> onSuccess
    ) {
        Completable.fromRunnable(() -> super.importDb(importToFolder, importedDbUri, onSuccess))
                .subscribeOn(Schedulers.io())
                .subscribe(EMPTY_ACTION, this::onErrorImportDb);
    }

    protected void onErrorImportDb(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, activity, TAG,
                "Error while importing DB."
        );
    }

    /**
     * D_R_12 Export database
     */
    public void launchExportDb(@NonNull String dbPath) {
        super.launchExportDb(exportDbLauncher, dbPath);
    }

    /**
     * D_C_13 Import database
     */
    public void launchImportDb(String importToFolder) {
        super.launchImportDb(importDbLauncher, importToFolder);
    }

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }
}
