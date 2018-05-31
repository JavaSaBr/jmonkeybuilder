package com.ss.editor.manager;

import static com.ss.rlib.common.util.FileUtils.getExtension;
import static java.awt.Image.SCALE_DEFAULT;
import static java.nio.file.StandardOpenOption.*;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.config.Config;
import com.ss.editor.file.reader.DdsReader;
import com.ss.editor.file.reader.TgaReader;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.event.FxEventManager;
import com.ss.editor.ui.event.impl.ChangedCurrentAssetFolderEvent;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.TimeTracker;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.StringUtils;
import com.ss.rlib.common.util.Utils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.IntegerDictionary;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import jme3tools.converters.ImageToAwt;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * The class to manage previews of images to JavaFX
 *
 * @author JavaSaBr
 */
public class JavaFxImageManager {

    private static final Logger LOGGER = LoggerManager.getLogger(JavaFxImageManager.class);

    private static final FxEventManager FX_EVENT_MANAGER = FxEventManager.getInstance();
    private static final InitializationManager INITIALIZATION_MANAGER = InitializationManager.getInstance();

    private static final String PREVIEW_CACHE_FOLDER = "preview-cache";

    private static final Array<String> FX_FORMATS = Array.of(
            FileExtensions.IMAGE_PNG,
            FileExtensions.IMAGE_JPG,
            FileExtensions.IMAGE_JPEG,
            FileExtensions.IMAGE_GIF
    );

    private static final Array<String> JME_FORMATS = Array.of(FileExtensions.IMAGE_BMP);

    private static final Array<String> IMAGE_IO_FORMATS = Array.of(
            FileExtensions.IMAGE_HDR,
            FileExtensions.IMAGE_TIFF);

    private static final Array<String> IMAGE_FORMATS = ArrayFactory.newArray(String.class);

    /**
     * The size of cached images.
     */
    private static final int CACHED_IMAGES_SIZE = 30;

    static {
        IMAGE_FORMATS.addAll(FX_FORMATS);
        IMAGE_FORMATS.addAll(JME_FORMATS);
        IMAGE_FORMATS.addAll(IMAGE_IO_FORMATS);
        IMAGE_FORMATS.add(FileExtensions.IMAGE_TGA);
        IMAGE_FORMATS.add(FileExtensions.IMAGE_DDS);
    }

    /**
     * Check the file.
     *
     * @param file the file
     * @return true if the file is image.
     */
    @FromAnyThread
    public static boolean isImage(@Nullable Path file) {
        return file != null && IMAGE_FORMATS.contains(getExtension(file));
    }

    /**
     * Check the file by the asset path.
     *
     * @param assetPath the asset path
     * @return true if the file is image.
     */
    @FromAnyThread
    public static boolean isImage(@Nullable String assetPath) {
        return assetPath != null && IMAGE_FORMATS.contains(getExtension(assetPath));
    }

    @Nullable
    private static JavaFxImageManager instance;

    @FromAnyThread
    public static @NotNull JavaFxImageManager getInstance() {
        if (instance == null) instance = new JavaFxImageManager();
        return instance;
    }

    /**
     * The cache of small images.
     */
    @NotNull
    private IntegerDictionary<IntegerDictionary<ObjectDictionary<String, Image>>> smallImageCache;

    /**
     * The cache folder.
     */
    @NotNull
    private final Path cacheFolder;

    private JavaFxImageManager() {
        InitializeManager.valid(getClass());

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_5)
                .start();

        var appFolder = Config.getAppFolderInUserHome();

        this.cacheFolder = appFolder.resolve(PREVIEW_CACHE_FOLDER);
        this.smallImageCache = DictionaryFactory.newIntegerDictionary();

