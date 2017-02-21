package com.ss.editor.ui.component.editing;

import com.ss.editor.annotation.FXThread;
import org.jetbrains.annotations.NotNull;
import rlib.util.HasName;

/**
 * The interface to implement editing component.
 *
 * @author JavaSaBr
 */
public interface EditingComponent extends HasName {

    /**
     * Init this component to work in a container.
     *
     * @param container the container.
     */
    @FXThread
    default void initFor(@NotNull final EditingContainer container) {
    }

    /**
     * Checks that an object can be edited using this component.
     *
     * @param object the object to check.
     * @return true if this object can be edited.
     */
    @FXThread
    default boolean isSupport(@NotNull Object object) {
        return false;
    }

    /**
     * Start editing an object.
     *
     * @param object the object ot edit.
     */
    @FXThread
    default void startEditing(@NotNull Object object) {
    }

    /**
     * @return the editing object.
     */
    @NotNull
    @FXThread
    default Object getEditedObject() {
        throw new RuntimeException("not implemented");
    }

    /**
     * Stop editing last object.
     */
    @FXThread
    default void stopEditing() {
    }

    /**
     * Notify about showed this component.
     */
    @FXThread
    default void notifyShowed() {
    }

    /**
     * Notify about hided this component.
     */
    @FXThread
    default void notifyHided() {
    }

    /**
     * Notify about changed property.
     */
    @FXThread
    void notifyChangeProperty(@NotNull Object object, @NotNull String propertyName);
}
