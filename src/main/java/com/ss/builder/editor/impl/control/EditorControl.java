package com.ss.builder.editor.impl.control;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.editor.event.FileEditorEvent;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement file editor's controls to extends state and functionality of file editors.
 *
 * @author JavaSaBr
 */
public interface EditorControl {

    /**
     * Initialize this control.
     */
    @FxThread
    default void initialize() {
    }

    /**
     * Notify this control 
     * @param event
     */
    @FxThread
    default void notify(@NotNull FileEditorEvent event) {
    }

    /**
     * Return true if this control knows about the property.
     *
     * @param propertyId the property id.
     * @return true if this control knows about the property.
     */
    @FxThread
    default boolean hasProperty(@NotNull String propertyId) {
        return false;
    }

    /**
     * Get a boolean property value.
     *
     * @param propertyId the property id.
     * @return the property value or false if the property is not known.
     */
    @FxThread
    default boolean getBooleanProperty(@NotNull String propertyId) {
        return false;
    }
}
