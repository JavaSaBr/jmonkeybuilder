package com.ss.builder.fx.control.property.builder;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.model.undo.editor.ChangeConsumer;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to implement property builder to create property controls for some objects.
 *
 * @author JavaSaBr
 */
public interface PropertyBuilder extends Comparable<PropertyBuilder> {

    /**
     * Build properties controls for the object to the container.
     *
     * @param object         the object for building property controls.
     * @param parent         the parent og the object.
     * @param container      the container for containing these controls.
     * @param changeConsumer the consumer for working between controls and editor.
     */
    @FxThread
    void buildFor(
            @NotNull Object object,
            @Nullable Object parent,
            @NotNull VBox container,
            @NotNull ChangeConsumer changeConsumer
    );

    /**
     * Get the priority of this builder.
     *
     * @return the priority of this builder.
     */
    default int getPriority() {
        return 0;
    }

    @Override
    default int compareTo(@NotNull PropertyBuilder o) {
        return o.getPriority() - getPriority();
    }
}
