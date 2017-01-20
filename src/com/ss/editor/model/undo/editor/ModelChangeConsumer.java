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
     * @return the current model of the editor.
     */
    @NotNull
    @FXThread
    Spatial getCurrentModel();

    /**
     * Notify about changed property.
     */
    @FXThread
    void notifyChangeProperty(@Nullable Object parent, @NotNull Object object, @NotNull String propertyName);

    /**
     * Notify about changed property count.
     */
    @FXThread
    void notifyChangePropertyCount(@Nullable Object parent, @NotNull Object object);

    /**
     * Notify about added child.
     */
    @FXThread
    void notifyAddedChild(@NotNull Object parent, @NotNull Object added, int index);

    /**
     * Notify about removed child.
     */
    @FXThread
    void notifyRemovedChild(@NotNull Object parent, @NotNull Object removed);

    /**
     * Notify about replaced child.
     */
    @FXThread
    void notifyReplaced(@NotNull Node parent, @NotNull Spatial oldChild, @NotNull Spatial newChild);

    /**
     * Notify about replaced child.
     */
    @FXThread
    void notifyReplaced(@NotNull Object parent, @Nullable Object oldChild, @Nullable Object newChild);

    /**
     * Notify about moved child.
     */
    @FXThread
    void notifyMoved(@NotNull Node prevParent, @NotNull Node newParent, @NotNull Spatial child, int index);
}
