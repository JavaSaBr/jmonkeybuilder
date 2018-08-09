package com.ss.editor.part3d.editor.control;

import com.jme3.app.Application;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.part3d.editor.event.Editor3dPartEvent;
import org.jetbrains.annotations.NotNull;

/**
 * Interface to implement an additional control for {@link Editor3dPart}.
 *
 * @author JavaSaBr
 */
public interface Editor3dPartControl {

    /**
     * Initialize the control during initializing parent 3d part.
     *
     * @param application the application.
     */
    @JmeThread
    void initialize(@NotNull Application application);

    /**
     * Cleanup this control during cleaning the parent 3d part.
     *
     * @param application the application.
     */
    @JmeThread
    void cleanup(@NotNull Application application);

    @JmeThread
    default void update(float tpf) {
    }

    @JmeThread
    default void preCameraUpdate(float tpf) {
    }

    @JmeThread
    default void cameraUpdate(float tpf) {
    }

    @JmeThread
    default void postCameraUpdate(float tpf) {
    }

    /**
     * Return true if this control knows about the property.
     *
     * @param propertyId the property id.
     * @return true if this control knows about the property.
     */
    @JmeThread
    default boolean hasProperty(@NotNull String propertyId) {
        return false;
    }

    /**
     * Get a boolean property value.
     *
     * @param propertyId the property id.
     * @return the property value or false if the property is not known.
     */
    @JmeThread
    default boolean getBooleanProperty(@NotNull String propertyId) {
        return false;
    }

    /**
     * Notify this 3d part control about some events.
     *
     * @param event the event.
     */
    @JmeThread
    void notify(@NotNull Editor3dPartEvent event);
}
