package com.ss.editor.ui.component.painting;

import com.ss.editor.model.editor.Painting3DProvider;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.container.ProcessingComponent;
import com.ss.editor.ui.component.container.ProcessingComponentContainer;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The class container of painting components.
 *
 * @author JavaSaBr
 */
public class PaintingComponentContainer extends ProcessingComponentContainer<Painting3DProvider, PaintingComponent> {

    /**
     * Instantiates a new Painting component container.
     *
     * @param changeConsumer     the change consumer
     * @param painting3DProvider the painting 3 d provider
     */
    public PaintingComponentContainer(@NotNull final ModelChangeConsumer changeConsumer,
                                      @NotNull final Painting3DProvider painting3DProvider) {
        super(changeConsumer, painting3DProvider, PaintingComponent.class);
    }

    @Override
    public void showComponentFor(@Nullable final Object element) {
        super.showComponentFor(null);

        if (element == null) return;

        final VBox container = getContainer();
        final ObservableList<Node> children = container.getChildren();

        getComponents().stream()
                .filter(component -> component.isSupport(element))
                .peek(component -> children.add((Node) component))
                .peek(component -> component.startProcessing(element))
                .filter(component -> isShowed())
                .forEach(ProcessingComponent::notifyShowed);
    }
}
