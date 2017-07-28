package com.ss.editor.ui.control.filter.property.control;

import static com.ss.editor.ui.control.filter.property.control.FilterPropertyControl.newChangeHandler;
import com.jme3.texture.Texture2D;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.impl.AbstractTexture2DPropertyControl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit texture 2D values.
 *
 * @param <T> the type parameter
 * @author JavaSaBr
 */
public class Texture2DFilterPropertyControl<T> extends AbstractTexture2DPropertyControl<SceneChangeConsumer, T> {

    /**
     * Instantiates a new Float filter property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     */
    public Texture2DFilterPropertyControl(@Nullable final Texture2D propertyValue, @NotNull final String propertyName,
                                          @NotNull final SceneChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
