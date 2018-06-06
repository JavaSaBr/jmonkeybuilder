package com.ss.editor.ui.component.creator;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.creator.impl.EmptyFileCreator;
import com.ss.editor.ui.component.creator.impl.EmptyModelCreator;
import com.ss.editor.ui.component.creator.impl.EmptySceneCreator;
import com.ss.editor.ui.component.creator.impl.FolderCreator;
import com.ss.editor.ui.component.creator.impl.material.MaterialFileCreator;
import com.ss.editor.ui.component.creator.impl.material.definition.MaterialDefinitionFileCreator;
import com.ss.editor.ui.component.creator.impl.texture.SingleColorTextureFileCreator;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.util.array.ConcurrentArray;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.Collection;

/**
 * The registry with file creators.
 *
 * @author JavaSaBr
 */
public class FileCreatorRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(FileCreatorRegistry.class);

    private static final FileCreatorRegistry INSTANCE = new FileCreatorRegistry();

    @FromAnyThread
    public @NotNull static FileCreatorRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of file creator descriptions.
     */
    @NotNull
    private final ConcurrentArray<FileCreatorDescription> descriptions;

    private FileCreatorRegistry() {
        this.descriptions = ConcurrentArray.of(FileCreatorDescription.class);
        register(MaterialFileCreator.DESCRIPTION);
        register(MaterialDefinitionFileCreator.DESCRIPTION);
        register(EmptyFileCreator.DESCRIPTION);
        register(FolderCreator.DESCRIPTION);
        register(EmptyModelCreator.DESCRIPTION);
        register(SingleColorTextureFileCreator.DESCRIPTION);
        register(EmptySceneCreator.DESCRIPTION);
    }

    /**
     * Add a new creator description.
     *
     * @param description the new description.
     */
    @FromAnyThread
    public void register(@NotNull FileCreatorDescription description) {
        descriptions.runInWriteLock(description, Collection::add);
    }

    /**
     * Gets descriptions.
     *
     * @return the list of file creator descriptions.
     */
    @FromAnyThread
    public @NotNull ConcurrentArray<FileCreatorDescription> getDescriptions() {
        return descriptions;
    }

    /**
     * Create a new creator of the description for the file.
     *
     * @param description the file creator description.
     * @param file        the file.
     * @return the file creator.
     */
    @FromAnyThread
    public @Nullable FileCreator newCreator(@NotNull FileCreatorDescription description, @NotNull Path file) {

        var constructor = description.getConstructor();
        try {
            return constructor.call();
        } catch (Exception e) {
            LOGGER.warning(e);
        }

        return null;
    }
}
