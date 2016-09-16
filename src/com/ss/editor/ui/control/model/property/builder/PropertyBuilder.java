package com.ss.editor.ui.control.model.property.builder;

import com.ss.editor.model.undo.editor.ModelChangeConsumer;

import org.jetbrains.annotations.NotNull;

import javafx.scene.layout.VBox;

/**
 * The interface for implementing property builder for creating property controls for some objects.
 *
 * @author JavaSaBr
 */
public interface PropertyBuilder {

    /**
     * Build properties controls for the object to the container.
     *
     * @param object              the object for building property controls.
     * @param container           the container for containing these controls.
     * @param modelChangeConsumer the consumer for working between controls and editor.
     */
    void buildFor(@NotNull final Object object, @NotNull final VBox container, @NotNull final ModelChangeConsumer modelChangeConsumer);
}
