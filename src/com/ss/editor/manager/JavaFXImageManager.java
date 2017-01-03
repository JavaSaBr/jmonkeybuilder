package com.ss.editor.manager;

import static java.awt.Image.SCALE_DEFAULT;

import com.ss.editor.FileExtensions;
import com.ss.editor.config.Config;
import com.ss.editor.file.reader.TGAReader;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiReader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;

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
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

/**
 * The manager for creating preview for image files.
 *
 * @author JavaSaBr
 */
public class JavaFXImageManager {

    private static final Logger LOGGER = LoggerManager.getLogger(JavaFXImageManager.class);

    public static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

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
     * The metadatas cache.
     */
    private final ObjectDictionary<Path, IIOMetadata> iioMetadatas;

    /**
     * The cache folder.
     */
    private final Path cacheFolder;

    public JavaFXImageManager() {
        InitializeManager.valid(getClass());
        final Path appFolder = Config.getAppFolderInUserHome();
        this.cacheFolder = appFolder.resolve(PREVIEW_CACHE_FOLDER);
        this.iioMetadatas = DictionaryFactory.newObjectDictionary();
        if (Files.exists(cacheFolder)) FileUtils.delete(cacheFolder);
        FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE, event -> processEvent((DeletedFileEvent) event));
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

            final String url = file.toUri().toString();

            Image image = new Image(url);

            final int imageWidth = (int) image.getWidth();
            final int imageHeight = (int) image.getHeight();

            if (imageWidth > width || imageHeight > height) {
                if (imageWidth == imageHeight) {
                    image = new Image(url, width, height, false, false);
                } else if (imageWidth > imageHeight) {
                    float mod = imageHeight * 1F / imageWidth;
                    image = new Image(url, width, height * mod, false, false);
                } else if (imageHeight > imageWidth) {
                    float mod = imageWidth * 1F / imageHeight;
                    image = new Image(url, width * mod, height, false, false);
                }
            }

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
                final BufferedImage awtImage = (BufferedImage) TGAReader.getImage(content);
                if (awtImage == null) return Icons.IMAGE_512;

                final int imageWidth = awtImage.getWidth();
                final int imageHeight = awtImage.getHeight();

                java.awt.Image newImage = awtImage;

                if (imageWidth > width || imageHeight > height) {
                    if (imageWidth == imageHeight) {
                        newImage = awtImage.getScaledInstance(width, height, SCALE_DEFAULT);
                    } else if (imageWidth > imageHeight) {
                        float mod = imageHeight * 1F / imageWidth;
                        newImage = awtImage.getScaledInstance(width, (int) (height * mod), SCALE_DEFAULT);
                    } else if (imageHeight > imageWidth) {
                        float mod = imageWidth * 1F / imageHeight;
                        newImage = awtImage.getScaledInstance((int) (width * mod), height, SCALE_DEFAULT);
                    }
                }

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

    private void processEvent(final DeletedFileEvent event) {

    }

    @Nullable
    public synchronized IIOMetadata getMetadata(@NotNull final Path file) {

        if (iioMetadatas.containsKey(file)) {
            return iioMetadatas.get(file);
        }

        try (final ImageInputStream iis = ImageIO.createImageInputStream(file.toFile())) {

            final Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);

            if (readers.hasNext()) {

                final ImageReader reader = readers.next();
                reader.setInput(iis, true);

                final IIOMetadata imageMetadata = reader.getImageMetadata(0);
                iioMetadatas.put(file, imageMetadata);
                return imageMetadata;
            }

        } catch (final Exception e) {
            iioMetadatas.put(file, null);
        }

        return null;
    }
}
