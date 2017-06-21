package com.ss.editor.file.converter;

import com.ss.editor.annotation.FXThread;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Supplier;

import com.ss.rlib.util.array.Array;

/**
 * The description of a file converter.
 *
 * @author JavaSaBr
 */
public class FileConverterDescription {

    /**
     * The description.
     */
    private String description;

    /**
     * The constructor.
     */
    private Supplier<FileConverter> constructor;

    /**
     * The list of extensions.
     */
    private Array<String> extensions;

    /**
     * @return the list of extensions.
     */
    @NotNull
    @FXThread
    public Array<String> getExtensions() {
        return Objects.requireNonNull(extensions);
    }

    /**
     * @param extensions the list of extensions.
     */
    @FXThread
    public void setExtensions(@NotNull final Array<String> extensions) {
        this.extensions = extensions;
    }

    /**
     * @return the constructor.
     */
    @NotNull
    @FXThread
    public Supplier<FileConverter> getConstructor() {
        return Objects.requireNonNull(constructor);
    }

    /**
     * @param constructor the constructor.
     */
    @FXThread
    public void setConstructor(@NotNull final Supplier<FileConverter> constructor) {
        this.constructor = constructor;
    }

    /**
     * @return the description.
     */
    @FXThread
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description.
     */
    @FXThread
    public void setDescription(@NotNull final String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "FileConverterDescription{" +
                "extensions=" + extensions +
                ", description='" + description + '\'' +
                '}';
    }
}
