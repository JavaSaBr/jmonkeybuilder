package com.ss.editor.ui.preview.impl;

import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.JavaFXImageManager;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The implementation of {@link com.ss.editor.ui.preview.FilePreview} to show image files.
 *
 * @author JavaSaBr
 */
public class ImageFilePreview extends AbstractFilePreview<ImageView> {

    /**
     * The image manager.
     */
    @NotNull
    protected static final JavaFXImageManager JAVA_FX_IMAGE_MANAGER = JavaFXImageManager.getInstance();

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
        getGraphicsNode().setImage(null);
    }

    @Override
    @FXThread
    public void show(@NotNull final Path file) {
        super.show(file);

        final ImageView imageView = getGraphicsNode();
        final int width = (int) imageView.getFitWidth();
        final int height = (int) imageView.getFitHeight();

        imageView.setImage(JAVA_FX_IMAGE_MANAGER.getImagePreview(file, width, height));
    }

    @Override
    @FXThread
    public void show(@NotNull final String resource) {
        super.show(resource);

        final ImageView imageView = getGraphicsNode();
        final int width = (int) imageView.getFitWidth();
        final int height = (int) imageView.getFitHeight();

        imageView.setImage(JAVA_FX_IMAGE_MANAGER.getImagePreview(resource, width, height));
    }

    @Override
    @FXThread
    public boolean isSupport(@NotNull final String resource) {
        return JavaFXImageManager.isImage(resource);
    }

    @Override
    @FXThread
    public boolean isSupport(@NotNull final Path file) {
        return JavaFXImageManager.isImage(file);
    }

    @Override
    @FXThread
    public int getOrder() {
        return 10;
    }

    @Override
    @FXThread
    public void release() {
        getGraphicsNode().setImage(null);
    }
}