        INITIALIZATION_MANAGER.addOnFinishLoading(() -> {
            FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE, this::processEvent);
            FX_EVENT_MANAGER.addEventHandler(ChangedCurrentAssetFolderEvent.EVENT_TYPE, this::processEvent);
        });

        TimeTracker.getStartupTracker(TimeTracker.STARTPUL_LEVEL_5)
                .finish(() -> "Initialized JavaFxImageManager");

        LOGGER.info("initialized.");
    }

    /**
     * Get the cache folder.
     *
     * @return the cache folder.
     */
    @FromAnyThread
    private @NotNull Path getCacheFolder() {
        return cacheFolder;
    }

    /**
     * Get an image preview.
     *
     * @param file   the image file.
     * @param width  the required width.
     * @param height the required height.
     * @return the image.
     */
    @FxThread
    public @NotNull Image getImagePreview(@Nullable Path file, int width, int height) {

        if (file == null || !Files.exists(file)) {
            return Icons.IMAGE_512;
        }

        if (width <= CACHED_IMAGES_SIZE && height <= CACHED_IMAGES_SIZE) {
            var image = getFromCache(file.toString(), width, height);
            if (image != null) {
                return image;
            }
        }

        var url = FileUtils.getUrl(file);
        var lastModFile = FileUtils.getLastModifiedTime(file);

        var image = getImagePreview(url, lastModFile, width, height);

        if (width <= CACHED_IMAGES_SIZE && height <= CACHED_IMAGES_SIZE) {
            putImageToCache(file.toString(), image, width, height);
        }

        return image;
    }

    /**
     * Try to get an image from the cache by path and size.
     *
     * @param path   the path to the image.
     * @param width  the width.
     * @param height the height.
     * @return the image or null.
     */
    @FxThread
    private @Nullable Image getFromCache(@NotNull String path, int width, int height) {

        var heightImages = smallImageCache.get(width);
        if (heightImages == null) {
            return null;
        }

        var images = heightImages.get(height);
        if (images == null) {
            return null;
        }

        return images.get(path);
    }

    /**
     * Put the image to the cache.
     *
     * @param path   the ath to the image.
     * @param image  the image.
     * @param width  the width.
     * @param height the height.
     */
    @FxThread
    private void putImageToCache(@NotNull String path, @NotNull Image image, int width, int height) {
        smallImageCache.get(width, DictionaryFactory::newIntegerDictionary)
                .get(height, DictionaryFactory::newObjectDictionary)
                .put(path, image);
    }

    /**
     * Get an image preview.
     *
     * @param resourcePath the resource path to an image.
     * @param width        the required width.
     * @param height       the required height.
     * @return the image.
     */
    @FxThread
    public @NotNull Image getImagePreview(@Nullable String resourcePath, int width, int height) {

        if (resourcePath == null) {
            throw new IllegalArgumentException("The resource path can't be null.");
        }

        if (width <= CACHED_IMAGES_SIZE && height <= CACHED_IMAGES_SIZE) {
            var image = getFromCache(resourcePath, width, height);
            if (image != null) {
                return image;
            }
        }

        var resourceManager = ResourceManager.getInstance();
        var url = resourceManager.tryToFindResource(resourcePath);

        if (url == null) {

            var realFile = EditorUtil.getRealFile(resourcePath);
            if (realFile == null || !Files.exists(realFile)) {
                return Icons.IMAGE_512;
            }

            url = FileUtils.getUrl(realFile);
        }

        var image = getImagePreview(url, null, width, height);

        if (width <= CACHED_IMAGES_SIZE && height <= CACHED_IMAGES_SIZE) {
            putImageToCache(resourcePath, image, width, height);
        }

        return image;
    }

    @FxThread
    private @NotNull Image getImagePreview(@NotNull URL url, @Nullable FileTime lastModFile, int width, int height) {

        var externalForm = url.toExternalForm();
        var cachedImage = getCachedImage(width, height, externalForm);

        if (Files.exists(cachedImage)) {

            var lastModCacheFile = FileUtils.getLastModifiedTime(cachedImage);

            if (lastModFile == null || lastModCacheFile.compareTo(lastModFile) >= 0) {

                var resultPath = FileUtils.getUrl(cachedImage)
                        .toExternalForm();

                return new Image(resultPath, width, height, false, false);
            }
        }

        FileUtils.createDirectories(cachedImage.getParent());

        var extension = getExtension(externalForm);

        if (FX_FORMATS.contains(extension)) {
            return readFxImage(width, height, externalForm, cachedImage);
        } else if (JME_FORMATS.contains(extension)) {
            return readJmeTexture(width, height, externalForm, cachedImage);
        } else if (IMAGE_IO_FORMATS.contains(extension)) {
            return readIOImage(url, width, height, cachedImage);
        } else if (FileExtensions.IMAGE_DDS.equals(extension)) {

            var content = Utils.get(url, first -> IOUtils.toByteArray(first.openStream()));
            var pixels = DdsReader.read(content, DdsReader.ARGB, 0);
            var currentWidth = DdsReader.getWidth(content);
            var currentHeight = DdsReader.getHeight(content);

            var read = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_ARGB);
            read.setRGB(0, 0, currentWidth, currentHeight, pixels, 0, currentWidth);

            return scaleAndWrite(width, height, cachedImage, read, currentWidth, currentHeight);

        } else if (FileExtensions.IMAGE_TGA.equals(extension)) {

            var content = Utils.get(url, toRead -> IOUtils.toByteArray(toRead.openStream()));

            BufferedImage awtImage;
            try {
                awtImage = (BufferedImage) TgaReader.getImage(content);
            } catch (Exception e) {
                LOGGER.warning(e);
                writeDefaultToCache(cachedImage);
                return Icons.IMAGE_512;
            }

            var imageWidth = awtImage.getWidth();
            var imageHeight = awtImage.getHeight();

            return scaleAndWrite(width, height, cachedImage, awtImage, imageWidth, imageHeight);
        }

        return Icons.IMAGE_512;
    }

    /**
     * Get a path to a cached image.
     *
     * @param width        the image width.
     * @param height       the image height.
     * @param externalForm the external form of URL.
     * @return the path to a cached image.
     */
    @FromAnyThread
    private @NotNull Path getCachedImage(int width, int height, String externalForm) {

        var fileHash = StringUtils.toMD5(externalForm) + ".png";

        var cacheFolder = getCacheFolder();
        var imageFolder = cacheFolder.resolve(String.valueOf(width))
                .resolve(String.valueOf(height));

        return imageFolder.resolve(fileHash);
    }

    @FxThread
    private void writeDefaultToCache(@NotNull Path cacheFile) {
        var bufferedImage = SwingFXUtils.fromFXImage(Icons.IMAGE_512, null);
        try (var out = Files.newOutputStream(cacheFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            ImageIO.write(bufferedImage, "png", out);
        } catch (IOException ex) {
            LOGGER.warning(ex);
        }
    }

    @FxThread
    private @NotNull Image readIOImage(@NotNull URL url, int width, int height, @NotNull Path cacheFile) {

        BufferedImage read;
        try {
            read = ImageIO.read(url);
        } catch (IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return Icons.IMAGE_512;
        }

        return scaleAndWrite(width, height, cacheFile, read, read.getWidth(), read.getHeight());
    }

    @FxThread
    private @NotNull Image readJmeTexture(
            int width,
            int height,
            @NotNull String externalForm,
            @NotNull Path cacheFile
    ) {

        var assetManager = EditorUtil.getAssetManager();
        var texture = assetManager.loadTexture(externalForm);

        BufferedImage textureImage;
        try {
            textureImage = ImageToAwt.convert(texture.getImage(), false, true, 0);
        } catch (UnsupportedOperationException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return Icons.IMAGE_512;
        }

        int imageWidth = textureImage.getWidth();
        int imageHeight = textureImage.getHeight();

        return scaleAndWrite(width, height, cacheFile, textureImage, imageWidth, imageHeight);
    }

    @FxThread
    private @NotNull Image readFxImage(int width, int height, @NotNull String externalForm, @NotNull Path cacheFile) {

        var image = new Image(externalForm);

        var imageWidth = (int) image.getWidth();
        var imageHeight = (int) image.getHeight();

        if (imageWidth > width || imageHeight > height) {
            if (imageWidth == imageHeight) {
                image = new Image(externalForm, width, height, false, false);
            } else if (imageWidth > imageHeight) {
                float mod = imageHeight * 1F / imageWidth;
                image = new Image(externalForm, width, height * mod, false, false);
            } else {
                float mod = imageWidth * 1F / imageHeight;
                image = new Image(externalForm, width * mod, height, false, false);
            }
        }

        var bufferedImage = SwingFXUtils.fromFXImage(image, null);

        try (var out = Files.newOutputStream(cacheFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            ImageIO.write(bufferedImage, "png", out);
        } catch (IOException e) {
            LOGGER.warning(e);
        }

        return image;
    }

    @FxThread
    private @NotNull Image scaleAndWrite(
            int targetWidth,
            int targetHeight,
            @NotNull Path cacheFile,
            @NotNull BufferedImage textureImage,
            int currentWidth,
            int currentHeight
    ) {

        var newImage = scaleImage(targetWidth, targetHeight, textureImage, currentWidth, currentHeight);

        try (var out = Files.newOutputStream(cacheFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            ImageIO.write(newImage, "png", out);
            return new Image(cacheFile.toUri().toString());
        } catch (IOException e) {
            LOGGER.warning(e);
            return Icons.IMAGE_512;
        }
    }

    @FxThread
    private @NotNull BufferedImage scaleImage(
            int width,
            int height,
            @NotNull BufferedImage read,
            int imageWidth,
            int imageHeight
    ) {

        java.awt.Image newImage = read;

        if (imageWidth > width || imageHeight > height) {
            if (imageWidth == imageHeight) {
                newImage = read.getScaledInstance(width, height, SCALE_DEFAULT);
            } else if (imageWidth > imageHeight) {
                float mod = imageHeight * 1F / imageWidth;
                newImage = read.getScaledInstance(width, (int) (height * mod), SCALE_DEFAULT);
            } else if (imageHeight > imageWidth) {
                float mod = imageWidth * 1F / imageHeight;
                newImage = read.getScaledInstance((int) (width * mod), height, SCALE_DEFAULT);
            }
        }

        var bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        var g2d = bufferedImage.createGraphics();
        g2d.drawImage(newImage, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    @FxThread
    private void processEvent(@NotNull DeletedFileEvent event) {
        //TODO need to add remove from cache
    }

    @FxThread
    private void processEvent(@NotNull ChangedCurrentAssetFolderEvent event) {
        smallImageCache.clear();
    }
}
