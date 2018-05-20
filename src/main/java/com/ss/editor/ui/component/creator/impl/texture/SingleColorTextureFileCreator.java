package com.ss.editor.ui.component.creator.impl.texture;

import static com.ss.editor.extension.property.EditablePropertyType.COLOR;
import static com.ss.editor.extension.property.EditablePropertyType.INTEGER;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.jme3.math.ColorRGBA;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.plugin.api.file.creator.GenericFileCreator;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The creator to create a texture which filled one color.
 *
 * @author JavaSaBr
 */
public class SingleColorTextureFileCreator extends GenericFileCreator {

    @NotNull
    private static final String PROP_COLOR = "color";

    @NotNull
    private static final String PROP_HEIGHT = "height";

    @NotNull
    private static final String PROP_WIDTH = "width";

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    @NotNull
    private static final Array<PropertyDefinition> DEFINITIONS = ArrayFactory.newArray(PropertyDefinition.class);

    static {
        DESCRIPTION.setFileDescription(Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(SingleColorTextureFileCreator::new);
        DEFINITIONS.add(new PropertyDefinition(COLOR, Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_COLOR, PROP_COLOR, ColorRGBA.Gray));
        DEFINITIONS.add(new PropertyDefinition(INTEGER, Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_HEIGHT, PROP_HEIGHT, 2, 1, 256));
        DEFINITIONS.add(new PropertyDefinition(INTEGER, Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_WIDTH, PROP_WIDTH, 2, 1, 256));
    }

    /**
     * The image view to show preview of texture.
     */
    @Nullable
    private ImageView imageView;

    @Override
    @FxThread
    protected void createPreview(@NotNull final BorderPane container) {
        super.createPreview(container);
        imageView = new ImageView();
        container.setCenter(imageView);
    }

    /**
     * @return the image view to show preview of texture.
     */
    @FxThread
    private @NotNull ImageView getImageView() {
        return notNull(imageView);
    }

    @Override
    @FromAnyThread
    protected boolean needPreview() {
        return true;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_TITLE;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getFileExtension() {
        return FileExtensions.IMAGE_PNG;
    }

    @Override
    @FromAnyThread
    protected @NotNull Array<PropertyDefinition> getPropertyDefinitions() {
        return DEFINITIONS;
    }

    @Override
    @FxThread
    protected boolean validate(@NotNull final VarTable vars) {

        final Color color = UiUtils.from(vars.get(PROP_COLOR, ColorRGBA.class));

        final int width = vars.getInteger(PROP_WIDTH);
        final int height = vars.getInteger(PROP_HEIGHT);

        final WritableImage writableImage = new WritableImage(width, height);
        final PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixelWriter.setColor(i, j, color);
            }
        }

        getImageView().setImage(writableImage);
        return true;
    }

    @Override
    @BackgroundThread
    protected void writeData(@NotNull final VarTable vars, final @NotNull Path resultFile) throws IOException {
        super.writeData(vars, resultFile);

        final Color color = UiUtils.from(vars.get(PROP_COLOR, ColorRGBA.class));

        final int width = vars.getInteger(PROP_WIDTH);
        final int height = vars.getInteger(PROP_HEIGHT);

        final WritableImage writableImage = new WritableImage(width, height);
        final PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixelWriter.setColor(i, j, color);
            }
        }

        final BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        try (final OutputStream out = Files.newOutputStream(resultFile)) {
            ImageIO.write(bufferedImage, "png", out);
        }
    }
}
