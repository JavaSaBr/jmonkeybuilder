package com.ss.builder.file.converter;

import static com.ss.rlib.common.util.FileUtils.containsExtensions;
import static com.ss.rlib.common.util.array.ArrayCollectors.toArray;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.file.converter.impl.*;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.file.converter.impl.*;
import com.ss.rlib.common.logging.Logger;
import com.ss.rlib.common.logging.LoggerManager;
import com.ss.rlib.common.plugin.extension.ExtensionPoint;
import com.ss.rlib.common.plugin.extension.ExtensionPointManager;
import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The registry of file converters.
 *
 * @author JavaSaBr
 */
public class FileConverterRegistry {

    private static final Logger LOGGER = LoggerManager.getLogger(FileConverterRegistry.class);

    /**
     * @see FileConverterDescription
     */
    public static final String EP_DESCRIPTORS = "FileConverterRegistry#descriptions";

    private static final ExtensionPoint<FileConverterDescription> DESCRIPTIONS =
            ExtensionPointManager.register(EP_DESCRIPTORS);

    private static final FileConverterRegistry INSTANCE = new FileConverterRegistry();

    @FromAnyThread
    public static @NotNull FileConverterRegistry getInstance() {
        return INSTANCE;
    }

    private FileConverterRegistry() {

        DESCRIPTIONS.register(BlendToJ3oFileConverter.DESCRIPTION)
                .register(FbxToJ3oFileConverter.DESCRIPTION)
                .register(ObjToJ3oFileConverter.DESCRIPTION)
                .register(SceneToJ3oFileConverter.DESCRIPTION)
                .register(MeshXmlToJ3oFileConverter.DESCRIPTION)
                .register(XbufToJ3oFileConverter.DESCRIPTION)
                .register(GltfToJ3oFileConverter.DESCRIPTION);

        LOGGER.info("initialized.");
    }

    /**
     * Get the list of available converters for the file.
     *
     * @param path the path.
     * @return the list of available converters.
     */
    @FromAnyThread
    public @NotNull Array<FileConverterDescription> getDescriptions(@NotNull Path path) {
        return DESCRIPTIONS.getExtensions().stream()
                .filter(desc -> containsExtensions(desc.getExtensions(), path))
                .collect(toArray(FileConverterDescription.class));
    }

    /**
     * Create a file converter using the converter description.
     *
     * @param description the converter description.
     * @param file        the file.
     * @return the new converter.
     */
    @FromAnyThread
    public @NotNull FileConverter newCreator(@NotNull FileConverterDescription description, @NotNull Path file) {
        return description.getConstructor()
                .get();
    }
}