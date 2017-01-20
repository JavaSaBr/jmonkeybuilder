package com.ss.editor.ui.control.app.state.property.control;

import static com.ss.editor.ui.control.app.state.property.control.AppStatePropertyControl.newChangeHandler;

import com.jme3.math.ColorRGBA;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.impl.AbstractColorPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit a color values.
 *
 * @author JavaSaBr
 */
public class ColorAppStatePropertyControl<T> extends AbstractColorPropertyControl<SceneChangeConsumer, T> {

    public ColorAppStatePropertyControl(@Nullable final ColorRGBA propertyValue, @NotNull final String propertyName,
                                        @NotNull final SceneChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
