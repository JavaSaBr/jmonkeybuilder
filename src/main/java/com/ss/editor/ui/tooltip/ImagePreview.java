package com.ss.editor.ui.tooltip;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.manager.JavaFxImageManager;
import com.ss.editor.ui.css.CssClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * THe implementation of tooltip for showing image channels.
 *
 * @author JavaSaBr
 */
public class ImagePreview extends CustomTooltip<BorderPane> {

    @NotNull
    private static final JavaFxImageManager IMAGE_MANAGER = JavaFxImageManager.getInstance();

    /**
     * The file to show.
     */
    @NotNull
    private final Path path;

    /**
     * The image view.
     */
    @Nullable
    private ImageView imageView;

    public ImagePreview(@NotNull final Path path) {
        this.path = path;
    }

    @Override
    @FxThread
    protected void createContent(@NotNull final BorderPane root) {
        super.createContent(root);

        imageView = new ImageView();
        imageView.fitHeightProperty().bind(root.widthProperty());
        imageView.fitWidthProperty().bind(root.heightProperty());

        root.setCenter(imageView);
    }

    @Override
    @FxThread
    public void show(final Window owner) {
        super.show(owner);
    }

    @Override
    @FxThread
    protected void show() {

        final ImageView imageView = getImageView();
        if (imageView.getImage() == null) {
            showImage(path);
        }

        super.show();
    }

    @Override
    @FxThread
    protected @NotNull BorderPane createRoot() {
        final BorderPane pane = new BorderPane();
        FXUtils.addClassesTo(pane, CssClasses.IMAGE_PREVIEW);
        return pane;
    }

    /**
     * @return the image view.
     */
    @FxThread
    private @NotNull ImageView getImageView() {
        return notNull(imageView);
    }

    /**
     * Show the file.
     *
     * @param file the file
     */
    @FxThread
    public void showImage(@Nullable final Path file) {
        getImageView().setImage(IMAGE_MANAGER.getImagePreview(file, 200, 200));
    }
}
