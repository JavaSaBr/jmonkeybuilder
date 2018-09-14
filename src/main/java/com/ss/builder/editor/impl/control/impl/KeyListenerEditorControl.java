package com.ss.builder.editor.impl.control.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.FileEditor;
import javafx.scene.input.KeyEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The control to save input state of {@link FileEditor}.
 *
 * @author JavaSaBr
 */
public class KeyListenerEditorControl extends AbstractEditorControl<FileEditor> {

    public KeyListenerEditorControl(@NotNull FileEditor fileEditor) {
        super(fileEditor);

    }

    @Override
    @FxThread
    public void initialize() {

        var rootPage = fileEditor.getLayout()
                .getRootPage();

        rootPage.setOnKeyPressed(this::handleKeyPressed);
        rootPage.setOnKeyReleased(this::handleKeyReleased);
    }

    /**
     * Handle the key released event.
     */
    @FxThread
    private void handleKeyReleased(@NotNull KeyEvent keyEvent) {

    }

    /**
     * Handle the key pressed event.
     */
    @FxThread
    private void handleKeyPressed(@NotNull KeyEvent keyEvent) {

    }
}
