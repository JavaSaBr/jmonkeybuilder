package com.ss.editor.ui.component.creator.impl;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Node;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The creator to create an empty model.
 *
 * @author JavaSaBr
 */
public class EmptyModelCreator extends AbstractFileCreator {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.EMPTY_MODEL_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(EmptyModelCreator::new);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.EMPTY_MODEL_CREATOR_TITLE;
    }

    @NotNull
    @Override
    protected String getFileExtension() {
        return FileExtensions.JME_OBJECT;
    }

    @Override
    @BackgroundThread
    protected void writeData(@NotNull final Path resultFile) throws IOException {
        super.writeData(resultFile);

        final BinaryExporter exporter = BinaryExporter.getInstance();
        final Node newNode = new Node("Model root");

        try (final OutputStream out = Files.newOutputStream(resultFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            exporter.save(newNode, out);
        }
    }
}
