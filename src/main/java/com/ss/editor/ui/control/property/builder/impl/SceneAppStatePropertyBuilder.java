package com.ss.editor.ui.control.property.builder.impl;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.app.state.EditableSceneAppState;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The property builder to build property controls of editable scene app states.
 *
 * @author JavaSaBr
 */
public class SceneAppStatePropertyBuilder extends EditableModelObjectPropertyBuilder {

    @NotNull
    private static final SceneAppStatePropertyBuilder INSTANCE = new SceneAppStatePropertyBuilder();

    @FromAnyThread
    public static @NotNull SceneAppStatePropertyBuilder getInstance() {
        return INSTANCE;
    }

    protected SceneAppStatePropertyBuilder() {
        super(SceneChangeConsumer.class);
    }

    @Override
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull final Object object) {
        if (object instanceof EditableSceneAppState) {
            return ((EditableSceneAppState) object).getEditableProperties();
        } else {
            return null;
        }
    }
}
