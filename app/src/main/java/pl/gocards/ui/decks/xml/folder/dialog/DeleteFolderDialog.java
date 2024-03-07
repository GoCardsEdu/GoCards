package pl.gocards.ui.decks.xml.folder.dialog;

import static io.reactivex.rxjava3.internal.functions.Functions.EMPTY_ACTION;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.IOException;
import java.nio.file.Path;

import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import pl.gocards.R;
import pl.gocards.ui.base.BaseDialogFragment;
import pl.gocards.ui.main.xml.MainActivity;

/**
 * F_D_05 Delete the folder
 * @author Grzegorz Ziemski
 */
public class DeleteFolderDialog extends BaseDialogFragment {

    private static final String TAG = "DeleteFolderDialog";

    private Path currentFolder;

    private final boolean isRotated;

    public DeleteFolderDialog() {
        // Dismiss on screen rotation
        isRotated = true;
    }

    public DeleteFolderDialog(@NonNull AppCompatActivity activity, Path currentFolder) {
        setParentActivity(activity);
        this.currentFolder = currentFolder;
        isRotated = false;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRotated) dismiss();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.decks_list_folder_delete_dialog_title)
                .setMessage(R.string.decks_list_folder_delete_dialog_message)
                .setPositiveButton(R.string.yes, (dialog, which) -> {
                    try {
                        getDeckDbUtil().deleteFolder(currentFolder);
                        deleteDecksInDb(currentFolder);
                    } catch (IOException e) {
                        onError(e);
                    }
                })
                .setNegativeButton(R.string.no, (dialog, which) -> {})
                .create();
    }

    @SuppressLint("CheckResult")
    private void deleteDecksInDb(@NonNull Path folder) {
        Disposable disposable = getAppDb().deckRxDao()
                .deleteByStartWithPath(folder.toString())
                .subscribeOn(Schedulers.io())
                .doOnComplete(() -> {
                    requireParentActivity().refreshItems();
                    runOnUiThread(() -> showShortToastMessage(R.string.decks_list_folder_delete_dialog_toast_deleted), this::onError);
                })
                .subscribe(EMPTY_ACTION, this::onError);
        requireParentActivity().addToDisposable(disposable);
    }

    @Override
    protected void onError(@NonNull Throwable e) {
        getExceptionHandler().handleException(
                e, requireParentActivity(), TAG,
                "Error while deleting folder."
        );
    }

    @NonNull
    @Override
    protected MainActivity requireParentActivity() {
        return (MainActivity) super.requireParentActivity();
    }
}