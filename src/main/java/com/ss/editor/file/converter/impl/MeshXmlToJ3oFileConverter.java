package com.ss.editor.file.converter.impl;

import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.file.converter.FileConverterDescription;
import org.jetbrains.annotations.NotNull;
import com.ss.rlib.util.FileUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;

import java.nio.file.Path;

/**
 * The implementation of {@link AbstractFileConverter} to convert .mesh.xml file to .j3o.
 *
 * @author JavaSaBr
 */
public class MeshXmlToJ3oFileConverter extends AbstractModelFileConverter {

    @NotNull
    private static final Array<String> EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        EXTENSIONS.add(FileExtensions.MODEL_MESH_XML);
        EXTENSIONS.asUnsafe().trimToSize();
    }

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileConverterDescription DESCRIPTION = new FileConverterDescription();

    static {
        DESCRIPTION.setDescription(Messages.MESH_XML_TO_J3O_FILE_CONVERTER_DESCRIPTION);
        DESCRIPTION.setConstructor(MeshXmlToJ3oFileConverter::new);
        DESCRIPTION.setExtensions(EXTENSIONS);
    }

    private MeshXmlToJ3oFileConverter() {
    }

    @Override
    public void convert(@NotNull final Path source) {

        final String filenameWithoutXml = FileUtils.getNameWithoutExtension(source);
        final String targetFileName = FileUtils.getNameWithoutExtension(filenameWithoutXml) + "." + getTargetExtension();

        final Path parent = source.getParent();
        final Path targetFile = parent.resolve(targetFileName);

        convert(source, targetFile);
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
