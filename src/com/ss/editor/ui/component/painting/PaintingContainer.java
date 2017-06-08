package com.ss.editor.ui.component.painting;

import com.ss.editor.model.editor.Painting3DProvider;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.container.EditorComponentContainer;
import com.ss.editor.ui.css.CSSIds;
import org.jetbrains.annotations.NotNull;

/**
 * The class container of painting components.
 *
 * @author JavaSaBr
 */
public class PaintingContainer extends EditorComponentContainer<Painting3DProvider, PaintingComponent> {

    public PaintingContainer(@NotNull final ModelChangeConsumer changeConsumer,
                             @NotNull final Painting3DProvider painting3DProvider) {
        super(changeConsumer, painting3DProvider, PaintingComponent.class);
        setId(CSSIds.PAINTING_CONTAINER);
    }
}
