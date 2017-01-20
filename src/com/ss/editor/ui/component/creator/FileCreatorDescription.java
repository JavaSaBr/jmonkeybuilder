package com.ss.editor.ui.component.creator;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.Callable;

import javafx.scene.image.Image;

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
     * The icon.
     */
    @Nullable
    private Image icon;

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

    /**
     * @param icon the icon.
     */
    public void setIcon(@Nullable final Image icon) {
        this.icon = icon;
    }

    /**
     * @return the icon.
     */
    @Nullable
    public Image getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "FileCreatorDescription{" +
                "fileDescription='" + fileDescription + '\'' +
                ", constructor=" + constructor +
                '}';
    }
}
