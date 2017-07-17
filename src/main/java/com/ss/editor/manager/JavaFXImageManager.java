package com.ss.editor.manager;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.awt.Image.SCALE_DEFAULT;
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
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import jme3tools.converters.ImageToAwt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.util.Iterator;

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
    private static final Array<String> FX_FORMATS = ArrayFactory.newArray(String.class);

    @NotNull
    private static final Array<String> JME_FORMATS = ArrayFactory.newArray(String.class);

    @NotNull
    private static final Array<String> IMAGE_IO_FORMATS = ArrayFactory.newArray(String.class);

    @NotNull
    private static final Array<String> IMAGE_FORMATS = ArrayFactory.newArray(String.class);

    static {
        FX_FORMATS.add(FileExtensions.IMAGE_PNG);
        FX_FORMATS.add(FileExtensions.IMAGE_JPG);
        FX_FORMATS.add(FileExtensions.IMAGE_JPEG);
        FX_FORMATS.add(FileExtensions.IMAGE_GIF);

        JME_FORMATS.add(FileExtensions.IMAGE_BMP);

        IMAGE_IO_FORMATS.add(FileExtensions.IMAGE_HDR);
        IMAGE_IO_FORMATS.add(FileExtensions.IMAGE_TIFF);

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
     * The metadatas cache.
     */
    @NotNull
    private final ObjectDictionary<Path, IIOMetadata> iioMetadatas;

    /**
     * The cache folder.
     */
    @NotNull
    private final Path cacheFolder;

    private JavaFXImageManager() {
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
    public Image getTexturePreview(@Nullable final Path file, final int width, final int height) {
        if (file == null || !Files.exists(file)) return Icons.IMAGE_512;

        final String absolutePath = file.toAbsolutePath().toString();
        final String fileHash = StringUtils.toMD5(absolutePath) + ".png";

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

        } else if (JME_FORMATS.contains(extension)) {

            final Path assetFile = notNull(getAssetFile(file));
            final String assetPath = toAssetPath(assetFile);

            final Editor editor = Editor.getInstance();
            final AssetManager assetManager = editor.getAssetManager();
            final Texture texture = assetManager.loadTexture(assetPath);
            final BufferedImage textureImage;

            try {
                textureImage = ImageToAwt.convert(texture.getImage(), false, true, 0);
            } catch (final UnsupportedOperationException e) {
                EditorUtil.handleException(LOGGER, this, e);
                return Icons.IMAGE_512;
            }

            final int imageWidth = textureImage.getWidth();
            final int imageHeight = textureImage.getHeight();

            final java.awt.Image newImage = scaleImage(width, height, textureImage, imageWidth, imageHeight);
            final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            final Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(newImage, 0, 0, null);
            g2d.dispose();

            Image javaFXImage;

            try (final OutputStream out = Files.newOutputStream(cacheFile)) {
                ImageIO.write(bufferedImage, "png", out);
                javaFXImage = new Image(cacheFile.toUri().toString());
            } catch (final IOException e) {
                LOGGER.warning(e);
                javaFXImage = Icons.IMAGE_512;
            }

            return javaFXImage;

        } else if (IMAGE_IO_FORMATS.contains(extension)) {

            final BufferedImage read;
            try {
                read = ImageIO.read(file.toFile());
            } catch (final IOException e) {
                EditorUtil.handleException(LOGGER, this, e);
                return Icons.IMAGE_512;
            }

            final int imageWidth = read.getWidth();
            final int imageHeight = read.getHeight();

            final java.awt.Image newImage = scaleImage(width, height, read, imageWidth, imageHeight);
            final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            final Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(newImage, 0, 0, null);
            g2d.dispose();

            Image javaFXImage;

            try (final OutputStream out = Files.newOutputStream(cacheFile)) {
                ImageIO.write(bufferedImage, "png", out);
                javaFXImage = new Image(cacheFile.toUri().toString());
            } catch (final IOException e) {
                LOGGER.warning(e);
                javaFXImage = Icons.IMAGE_512;
            }

            return javaFXImage;

        } else if (FileExtensions.IMAGE_DDS.equals(extension)) {

            final byte[] content = notNull(Utils.get(file, Files::readAllBytes));
            final int[] pixels = DDSReader.read(content, DDSReader.ARGB, 0);
            final int currentWidth = DDSReader.getWidth(content);
            final int currentHeight = DDSReader.getHeight(content);

            final BufferedImage read = new BufferedImage(currentWidth, currentHeight, BufferedImage.TYPE_INT_ARGB);
            read.setRGB(0, 0, currentWidth, currentHeight, pixels, 0, currentWidth);

            final java.awt.Image newImage = scaleImage(width, height, read, currentWidth, currentHeight);
            final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            final Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(newImage, 0, 0, null);
            g2d.dispose();

            Image javaFXImage;

            try (final OutputStream out = Files.newOutputStream(cacheFile)) {
                ImageIO.write(bufferedImage, "png", out);
                javaFXImage = new Image(cacheFile.toUri().toString());
            } catch (final IOException e) {
                LOGGER.warning(e);
                javaFXImage = Icons.IMAGE_512;
            }

            return javaFXImage;

        } else if (FileExtensions.IMAGE_TGA.equals(extension)) {

            final byte[] content = notNull(Utils.get(file, Files::readAllBytes));

            final BufferedImage awtImage = (BufferedImage) TGAReader.getImage(content);
            if (awtImage == null) return Icons.IMAGE_512;

            final int imageWidth = awtImage.getWidth();
            final int imageHeight = awtImage.getHeight();

            final java.awt.Image newImage = scaleImage(width, height, awtImage, imageWidth, imageHeight);
            final BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            final Graphics2D g2d = bufferedImage.createGraphics();
            g2d.drawImage(newImage, 0, 0, null);
            g2d.dispose();

            Image javaFXImage;

            try (final OutputStream out = Files.newOutputStream(cacheFile)) {
                ImageIO.write(bufferedImage, "png", out);
                javaFXImage = new Image(cacheFile.toUri().toString());
            } catch (final IOException e) {
                LOGGER.warning(e);
                javaFXImage = Icons.IMAGE_512;
            }

            return javaFXImage;
        }

        return Icons.IMAGE_512;
    }

    @NotNull
    private java.awt.Image scaleImage(final int width, final int height, @NotNull final BufferedImage read,
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

        return newImage;
    }

    private void processEvent(@NotNull final DeletedFileEvent event) {
        //TODO need to add remove from cache
    }

    /**
     * Gets metadata.
     *
     * @param file the file
     * @return the metadata
     */
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
