package com.ss.builder.ui.preview.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.JavaFxImageManager;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.JavaFxImageManager;
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
    protected static final JavaFxImageManager JAVA_FX_IMAGE_MANAGER = JavaFxImageManager.getInstance();

    @Override
    @FxThread
    protected @NotNull ImageView createGraphicsNode() {
        return new ImageView();
    }

    @Override
    @FxThread
    protected void initialize(@NotNull final ImageView node, @NotNull final StackPane pane) {
        super.initialize(node, pane);

        node.fitHeightProperty().bind(pane.heightProperty().subtract(2));
        node.fitWidthProperty().bind(pane.widthProperty().subtract(2));
    }

    @Override
    @FxThread
    public void hide() {
        super.hide();
        getGraphicsNode().setImage(null);
    }

    @Override
    @FxThread
    public void show(@NotNull final Path file) {
        super.show(file);

        final ImageView imageView = getGraphicsNode();
        final int width = (int) imageView.getFitWidth();
        final int height = (int) imageView.getFitHeight();

        imageView.setImage(JAVA_FX_IMAGE_MANAGER.getImagePreview(file, width, height));
    }

    @Override
    @FxThread
    public void show(@NotNull final String resource) {
        super.show(resource);

        final ImageView imageView = getGraphicsNode();
        final int width = (int) imageView.getFitWidth();
        final int height = (int) imageView.getFitHeight();

        imageView.setImage(JAVA_FX_IMAGE_MANAGER.getImagePreview(resource, width, height));
    }

    @Override
    @FxThread
    public boolean isSupport(@NotNull final String resource) {
        return JavaFxImageManager.isImage(resource);
    }

    @Override
    @FxThread
    public boolean isSupport(@NotNull final Path file) {
        return JavaFxImageManager.isImage(file);
    }

    @Override
    @FxThread
    public int getOrder() {
        return 10;
    }

    @Override
    @FxThread
    public void release() {
        getGraphicsNode().setImage(null);
    }
}
