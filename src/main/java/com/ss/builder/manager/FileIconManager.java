package com.ss.builder.manager;

import static com.ss.builder.config.DefaultSettingsProvider.Defaults.PREF_DEFAULT_THEME;
import static com.ss.builder.config.DefaultSettingsProvider.Preferences.PREF_UI_THEME;
import static com.ss.builder.util.EditorUtils.toAssetPath;
import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.FileExtensions;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.config.EditorConfig;
import com.ss.builder.util.EditorUtils;
import com.ss.builder.util.svg.SvgImageLoader;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.manager.InitializeManager;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import com.ss.rlib.common.util.FileUtils;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.dictionary.DictionaryFactory;
import com.ss.rlib.common.util.dictionary.IntegerDictionary;
import com.ss.rlib.common.util.dictionary.ObjectDictionary;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The class to manage file icons.
 *
 * @author JavaSaBr
 */
public class FileIconManager {

    private static final Logger LOGGER = LoggerManager.getLogger(FileIconManager.class);

    @FunctionalInterface
    public interface IconFinder {
        @Nullable String find(@NotNull Path file, @NotNull String extension);
    }

    public static final String EP_ICON_FINDERS = "FileIconManager#iconFinders";

    public static final int DEFAULT_FILE_ICON_SIZE = 16;

    private static final ExtensionPoint<IconFinder> ICON_FINDERS =
            ExtensionPointManager.register(EP_ICON_FINDERS);

