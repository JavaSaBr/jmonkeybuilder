package com.ss.editor.file.converter.impl;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.file.converter.FileConverterDescription;

import org.jetbrains.annotations.NotNull;

import com.ss.rlib.common.util.array.Array;
import com.ss.rlib.common.util.array.ArrayFactory;

/**
 * The implementation of {@link AbstractFileConverter} to convert .xbuf file to .j3o.
 *
 * @author JavaSaBr
 */
public class XbufToJ3oFileConverter extends AbstractModelFileConverter {

    @NotNull
    private static final Array<String> EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        EXTENSIONS.add(FileExtensions.MODEL_XBUF);
        EXTENSIONS.asUnsafe().trimToSize();
    }

    /**
     * The description.
     */
    @NotNull
    public static final FileConverterDescription DESCRIPTION = new FileConverterDescription();

    static {
        DESCRIPTION.setDescription(Messages.XBUF_TO_J3O_FILE_CONVERTER_DESCRIPTION);
        DESCRIPTION.setConstructor(XbufToJ3oFileConverter::new);
        DESCRIPTION.setExtensions(EXTENSIONS);
    }

    private XbufToJ3oFileConverter() {
    }

    @Override
    protected @NotNull Array<String> getAvailableExtensions() {
        return EXTENSIONS;
    }

    @Override
    public @NotNull String getTargetExtension() {
        return FileExtensions.JME_OBJECT;
    }
}
