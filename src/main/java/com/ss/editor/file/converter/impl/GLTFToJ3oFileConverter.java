package com.ss.editor.file.converter.impl;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.file.converter.FileConverterDescription;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of {@link AbstractFileConverter} to convert .glft file to .j3o.
 *
 * @author JavaSaBr
 */
public class GLTFToJ3oFileConverter extends AbstractModelFileConverter {

    @NotNull
    private static final Array<String> EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        EXTENSIONS.add(FileExtensions.MODEL_GLTF);
        EXTENSIONS.asUnsafe().trimToSize();
    }

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileConverterDescription DESCRIPTION = new FileConverterDescription();

    static {
        DESCRIPTION.setDescription(Messages.GLTF_TO_J3O_FILE_CONVERTER_DESCRIPTION);
        DESCRIPTION.setConstructor(GLTFToJ3oFileConverter::new);
        DESCRIPTION.setExtensions(EXTENSIONS);
    }

    private GLTFToJ3oFileConverter() {
    }

    @NotNull
    @Override
    protected Array<String> getAvailableExtensions() {
        return EXTENSIONS;
    }

    @NotNull
    @Override
    public String getTargetExtension() {
        return FileExtensions.JME_OBJECT;
    }
}
