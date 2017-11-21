package com.ss.editor.ui.preview.impl;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.JMEFilePreviewManager;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The implementation of {@link com.ss.editor.ui.preview.FilePreview} to show jME 3D files.
 *
 * @author JavaSaBr
 */
public class JmeObjectFilePreview extends AbstractFilePreview<ImageView> {

    @Override
    @FXThread
    protected @NotNull ImageView createGraphicsNode() {
        return new ImageView();
    }

    @Override
    @FXThread
    protected void initialize(@NotNull final ImageView node, @NotNull final StackPane pane) {
        super.initialize(node, pane);

        node.fitHeightProperty().bind(pane.heightProperty().subtract(2));
        node.fitWidthProperty().bind(pane.widthProperty().subtract(2));
    }

    @Override
    @FXThread
    public void hide() {
        super.hide();
        getGraphicsNode().imageProperty().unbind();
    }

    @Override
    @FXThread
    public void show(@NotNull final Path file) {
        super.show(file);

        final ImageView imageView = getGraphicsNode();
        final int width = (int) imageView.getFitWidth();
        final int height = (int) imageView.getFitHeight();

        final JMEFilePreviewManager previewManager = JMEFilePreviewManager.getInstance();
        previewManager.show(file, width, height);

        final ImageView sourceView = previewManager.getImageView();
        imageView.imageProperty().bind(sourceView.imageProperty());
    }

    @Override
    @FXThread
    public void show(@NotNull final String resource) {
        super.show(resource);

        final ImageView imageView = getGraphicsNode();
        final int width = (int) imageView.getFitWidth();
        final int height = (int) imageView.getFitHeight();

        final JMEFilePreviewManager previewManager = JMEFilePreviewManager.getInstance();
        previewManager.show(resource, width, height);

        final ImageView sourceView = previewManager.getImageView();
        imageView.imageProperty().bind(sourceView.imageProperty());
    }

    @Override
    @FXThread
    public boolean isSupport(@NotNull final String resource) {
        return JMEFilePreviewManager.isJmeFile(resource);
    }

    @Override
    @FXThread
    public boolean isSupport(@NotNull final Path file) {
        return JMEFilePreviewManager.isJmeFile(file);
    }

    @Override
    @FXThread
    public int getOrder() {
        return 10;
    }

    @Override
    @FXThread
    public void release() {
        final JMEFilePreviewManager previewManager = JMEFilePreviewManager.getInstance();
        previewManager.clear();
    }
}
