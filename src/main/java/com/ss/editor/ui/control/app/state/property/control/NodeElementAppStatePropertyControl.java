package com.ss.editor.ui.control.app.state.property.control;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.model.undo.editor.SceneChangeConsumer;
import com.ss.editor.ui.control.model.tree.dialog.NodeSelectorDialog;
import javafx.scene.control.Label;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractElementAppStatePropertyControl} to edit a spatial from a scene.
 *
 * @param <D> the type parameter
 * @author JavaSaBr
 */
public class NodeElementAppStatePropertyControl<D> extends AbstractElementAppStatePropertyControl<D, Node> {

    /**
     * Instantiates a new Point light element filter property control.
     *
     * @param propertyValue  the property value
     * @param propertyName   the property name
     * @param changeConsumer the change consumer
     */
    public NodeElementAppStatePropertyControl(@Nullable final Node propertyValue, @NotNull final String propertyName,
                                              @NotNull final SceneChangeConsumer changeConsumer) {
        super(Node.class, propertyValue, propertyName, changeConsumer);
    }

    @NotNull
    protected NodeSelectorDialog<Node> createNodeSelectorDialog() {
        final SceneChangeConsumer changeConsumer = getChangeConsumer();
        return new NodeSelectorDialog<>(changeConsumer.getCurrentModel(), type, this::processAdd);
    }

    @Override
    protected void reload() {

        final Spatial spatial = getPropertyValue();
        final Label elementLabel = getElementLabel();

        String name = spatial == null ? null : spatial.getName();
        name = name == null && spatial != null ? spatial.getClass().getSimpleName() : name;

        elementLabel.setText(name == null ? NO_ELEMENT : name);
    }
}
