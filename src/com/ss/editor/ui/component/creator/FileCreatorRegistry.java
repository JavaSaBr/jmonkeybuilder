package com.ss.editor.ui.component.creator;

import com.ss.editor.ui.component.creator.impl.EmptyFileCreator;
import com.ss.editor.ui.component.creator.impl.EmptyModelCreator;
import com.ss.editor.ui.component.creator.impl.EmptySceneCreator;
import com.ss.editor.ui.component.creator.impl.FolderCreator;
import com.ss.editor.ui.component.creator.impl.MaterialFileCreator;
import com.ss.editor.ui.component.creator.impl.PostFilterViewFileCreator;
import com.ss.editor.ui.component.creator.impl.texture.SingleColorTextureFileCreator;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.concurrent.Callable;

import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The registry with file creators.
 *
 * @author JavaSaBr
 */
public class FileCreatorRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(FileCreatorRegistry.class);

    @NotNull
    private static final FileCreatorRegistry INSTANCE = new FileCreatorRegistry();

    @NotNull
    public static FileCreatorRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of file creator descriptions.
     */
    @NotNull
    private final Array<FileCreatorDescription> descriptions;

    public FileCreatorRegistry() {
        this.descriptions = ArrayFactory.newArray(FileCreatorDescription.class);
        addDescription(MaterialFileCreator.DESCRIPTION);
        addDescription(PostFilterViewFileCreator.DESCRIPTION);
        addDescription(EmptyFileCreator.DESCRIPTION);
        addDescription(FolderCreator.DESCRIPTION);
        addDescription(EmptyModelCreator.DESCRIPTION);
        addDescription(SingleColorTextureFileCreator.DESCRIPTION);
        addDescription(EmptySceneCreator.DESCRIPTION);
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
    public FileCreator newCreator(@NotNull final FileCreatorDescription description, @NotNull final Path file) {

        final Callable<FileCreator> constructor = description.getConstructor();
        try {
            return constructor.call();
        } catch (Exception e) {
            LOGGER.warning(e);
        }

        return null;
    }
}
