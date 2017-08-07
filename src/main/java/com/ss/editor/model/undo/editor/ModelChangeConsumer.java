package com.ss.editor.model.undo.editor;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.JMEThread;
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
     * Notify about changed property from JME thread.
     *
     * @param object       the object
     * @param propertyName the property name
     */
    @JMEThread
    void notifyJMEChangeProperty(@NotNull Object object, @NotNull String propertyName);

    /**
     * Notify about changed property from JME thread.
     *
     * @param object       the object
     * @param propertyName the property name
     */
    @FXThread
    default void notifyFXChangeProperty(@NotNull Object object, @NotNull String propertyName) {
        notifyFXChangeProperty(null, object, propertyName);
    }

    /**
     * Notify about changed property.
     *
     * @param parent       the parent
     * @param object       the object
     * @param propertyName the property name
     */
    @FXThread
    void notifyFXChangeProperty(@Nullable Object parent, @NotNull Object object, @NotNull String propertyName);

    /**
     * Notify about changed property count from FX thread.
     *
     * @param parent the parent
     * @param object the object
     */
    @FXThread
    void notifyFXChangePropertyCount(@Nullable Object parent, @NotNull Object object);

    /**
     * Notify about added child from FX thread.
     *
     * @param parent     the parent.
     * @param added      the added.
     * @param index      the index of position.
     * @param needSelect true if need to select the child.
     */
    @FXThread
    void notifyFXAddedChild(@NotNull Object parent, @NotNull Object added, int index, boolean needSelect);

    /**
     * Notify about removed child from FX thread.
     *
     * @param parent  the parent
     * @param removed the removed
     */
    @FXThread
    void notifyFXRemovedChild(@NotNull Object parent, @NotNull Object removed);

    /**
     * Notify about replaced child from FX thread.
     *
     * @param parent   the parent
     * @param oldChild the old child
     * @param newChild the new child
     */
    @FXThread
    void notifyFXReplaced(@NotNull Node parent, @NotNull Spatial oldChild, @NotNull Spatial newChild);

    /**
     * Notify about replaced child from FX thread.
     *
     * @param parent   the parent
     * @param oldChild the old child
     * @param newChild the new child
     */
    @FXThread
    void notifyFXReplaced(@NotNull Object parent, @Nullable Object oldChild, @Nullable Object newChild);

    /**
     * Notify about moved child from FX thread.
     *
     * @param prevParent the prev parent.
     * @param newParent  the new parent.
     * @param child      the child.
     * @param index      the index of position.
     * @param needSelect true if need select this object.
     */
    @FXThread
    void notifyFXMoved(@NotNull Object prevParent, @NotNull Object newParent, @NotNull Object child, int index,
                       boolean needSelect);
}
