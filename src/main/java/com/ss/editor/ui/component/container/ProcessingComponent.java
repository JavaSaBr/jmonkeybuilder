package com.ss.editor.ui.component.container;

import com.ss.editor.annotation.FXThread;
import com.ss.rlib.util.HasName;
import org.jetbrains.annotations.NotNull;

/**
 * The interface to implement a processing component.
 *
 * @author JavaSaBr
 */
public interface ProcessingComponent extends HasName {

    /**
     * Init this component to process in a container.
     *
     * @param container the container.
     */
    @FXThread
    default void initFor(@NotNull Object container) {
    }

    /**
     * Checks that an object can be processed using this component.
     *
     * @param object the object to check.
     * @return true if this object can be processed.
     */
    @FXThread
    default boolean isSupport(@NotNull Object object) {
        return false;
    }

    /**
     * Start to process the object.
     *
     * @param object the object to process.
     */
    @FXThread
    default void startProcessing(@NotNull Object object) {
    }

    /**
     * Gets processed object.
     *
     * @return the processed object.
     */
    @NotNull
    @FXThread
    default Object getProcessedObject() {
        throw new RuntimeException("not implemented");
    }

    /**
     * Stop processing last object.
     */
    @FXThread
    default void stopProcessing() {
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
     *
     * @param object       the object
     * @param propertyName the property name
     */
    @FXThread
    void notifyChangeProperty(@NotNull Object object, @NotNull String propertyName);
}
