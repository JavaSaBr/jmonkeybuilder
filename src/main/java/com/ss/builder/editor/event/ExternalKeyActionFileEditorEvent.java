package com.ss.builder.editor.event;

import com.ss.builder.annotation.FromAnyThread;
import javafx.scene.input.KeyCode;
import org.jetbrains.annotations.NotNull;

/**
 * The event about a moved file.
 *
 * @author JavaSaBr
 */
public class ExternalKeyActionFileEditorEvent extends AbstractFileEditorEvent {

    @NotNull
    private final KeyCode keyCode;

    private final boolean isPressed;
    private final boolean isControlDown;
    private final boolean isShiftDown;
    private final boolean isButtonMiddleDown;

    public ExternalKeyActionFileEditorEvent(
            @NotNull KeyCode keyCode,
            boolean isPressed,
            boolean isControlDown,
            boolean isShiftDown,
            boolean isButtonMiddleDown
    ) {
        this.keyCode = keyCode;
        this.isPressed = isPressed;
        this.isControlDown = isControlDown;
        this.isShiftDown = isShiftDown;
        this.isButtonMiddleDown = isButtonMiddleDown;
    }

    /**
     * Get the key code.
     *
     * @return the key code.
     */
    @FromAnyThread
    public @NotNull KeyCode getKeyCode() {
        return keyCode;
    }

    /**
     * Return true is the mouse middle button is pressed.
     *
     * @return true is the mouse middle button is pressed.
     */
    @FromAnyThread
    public boolean isButtonMiddleDown() {
        return isButtonMiddleDown;
    }

    /**
     * Return true is the control button is pressed.
     *
     * @return true is the control button is pressed.
     */
    @FromAnyThread
    public boolean isControlDown() {
        return isControlDown;
    }

    /**
     * Return true is the button is pressed.
     *
     * @return true is the button is pressed.
     */
    @FromAnyThread
    public boolean isPressed() {
        return isPressed;
    }

    /**
     * Return true is the shift button is pressed.
     *
     * @return true is the shift button is pressed.
     */
    @FromAnyThread
    public boolean isShiftDown() {
        return isShiftDown;
    }
}
