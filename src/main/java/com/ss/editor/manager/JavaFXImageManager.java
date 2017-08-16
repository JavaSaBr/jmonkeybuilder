package com.ss.editor.manager;

import static com.ss.rlib.util.array.ArrayFactory.asArray;
import static java.awt.Image.SCALE_DEFAULT;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import com.jme3.asset.AssetManager;
import com.jme3.texture.Texture;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.config.Config;
import com.ss.editor.file.reader.DDSReader;
import com.ss.editor.file.reader.TGAReader;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.DeletedFileEvent;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.manager.InitializeManager;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.Utils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import jme3tools.converters.ImageToAwt;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;

/**
 * The class to manage previews of images to JavaFX
 *
 * @author JavaSaBr
 */
public class JavaFXImageManager {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(JavaFXImageManager.class);

    @NotNull
    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();

    @NotNull
    private static final String PREVIEW_CACHE_FOLDER = "preview-cache";

    @NotNull
    private static final Array<String> FX_FORMATS = asArray(
            FileExtensions.IMAGE_PNG,
            FileExtensions.IMAGE_JPG,
            FileExtensions.IMAGE_JPEG,
            FileExtensions.IMAGE_GIF);

    @NotNull
    private static final Array<String> JME_FORMATS = asArray(FileExtensions.IMAGE_BMP);

    @NotNull
    private static final Array<String> IMAGE_IO_FORMATS = asArray(
            FileExtensions.IMAGE_HDR,
            FileExtensions.IMAGE_TIFF);

    @NotNull
    private static final Array<String> IMAGE_FORMATS = ArrayFactory.newArray(String.class);

    static {
        IMAGE_FORMATS.addAll(FX_FORMATS);
        IMAGE_FORMATS.addAll(JME_FORMATS);
        IMAGE_FORMATS.addAll(IMAGE_IO_FORMATS);
        IMAGE_FORMATS.add(FileExtensions.IMAGE_TGA);
        IMAGE_FORMATS.add(FileExtensions.IMAGE_DDS);
    }

    /**
     * Is image boolean.
     *
     * @param file the file
     * @return true if the file is image.
     */
    public static boolean isImage(@Nullable final Path file) {
        if (file == null) return false;
        final String extension = FileUtils.getExtension(file);
        return IMAGE_FORMATS.contains(extension);
    }

    @Nullable
    private static JavaFXImageManager instance;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static JavaFXImageManager getInstance() {
        if (instance == null) instance = new JavaFXImageManager();
        return instance;
    }

    /**
     * The cache folder.
     */
    @NotNull
    private final Path cacheFolder;

    private JavaFXImageManager() {
        InitializeManager.valid(getClass());

        final Path appFolder = Config.getAppFolderInUserHome();

        this.cacheFolder = appFolder.resolve(PREVIEW_CACHE_FOLDER);

        if (Files.exists(cacheFolder)) {
            FileUtils.delete(cacheFolder);
        }

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addFXTask(() -> FX_EVENT_MANAGER.addEventHandler(DeletedFileEvent.EVENT_TYPE,
                event -> processEvent((DeletedFileEvent) event)));
    }

    /**
     * @return the cache folder.
     */
    @NotNull
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
    @NotNull
    @FXThread
    public Image getImagePreview(@Nullable final Path file, final int width, final int height) {
        if (file == null || !Files.exists(file)) return Icons.IMAGE_512;

        final FileTime lastModFile = Utils.get(file, first -> Files.getLastModifiedTime(first));
        final URL url = Utils.get(file, first -> first.toUri().toURL());

        return getImagePreview(url, lastModFile, width, height);
    }

    /**
     * Get image preview.
     *
     * @param resourcePath the resource path to an image.
     * @param width        the required width.
     * @param height       the required height.
     * @return the image.
     */
    @NotNull
    @FXThread
    public Image getImagePreview(@Nullable final String resourcePath, final int width, final int height) {

        URL url = getClass().getResource(resourcePath);

        if (url == null) {
            url = getClass().getResource("/" + resourcePath);
        }

        if (url == null) {
            return Icons.IMAGE_512;
        }

        return getImagePreview(url, null, width, height);
    }

