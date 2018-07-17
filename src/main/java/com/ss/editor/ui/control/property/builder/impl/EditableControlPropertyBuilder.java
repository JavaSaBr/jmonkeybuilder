package com.ss.editor.ui.control.property.builder.impl;

import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.property.EditableProperty;
import com.ss.editor.extension.scene.control.EditableControl;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * The property builder to build property controls of editable controls.
 *
 * @author JavaSaBr
 */
public class EditableControlPropertyBuilder extends EditableModelObjectPropertyBuilder {

    private static final EditableControlPropertyBuilder INSTANCE = new EditableControlPropertyBuilder();

    @FromAnyThread
    public static @NotNull EditableControlPropertyBuilder getInstance() {
        return INSTANCE;
    }

    private EditableControlPropertyBuilder() {
        super(ModelChangeConsumer.class);
    }

    @Override
    @FxThread
    protected @Nullable List<EditableProperty<?, ?>> getProperties(@NotNull Object object) {
        if (object instanceof EditableControl) {
            return ((EditableControl) object).getEditableProperties();
        } else {
            return null;
        }
    }
}
