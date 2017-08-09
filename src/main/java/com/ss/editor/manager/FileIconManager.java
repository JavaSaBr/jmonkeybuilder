package com.ss.editor.manager;

import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.ui.css.CssColorTheme;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.manager.InitializeManager;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import com.ss.rlib.util.dictionary.DictionaryFactory;
import com.ss.rlib.util.dictionary.IntegerDictionary;
import com.ss.rlib.util.dictionary.ObjectDictionary;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.BiFunction;

/**
 * The class to manage file icons.
 *
 * @author JavaSaBr
 */
public class FileIconManager {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(FileIconManager.class);

    /**
     * The constant DEFAULT_FILE_ICON_SIZE.
     */
    public static final int DEFAULT_FILE_ICON_SIZE = 16;

    @NotNull
    private static final ObjectDictionary<String, String> EXTENSION_TO_CONTENT_TYPE = DictionaryFactory.newObjectDictionary();

    static {
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_JPEG, "image-jpeg");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_JPG, "image-jpeg");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_TIFF, "image-tiff");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_GIF, "image-gif");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_BMP, "image-bmp");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_PNG, "image-png");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_TGA, "image-x-tga");
        EXTENSION_TO_CONTENT_TYPE.put("psd", "image-x-psd");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_DDS, "image");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_HDR, "image");

        EXTENSION_TO_CONTENT_TYPE.put("ogg", "audio-x-generic");
        EXTENSION_TO_CONTENT_TYPE.put("wav", "audio-x-generic");
        EXTENSION_TO_CONTENT_TYPE.put("mp3", "audio-x-generic");

        EXTENSION_TO_CONTENT_TYPE.put("txt", "text");
        EXTENSION_TO_CONTENT_TYPE.put("log", "text");

        EXTENSION_TO_CONTENT_TYPE.put("zip", "application-zip");
        EXTENSION_TO_CONTENT_TYPE.put("rar", "application-rar");
        EXTENSION_TO_CONTENT_TYPE.put("gz", "application-x-gzip");
        EXTENSION_TO_CONTENT_TYPE.put("jar", "application-x-java-archive");

        EXTENSION_TO_CONTENT_TYPE.put("java", "application-x-java");

        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_OBJECT, "jme3");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_SCENE, "sse");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_MATERIAL, "text");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_MATERIAL_DEFINITION, "text");

        EXTENSION_TO_CONTENT_TYPE.put("obj", "x-office-drawing");
        EXTENSION_TO_CONTENT_TYPE.put("blend", "application-x-blender");
        EXTENSION_TO_CONTENT_TYPE.put("fbx", "fbx");
        EXTENSION_TO_CONTENT_TYPE.put("j3odata", "text");
        EXTENSION_TO_CONTENT_TYPE.put("xml", "text-xml");
        EXTENSION_TO_CONTENT_TYPE.put("exe", "exec");
        EXTENSION_TO_CONTENT_TYPE.put("sh", "text-x-script");
        EXTENSION_TO_CONTENT_TYPE.put("ico", "image-x-ico");
        EXTENSION_TO_CONTENT_TYPE.put("ani", "video-x-generic");

        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.GLSL_FRAGMENT, "text-x-csharp");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.GLSL_VERTEX, "text-x-csharp");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.GLSL_GEOM, "text-x-csharp");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.GLSL_LIB, "text-x-csharp");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.GLSL_TESSELLATION_CONTROL, "text-x-csharp");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.GLSL_TESSELLATION_EVALUATION, "text-x-csharp");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.MODEL_XBUF, "image-svg+xml-compressed");
    }

    @NotNull
    private static final Array<Path> MIME_TYPES_FOLDERS = ArrayFactory.newArray(Path.class);

    static {
        MIME_TYPES_FOLDERS.add(Paths.get("/ui/icons/filetypes/emerald/mimetypes"));
        MIME_TYPES_FOLDERS.add(Paths.get("/ui/icons/filetypes/"));
    }

    private static boolean supportedRepaint = true;


    @Nullable
    private static FileIconManager instance;

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static FileIconManager getInstance() {
        if (instance == null) instance = new FileIconManager();
        return instance;
    }

    /**
     * The image cache.
     */
    @NotNull
    private final IntegerDictionary<ObjectDictionary<String, Image>> imageCache;

    /**
     * The cache of original images.
     */
    @NotNull
    private final ObjectDictionary<Image, Image> originalImageCache;

    /**
     * The cache of urs by a file extension.
     */
    @NotNull
    private final ObjectDictionary<String, String> extensionToUrl;

    /**
     * The list of icon finders.
     */
    @NotNull
    private final Array<BiFunction<Path, String, String>> iconFinders;

    private FileIconManager() {
        InitializeManager.valid(getClass());
        this.iconFinders = ArrayFactory.newArray(BiFunction.class);
        this.imageCache = DictionaryFactory.newIntegerDictionary();
        this.extensionToUrl = DictionaryFactory.newObjectDictionary();
        this.originalImageCache = DictionaryFactory.newObjectDictionary();
    }

    /**
     * Register a new icon finder. It's a function which receives a file and
     * its extension and should return an URL to load an image.
     *
     * @param iconFinder the icon finder.
     */
    @FromAnyThread
    public void register(@NotNull final BiFunction<Path, String, String> iconFinder) {
        this.iconFinders.add(iconFinder);
    }

    /**
     * Get an icon to a file.
     *
     * @param path the file.
     * @param size the icon size.
     * @return the icon.
     */
    @NotNull
    @FXThread
    public Image getIcon(@NotNull final Path path, int size) {

        final String extension = FileUtils.getExtension(path);
        final Array<BiFunction<Path, String, String>> iconFinders = getIconFinders();

        if (!iconFinders.isEmpty()) {
            for (final BiFunction<Path, String, String> iconFinder : iconFinders) {

                final String url = iconFinder.apply(path, extension);
                final ClassLoader classLoader = iconFinder.getClass().getClassLoader();

                if (url == null || !EditorUtil.checkExists(url, classLoader)) {
                    continue;
                }

                return buildImage(url, EditorUtil.getInputStream(url, classLoader), size);
            }
        }

        String contentType = EXTENSION_TO_CONTENT_TYPE.get(extension);

        if (contentType == null) {
            try {
                contentType = Files.probeContentType(path);
            } catch (IOException e) {
                LOGGER.warning(e);
            }
        }

        if (Files.isDirectory(path)) contentType = "folder";
        if (contentType != null) contentType = contentType.replace("/", "-");

        if (contentType == null) {
            LOGGER.debug("not found content type for " + path);
            contentType = "none";
        }

        String url = extensionToUrl.get(extension);

        if (url == null) {
            for (final Path mimeTypes : MIME_TYPES_FOLDERS) {

                Path iconPath = mimeTypes.resolve(contentType + ".svg");
                url = toAssetPath(iconPath);

                if (!EditorUtil.checkExists(url)) {
                    contentType = EXTENSION_TO_CONTENT_TYPE.get(extension);
                    iconPath = mimeTypes.resolve(contentType + ".svg");
                    url = toAssetPath(iconPath);
                }

                if (!EditorUtil.checkExists(url)) {
                    contentType = EXTENSION_TO_CONTENT_TYPE.get(extension);
                    iconPath = mimeTypes.resolve(contentType + ".png");
                    url = toAssetPath(iconPath);
                }

                if (EditorUtil.checkExists(url)) {
                    break;
                }
            }

            if (url == null || !EditorUtil.checkExists(url)) {
                LOGGER.warning("not found image for contentType " + contentType + " and path " + path);
                url = "/ui/icons/svg/document.svg";
            }

            extensionToUrl.put(extension, url);
        }

        return getImage(url, size);
    }

    /**
     * Get an image by an URL.
     *
     * @param url the url.
     * @return the image.
     */
    @NotNull
    @FXThread
    public Image getImage(@NotNull final String url) {
        return getImage(url, 16);
    }

    /**
     * Get an image by an URL.
     *
     * @param url  the url.
     * @param size the size.
     * @return the image.
     */
    @NotNull
    @FXThread
    public Image getImage(@NotNull final String url, final int size) {
        return getImage(url, size, true);
    }

    /**
     * Get an image by an URL.
     *
     * @param url      the url.
     * @param size     the size.
     * @param useCache true if need to use cache.
     * @return the image.
     */
    @NotNull
    @FXThread
    public Image getImage(@NotNull final String url, final int size, final boolean useCache) {
        if (!useCache) return buildImage(url, size);
        final ObjectDictionary<String, Image> cache = imageCache.get(size, DictionaryFactory::newObjectDictionary);
        final Image image = cache.get(url, () -> buildImage(url, size));
        return notNull(image);
    }

    @NotNull
    private Image buildImage(@NotNull final String url, final int size) {
        return buildImage(url, null, size);
    }

    @NotNull
    private Image buildImage(@NotNull final String url, @Nullable final InputStream in, final int size) {

        final Image image;

        if (in != null) {
            image = new Image(in, size, size, false, true);
        } else {
            image = new Image(url, size, size, false, true);
        }

        if (!url.contains("icons/svg/")) {
            originalImageCache.put(image, image);
            return image;
        }

        final EditorConfig config = EditorConfig.getInstance();
        final CssColorTheme theme = config.getTheme();

        if (theme.needRepaintIcons() && supportedRepaint) {
            try {

                final Color iconColor = theme.getIconColor();

                final int width = (int) image.getWidth();
                final int height = (int) image.getHeight();

                final WritableImage wimage = new WritableImage(width, height);

                final PixelWriter pixelWriter = wimage.getPixelWriter();
                final PixelReader pixelReader = image.getPixelReader();

                for (int i = 0; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        final Color color = pixelReader.getColor(i, j);
                        if (color.getOpacity() > 0.1) {
                            pixelWriter.setColor(i, j, new Color(iconColor.getRed(), iconColor.getGreen(), iconColor.getBlue(), color.getOpacity()));
                        }
                    }
                }

                originalImageCache.put(wimage, image);

                return wimage;

            } catch (final Throwable e) {
                supportedRepaint = false;
            }
        }

        originalImageCache.put(image, image);

        return image;
    }

    /**
     * Gets an original image of the image.
     *
     * @param image the image.
     * @return the original image.
     */
    @NotNull
    public Image getOriginal(@NotNull final Image image) {
        return notNull(originalImageCache.get(image), "not found original for " + image.impl_getUrl());
    }

    /**
     * @return the list of icon finders.
     */
    @NotNull
    private Array<BiFunction<Path, String, String>> getIconFinders() {
        return iconFinders;
    }
}
