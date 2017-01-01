package com.ss.editor.file.converter.impl;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.file.converter.FileConverterDescription;

import org.jetbrains.annotations.NotNull;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of {@link AbstractFileConverter} for converting .blend file to .j3o.
 *
 * @author JavaSaBr
 */
public class BlendToJ3oFileConverter extends AbstractFileConverter {

    private static final Array<String> EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        EXTENSIONS.add(FileExtensions.MODEL_BLENDER);
        EXTENSIONS.asUnsafe().trimToSize();
    }

    public static final FileConverterDescription DESCRIPTION = new FileConverterDescription();

    static {
        DESCRIPTION.setDescription(Messages.BLEND_TO_J3O_FILE_CONVERTER_DESCRIPTION);
        DESCRIPTION.setConstructor(BlendToJ3oFileConverter::new);
        DESCRIPTION.setExtensions(EXTENSIONS);
    }

    private BlendToJ3oFileConverter() {
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
