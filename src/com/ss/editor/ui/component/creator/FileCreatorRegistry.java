package com.ss.editor.ui.component.creator;

import com.ss.editor.ui.component.creator.impl.*;
import com.ss.editor.ui.component.creator.impl.material.MaterialFileCreator;
import com.ss.editor.ui.component.creator.impl.material.definition.MaterialDefinitionFileCreator;
import com.ss.editor.ui.component.creator.impl.texture.SingleColorTextureFileCreator;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.Callable;

/**
 * The registry with file creators.
 *
 * @author JavaSaBr
 */
public class FileCreatorRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(FileCreatorRegistry.class);

    @NotNull
    private static final FileCreatorRegistry INSTANCE = new FileCreatorRegistry();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static FileCreatorRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of file creator descriptions.
     */
    @NotNull
    private final Array<FileCreatorDescription> descriptions;

    /**
     * Instantiates a new File creator registry.
     */
    private FileCreatorRegistry() {
        this.descriptions = ArrayFactory.newArray(FileCreatorDescription.class);
        addDescription(MaterialFileCreator.DESCRIPTION);
        addDescription(MaterialDefinitionFileCreator.DESCRIPTION);
        addDescription(EmptyFileCreator.DESCRIPTION);
        addDescription(FolderCreator.DESCRIPTION);
        addDescription(EmptyModelCreator.DESCRIPTION);
        addDescription(SingleColorTextureFileCreator.DESCRIPTION);
        addDescription(EmptySceneCreator.DESCRIPTION);
        addDescription(DefaultSceneCreator.DESCRIPTION);
    }

    /**
     * Add a new creator description.
     *
     * @param description the new description.
     */
    private void addDescription(@NotNull final FileCreatorDescription description) {
        this.descriptions.add(description);
    }

    /**
     * Gets descriptions.
     *
     * @return the list of file creator descriptions.
     */
    @NotNull
    public Array<FileCreatorDescription> getDescriptions() {
        return descriptions;
    }

    /**
     * Create a new creator of the description for the file.
     *
     * @param description the file creator description.
     * @param file        the file.
     * @return the file creator.
     */
    @Nullable
    public FileCreator newCreator(@NotNull final FileCreatorDescription description, @NotNull final Path file) {

        final Callable<FileCreator> constructor = description.getConstructor();
        try {
            return constructor.call();
        } catch (final Exception e) {
            LOGGER.warning(e);
        }

        return null;
    }
}
