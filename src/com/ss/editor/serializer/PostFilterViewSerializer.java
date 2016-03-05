package com.ss.editor.serializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.ss.editor.file.PostFilterViewFile;

import java.nio.file.Path;

import rlib.util.FileUtils;

/**
 * Сериализатор PostFilterViewFile.
 *
 * @author Ronn
 */
public class PostFilterViewSerializer {

    private static final ThreadLocal<Gson> GSON_LOCAL = new ThreadLocal<Gson>() {

        @Override
        protected Gson initialValue() {
            return new GsonBuilder().setPrettyPrinting().create();
        }
    };

    /**
     * Прочитать PostFilterViewFile из файла.
     */
    public static PostFilterViewFile deserialize(final Path file) {

        final byte[] content = FileUtils.getContent(file);

        if (content == null || content.length < 1) {
            return new PostFilterViewFile();
        }

        final String stringContent = new String(content);

        final Gson gson = GSON_LOCAL.get();

        return gson.fromJson(stringContent, PostFilterViewFile.class);
    }

    /**
     * Конвертировать PostFilterViewFile в файл.
     */
    public static String serializeToString(final PostFilterViewFile file) {
        final Gson gson = GSON_LOCAL.get();
        return gson.toJson(file);
    }
}
