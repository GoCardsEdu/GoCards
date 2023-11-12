package pl.gocards.ui;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.FileUtils;
import android.provider.OpenableColumns;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Objects;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.room.util.DbUtil;
import pl.gocards.db.deck.AppDeckDbUtil;
import pl.gocards.db.storage.DatabaseException;
import pl.gocards.ui.main.MainActivity;
import pl.gocards.util.ExceptionHandler;

/**
 * @author Grzegorz Ziemski
 */
public class ExportImportDbUtil {

    private static final String TAG = "ExportImportDbUtil";

    protected String path;

    @NonNull
    protected final AppCompatActivity activity;

    @NonNull
    protected final ActivityResultLauncher<String> exportDbLauncher;

    @NonNull
    protected final ActivityResultLauncher<String[]> importDbLauncher;

    public ExportImportDbUtil(@NonNull AppCompatActivity activity) {
        this.activity = activity;
        exportDbLauncher = initExportDbLauncher();
        importDbLauncher = initImportDbLauncher();
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
    protected ActivityResultLauncher<String[]> initImportDbLauncher() {
        return activity.registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                importedDbUri -> {
                    if (importedDbUri != null)
                        importDb(path, importedDbUri);
                }
        );
    }

    /**
     * D_R_12 Export database
     */
    @SuppressWarnings("ResultOfMethodCallIgnored")
    @SuppressLint("CheckResult")
    protected void exportDb(Uri exportToUri, @NonNull String dbPath) {
        Completable.fromRunnable(() -> {
                    try {
                        AppDeckDbUtil
                                .getInstance(activity)
                                .flushDatabase(dbPath);
                    } catch (DatabaseException e) {
                        throw new RuntimeException(e);
                    }

                    try (OutputStream out = activity
                            .getContentResolver()
                            .openOutputStream(exportToUri)) {
                        try (FileInputStream in = new FileInputStream(dbPath)) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                                FileUtils.copy(in, Objects.requireNonNull(out));
                            } else {
                                FileChannel inChannel = in.getChannel();
                                FileChannel outChannel = ((FileOutputStream) Objects.requireNonNull(out)).getChannel();
                                inChannel.transferTo(0, inChannel.size(), outChannel);
                            }
                        }
                    } catch (Exception e) {
                        onErrorExportDb(e);
                    }
                })
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
    protected void importDb(@NonNull String importToFolder, Uri importedDbUri) {
        Completable.fromRunnable(() -> {
                    AppDeckDbUtil folderDeckDbUtil = AppDeckDbUtil
                            .getInstance(activity.getApplicationContext());
                    try {
                        String deckDbPath = null;
                        try (Cursor cursor = activity.getContentResolver()
                                .query(importedDbUri, null, null, null)) {
                            if (cursor != null && cursor.moveToFirst()) {
                                deckDbPath = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                                deckDbPath = folderDeckDbUtil.findFreePath(importToFolder, deckDbPath);
                            }
                        }
                        InputStream in = activity.getContentResolver().openInputStream(importedDbUri);
                        Objects.requireNonNull(in);
                        FileOutputStream out = new FileOutputStream(deckDbPath);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            FileUtils.copy(in, out);
                        } else {
                            FileChannel inChannel = ((FileInputStream) in).getChannel();
                            FileChannel outChannel = out.getChannel();
                            inChannel.transferTo(0, inChannel.size(), outChannel);
                        }
                        in.close();
                        out.close();
                        if (activity instanceof MainActivity) {
                            ((MainActivity) activity).refreshItems();
                        }
                    } catch (IOException e) {
                        onErrorImportDb(e);
                    }
                })
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
        path = dbPath;
        exportDbLauncher.launch(DbUtil.addDbExtension(getDeckName(dbPath)));
    }

    /**
     * D_C_13 Import database
     */
    public void launchImportDb(String importToFolder) {
        path = importToFolder;
        importDbLauncher.launch(new String[]{
                "application/vnd.sqlite3",
                "application/octet-stream"
        });
    }

    @NonNull
    protected String getDeckName(@NonNull String dbPath) {
        return AppDeckDbUtil.getDeckName(dbPath);
    }

    @NonNull
    protected ExceptionHandler getExceptionHandler() {
        return ExceptionHandler.getInstance();
    }
}
