package com.ss.editor.ui.preview.impl;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.ui.preview.FilePreview;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The base implementation of the {@link FilePreview}
 *
 * @author JavaSaBr
 */
public abstract class AbstractFilePreview<T extends Node> implements FilePreview {

    /**
     * The graphics node.
     */
    @NotNull
    private final T graphicsNode;

    protected AbstractFilePreview() {
        this.graphicsNode = createGraphicsNode();
    }

    /**
     * Create the graphics node.
     *
     * @return the graphics node.
     */
    @FXThread
    protected abstract @NotNull T createGraphicsNode();

    @Override
    @FXThread
    public void initialize(@NotNull final StackPane pane) {
        initialize(getGraphicsNode(), pane);
    }

    /**
     * Initialize the graphics node with the pane container.
     *
     * @param node the graphics node.
     * @param pane the pane container.
     */
    @FXThread
    protected void initialize(@NotNull final T node, @NotNull final StackPane pane) {
        FXUtils.addToPane(node, pane);
    }

    /**
     * Get the graphics node.
     *
     * @return the graphics node.
     */
    @FXThread
    protected @NotNull T getGraphicsNode() {
        return graphicsNode;
    }

    @Override
    @FXThread
    public void hide() {
        final T node = getGraphicsNode();
        node.setVisible(false);
        node.setManaged(false);
        node.setMouseTransparent(true);
    }

    @Override
    @FXThread
    public void show(@NotNull final String resource) {
        prepareToShow();
    }

    @FXThread
    protected void prepareToShow() {
        final T node = getGraphicsNode();
        node.setVisible(true);
        node.setManaged(true);
        node.setMouseTransparent(false);
    }

    @Override
    @FXThread
    public void show(@NotNull final Path file) {
        prepareToShow();
    }
}
