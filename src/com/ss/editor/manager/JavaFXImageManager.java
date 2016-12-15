package com.ss.editor.manager;

import com.ss.editor.FileExtensions;
import com.ss.editor.config.Config;
import com.ss.editor.file.reader.TGAReader;
import com.ss.editor.ui.Icons;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiReader;

import org.jetbrains.annotations.Nullable;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.manager.InitializeManager;
import rlib.util.FileUtils;
import rlib.util.StringUtils;
import rlib.util.Util;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The manager for creating preview for image files.
 *
 * @author JavaSaBr
 */
public class JavaFXImageManager {

    private static final Logger LOGGER = LoggerManager.getLogger(JavaFXImageManager.class);

    private static final String PREVIEW_CACHE_FOLDER = "preview-cache";

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
     * @return true if the file is image.
     */
    public static boolean isImage(@Nullable final Path file) {
        if (file == null) return false;
        final String extension = FileUtils.getExtension(file);
        return IMAGE_FORMATS.contains(extension);
    }

    private static JavaFXImageManager instance;

    public static JavaFXImageManager getInstance() {
        if (instance == null) instance = new JavaFXImageManager();
        return instance;
    }

    /**
     * The cache folder.
     */
    private final Path cacheFolder;

    public JavaFXImageManager() {
        InitializeManager.valid(getClass());
        final Path appFolder = Config.getAppFolderInUserHome();
        this.cacheFolder = appFolder.resolve(PREVIEW_CACHE_FOLDER);
    }

    /**
     * @return the cache folder.
     */
    private Path getCacheFolder() {
        return cacheFolder;
    }

    /**
     * Get image preview.
     *
     * @param file   the image file.
     * @param width  the required width.
     * @param height the required height.
     * @return the image.
     */
    public Image getTexturePreview(@Nullable final Path file, final int width, final int height) {
        if (file == null || !Files.exists(file)) return Icons.IMAGE_512;

        final String absolutePath = file.toAbsolutePath().toString();
        final String fileHash = StringUtils.passwordToHash(absolutePath) + ".png";

        final Path cacheFolder = getCacheFolder();
        final Path imageFolder = cacheFolder.resolve(String.valueOf(width)).resolve(String.valueOf(height));
        final Path cacheFile = imageFolder.resolve(fileHash);

        if (Files.exists(cacheFile)) {

            try {

                final FileTime lastModCacheFile = Files.getLastModifiedTime(cacheFile);
                final FileTime lastModFile = Files.getLastModifiedTime(file);

                if (lastModCacheFile.compareTo(lastModFile) >= 0) {
                    return new Image(cacheFile.toUri().toString(), width, height, false, false);
                }

            } catch (final IOException e) {
                LOGGER.warning(e);
            }
        }

        final Path parent = cacheFile.getParent();

        try {
            Files.createDirectories(parent);
        } catch (final IOException e) {
            LOGGER.warning(e);
        }

        final String extension = FileUtils.getExtension(file);

        if (FX_FORMATS.contains(extension)) {

            final Image image = new Image(file.toUri().toString(), width, height, false, false);
            final BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

            try (final OutputStream out = Files.newOutputStream(cacheFile)) {
                ImageIO.write(bufferedImage, "png", out);
            } catch (final IOException e) {
                LOGGER.warning(e);
            }

            return image;

        } else if (FileExtensions.IMAGE_TGA.equals(extension)) {

            try {

                final byte[] content = Util.safeGet(file, Files::readAllBytes);
                final java.awt.Image awtImage = TGAReader.getImage(content);
                if (awtImage == null) return Icons.IMAGE_512;

                final java.awt.Image newImage = awtImage.getScaledInstance(width, height, java.awt.Image.SCALE_FAST);
                final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

                final Graphics2D g2d = bufferedImage.createGraphics();
                g2d.drawImage(newImage, 0, 0, null);
                g2d.dispose();

                Image javaFXImage;

                try (final OutputStream out = Files.newOutputStream(cacheFile)) {
                    ImageIO.write(bufferedImage, "png", out);
                    javaFXImage = new Image(cacheFile.toUri().toString());
                }

                return javaFXImage;

            } catch (final IOException e) {
                LOGGER.warning("can't read " + file);
            }

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

            Image javaFXImage;

            try (final OutputStream out = Files.newOutputStream(cacheFile)) {
                ImageIO.write(bufferedImage, "png", out);
                javaFXImage = new Image(cacheFile.toUri().toString());
            }

            return javaFXImage;

        } catch (final Exception e) {
            LOGGER.warning("can't read " + file);
        }

        return Icons.IMAGE_512;
    }
}
