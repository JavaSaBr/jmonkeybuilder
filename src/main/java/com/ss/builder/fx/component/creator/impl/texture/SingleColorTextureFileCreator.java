package com.ss.builder.ui.component.creator.impl.texture;

import static com.ss.editor.extension.property.EditablePropertyType.COLOR;
import static com.ss.editor.extension.property.EditablePropertyType.INTEGER;
import com.jme3.math.ColorRGBA;
import com.ss.builder.FileExtensions;
import com.ss.builder.Messages;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.plugin.api.file.creator.GenericFileCreator;
import com.ss.builder.plugin.api.property.PropertyDefinition;
import com.ss.builder.ui.util.UiUtils;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.plugin.api.file.creator.GenericFileCreator;
import com.ss.editor.plugin.api.property.PropertyDefinition;
import com.ss.editor.ui.component.creator.FileCreatorDescriptor;
import com.ss.editor.ui.util.UiUtils;
import com.ss.rlib.common.util.VarTable;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import org.jetbrains.annotations.NotNull;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The creator to create a texture which filled one color.
 *
 * @author JavaSaBr
 */
public class SingleColorTextureFileCreator extends GenericFileCreator {

    private static final String PROP_COLOR = "color";
    private static final String PROP_HEIGHT = "height";
    private static final String PROP_WIDTH = "width";

    public static final FileCreatorDescriptor DESCRIPTOR = new FileCreatorDescriptor(
            Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_DESCRIPTION,
            SingleColorTextureFileCreator::new
    );

    @NotNull
    private static final Array<PropertyDefinition> DEFINITIONS = ArrayFactory.newArray(PropertyDefinition.class);

    static {
        DEFINITIONS.add(new PropertyDefinition(COLOR, Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_COLOR, PROP_COLOR, ColorRGBA.Gray));
        DEFINITIONS.add(new PropertyDefinition(INTEGER, Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_HEIGHT, PROP_HEIGHT, 2, 1, 256));
        DEFINITIONS.add(new PropertyDefinition(INTEGER, Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_WIDTH, PROP_WIDTH, 2, 1, 256));
    }

    /**
     * The image view to show preview of texture.
     */
    @NotNull
    private final ImageView imageView;

    private SingleColorTextureFileCreator() {
        this.imageView = new ImageView();
    }

    @Override
    @FxThread
    protected void createPreview(@NotNull BorderPane container) {
        super.createPreview(container);
        container.setCenter(imageView);
    }

    /**
     * Get the image view to show preview of texture.
     *
     * @return the image view to show preview of texture.
     */
    @FxThread
    private @NotNull ImageView getImageView() {
        return imageView;
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
    protected boolean validate(@NotNull VarTable vars) {

        var color = UiUtils.from(vars.get(PROP_COLOR, ColorRGBA.class));

        var width = vars.getInteger(PROP_WIDTH);
        var height = vars.getInteger(PROP_HEIGHT);

        var writableImage = new WritableImage(width, height);
        var pixelWriter = writableImage.getPixelWriter();

        for (var i = 0; i < width; i++) {
            for (var j = 0; j < height; j++) {
                pixelWriter.setColor(i, j, color);
            }
        }

        getImageView().setImage(writableImage);

        return true;
    }

    @Override
    @BackgroundThread
    protected void writeData(@NotNull VarTable vars, @NotNull Path resultFile) throws IOException {
        super.writeData(vars, resultFile);

        var color = UiUtils.from(vars.get(PROP_COLOR, ColorRGBA.class));

        var width = vars.getInteger(PROP_WIDTH);
        var height = vars.getInteger(PROP_HEIGHT);

        var writableImage = new WritableImage(width, height);
        var pixelWriter = writableImage.getPixelWriter();

        for (var i = 0; i < width; i++) {
            for (var j = 0; j < height; j++) {
                pixelWriter.setColor(i, j, color);
            }
        }

        var bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        try (var out = Files.newOutputStream(resultFile)) {
            ImageIO.write(bufferedImage, "png", out);
        }
    }
}
