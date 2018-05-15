package com.ss.editor.ui.component.painting;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.editor.state.EditorState;
import com.ss.rlib.common.util.HasName;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to implement a component to paint something.
 *
 * @author JavaSaBr
 */
public interface PaintingComponent extends HasName {

    /**
     * Get the name of this component.
     *
     * @return the name of this component.
     */
    @Override
    @FromAnyThread
    default @NotNull String getName() {
        return getClass().getSimpleName();
    }

    /**
     * Get the icon of this component.
     *
     * @return the icon.
     */
    @FxThread
    default @Nullable Image getIcon() {
        return null;
    }

    /**
     * Get the container.
     *
     * @return the container.
     */
    @FromAnyThread
    @NotNull PaintingComponentContainer getContainer();

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
     * Start painting process for the object.
     *
     * @param object the object.
     */
    @FxThread
    default void startPainting(@NotNull Object object) {
    }

    /**
     * Get the painted object.
     *
     * @return the painted object.
     */
    @FxThread
    default @Nullable Object getPaintedObject() {
        throw new RuntimeException("not implemented");
    }

    /**
     * Stop painting of the last object.
     */
    @FxThread
    default void stopPainting() {
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
    default void notifyChangeProperty(@NotNull Object object, @NotNull String propertyName) {
    }
}
