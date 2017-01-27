package com.ss.editor.ui.control.filter.property;

import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.property.AbstractPropertyEditor;

import org.jetbrains.annotations.NotNull;

/**
 * The component to contains property controls in the editor.
 *
 * @author JavaSaBr
 */
public class FilterPropertyEditor extends AbstractPropertyEditor<SceneChangeConsumer> {

    public FilterPropertyEditor(@NotNull final SceneChangeConsumer changeConsumer) {
        super(changeConsumer);
    }
}
