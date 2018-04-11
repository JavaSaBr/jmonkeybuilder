package com.ss.editor.plugin.api.property.control;

import com.ss.editor.Messages;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.dialog.file.chooser.ExternalFileEditorDialog;
import com.ss.rlib.util.VarTable;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The control to edit files from file system.
 *
 * @author JavaSaBr
 */
public class ExternalFileResourcePropertyControl extends ResourcePropertyEditorControl<Path> {

    /**
     * The target extension.
     */
    @Nullable
    private final String extension;

    public ExternalFileResourcePropertyControl(@NotNull final VarTable vars,
                                               @NotNull final PropertyDefinition definition,
                                               @NotNull final Runnable validationCallback) {
        super(vars, definition, validationCallback);
        this.extension = definition.getExtension();
    }

    @Override
    @FxThread
    protected void processSelect() {
        super.processSelect();

        final ExternalFileEditorDialog dialog = new ExternalFileEditorDialog(this::openExternalFile);
        dialog.setTitleText(Messages.ASSET_EDITOR_DIALOG_TITLE);
        dialog.setInitDirectory(Paths.get(System.getProperty("user.home")));
        dialog.show();
    }

    /**
     * Handle selected file.
     *
     * @param path the selected file.
     */
    @FxThread
    private void openExternalFile(@NotNull final Path path) {
        setPropertyValue(path);
        change();
        reload();
    }


    @Override
    @FxThread
    public void reload() {

        final Path resource = getPropertyValue();
        final Label resourceLabel = getResourceLabel();
        resourceLabel.setText(resource == null ? NOT_SELECTED : resource.toString());

        super.reload();
    }
}
