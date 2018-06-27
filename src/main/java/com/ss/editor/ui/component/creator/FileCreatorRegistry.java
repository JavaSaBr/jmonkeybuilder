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
     * @see FileCreatorDescriptor
     */
    public static final String EP_DESCRIPTORS = "FileCreatorRegistry#descriptors";

    private static final ExtensionPoint<FileCreatorDescriptor> DESCRIPTORS =
            ExtensionPointManager.register(EP_DESCRIPTORS);

    private static final FileCreatorRegistry INSTANCE = new FileCreatorRegistry();

    @FromAnyThread
    public @NotNull static FileCreatorRegistry getInstance() {
        return INSTANCE;
    }

    private FileCreatorRegistry() {

        DESCRIPTORS.register(MaterialFileCreator.DESCRIPTOR)
                .register(MaterialDefinitionFileCreator.DESCRIPTOR)
                .register(EmptyFileCreator.DESCRIPTOR)
                .register(FolderCreator.DESCRIPTOR)
                .register(EmptyModelCreator.DESCRIPTOR)
                .register(SingleColorTextureFileCreator.DESCRIPTOR)
                .register(EmptySceneCreator.DESCRIPTOR);

        LOGGER.info("initialized.");
    }

    /**
     * Get the available descriptors.
     *
     * @return the available descriptors.
     */
    @FromAnyThread
    public @NotNull List<FileCreatorDescriptor> getDescriptors() {
        return DESCRIPTORS.getExtensions();
    }

    /**
     * Create a new creator by the descriptor for the file.
     *
     * @param descriptor the file creator descriptor.
     * @param file        the file.
     * @return the file creator.
     */
    @FromAnyThread
    public @Nullable FileCreator newCreator(@NotNull FileCreatorDescriptor descriptor, @NotNull Path file) {

        var constructor = descriptor.getConstructor();
        try {
            return constructor.call();
        } catch (Exception e) {
            LOGGER.warning(e);
        }

        return null;
    }
}
