package com.ss.editor.part3d.editor.control;

import com.jme3.input.InputManager;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.ss.editor.annotation.JmeThread;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a control which works with input system of editor 3d part.
 *
 * @author JavaSaBr
 */
public interface InputEditor3dPartControl extends Editor3dPartControl, AnalogListener, ActionListener {

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
        inputManager.removeListener(this);
    }

    @Override
    @JmeThread
    default void onAction(@NotNull String name, boolean isPressed, float tpf) {
    }

    @Override
    @JmeThread
    default void onAnalog(@NotNull String name, float value, float tpf) {

    }
}