    @NotNull
    private Image getImagePreview(@NotNull URL url, @Nullable final FileTime lastModFile,
                                  final int width, final int height) {

        final String externalForm = url.toExternalForm();
        final String fileHash = StringUtils.toMD5(externalForm) + ".png";

        final Path cacheFolder = getCacheFolder();
        final Path imageFolder = cacheFolder.resolve(String.valueOf(width)).resolve(String.valueOf(height));
        final Path cacheFile = imageFolder.resolve(fileHash);

        if (Files.exists(cacheFile)) {

            final FileTime lastModCacheFile = Utils.get(cacheFile, file -> Files.getLastModifiedTime(file));

            if (lastModFile == null || lastModCacheFile.compareTo(lastModFile) >= 0) {
                final String pathToCache = Utils.get(cacheFile, first -> first.toUri().toURL().toExternalForm());
                return new Image(pathToCache, width, height, false, false);
            }
        }

        Utils.run(cacheFile, first -> Files.createDirectories(first.getParent()));

        final String extension = FileUtils.getExtension(externalForm);

        if (FX_FORMATS.contains(extension)) {
            return readFXImage(width, height, externalForm, cacheFile);
        } else if (JME_FORMATS.contains(extension)) {
            return readJMETexture(width, height, externalForm, cacheFile);
        } else if (IMAGE_IO_FORMATS.contains(extension)) {
            return readIOImage(url, width, height, cacheFile);
        } else if (FileExtensions.IMAGE_DDS.equals(extension)) {

            final byte[] content = Utils.get(url, first -> IOUtils.toByteArray(first.openStream()));
            final int[] pixels = DDSReader.read(content, DDSReader.ARGB, 0);
            final int currentWidth = DDSReader.getWidth(content);
            final int currentHeight = DDSReader.getHeight(content);

            final BufferedImage read = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_ARGB);
            read.setRGB(0, 0, currentWidth, currentHeight, pixels, 0, currentWidth);

            return scaleAndWrite(width, height, cacheFile, read, currentWidth, currentHeight);

        } else if (FileExtensions.IMAGE_TGA.equals(extension)) {

            final byte[] content = Utils.get(url, first -> IOUtils.toByteArray(first.openStream()));
            final BufferedImage awtImage;
            try {
                awtImage = (BufferedImage) TGAReader.getImage(content);
            } catch (final Exception e) {
                LOGGER.warning(e);
                writeDefaultToCache(cacheFile);
                return Icons.IMAGE_512;
            }

            if (awtImage == null) {
                return Icons.IMAGE_512;
            }

            final int imageWidth = awtImage.getWidth();
            final int imageHeight = awtImage.getHeight();

            return scaleAndWrite(width, height, cacheFile, awtImage, imageWidth, imageHeight);
        }

        return Icons.IMAGE_512;
    }

    private void writeDefaultToCache(@NotNull final Path cacheFile) {
        final BufferedImage bufferedImage = SwingFXUtils.fromFXImage(Icons.IMAGE_512, null);
        try (final OutputStream out = Files.newOutputStream(cacheFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            ImageIO.write(bufferedImage, "png", out);
        } catch (final IOException ex) {
            LOGGER.warning(ex);
        }
    }

    @NotNull
    private Image readIOImage(@NotNull final URL url, final int width, final int height, @NotNull final Path cacheFile) {

        final BufferedImage read;
        try {
            read = ImageIO.read(url);
        } catch (final IOException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return Icons.IMAGE_512;
        }

        return scaleAndWrite(width, height, cacheFile, read, read.getWidth(), read.getHeight());
    }

    @NotNull
    private Image readJMETexture(final int width, final int height, @NotNull final String externalForm,
                                 @NotNull final Path cacheFile) {

        final Editor editor = Editor.getInstance();
        final AssetManager assetManager = editor.getAssetManager();
        final Texture texture = assetManager.loadTexture(externalForm);

        final BufferedImage textureImage;
        try {
            textureImage = ImageToAwt.convert(texture.getImage(), false, true, 0);
        } catch (final UnsupportedOperationException e) {
            EditorUtil.handleException(LOGGER, this, e);
            return Icons.IMAGE_512;
        }

        final int imageWidth = textureImage.getWidth();
        final int imageHeight = textureImage.getHeight();

        return scaleAndWrite(width, height, cacheFile, textureImage, imageWidth, imageHeight);
    }

    @NotNull
    private Image readFXImage(final int width, final int height, @NotNull final String externalForm,
                              @NotNull final Path cacheFile) {

        Image image = new Image(externalForm);

        final int imageWidth = (int) image.getWidth();
        final int imageHeight = (int) image.getHeight();

        if (imageWidth > width || imageHeight > height) {
            if (imageWidth == imageHeight) {
                image = new Image(externalForm, width, height, false, false);
            } else if (imageWidth > imageHeight) {
                float mod = imageHeight * 1F / imageWidth;
                image = new Image(externalForm, width, height * mod, false, false);
            } else if (imageHeight > imageWidth) {
                float mod = imageWidth * 1F / imageHeight;
                image = new Image(externalForm, width * mod, height, false, false);
            }
        }

        final BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);

        try (final OutputStream out = Files.newOutputStream(cacheFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            ImageIO.write(bufferedImage, "png", out);
        } catch (final IOException e) {
            LOGGER.warning(e);
        }

        return image;
    }

    @NotNull
    private Image scaleAndWrite(final int targetWidth, final int targetHeight, @NotNull final Path cacheFile,
                                @NotNull final BufferedImage textureImage, final int currentWidth,
                                final int currentHeight) {

        final BufferedImage newImage = scaleImage(targetWidth, targetHeight, textureImage, currentWidth, currentHeight);

        try (final OutputStream out = Files.newOutputStream(cacheFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            ImageIO.write(newImage, "png", out);
            return new Image(cacheFile.toUri().toString());
        } catch (final IOException e) {
            LOGGER.warning(e);
            return Icons.IMAGE_512;
        }
    }

    @NotNull
    private BufferedImage scaleImage(final int width, final int height, @NotNull final BufferedImage read,
                                     final int imageWidth, final int imageHeight) {

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

        final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

        final Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(newImage, 0, 0, null);
        g2d.dispose();

        return bufferedImage;
    }

    private void processEvent(@NotNull final DeletedFileEvent event) {
        //TODO need to add remove from cache
    }
}