    private static final ObjectDictionary<String, String> EXTENSION_TO_CONTENT_TYPE =
            ObjectDictionary.ofType(String.class);

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
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_SHADER_NODE, "vector");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_SCENE, "j3s");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_MATERIAL, "parquet");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_MATERIAL_DEFINITION, "text");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.MODEL_GLTF, "cube");

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

    private static final Array<Path> MIME_TYPES_FOLDERS = Array.of(
            Paths.get("/ui/icons/filetypes/emerald/mimetypes"),
            Paths.get("/ui/icons/filetypes/")
    );

    @Nullable
    private static FileIconManager instance;

    @FromAnyThread
    public static @NotNull FileIconManager getInstance() {
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

    private FileIconManager() {
        InitializeManager.valid(getClass());

        this.imageCache = IntegerDictionary.ofType(ObjectDictionary.class);
        this.extensionToUrl = ObjectDictionary.ofType(String.class);
        this.originalImageCache = ObjectDictionary.ofType(Image.class);

        LOGGER.info("initialized.");
    }

    /**
     * Get an icon of the file.
     *
     * @param path the file.
     * @param size the icon size.
     * @return the icon.
     */
    @FxThread
    public @NotNull Image getIcon(@NotNull Path path, int size) {
        return getIcon(path, Files.isDirectory(path), true, size);
    }

    /**
     * Get an icon of the file.
     *
     * @param path                the file.
     * @param directory           the directory.
     * @param tryToGetContentType true of we can try to get content type of the file.
     * @param size                the icon size.
     * @return the icon.
     */
    @FxThread
    public @NotNull Image getIcon(@NotNull Path path, boolean directory, boolean tryToGetContentType, int size) {

        var extension = directory ? "folder" : FileUtils.getExtension(path);
        var url = extensionToUrl.get(extension);

        if (url != null) {
            return getImage(url, size);
        }

        var iconFinders = ICON_FINDERS.getExtensions();

        if (!iconFinders.isEmpty()) {
            for (var iconFinder : iconFinders) {

                url = iconFinder.find(path, extension);

                var classLoader = iconFinder.getClass()
                        .getClassLoader();

                if (url == null || !EditorUtils.checkExists(url, classLoader)) {
                    continue;
                }

                extensionToUrl.put(extension, url);

                return getImage(url, classLoader, size);
            }
        }

        String contentType;

        if (directory) {
            contentType = "folder";
        } else {

            contentType = EXTENSION_TO_CONTENT_TYPE.get(extension);

            if (contentType == null && tryToGetContentType) {
                try {
                    contentType = Files.probeContentType(path);
                } catch (IOException e) {
                    LOGGER.warning(e);
                }
            }
        }

        if (contentType != null) {
            contentType = contentType.replace("/", "-");
        }

        if (contentType == null) {
            LOGGER.debug("not found content type for " + path);
            contentType = "none";
        }

        for (var mimeTypes : MIME_TYPES_FOLDERS) {

            Path iconPath = mimeTypes.resolve(contentType + ".svg");
            url = toAssetPath(iconPath);

            if (!EditorUtils.checkExists(url)) {
                contentType = EXTENSION_TO_CONTENT_TYPE.get(extension);
                iconPath = mimeTypes.resolve(contentType + ".svg");
                url = toAssetPath(iconPath);
            }

            if (!EditorUtils.checkExists(url)) {
                contentType = EXTENSION_TO_CONTENT_TYPE.get(extension);
                iconPath = mimeTypes.resolve(contentType + ".png");
                url = toAssetPath(iconPath);
            }

            if (EditorUtils.checkExists(url)) {
                break;
            }
        }

        if (url == null || !EditorUtils.checkExists(url)) {
            LOGGER.warning("not found image for contentType " + contentType + " and path " + path);
            url = "/ui/icons/svg/document.svg";
        }

        extensionToUrl.put(extension, url);

        return getImage(url, size);
    }

    /**
     * Get an image by the URL.
     *
     * @param url the url.
     * @return the image.
     */
    @FxThread
    public @NotNull Image getImage(@NotNull String url) {
        return getImage(url, 16);
    }

    /**
     * Get an image by the URL.
     *
     * @param url  the url.
     * @param size the size.
     * @return the image.
     */
    @FxThread
    public @NotNull Image getImage(@NotNull String url, int size) {
        return getImage(url, size, true);
    }

    /**
     * Get an image by the URL.
     *
     * @param url         the url.
     * @param classLoader the class loader.
     * @param size        the size.
     * @return the image.
     */
    @FxThread
    public @NotNull Image getImage(@NotNull String url, @NotNull ClassLoader classLoader, int size) {
        return getImage(url, classLoader, size, true);
    }

    /**
     * Get an image by the URL.
     *
     * @param url      the url.
     * @param size     the size.
     * @param useCache true if need to use cache.
     * @return the image.
     */
    @FxThread
    public @NotNull Image getImage(@NotNull String url, int size, boolean useCache) {
        return getImage(url, getClass().getClassLoader(), size, useCache);
    }

    /**
     * Get an image by the URL.
     *
     * @param url         the url.
     * @param classLoader the class loader.
     * @param size        the size.
     * @param useCache    true if need to use cache.
     * @return the image.
     */
    @FxThread
    public @NotNull Image getImage(@NotNull String url, @NotNull ClassLoader classLoader, int size, boolean useCache) {

        if (!useCache) {
            return buildImage(url, classLoader, size);
        }

        return imageCache.getOrCompute(size, DictionaryFactory::newObjectDictionary)
                .getOrCompute(url, () -> buildImage(url, classLoader, size));
    }

    @FxThread
    private @NotNull Image buildImage(@NotNull String url, @NotNull ClassLoader classLoader, int size) {
        return buildImage(url, EditorUtils.getInputStream(url, classLoader), size);
    }

    @FxThread
    private @NotNull Image buildImage(@NotNull String url, @Nullable InputStream in, int size) {

        Image image;

        if (in != null) {
            image = new Image(in, size, size, false, true);
        } else {
            image = new Image(url, size, size, false, true);
        }

        if (!url.contains("icons/svg/")) {
            originalImageCache.put(image, image);
            return image;
        }

        var config = EditorConfig.getInstance();
        var theme = config.getEnum(PREF_UI_THEME, PREF_DEFAULT_THEME);

        if (theme.needRepaintIcons()) {
            try {

                var iconColor = theme.getIconColor();

                Image coloredImage;

                SvgImageLoader.OVERRIDE_COLOR.set(iconColor);
                try {

                    if (in != null) {
                        coloredImage = new Image(in, size, size, false, true);
                    } else {
                        coloredImage = new Image(url, size, size, false, true);
                    }

                } finally {
                    SvgImageLoader.OVERRIDE_COLOR.set(null);
                }

                originalImageCache.put(coloredImage, image);

                return coloredImage;

            } catch (Throwable e) {
                LOGGER.warning(e);
            }
        }

        originalImageCache.put(image, image);

        return image;
    }

    /**
     * Get an original image of the image.
     *
     * @param image the image.
     * @return the original image.
     */
    @FromAnyThread
    public @NotNull Image getOriginal(@NotNull Image image) {
        return notNull(originalImageCache.get(image), "not found original for " + image.getUrl());
    }
}
