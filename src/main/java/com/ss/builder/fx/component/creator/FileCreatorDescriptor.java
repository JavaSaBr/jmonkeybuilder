package com.ss.builder.fx.component.creator;

import static com.ss.rlib.common.util.ObjectUtils.notNull;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.rlib.common.util.StringUtils;
import javafx.scene.image.Image;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.Callable;

/**
 * The descriptor of a file creator.
 *
 * @author JavaSaBr
 */
public class FileCreatorDescriptor {

    /**
     * The description.
     */
    @NotNull
    private final String description;

    /**
     * The constructor.
     */
    @NotNull
    private final Callable<FileCreator> constructor;

    /**
     * The icon.
     */
    @Nullable
    private final Image icon;

    public FileCreatorDescriptor(@Nullable String description, @NotNull Callable<FileCreator> constructor) {
        this(description, constructor, null);
    }

    public FileCreatorDescriptor(
            @Nullable String description,
            @NotNull Callable<FileCreator> constructor,
            @Nullable Image icon
    ) {
        this.description = StringUtils.emptyIfNull(description);
        this.constructor = constructor;
        this.icon = icon;
    }

    /**
     * Get the constructor.
     *
     * @return the constructor.
     */
    @FromAnyThread
    public @NotNull Callable<FileCreator> getConstructor() {
        return notNull(constructor);
    }

    /**
     * Get the description.
     *
     * @return the description.
     */
    @FromAnyThread
    public @NotNull String getDescription() {
        return description;
    }

    /**
     * Get the icon.
     *
     * @return the icon or null.
     */
    @FromAnyThread
    public @Nullable Image getIcon() {
        return icon;
    }

    @Override
    public String toString() {
        return "FileCreatorDescriptor{" +
                "description='" + description + '\'' +
                ", constructor=" + constructor +
                '}';
    }
}
