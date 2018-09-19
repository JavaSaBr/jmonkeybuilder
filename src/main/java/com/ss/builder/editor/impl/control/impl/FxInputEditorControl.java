package com.ss.builder.editor.impl.control.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.FileEditor;
import com.ss.builder.editor.event.KeyActionFileEditorEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The control to handle FX input of {@link FileEditor}.
 *
 * @author JavaSaBr
 */
public class FxInputEditorControl extends AbstractEditorControl<FileEditor> {

    public static final String PROP_BUTTON_LEFT_DOWN = "InputStateEditorControl.buttonLeftDown";
    public static final String PROP_BUTTON_RIGHT_DOWN = "InputStateEditorControl.buttonRightDown";
    public static final String PROP_BUTTON_MIDDLE_DOWN = "InputStateEditorControl.buttonMiddleDown";
    public static final String PROP_CONTROL_DOWN = "InputStateEditorControl.controlDown";
    public static final String PROP_ALT_DOWN = "InputStateEditorControl.altDown";
    public static final String PROP_SHIFT_DOWN = "InputStateEditorControl.shiftDown";

    /**
     * True if the left button is pressed.
     */
    private boolean buttonLeftDown;

    /**
     * True if the right button is pressed.
     */
    private boolean buttonRightDown;

    /**
     * True if the middle button is pressed.
     */
    private boolean buttonMiddleDown;

    /**
     * True if the control button is pressed.
     */
    private boolean controlDown;

    /**
     * True if the alt button is pressed.
     */
    private boolean altDown;

    /**
     * True if the shift button is pressed.
     */
    private boolean shiftDown;

    public FxInputEditorControl(@NotNull FileEditor fileEditor) {
        super(fileEditor);

    }

    @Override
    @FxThread
    public void initialize() {

        var rootPage = fileEditor.getLayout()
                .getRootPage();

        rootPage.setOnMouseReleased(this::processMouseReleased);
        rootPage.setOnMousePressed(this::processMousePressed);
        rootPage.setOnKeyPressed(this::handleKeyPressed);
        rootPage.setOnKeyReleased(this::handleKeyReleased);
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

    /**
     * Handle the key released event.
     */
    @FxThread
    private void handleKeyReleased(@NotNull KeyEvent keyEvent) {
        updateKeyState(keyEvent);

        fileEditor.notify(new KeyActionFileEditorEvent(this, keyEvent.getCode(), false,
                controlDown, shiftDown, buttonMiddleDown));
    }

    /**
     * Handle the key pressed event.
     */
    @FxThread
    private void handleKeyPressed(@NotNull KeyEvent keyEvent) {
        updateKeyState(keyEvent);

        fileEditor.notify(new KeyActionFileEditorEvent(this, keyEvent.getCode(), true,
                controlDown, shiftDown, buttonMiddleDown));
    }

    @FxThread
    private void updateKeyState(@NotNull KeyEvent keyEvent) {
        shiftDown = keyEvent.isShiftDown();
        controlDown = keyEvent.isControlDown();
        altDown = keyEvent.isAltDown();
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
            case PROP_ALT_DOWN:
                return altDown;
            case PROP_SHIFT_DOWN:
                return shiftDown;
            case PROP_CONTROL_DOWN:
                return controlDown;
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
            case PROP_ALT_DOWN:
            case PROP_SHIFT_DOWN:
            case PROP_CONTROL_DOWN:
                return true;
        }

        return false;
    }
}
