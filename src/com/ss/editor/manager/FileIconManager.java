package com.ss.editor.manager;

import com.ss.editor.util.EditorUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.image.Image;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.manager.InitializeManager;
import rlib.util.FileUtils;
import rlib.util.dictionary.DictionaryFactory;
import rlib.util.dictionary.ObjectDictionary;

import static com.ss.editor.util.EditorUtil.toClasspath;
import static java.lang.String.valueOf;

/**
 * Менеджер по работе с иконками файлов.
 *
 * @author Ronn
 */
public class FileIconManager {

    private static final Logger LOGGER = LoggerManager.getLogger(FileIconManager.class);

    public static final int DEFAULT_FILE_ICON_SIZE = 16;

    private static final ObjectDictionary<String, String> EXTENSION_TO_CONTENT_TYPE = DictionaryFactory.newObjectDictionary();

    static {
        EXTENSION_TO_CONTENT_TYPE.put("png", "image-png");
        EXTENSION_TO_CONTENT_TYPE.put("jpg", "image-jpeg");
        EXTENSION_TO_CONTENT_TYPE.put("jpeg", "image-jpeg");
        EXTENSION_TO_CONTENT_TYPE.put("tiff", "image-tiff");
        EXTENSION_TO_CONTENT_TYPE.put("gif", "image-gif");
        EXTENSION_TO_CONTENT_TYPE.put("bmp", "image-bmp");
        EXTENSION_TO_CONTENT_TYPE.put("png", "image-png");
        EXTENSION_TO_CONTENT_TYPE.put("png", "image-png");
        EXTENSION_TO_CONTENT_TYPE.put("tga", "image-tga");
        EXTENSION_TO_CONTENT_TYPE.put("psd", "image-psd");
        EXTENSION_TO_CONTENT_TYPE.put("dds", "image-jpeg");

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

        EXTENSION_TO_CONTENT_TYPE.put("j3o", "jme3");
        EXTENSION_TO_CONTENT_TYPE.put("j3m", "jme3");
        EXTENSION_TO_CONTENT_TYPE.put("j3md", "jme3");
    }

    private static FileIconManager instance;

    public static FileIconManager getInstance() {

        if (instance == null) {
            instance = new FileIconManager();
        }

        return instance;
    }

    /**
     * Кеш для хранения загруженных иконок.
     */
    private final Map<String, Image> imageCache;

    public FileIconManager() {
        InitializeManager.valid(getClass());
        this.imageCache = new HashMap<>();
    }

    /**
     * Получение иконки для указанного файла.
     *
     * @param path файл для которого надо получить иконку.
     * @param size размер иконки.
     * @return найденная иконка.
     */
    public Image getIcon(final Path path, int size) {

        String contentType = null;

        try {
            contentType = Files.probeContentType(path);
        } catch (IOException e) {
            LOGGER.warning(e);
        }

        if (Files.isDirectory(path)) {
            contentType = "folder";
        } else if (contentType == null) {
            final String extension = FileUtils.getExtension(path);
            contentType = EXTENSION_TO_CONTENT_TYPE.get(extension);
        }

        if (contentType != null) {
            contentType = contentType.replace("/", "-");
        }

        if (contentType == null) {
            LOGGER.warning("not found content type for " + path);
            contentType = "none";
        }

        final Path mimeTypes = Paths.get("/ui/icons/faenza/mimetypes");

        Path iconPath = mimeTypes.resolve(valueOf(size)).resolve(contentType + ".png");
        String url = toClasspath(iconPath);

        if (!EditorUtil.checkExists(url)) {
            LOGGER.warning("not found image for contentType " + contentType + " and path " + path);
            iconPath = mimeTypes.resolve(valueOf(size)).resolve("none.png");
            url = toClasspath(iconPath);
        }

        return getImage(url);
    }

    /**
     * Получение картинки по адрессу.
     */
    public Image getImage(final String url) {

        Image image = imageCache.get(url);

        if (image == null) {
            image = new Image(url);
            imageCache.put(url, image);
        }

        return image;
    }
}
