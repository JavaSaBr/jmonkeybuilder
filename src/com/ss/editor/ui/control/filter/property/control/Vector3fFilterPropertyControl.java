package com.ss.editor.ui.control.filter.property.control;

import static com.ss.editor.ui.control.filter.property.control.FilterPropertyControl.newChangeHandler;

import com.jme3.math.Vector3f;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.impl.AbstractVector3fPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit vector3f values.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class Vector3fFilterPropertyControl<T> extends AbstractVector3fPropertyControl<SceneChangeConsumer, T> {

    /**
     * Instantiates a new Vector 3 f filter property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     */
    public Vector3fFilterPropertyControl(@Nullable final Vector3f propertyValue, @NotNull final String propertyName,
                                         @NotNull final SceneChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
