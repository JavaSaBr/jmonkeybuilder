package com.ss.builder.editor.impl;

import com.ss.builder.FileExtensions;
import com.ss.builder.JmeApplication;
import com.ss.builder.Messages;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.manager.ExecutorManager;
import com.ss.builder.manager.JavaFxImageManager;
import com.ss.builder.fx.css.CssClasses;
import com.ss.builder.fx.event.impl.FileChangedEvent;
import com.ss.builder.editor.EditorDescriptor;
import com.ss.rlib.fx.util.FxUtils;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The implementation of the {@link JmeApplication} to view image files.
 *
 * @author JavaSaBr
 */
public class ImageViewerEditor extends AbstractFileEditorLegacy<VBox> {

    public static final EditorDescriptor DESCRIPTOR = new EditorDescriptor(
            ImageViewerEditor::new,
            Messages.IMAGE_VIEWER_EDITOR_NAME,
            ImageViewerEditor.class.getSimpleName(),
            FileExtensions.IMAGE_EXTENSIONS
    );

    private static final int IMAGE_SIZE = 512;

    /**
     * The image view.
     */
    @NotNull
    private final ImageView imageView;

    private ImageViewerEditor() {
        this.imageView = new ImageView();
    }

    @Override
    @FxThread
    protected @NotNull VBox createRoot() {
        return new VBox();
    }

    @Override
    @FxThread
    protected void createContent(@NotNull VBox root) {
        FxUtils.addClass(root, CssClasses.IMAGE_VIEW_EDITOR_CONTAINER);
        FxUtils.addChild(root, imageView);
    }

    @Override
    protected void processChangedFileImpl(@NotNull FileChangedEvent event) {
        super.processChangedFileImpl(event);

        var executorManager = ExecutorManager.getInstance();
        executorManager.schedule(() -> executorManager.addFxTask(() -> showImage(getFile())), 1000);
    }

    @FxThread
    private void showImage(@NotNull Path file) {

        var preview = JavaFxImageManager.getInstance()
                .getImagePreview(file, IMAGE_SIZE, IMAGE_SIZE);

        imageView.setImage(preview);
    }

    @Override
    @FxThread
    public void openFile(@NotNull Path file) {
        super.openFile(file);
        showImage(file);
    }

    @Override
    @FromAnyThread
    public @NotNull EditorDescriptor getDescriptor() {
        return DESCRIPTOR;
    }
}
