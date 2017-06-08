package com.ss.editor.ui.component.container;

import com.ss.editor.annotation.FXThread;
import org.jetbrains.annotations.NotNull;
import rlib.util.HasName;

/**
 * The interface to implement an editor component.
 *
 * @author JavaSaBr
 */
public interface EditorComponent extends HasName {

    /**
     * Init this component to work in a container.
     *
     * @param container the container.
     */
    @FXThread
    default void initFor(@NotNull final Object container) {
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
     * Start working with an object.
     *
     * @param object the object ot work.
     */
    @FXThread
    default void startWorkingWith(@NotNull Object object) {
    }

    /**
     * @return the working object.
     */
    @NotNull
    @FXThread
    default Object getWorkedObject() {
        throw new RuntimeException("not implemented");
    }

    /**
     * Stop working last object.
     */
    @FXThread
    default void stopWorking() {
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
