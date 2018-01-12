package com.ss.editor.ui.component.container;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.editor.state.EditorState;
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
    @FxThread
    default void initFor(@NotNull Object container) {
    }

    /**
     * Load state of this component form the editor state.
     *
     * @param editorState the editor state.
     */
    @FxThread
    default void loadState(@NotNull EditorState editorState) {
    }

    /**
     * Checks that an object can be processed using this component.
     *
     * @param object the object to check.
     * @return true if this object can be processed.
     */
    @FxThread
    default boolean isSupport(@NotNull Object object) {
        return false;
    }

    /**
     * Start to process the object.
     *
     * @param object the object to process.
     */
    @FxThread
    default void startProcessing(@NotNull Object object) {
    }

    /**
     * Gets processed object.
     *
     * @return the processed object.
     */
    @NotNull
    @FxThread
    default Object getProcessedObject() {
        throw new RuntimeException("not implemented");
    }

    /**
     * Stop processing last object.
     */
    @FxThread
    default void stopProcessing() {
    }

    /**
     * Notify about showed this component.
     */
    @FxThread
    default void notifyShowed() {
    }

    /**
     * Notify about hided this component.
     */
    @FxThread
    default void notifyHided() {
    }

    /**
     * Notify about changed property.
     *
     * @param object       the object
     * @param propertyName the property name
     */
    @FxThread
    void notifyChangeProperty(@NotNull Object object, @NotNull String propertyName);
}
