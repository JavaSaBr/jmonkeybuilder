package com.ss.builder.fx.editor.toolbar;

import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.FileEditor;
import com.ss.builder.fx.Icons;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * The action to save an opened file in an editor.
 *
 * @author JavaSaBr
 */
public class SaveToolbarAction extends ToolbarAction<FileEditor> {

    public SaveToolbarAction(@NotNull FileEditor fileEditor) {
        super(fileEditor);
        disableProperty().bind(fileEditor.dirtyProperty().not());
    }

    @Override
    @FromAnyThread
    protected @NotNull Optional<String> getToolTipText() {
        return Optional.of(Messages.FILE_EDITOR_ACTION_SAVE + " (Ctrl + S)");
    }

    @Override
    @FromAnyThread
    protected @NotNull Image getIcon() {
        return Icons.SAVE_16;
    }

    @Override
    @FxThread
    protected void activate() {
        fileEditor.save();
    }
}
