package com.ss.editor.model.undo.editor;

import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.model.undo.EditorOperation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The interface for notifying with model editor.
 *
 * @author JavaSaBr
 */
public interface ModelChangeConsumer {

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
     * Notify about added child.
     */
    @FXThread
    void notifyAddedChild(@NotNull Node parent, @NotNull Spatial added, int index);

    /**
     * Notify about added child.
     */
    @FXThread
    void notifyAddedChild(@NotNull Object parent, @NotNull Object added, int index);

    /**
     * Notify about adding the new control to the spatial.
     */
    @FXThread
    void notifyAddedControl(@NotNull Spatial spatial, @NotNull Control control, int index);

    /**
     * Notify about removing the old control from the spatial.
     */
    @FXThread
    void notifyRemovedControl(@NotNull Spatial spatial, @NotNull Control control);

    /**
     * Notify about added light.
     */
    @FXThread
    void notifyAddedLight(@NotNull Node parent, @NotNull Light added, int index);

    /**
     * Notify about removed child.
     */
    @FXThread
    void notifyRemovedChild(@NotNull Node parent, @NotNull Spatial removed);

    /**
     * Notify about removed child.
     */
    @FXThread
    void notifyRemovedChild(@NotNull Object parent, @NotNull Object removed);

    /**
     * Notify about removed light.
     */
    @FXThread
    void notifyRemovedLight(@NotNull Node parent, @NotNull Light removed);

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

    /**
     * Execute the operation.
     */
    @FromAnyThread
    void execute(@NotNull EditorOperation operation);
}
