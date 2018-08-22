package com.ss.builder.editor.part3d.control;

import com.jme3.app.Application;
import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a control which works with input system of editor 3d part.
 *
 * @author JavaSaBr
 */
public interface InputEditor3dPartControl extends Editor3dPartControl {

    @Override
    @JmeThread
    default void initialize(@NotNull Application application) {
        register(application.getInputManager());
    }

    @Override
    @JmeThread
    default void cleanup(@NotNull Application application) {
        unregister(application.getInputManager());
    }

    /**
     * Register interested mappings and listeners.
     *
     * @param inputManager the input manager.
     */
    @JmeThread
    void register(@NotNull InputManager inputManager);

    /**
     * Unregister listeners.
     *
     * @param inputManager the input manager.
     */
    @JmeThread
    default void unregister(@NotNull InputManager inputManager) {
        inputManager.removeListener(getActionListener());
        inputManager.removeListener(getAnalogListener());
    }

    /**
     * Get the action listener.
     *
     * @return the action listener.
     */
    @FromAnyThread
    @NotNull ActionListener getActionListener();

    /**
     * Get the analog listener.
     *
     * @return the analog listener.
     */
    @FromAnyThread
    @NotNull AnalogListener getAnalogListener();

    /**
     * @see ActionListener#onAction(String, boolean, float)
     */
    @JmeThread
    default void onAction(@NotNull String name, boolean isPressed, float tpf) {
    }

    /**
     * @see AnalogListener#onAnalog(String, float, float)
     */
    @JmeThread
    default void onAnalog(@NotNull String name, float value, float tpf) {

    }
}
