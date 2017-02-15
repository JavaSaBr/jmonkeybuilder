package com.ss.editor.ui.component.editing.impl;

import static java.util.Objects.requireNonNull;
import static rlib.util.ClassUtils.unsafeCast;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.ui.component.editing.EditingComponent;
import com.ss.editor.ui.component.editing.EditingContainer;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * The base implementation of an editing component.
 *
 * @author JavaSaBr
 */
public abstract class AbstractEditingComponent<T> extends VBox implements EditingComponent {

    /**
     * The parent container.
     */
    @Nullable
    protected EditingContainer editingContainer;

    /**
     * The cursor node.
     */
    @Nullable
    protected Node cursorNode;

    /**
     * The edited object.
     */
    @Nullable
    protected T editedObject;

    public AbstractEditingComponent() {
        createComponents();
    }

    @Override
    public void initFor(@NotNull final EditingContainer container) {
        this.editingContainer = container;
    }

    /**
     * @return the parent container.
     */
    @NotNull
    protected EditingContainer getEditingContainer() {
        return requireNonNull(editingContainer);
    }

    /**
     * @return the edited object.
     */
    @NotNull
    protected T getEditedObject() {
        return requireNonNull(editedObject);
    }

    @Override
    public void startEditing(@NotNull final Object object) {
        this.editedObject = unsafeCast(object);
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
