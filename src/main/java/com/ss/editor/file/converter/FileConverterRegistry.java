package com.ss.editor.file.converter;

import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.file.converter.impl.*;
import com.ss.rlib.logging.Logger;
import com.ss.rlib.logging.LoggerManager;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.function.Supplier;

/**
 * The registry of file converters.
 *
 * @author JavaSaBr
 */
public class FileConverterRegistry {

    @NotNull
    private static final Logger LOGGER = LoggerManager.getLogger(FileConverterRegistry.class);

    @NotNull
    private static final FileConverterRegistry INSTANCE = new FileConverterRegistry();

    /**
     * Gets instance.
     *
     * @return the instance
     */
    @NotNull
    public static FileConverterRegistry getInstance() {
        return INSTANCE;
    }

    /**
     * The list of converters.
     */
    @NotNull
    private final Array<FileConverterDescription> descriptions;

    private FileConverterRegistry() {
        this.descriptions = ArrayFactory.newArray(FileConverterDescription.class);
        register(BlendToJ3oFileConverter.DESCRIPTION);
        register(FBXToJ3oFileConverter.DESCRIPTION);
        register(ObjToJ3oFileConverter.DESCRIPTION);
        register(SceneToJ3oFileConverter.DESCRIPTION);
        register(MeshXmlToJ3oFileConverter.DESCRIPTION);
        register(XBufToJ3oFileConverter.DESCRIPTION);
    }

    /**
     * Add a new file converter descriptor.
     *
     * @param description the new descriptor.
     */
    @FromAnyThread
    public void register(@NotNull final FileConverterDescription description) {
        this.descriptions.add(description);
    }

    /**
     * @return the list of converters.
     */
    @NotNull
    @FromAnyThread
    private Array<FileConverterDescription> getDescriptions() {
        return descriptions;
    }

    /**
     * Get the list of available converters for a file.
     *
     * @param path the path
     * @return the list of available converters.
     */
    @FromAnyThread
    public Array<FileConverterDescription> getDescriptions(@NotNull final Path path) {

        final Array<FileConverterDescription> result = ArrayFactory.newArray(FileConverterDescription.class);
        final Array<FileConverterDescription> descriptions = getDescriptions();
        descriptions.forEach(description -> {

            final Array<String> extensions = description.getExtensions();

            if (FileUtils.containsExtensions(extensions.array(), path)) {
                result.add(description);
            }
        });

        return result;
    }

    /**
     * Create a file converter using a converter description.
     *
     * @param description the converter description.
     * @param file        the file.
     * @return the new converter.
     */
    @FromAnyThread
    public FileConverter newCreator(@NotNull final FileConverterDescription description, @NotNull final Path file) {
        final Supplier<FileConverter> constructor = description.getConstructor();
        return constructor.get();
    }
}
