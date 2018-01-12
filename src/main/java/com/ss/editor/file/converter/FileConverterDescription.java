package com.ss.editor.file.converter;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.ss.editor.annotation.FxThread;
import com.ss.rlib.util.array.Array;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    private String description;

    /**
     * The constructor.
     */
    @Nullable
    private Supplier<FileConverter> constructor;

    /**
     * The list of extensions.
     */
    @Nullable
    private Array<String> extensions;

    /**
     * Get the list of extensions.
     *
     * @return the list of extensions.
     */
    @FxThread
    public @NotNull Array<String> getExtensions() {
        return notNull(extensions);
    }

    /**
     * Set the list of extensions.
     *
     * @param extensions the list of extensions.
     */
    @FxThread
    public void setExtensions(@NotNull final Array<String> extensions) {
        this.extensions = extensions;
    }

    /**
     * Get the constructor.
     *
     * @return the constructor.
     */
    @FxThread
    public @NotNull Supplier<FileConverter> getConstructor() {
        return notNull(constructor);
    }

    /**
     * Set the constructor.
     *
     * @param constructor the constructor.
     */
    @FxThread
    public void setConstructor(@NotNull final Supplier<FileConverter> constructor) {
        this.constructor = constructor;
    }

    /**
     * Get the description.
     *
     * @return the description.
     */
    @FxThread
    public @Nullable String getDescription() {
        return description;
    }

    /**
     * Set the description.
     *
     * @param description the description.
     */
    @FxThread
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
