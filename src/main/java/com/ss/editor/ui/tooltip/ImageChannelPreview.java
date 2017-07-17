package com.ss.editor.ui.tooltip;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.rlib.ui.util.FXUtils;
import javafx.scene.image.*;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;

/**
 * THe implementation of tooltip for showing image channels.
 *
 * @author JavaSaBr
 */
public class ImageChannelPreview extends CustomTooltip<GridPane> {

    @NotNull
    private static final JavaFXImageManager IMAGE_MANAGER = JavaFXImageManager.getInstance();

    /**
     * The red image.
     */
    @NotNull
    private final WritableImage redImage;

    /**
     * The green image.
     */
    @NotNull
    private final WritableImage greenImage;

    /**
     * The blue image.
     */
    @NotNull
    private final WritableImage blueImage;

    /**
     * The alpha image.
     */
    @NotNull
    private final WritableImage alphaImage;

    /**
     * The red image view.
     */
    @Nullable
    private ImageView redView;

    /**
     * The green image view.
     */
    @Nullable
    private ImageView greenView;

    /**
     * The blue image view.
     */
    @Nullable
    private ImageView blueView;

    /**
     * The alpha image view.
     */
    @Nullable
    private ImageView alphaView;

    /**
     * Instantiates a new Image channel preview.
     */
    public ImageChannelPreview() {
        redImage = new WritableImage(120, 120);
        greenImage = new WritableImage(120, 120);
        blueImage = new WritableImage(120, 120);
        alphaImage = new WritableImage(120, 120);
    }

    /**
     * @return the alpha image.
     */
    @NotNull
    @FXThread
    private WritableImage getAlphaImage() {
        return alphaImage;
    }

    /**
     * @return the red image.
     */
    @NotNull
    @FXThread
    private WritableImage getRedImage() {
        return redImage;
    }

    /**
     * @return the blue image.
     */
    @NotNull
    @FXThread
    private WritableImage getBlueImage() {
        return blueImage;
    }

    /**
     * @return the green image.
     */
    @NotNull
    @FXThread
    private WritableImage getGreenImage() {
        return greenImage;
    }

    @Override
    protected void createContent(@NotNull final GridPane root) {
        super.createContent(root);

        redView = new ImageView();
        greenView = new ImageView();
        blueView = new ImageView();
        alphaView = new ImageView();

        root.add(redView, 0, 0);
        root.add(greenView, 1, 0);
        root.add(blueView, 0, 1);
        root.add(alphaView, 1, 1);
    }

    @NotNull
    @Override
    protected GridPane createRoot() {
        final GridPane gridPane = new GridPane();
        FXUtils.addClassesTo(gridPane, CSSClasses.DEF_GRID_PANE, CSSClasses.IMAGE_CHANNEL_PREVIEW);
        return gridPane;
    }

    /**
     * @return the alpha image view.
     */
    @NotNull
    @FXThread
    private ImageView getAlphaView() {
        return notNull(alphaView);
    }

    /**
     * @return the blue image view.
     */
    @NotNull
    @FXThread
    private ImageView getBlueView() {
        return notNull(blueView);
    }

    /**
     * @return the green image view.
     */
    @NotNull
    @FXThread
    private ImageView getGreenView() {
        return notNull(greenView);
    }

    /**
     * @return the red image view.
     */
    @NotNull
    @FXThread
    private ImageView getRedView() {
        return notNull(redView);
    }

    /**
     * Show the file.
     *
     * @param file the file
     */
    @FXThread
    public void showImage(@Nullable final Path file) {

        final Image image = file == null ? null : IMAGE_MANAGER.getTexturePreview(file, 120, 120);

        if (file == null || image.getWidth() != 120) {

            final ImageView redView = getRedView();
            redView.setImage(null);

            final ImageView greenView = getGreenView();
            greenView.setImage(null);

            final ImageView blueView = getBlueView();
            blueView.setImage(null);

            final ImageView alphaView = getAlphaView();
            alphaView.setImage(null);
            return;
        }

        final PixelReader pixelReader = image.getPixelReader();

        final WritableImage alphaImage = getAlphaImage();
        final PixelWriter alphaWriter = alphaImage.getPixelWriter();

        final WritableImage redImage = getRedImage();
        final PixelWriter redWriter = redImage.getPixelWriter();

        final WritableImage greenImage = getGreenImage();
        final PixelWriter greenWriter = greenImage.getPixelWriter();

        final WritableImage blueImage = getBlueImage();
        final PixelWriter blueWriter = blueImage.getPixelWriter();

        for (int y = 0, height = (int) image.getHeight(); y < height; y++) {
            for (int x = 0, width = (int) image.getWidth(); x < width; x++) {

                final int argb = pixelReader.getArgb(x, y);

                final int alpha = argb >>> 24;
                final int red = (argb >> 16) & 0xff;
                final int green = (argb >> 8) & 0xff;
                final int blue = (argb) & 0xff;

                redWriter.setArgb(x, y, ((255 << 24) | (red << 16) | (red << 8) | red));
                greenWriter.setArgb(x, y, ((255 << 24) | (green << 16) | (green << 8) | green));
                blueWriter.setArgb(x, y, ((255 << 24) | (blue << 16) | (blue << 8) | blue));
                alphaWriter.setArgb(x, y, ((255 << 24) | (alpha << 16) | (alpha << 8) | alpha));
            }
        }

        final ImageView redView = getRedView();
        redView.setImage(null);
        redView.setImage(redImage);

        final ImageView greenView = getGreenView();
        greenView.setImage(null);
        greenView.setImage(greenImage);

        final ImageView blueView = getBlueView();
        blueView.setImage(null);
        blueView.setImage(blueImage);

        final ImageView alphaView = getAlphaView();
        alphaView.setImage(null);
        alphaView.setImage(alphaImage);
    }
}
