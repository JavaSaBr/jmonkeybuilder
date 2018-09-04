package com.ss.builder.editor.impl.control.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.FileEditor;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The control to save input state of {@link FileEditor}.
 *
 * @author JavaSaBr
 */
public class InputStateEditorControl extends AbstractEditorControl<FileEditor> {

    public static final String PROP_BUTTON_LEFT_DOWN = "InputStateEditorControl.buttonLeftDown";
    public static final String PROP_BUTTON_RIGHT_DOWN = "InputStateEditorControl.buttonRightDown";
    public static final String PROP_BUTTON_MIDDLE_DOWN = "InputStateEditorControl.buttonMiddleDown";

    /**
     * True if the left button pressed.
     */
    private boolean buttonLeftDown;

    /**
     * True if the right button pressed.
     */
    private boolean buttonRightDown;

    /**
     * True if the middle button pressed.
     */
    private boolean buttonMiddleDown;

    public InputStateEditorControl(@NotNull FileEditor fileEditor) {
        super(fileEditor);

    }

    @Override
    @FxThread
    public void initialize() {

        var rootPage = fileEditor.getLayout()
                .getRootPage();

        rootPage.setOnMouseReleased(this::processMouseReleased);
        rootPage.setOnMousePressed(this::processMousePressed);
    }

    /**
     * Handle the mouse released event.
     */
    @FxThread
    private void processMouseReleased(@NotNull MouseEvent mouseEvent) {
        buttonLeftDown = mouseEvent.isPrimaryButtonDown();
        buttonRightDown = mouseEvent.isMiddleButtonDown();
        buttonMiddleDown = mouseEvent.isSecondaryButtonDown();
    }

    /**
     * Handle the mouse pressed event.
     */
    @FxThread
    private void processMousePressed(@NotNull MouseEvent mouseEvent) {
        buttonLeftDown = mouseEvent.isPrimaryButtonDown();
        buttonRightDown = mouseEvent.isMiddleButtonDown();
        buttonMiddleDown = mouseEvent.isSecondaryButtonDown();
    }

    @Override
    @FxThread
    public boolean getBooleanProperty(@NotNull String propertyId) {

        switch (propertyId) {
            case PROP_BUTTON_LEFT_DOWN:
                return buttonLeftDown;
            case PROP_BUTTON_MIDDLE_DOWN:
                return buttonMiddleDown;
            case PROP_BUTTON_RIGHT_DOWN:
                return buttonRightDown;
        }

        return false;
    }

    @Override
    @FxThread
    public boolean hasProperty(@NotNull String propertyId) {

        switch (propertyId) {
            case PROP_BUTTON_LEFT_DOWN:
            case PROP_BUTTON_MIDDLE_DOWN:
            case PROP_BUTTON_RIGHT_DOWN:
                return true;
        }

        return false;
    }
}
