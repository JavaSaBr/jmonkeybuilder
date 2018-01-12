package com.ss.editor.model.undo.editor;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.model.undo.EditorOperation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to notify about any changes.
 *
 * @author JavaSaBr
 */
public interface ChangeConsumer {

    /**
     * Notify about changed property from JME thread.
     *
     * @param object       the object
     * @param propertyName the property name
     */
    @JmeThread
    default void notifyJMEChangeProperty(@NotNull Object object, @NotNull String propertyName) {
    }

    /**
     * Notify about changed property from JME thread.
     *
     * @param object       the object
     * @param propertyName the property name
     */
    @FxThread
    default void notifyFXChangeProperty(@NotNull Object object, @NotNull String propertyName) {
        notifyFXChangeProperty(null, object, propertyName);
    }

    /**
     * Notify about changed property count in the object from FX thread.
     *
     * @param object the object
     */
    @FxThread
    default void notifyFXChangePropertyCount(@NotNull Object object) {
    }

    /**
     * Notify about changed property.
     *
     * @param parent       the parent
     * @param object       the object
     * @param propertyName the property name
     */
    @FxThread
    default void notifyFXChangeProperty(@Nullable Object parent, @NotNull Object object, @NotNull String propertyName) {
    }

    /**
     * Notify about added child from FX thread.
     *
     * @param parent     the parent.
     * @param added      the added.
     * @param index      the index of position.
     * @param needSelect true if need to select the child.
     */
    @FxThread
    default void notifyFXAddedChild(@NotNull Object parent, @NotNull Object added, int index, boolean needSelect) {
    }

    /**
     * Notify about removed child from FX thread.
     *
     * @param parent  the parent
     * @param removed the removed
     */
    @FxThread
    default void notifyFXRemovedChild(@NotNull Object parent, @NotNull Object removed) {
    }

    /**
     * Notify about replaced child from FX thread.
     *
     * @param parent         the parent.
     * @param oldChild       the old child.
     * @param newChild       the new child.
     * @param needExpand     true of need to expand new node.
     * @param needDeepExpand true of need to expand new node deeply.
     */
    @FxThread
    default void notifyFXReplaced(@NotNull Object parent, @Nullable Object oldChild, @Nullable Object newChild,
                                  boolean needExpand, boolean needDeepExpand) {
    }

    /**
     * Notify about moved child from FX thread.
     *
     * @param prevParent the prev parent.
     * @param newParent  the new parent.
     * @param child      the child.
     * @param index      the index of position.
     * @param needSelect true if need select this object.
     */
    @FxThread
    default void notifyFXMoved(@NotNull Object prevParent, @NotNull Object newParent, @NotNull Object child, int index,
                               boolean needSelect) {
    }

    /**
     * Execute the operation.
     *
     * @param operation the operation
     */
    @FromAnyThread
    void execute(@NotNull EditorOperation operation);
}
