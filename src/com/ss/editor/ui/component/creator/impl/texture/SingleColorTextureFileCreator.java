package com.ss.editor.ui.component.creator.impl.texture;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.ui.component.creator.impl.AbstractFileCreator;
import com.ss.editor.ui.control.fx.IntegerTextField;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.util.EditorUtil;

import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import rlib.ui.util.FXUtils;

/**
 * The creator for creating a texture which filled one color.
 *
 * @author JavaSaBr
 */
public class SingleColorTextureFileCreator extends AbstractFileCreator {

    protected static final Insets CONTAINER_OFFSET = new Insets(1, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);

    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(SingleColorTextureFileCreator::new);
    }

    /**
     * The width field.
     */
    private IntegerTextField widthField;

    /**
     * The height field.
     */
    private IntegerTextField heightField;

    /**
     * The color picker.
     */
    private ColorPicker colorPicker;

    public SingleColorTextureFileCreator() {
        super();
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_TITLE;
    }

    @Override
    protected String getFileExtension() {
        return FileExtensions.IMAGE_PNG;
    }

    @Override
    protected void createSettings(final VBox root) {
        super.createSettings(root);

        final HBox widthContainer = new HBox();

        final Label widthLabel = new Label(Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_WIDTH + ":");
        widthLabel.setId(CSSIds.FILE_CREATOR_LABEL);

        widthField = new IntegerTextField();
        widthField.setId(CSSIds.FILE_CREATOR_TEXT_FIELD);
        widthField.prefWidthProperty().bind(root.widthProperty());
        widthField.setMinMax(2, 1024);
        widthField.setValue(2);

        FXUtils.addToPane(widthLabel, widthContainer);
        FXUtils.addToPane(widthField, widthContainer);
        FXUtils.addToPane(widthContainer, root);

        final HBox heightContainer = new HBox();

        final Label heightLabel = new Label(Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_HEIGHT + ":");
        heightLabel.setId(CSSIds.FILE_CREATOR_LABEL);

        heightField = new IntegerTextField();
        heightField.setId(CSSIds.FILE_CREATOR_TEXT_FIELD);
        heightField.prefWidthProperty().bind(root.widthProperty());
        heightField.setMinMax(2, 1024);
        heightField.setValue(2);

        FXUtils.addToPane(heightLabel, heightContainer);
        FXUtils.addToPane(heightField, heightContainer);
        FXUtils.addToPane(heightContainer, root);

        final HBox colorContainer = new HBox();

        final Label colorLabel = new Label(Messages.SINGLE_COLOR_TEXTURE_FILE_CREATOR_COLOR + ":");
        colorLabel.setId(CSSIds.FILE_CREATOR_LABEL);

        colorPicker = new ColorPicker(Color.GRAY);
        colorPicker.setId(CSSIds.FILE_CREATOR_TEXT_FIELD);
        colorPicker.prefWidthProperty().bind(root.widthProperty());

        FXUtils.addToPane(colorLabel, colorContainer);
        FXUtils.addToPane(colorPicker, colorContainer);
        FXUtils.addToPane(colorContainer, root);

        FXUtils.addClassTo(widthLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(widthField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(heightLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(heightField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(colorLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(colorPicker, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(widthContainer, FILE_NAME_CONTAINER_OFFSET);
        VBox.setMargin(heightContainer, CONTAINER_OFFSET);
        VBox.setMargin(colorContainer, CONTAINER_OFFSET);
    }

    /**
     * @return the width field.
     */
    private IntegerTextField getWidthField() {
        return widthField;
    }

    /**
     * @return the height field.
     */
    private IntegerTextField getHeightField() {
        return heightField;
    }

    /**
     * @return the color picker.
     */
    private ColorPicker getColorPicker() {
        return colorPicker;
    }

    @Override
    protected void processCreate() {
        super.processCreate();

        final Path fileToCreate = getFileToCreate();
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
