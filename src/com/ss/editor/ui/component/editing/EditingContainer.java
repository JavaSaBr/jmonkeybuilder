package com.ss.editor.ui.component.editing;

import com.ss.editor.model.editor.Editing3DProvider;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.container.EditorComponentContainer;
import org.jetbrains.annotations.NotNull;

/**
 * The class container of editing components.
 *
 * @author JavaSaBr
 */
public class EditingContainer extends EditorComponentContainer<Editing3DProvider, EditingComponent> {

    public EditingContainer(@NotNull final ModelChangeConsumer changeConsumer,
                            @NotNull final Editing3DProvider provider) {
        super(changeConsumer, provider, EditingComponent.class);
    }
}
