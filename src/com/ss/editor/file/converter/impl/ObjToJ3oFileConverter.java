package com.ss.editor.file.converter.impl;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.file.converter.FileConverterDescription;

import org.jetbrains.annotations.NotNull;

import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

/**
 * The implementation of {@link AbstractFileConverter} to convert .obj file to .j3o.
 *
 * @author JavaSaBr
 */
public class ObjToJ3oFileConverter extends AbstractModelFileConverter {

    @NotNull
    private static final Array<String> EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        EXTENSIONS.add(FileExtensions.MODEL_OBJ);
        EXTENSIONS.asUnsafe().trimToSize();
    }

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileConverterDescription DESCRIPTION = new FileConverterDescription();

    static {
        DESCRIPTION.setDescription(Messages.OBJ_TO_J3O_FILE_CONVERTER_DESCRIPTION);
        DESCRIPTION.setConstructor(ObjToJ3oFileConverter::new);
        DESCRIPTION.setExtensions(EXTENSIONS);
    }

    private ObjToJ3oFileConverter() {
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
