package com.ss.editor.model.undo.editor;

import com.jme3.light.Light;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
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
    Spatial getCurrentModel();

    /**
     * Notify about changed property.
     */
    void notifyChangeProperty(@NotNull Object object, @NotNull String propertyName);

    /**
     * Notify about added child.
     */
    void notifyAddedChild(@NotNull Node parent, @NotNull Spatial added);

    /**
     * Notify about adding the new control to the spatial.
     */
    void notifyAddedControl(@NotNull Spatial spatial, @NotNull Control control);

    /**
     * Notify about removing the old control from the spatial.
     */
    void notifyRemovedControl(@NotNull Spatial spatial, @NotNull Control control);

    /**
     * Notify about added light.
     */
    void notifyAddedLight(@NotNull Node parent, @NotNull Light added);

    /**
     * Notify about removed child.
     */
    void notifyRemovedChild(@NotNull Node parent, @NotNull Spatial removed);

    /**
     * Notify about removed light.
     */
    void notifyRemovedLight(@NotNull Node parent, @NotNull Light removed);

    /**
     * Notify about replaced child.
     */
    void notifyReplaced(@NotNull Node parent, @NotNull Spatial oldChild, @NotNull Spatial newChild);

    /**
     * Notify about replaced child.
     */
    void notifyReplaced(@NotNull Object parent, @Nullable Object oldChild, @Nullable Object newChild);

    /**
     * Notify about moved child.
     */
    void notifyMoved(@NotNull Node prevParent, @NotNull Node newParent, @NotNull Spatial child, int index);

    /**
     * Execute the operation.
     */
    void execute(@NotNull EditorOperation operation);
}
