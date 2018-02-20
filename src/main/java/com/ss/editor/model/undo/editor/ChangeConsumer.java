package com.ss.editor.model.undo.editor;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
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
     * Notify about an attempt to change the property from jME thread.
     *
     * @param object       the object.
     * @param propertyName the property name.
     */
    @JmeThread
    default void notifyJmePreChangeProperty(@NotNull Object object, @NotNull String propertyName) {
    }

    /**
     * Notify about changed the property from jME thread.
     *
     * @param object       the object.
     * @param propertyName the property name.
     */
    @JmeThread
    default void notifyJmeChangedProperty(@NotNull Object object, @NotNull String propertyName) {
    }

    /**
     * Notify about changed property from jME thread.
     *
     * @param object       the object
     * @param propertyName the property name
     */
    @FxThread
    default void notifyFxChangeProperty(@NotNull Object object, @NotNull String propertyName) {
        notifyFxChangeProperty(null, object, propertyName);
    }

    /**
     * Notify about changed property count in the object from FX thread.
     *
     * @param object the object
     */
    @FxThread
    default void notifyFxChangePropertyCount(@NotNull Object object) {
    }

    /**
     * Notify about changed property.
     *
     * @param parent       the parent
     * @param object       the object
     * @param propertyName the property name
     */
    @FxThread
    default void notifyFxChangeProperty(@Nullable Object parent, @NotNull Object object, @NotNull String propertyName) {
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
    default void notifyFxAddedChild(@NotNull Object parent, @NotNull Object added, int index, boolean needSelect) {
    }

    /**
     * Notify about removed child from FX thread.
     *
     * @param parent  the parent
     * @param removed the removed
     */
    @FxThread
    default void notifyFxRemovedChild(@NotNull Object parent, @NotNull Object removed) {
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
    default void notifyFxReplaced(@NotNull Object parent, @Nullable Object oldChild, @Nullable Object newChild,
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
    default void notifyFxMoved(@NotNull Object prevParent, @NotNull Object newParent, @NotNull Object child, int index,
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
