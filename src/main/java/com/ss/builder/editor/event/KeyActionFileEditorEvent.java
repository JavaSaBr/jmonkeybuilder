package com.ss.builder.editor.event;

import com.ss.builder.annotation.FromAnyThread;
import javafx.scene.input.KeyCode;
import org.jetbrains.annotations.NotNull;

/**
 * The event about a key event.
 *
 * @author JavaSaBr
 */
public class KeyActionFileEditorEvent extends AbstractFileEditorEvent<Object> {

    @NotNull
    private final KeyCode keyCode;

    private final boolean isPressed;
    private final boolean isControlDown;
    private final boolean isShiftDown;
    private final boolean isButtonMiddleDown;

    public KeyActionFileEditorEvent(
            @NotNull Object source,
            @NotNull KeyCode keyCode,
            boolean isPressed,
            boolean isControlDown,
            boolean isShiftDown,
            boolean isButtonMiddleDown
    ) {
        super(source);
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
