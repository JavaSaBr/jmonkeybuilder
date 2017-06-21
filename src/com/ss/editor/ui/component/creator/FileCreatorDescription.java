package com.ss.editor.ui.component.creator;

import static java.util.Objects.requireNonNull;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
    @Nullable
    private String fileDescription;

    /**
     * The constructor of a file creator.
     */
    @Nullable
    private Callable<FileCreator> constructor;

    /**
     * The icon.
     */
    @Nullable
    private Image icon;

    /**
     * Sets constructor.
     *
     * @param constructor the constructor of a file creator.
     */
    public void setConstructor(@NotNull final Callable<FileCreator> constructor) {
        this.constructor = constructor;
    }

    /**
     * Sets file description.
     *
     * @param fileDescription the file description.
     */
    public void setFileDescription(@NotNull final String fileDescription) {
        this.fileDescription = fileDescription;
    }

    /**
     * Gets constructor.
     *
     * @return the constructor of a file creator.
     */
    @NotNull
    public Callable<FileCreator> getConstructor() {
        return requireNonNull(constructor);
    }

    /**
     * Gets file description.
     *
     * @return the file description.
     */
    @NotNull
    public String getFileDescription() {
        return requireNonNull(fileDescription);
    }

    /**
     * Sets icon.
     *
     * @param icon the icon.
     */
    public void setIcon(@Nullable final Image icon) {
        this.icon = icon;
    }

    /**
     * Gets icon.
     *
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
