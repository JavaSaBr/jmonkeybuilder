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
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;

/**
 * The registry of available file creators.
 *
 * @author JavaSaBr
 */
public class FileCreatorRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(FileCreatorRegistry.class);

    /**
     * @see FileCreatorDescription
     */
    public static final String EP_DESCRIPTIONS = "FileCreatorRegistry#descriptions";

    private static final ExtensionPoint<FileCreatorDescription> DESCRIPTIONS =
            ExtensionPointManager.register(EP_DESCRIPTIONS);

    private static final FileCreatorRegistry INSTANCE = new FileCreatorRegistry();

    @FromAnyThread
    public @NotNull static FileCreatorRegistry getInstance() {
        return INSTANCE;
    }

    private FileCreatorRegistry() {

        DESCRIPTIONS.register(MaterialFileCreator.DESCRIPTION)
                .register(MaterialDefinitionFileCreator.DESCRIPTION)
                .register(EmptyFileCreator.DESCRIPTION)
                .register(FolderCreator.DESCRIPTION)
                .register(EmptyModelCreator.DESCRIPTION)
                .register(SingleColorTextureFileCreator.DESCRIPTION)
                .register(EmptySceneCreator.DESCRIPTION);

        LOGGER.info("initialized.");
    }

    /**
     * Get the available descriptions.
     *
     * @return the available descriptions.
     */
    @FromAnyThread
    public @NotNull List<FileCreatorDescription> getDescriptions() {
        return DESCRIPTIONS.getExtensions();
    }

    /**
     * Create a new creator by the description for the file.
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
