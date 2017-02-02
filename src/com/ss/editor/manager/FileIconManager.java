package com.ss.editor.manager;

import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.lang.String.valueOf;
import com.ss.editor.FileExtensions;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.util.EditorUtil;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.manager.InitializeManager;
import rlib.util.FileUtils;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * The class to manage file icons.
 *
 * @author JavaSaBr
 */
public class FileIconManager {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(FileIconManager.class);

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
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_TGA, "image-jpeg");
        EXTENSION_TO_CONTENT_TYPE.put("psd", "image-psd");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_DDS, "image-jpeg");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.IMAGE_HDR, "image-jpeg");

        EXTENSION_TO_CONTENT_TYPE.put("ogg", "sound");
        EXTENSION_TO_CONTENT_TYPE.put("wav", "sound");
        EXTENSION_TO_CONTENT_TYPE.put("mp3", "sound");

        EXTENSION_TO_CONTENT_TYPE.put("txt", "application-text");
        EXTENSION_TO_CONTENT_TYPE.put("log", "application-text");

        EXTENSION_TO_CONTENT_TYPE.put("zip", "application-x-archive");
        EXTENSION_TO_CONTENT_TYPE.put("rar", "application-x-archive");
        EXTENSION_TO_CONTENT_TYPE.put("gz", "application-x-archive");
        EXTENSION_TO_CONTENT_TYPE.put("jar", "application-x-archive");

        EXTENSION_TO_CONTENT_TYPE.put("java", "application-x-java");

        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_OBJECT, "jme3");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_SCENE, "sse");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_MATERIAL, "gnome-mime-text");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.JME_MATERIAL_DEFINITION, "gnome-mime-text");

        EXTENSION_TO_CONTENT_TYPE.put("obj", "application-x-tgif");
        EXTENSION_TO_CONTENT_TYPE.put("blend", "application-x-blender");
        EXTENSION_TO_CONTENT_TYPE.put("fbx", "fbx");
        EXTENSION_TO_CONTENT_TYPE.put("j3odata", "gnome-mime-text");
        EXTENSION_TO_CONTENT_TYPE.put("pfv", "gnome-mime-text");
        EXTENSION_TO_CONTENT_TYPE.put("xml", "application-xml");
        EXTENSION_TO_CONTENT_TYPE.put("exe", "application-x-ms-dos-executable");
        EXTENSION_TO_CONTENT_TYPE.put("sh", "application-x-shellscript");

        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.GLSL_FRAGMENT, "gnome-mime-text-x-csharp");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.GLSL_VERTEX, "gnome-mime-text-x-csharp");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.GLSL_LIB, "gnome-mime-text-x-csharp");
        EXTENSION_TO_CONTENT_TYPE.put(FileExtensions.MODEL_XBUF, "image-svg+xml");
    }

    @Nullable
    private static FileIconManager instance;

    @NotNull
    public static FileIconManager getInstance() {
        if (instance == null) instance = new FileIconManager();
        return instance;
    }

    /**
     * The image cache.
     */
    @NotNull
    private final ObjectDictionary<String, Image> imageCache;

    private FileIconManager() {
        InitializeManager.valid(getClass());
        this.imageCache = DictionaryFactory.newObjectDictionary();
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
            LOGGER.warning("not found content type for " + path);
            contentType = "none";
        }

        final Path mimeTypes = Paths.get("/ui/icons/vibrancy/mimetypes");

        Path iconPath = mimeTypes.resolve(valueOf(size)).resolve(contentType + ".svg");
        String url = toAssetPath(iconPath);

        if (!EditorUtil.checkExists(url)) {
            contentType = EXTENSION_TO_CONTENT_TYPE.get(FileUtils.getExtension(path));
            iconPath = mimeTypes.resolve(valueOf(size)).resolve(contentType + ".svg");
            url = toAssetPath(iconPath);
        }

        if (!EditorUtil.checkExists(url)) {
            contentType = EXTENSION_TO_CONTENT_TYPE.get(FileUtils.getExtension(path));
            iconPath = mimeTypes.resolve(valueOf(size)).resolve(contentType + ".png");
            url = toAssetPath(iconPath);
        }

        if (!EditorUtil.checkExists(url)) {
            LOGGER.warning("not found image for contentType " + contentType + " and path " + path);
            iconPath = mimeTypes.resolve(valueOf(size)).resolve("none.svg");
            url = toAssetPath(iconPath);
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
        final Image image = imageCache.get(url, () -> new Image(url, size, size, false, true));
        return Objects.requireNonNull(image);
    }
}
