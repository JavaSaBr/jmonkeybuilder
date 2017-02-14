package com.ss.editor.ui.component.editing.impl;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.ui.component.editing.EditingComponent;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * The base implementation of an editing component.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditingComponent extends VBox implements EditingComponent {

    /**
     * The cursor node.
     */
    @Nullable
    protected Node cursorNode;

    public AbstractEditingComponent() {
        createComponents();
    }

    protected void createComponents() {

    }

    @Override
    public Control cloneForSpatial(final Spatial spatial) {
        throw new RuntimeException("unsupported");
    }

    @Override
    public void setSpatial(final Spatial spatial) {

    }

    @Override
    public void update(final float tpf) {

    }

    @Override
    public void render(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {

    }

    @Override
    public void write(@NotNull final JmeExporter ex) throws IOException {
    }

    @Override
    public void read(@NotNull final JmeImporter im) throws IOException {
    }
}
