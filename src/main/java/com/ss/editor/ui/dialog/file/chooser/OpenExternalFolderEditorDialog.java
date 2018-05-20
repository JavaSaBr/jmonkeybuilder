package com.ss.editor.ui.dialog.file.chooser;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.dialog.EditorDialog;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Consumer;

/**
 * The implementation of the {@link EditorDialog} to choose a folder on machine.
 *
 * @author JavaSaBr
 */
public class OpenExternalFolderEditorDialog extends ExternalFileEditorDialog {

    public OpenExternalFolderEditorDialog(@NotNull final Consumer<@NotNull Path> consumer) {
        super(consumer);
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);
        getResourceTree().setOnlyFolders(true);
    }
}
