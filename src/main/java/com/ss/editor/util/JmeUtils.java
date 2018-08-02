package com.ss.editor.util;

import com.jme3.input.InputManager;
import com.jme3.input.controls.Trigger;
import com.ss.editor.annotation.JmeThread;
import org.jetbrains.annotations.NotNull;

/**
 * The class with utility methods for the jME things.
 *
 * @author JavaSaBr
 */
public class JmeUtils {

    /**
     * Add mapping to the input manager.
     *
     * @param inputManager the input manager.
     * @param name         the mapping name.
     * @param trigger      the trigger.
     */
    @JmeThread
    public static void addMapping(@NotNull InputManager inputManager, @NotNull String name, @NotNull Trigger trigger) {
        if (!inputManager.hasMapping(name)) {
            inputManager.addMapping(name, trigger);
        }
    }

    /**
     * Add mapping to the input manager.
     *
     * @param inputManager the input manager.
     * @param name         the mapping name.
     * @param triggers     the triggers.
     */
    @JmeThread
    public static void addMapping(
            @NotNull InputManager inputManager,
            @NotNull String name,
            @NotNull Trigger... triggers
    ) {
        if (!inputManager.hasMapping(name)) {
            inputManager.addMapping(name, triggers);
        }
    }
}
