package com.ss.editor.ui.component.editing;

import com.ss.editor.model.editor.Editing3DProvider;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.container.ProcessingComponentContainer;
import org.jetbrains.annotations.NotNull;

/**
 * The class container of editing components.
 *
 * @author JavaSaBr
 */
public class EditingComponentContainer extends ProcessingComponentContainer<Editing3DProvider, EditingComponent> {

    /**
     * Instantiates a new Editing component container.
     *
     * @param changeConsumer the change consumer
     * @param provider       the provider
     */
    public EditingComponentContainer(@NotNull final ModelChangeConsumer changeConsumer,
                                     @NotNull final Editing3DProvider provider) {
        super(changeConsumer, provider, EditingComponent.class);
    }
}
