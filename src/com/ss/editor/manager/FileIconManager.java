package com.ss.editor.manager;

import com.ss.editor.util.EditorUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import javax.activation.FileTypeMap;
import javax.activation.MimetypesFileTypeMap;

import javafx.scene.image.Image;
import rlib.logging.Logger;
import rlib.logging.LoggerManager;
import rlib.manager.InitializeManager;

import static java.lang.String.valueOf;

/**
 * Менеджер по работе с иконками файлов.
 *
 * @author Ronn
 */
public class FileIconManager {

    private static final Logger LOGGER = LoggerManager.getLogger(FileIconManager.class);

    public static final int DEFAULT_FILE_ICON_SIZE = 16;

    private static final FileTypeMap FILE_TYPE_MAP = MimetypesFileTypeMap.getDefaultFileTypeMap();

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

        if(contentType == null && FILE_TYPE_MAP != null) {
            contentType = FILE_TYPE_MAP.getContentType(path.toFile());
        }

        LOGGER.info("contentType " + contentType + " for " + path);

        if (contentType != null) {
            contentType = contentType.replace("/", "-");
        }

        if (contentType == null) {
            contentType = "none";
        } else if ("inode-directory".equals(contentType)) {
            contentType = "folder";
        }

        final Path mimeTypes = Paths.get("/ui/icons/faenza/mimetypes");

        Path iconPath = mimeTypes.resolve(valueOf(size)).resolve(contentType + ".png");
        String url = EditorUtil.normalizePath(iconPath);

        LOGGER.info("url " + url);

        if (!EditorUtil.checkExists(url)) {
            iconPath = mimeTypes.resolve(valueOf(size)).resolve("none.png");
            url = EditorUtil.normalizePath(iconPath);
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
