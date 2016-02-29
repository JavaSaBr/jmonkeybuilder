package com.ss.editor.manager;

import com.ss.editor.FileExtensions;
import com.ss.editor.ui.Icons;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiReader;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.imageio.ImageIO;

import javafx.scene.image.Image;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.manager.InitializeManager;
import rlib.util.FileUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация менеджера по работе с изображениями в JavaFX.
 *
 * @author Ronn
 */
public class JavaFXImageManager {

    private static final Logger LOGGER = LoggerManager.getLogger(JavaFXImageManager.class);

    private static final Array<String> FX_FORMATS = ArrayFactory.newArray(String.class);
    private static final Array<String> JIMI_FORMATS = ArrayFactory.newArray(String.class);
    private static final Array<String> IMAGE_FORMATS = ArrayFactory.newArray(String.class);

    static {

        FX_FORMATS.add(FileExtensions.IMAGE_PNG);
        FX_FORMATS.add(FileExtensions.IMAGE_JPG);
        FX_FORMATS.add(FileExtensions.IMAGE_JPEG);
        FX_FORMATS.add(FileExtensions.IMAGE_GIF);

        JIMI_FORMATS.add(FileExtensions.IMAGE_TGA);
        JIMI_FORMATS.add(FileExtensions.IMAGE_BMP);
        JIMI_FORMATS.add(FileExtensions.IMAGE_TIFF);

        IMAGE_FORMATS.addAll(FX_FORMATS);
        IMAGE_FORMATS.addAll(JIMI_FORMATS);
        IMAGE_FORMATS.add(FileExtensions.IMAGE_DDS);
    }

    /**
     * Определение, является ли файл картинкой.
     */
    public static boolean isImage(final Path file) {

        final Path fileName = file.getFileName();
        final String extension = FileUtils.getExtension(fileName.toString());

        return IMAGE_FORMATS.contains(extension);
    }

    private static JavaFXImageManager instance;

    public static JavaFXImageManager getInstance() {

        if (instance == null) {
            instance = new JavaFXImageManager();
        }

        return instance;
    }

    public JavaFXImageManager() {
        InitializeManager.valid(getClass());
    }

    /**
     * Получение упрощенного превью текстуры.
     *
     * @param file   файл с текстурой.
     * @param width  ширина для превью.
     * @param height высота для превью.
     * @return загруженное изображение.
     */
    public Image getTexturePreview(final Path file, final int width, final int height) {

        final String extension = FileUtils.getExtension(file.getFileName().toString());

        if (FX_FORMATS.contains(extension)) {
            return new Image(file.toUri().toString(), width, height, false, false);
        } else if (!JIMI_FORMATS.contains(extension)) {
            return Icons.IMAGE_512;
        }

        try {

            final JimiReader reader = Jimi.createJimiReader(file.toString());

            final java.awt.Image awtImage = reader.getImage();

            if (awtImage == null) {
                return Icons.IMAGE_512;
            }

            final java.awt.Image newImage = awtImage.getScaledInstance(width, height, java.awt.Image.SCALE_FAST);
            final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            final Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(newImage, 0, 0, null);
            g2d.dispose();

            final Path tempFile = Files.createTempFile("javaFX", String.valueOf(System.nanoTime()));

            Image javaFXImage = null;

            try (final OutputStream out = Files.newOutputStream(tempFile)) {
                ImageIO.write(bufferedImage, "png", out);
                javaFXImage = new Image(tempFile.toUri().toString());
            }

            Files.delete(tempFile);

            return javaFXImage;

        } catch (final Exception e) {
            LOGGER.warning("can't read " + file);
        }

        return Icons.IMAGE_512;
    }
}
