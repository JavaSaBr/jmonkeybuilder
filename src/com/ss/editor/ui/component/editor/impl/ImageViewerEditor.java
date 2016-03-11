package com.ss.editor.ui.component.editor.impl;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.ui.component.editor.EditorDescription;

import java.nio.file.Path;

import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * Реализация редактора для просмотра картинок.
 *
 * @author Ronn
 */
public class ImageViewerEditor extends AbstractFileEditor<VBox> {

    public static final EditorDescription DESCRIPTION = new EditorDescription();
    public static final JavaFXImageManager JAVA_FX_IMAGE_MANAGER = JavaFXImageManager.getInstance();

    public static final int IMAGE_SIZE = 512;

    static {
        DESCRIPTION.setConstructor(ImageViewerEditor::new);
        DESCRIPTION.setEditorName(Messages.IMAGE_VIEWER_EDITOR_NAME);
        DESCRIPTION.addExtension(FileExtensions.IMAGE_BMP);
        DESCRIPTION.addExtension(FileExtensions.IMAGE_GIF);
        DESCRIPTION.addExtension(FileExtensions.IMAGE_JPEG);
        DESCRIPTION.addExtension(FileExtensions.IMAGE_PNG);
        DESCRIPTION.addExtension(FileExtensions.IMAGE_TGA);
        DESCRIPTION.addExtension(FileExtensions.IMAGE_JPG);
        DESCRIPTION.addExtension(FileExtensions.IMAGE_TIFF);
    }

    /**
     * Область для просмотра картинки.
     */
    private ImageView imageView;

    @Override
    protected VBox createRoot() {
        return new VBox();
    }

    @Override
    protected void createContent(final VBox root) {
        root.setAlignment(Pos.CENTER);

        imageView = new ImageView();
        imageView.setFitHeight(IMAGE_SIZE);
        imageView.setFitWidth(IMAGE_SIZE);

        FXUtils.addToPane(imageView, root);
    }

    /**
     * @return область для просмотра картинки.
     */
    private ImageView getImageView() {
        return imageView;
    }

    @Override
    public void openFile(final Path file) {
        super.openFile(file);

        final Image preview = JAVA_FX_IMAGE_MANAGER.getTexturePreview(file, IMAGE_SIZE, IMAGE_SIZE);

        final ImageView imageView = getImageView();
        imageView.setImage(preview);
    }
}
