package com.ss.editor.ui.component.creator;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * The description of a file creator.
 *
 * @author JavaSaBr
 */
public class FileCreatorDescription {

    /**
     * The file description.
     */
    private String fileDescription;

    /**
     * The constructor of a file creator.
     */
    private Callable<FileCreator> constructor;

    /**
     * @param constructor the constructor of a file creator.
     */
    public void setConstructor(@NotNull final Callable<FileCreator> constructor) {
        this.constructor = constructor;
    }

    /**
     * @param fileDescription the file description.
     */
    public void setFileDescription(@NotNull final String fileDescription) {
        this.fileDescription = fileDescription;
    }

    /**
     * @return the constructor of a file creator.
     */
    @NotNull
    public Callable<FileCreator> getConstructor() {
        return Objects.requireNonNull(constructor);
    }

    /**
     * @return the file description.
     */
    @NotNull
    public String getFileDescription() {
        return Objects.requireNonNull(fileDescription);
    }

    @Override
    public String toString() {
        return "FileCreatorDescription{" +
                "fileDescription='" + fileDescription + '\'' +
                ", constructor=" + constructor +
                '}';
    }
}
