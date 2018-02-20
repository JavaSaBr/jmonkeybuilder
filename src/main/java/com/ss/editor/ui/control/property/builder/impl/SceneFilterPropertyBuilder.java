package com.ss.editor.ui.control.property.builder.impl;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.filter.EditableSceneFilter;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The property builder to build property controls of editable scene app states.
 *
 * @author JavaSaBr
 */
public class SceneFilterPropertyBuilder extends EditableModelObjectPropertyBuilder {

    @NotNull
    private static final SceneFilterPropertyBuilder INSTANCE = new SceneFilterPropertyBuilder();

    @FromAnyThread
    public static @NotNull SceneFilterPropertyBuilder getInstance() {
        return INSTANCE;
    }

    protected SceneFilterPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull final Object object) {
        if (object instanceof EditableSceneFilter) {
            return ((EditableSceneFilter) object).getEditableProperties();
        } else {
            return null;
        }
    }
}