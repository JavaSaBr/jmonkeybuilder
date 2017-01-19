package com.ss.editor.ui.control.app.state.property.control;

import static com.ss.editor.ui.control.app.state.property.control.AppStatePropertyControl.newChangeHandler;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyControl;
import com.ss.editor.ui.control.property.impl.AbstractFloatPropertyControl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractPropertyControl} to edit float values.
 *
 * @author JavaSaBr
 */
public class FloatAppStatePropertyControl<T> extends AbstractFloatPropertyControl<SceneChangeConsumer, T> {

    public FloatAppStatePropertyControl(@Nullable final Float propertyValue, @NotNull final String propertyName,
                                        @NotNull final SceneChangeConsumer changeConsumer) {
        super(propertyValue, propertyName, changeConsumer, newChangeHandler());
    }
}
