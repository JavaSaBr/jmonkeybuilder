package com.ss.editor.ui.component.creator.impl.texture;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.component.creator.impl.AbstractFileCreator;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.control.input.IntegerTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
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
public class SingleColorTextureFileCreator extends AbstractFileCreator {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(SingleColorTextureFileCreator::new);
    }

    /**
     * The width field.
     */
    @Nullable
    private IntegerTextField widthField;

    /**
     * The height field.
     */
    @Nullable
    private IntegerTextField heightField;

    /**
     * The color picker.
     */
    @Nullable
    private ColorPicker colorPicker;

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_TITLE;
    }

    @NotNull
    @Override
    protected String getFileExtension() {
        return FileExtensions.IMAGE_PNG;
    }

    @Override
    protected void createSettings(@NotNull final GridPane root) {
        super.createSettings(root);

        final Label widthLabel = new Label(Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_WIDTH + ":");
        widthLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        widthField = new IntegerTextField();
        widthField.setMinMax(2, 1024);
        widthField.setValue(2);
        widthField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final Label heightLabel = new Label(Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_HEIGHT + ":");
        heightLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        heightField = new IntegerTextField();
        heightField.setMinMax(2, 1024);
        heightField.setValue(2);
        heightField.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final Label colorLabel = new Label(Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_COLOR + ":");
        colorLabel.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        colorPicker = new ColorPicker(Color.GRAY);
        colorPicker.prefWidthProperty().bind(root.widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        root.add(widthLabel, 0, 1);
        root.add(widthField, 1, 1);
        root.add(heightLabel, 0, 2);
        root.add(heightField, 1, 2);
        root.add(colorLabel, 0, 3);
        root.add(colorPicker, 1, 3);

        FXUtils.addClassTo(widthLabel, heightLabel, colorLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(widthField, heightField, colorPicker, CSSClasses.DIALOG_FIELD);
    }

    /**
     * @return the width field.
     */
    @NotNull
    private IntegerTextField getWidthField() {
        return notNull(widthField);
    }

    /**
     * @return the height field.
     */
    @NotNull
    private IntegerTextField getHeightField() {
        return notNull(heightField);
    }

    /**
     * @return the color picker.
     */
    @NotNull
    private ColorPicker getColorPicker() {
        return notNull(colorPicker);
    }

    @Override
    protected void processOk() {
        super.processOk();

        final Path fileToCreate = notNull(getFileToCreate());
        try {
            Files.createFile(fileToCreate);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        final IntegerTextField widthField = getWidthField();
        final IntegerTextField heightField = getHeightField();
        final ColorPicker colorPicker = getColorPicker();

        final Color color = colorPicker.getValue();

        final int width = widthField.getValue();
        final int height = heightField.getValue();

        final WritableImage writableImage = new WritableImage(width, height);
        final PixelWriter pixelWriter = writableImage.getPixelWriter();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixelWriter.setColor(i, j, color);
            }
        }

        final BufferedImage bufferedImage = SwingFXUtils.fromFXImage(writableImage, null);

        try (final OutputStream out = Files.newOutputStream(fileToCreate)) {
            ImageIO.write(bufferedImage, "png", out);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        notifyFileCreated(fileToCreate, true);
    }
}
