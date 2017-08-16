package com.ss.editor.file.converter;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FXThread;
import com.ss.rlib.util.array.Array;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

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
     * Gets extensions.
     *
     * @return the list of extensions.
     */
    @NotNull
    @FXThread
    public Array<String> getExtensions() {
        return notNull(extensions);
    }

    /**
     * Sets extensions.
     *
     * @param extensions the list of extensions.
     */
    @FXThread
    public void setExtensions(@NotNull final Array<String> extensions) {
        this.extensions = extensions;
    }

    /**
     * Gets constructor.
     *
     * @return the constructor.
     */
    @NotNull
    @FXThread
    public Supplier<FileConverter> getConstructor() {
        return notNull(constructor);
    }

    /**
     * Sets constructor.
     *
     * @param constructor the constructor.
     */
    @FXThread
    public void setConstructor(@NotNull final Supplier<FileConverter> constructor) {
        this.constructor = constructor;
    }

    /**
     * Gets description.
     *
     * @return the description.
     */
    @FXThread
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
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
