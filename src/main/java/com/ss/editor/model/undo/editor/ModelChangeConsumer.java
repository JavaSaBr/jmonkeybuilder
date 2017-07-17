package com.ss.editor.model.undo.editor;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FXThread;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface to notify about any changes of models.
 *
 * @author JavaSaBr
 */
public interface ModelChangeConsumer extends ChangeConsumer {

    /**
     * Gets current model.
     *
     * @return the current model of the editor.
     */
    @NotNull
    @FXThread
    Spatial getCurrentModel();

    /**
     * Notify about changed property.
     *
     * @param parent       the parent
     * @param object       the object
     * @param propertyName the property name
     */
    @FXThread
    void notifyChangeProperty(@Nullable Object parent, @NotNull Object object, @NotNull String propertyName);

    /**
     * Notify about changed property count.
     *
     * @param parent the parent
     * @param object the object
     */
    @FXThread
    void notifyChangePropertyCount(@Nullable Object parent, @NotNull Object object);

    /**
     * Notify about added child.
     *
     * @param parent the parent
     * @param added  the added
     * @param index  the index
     */
    @FXThread
    void notifyAddedChild(@NotNull Object parent, @NotNull Object added, int index);

    /**
     * Notify about removed child.
     *
     * @param parent  the parent
     * @param removed the removed
     */
    @FXThread
    void notifyRemovedChild(@NotNull Object parent, @NotNull Object removed);

    /**
     * Notify about replaced child.
     *
     * @param parent   the parent
     * @param oldChild the old child
     * @param newChild the new child
     */
    @FXThread
    void notifyReplaced(@NotNull Node parent, @NotNull Spatial oldChild, @NotNull Spatial newChild);

    /**
     * Notify about replaced child.
     *
     * @param parent   the parent
     * @param oldChild the old child
     * @param newChild the new child
     */
    @FXThread
    void notifyReplaced(@NotNull Object parent, @Nullable Object oldChild, @Nullable Object newChild);

    /**
     * Notify about moved child.
     *
     * @param prevParent the prev parent
     * @param newParent  the new parent
     * @param child      the child
     * @param index      the index
     */
    @FXThread
    void notifyMoved(@NotNull Node prevParent, @NotNull Node newParent, @NotNull Spatial child, int index);
}
