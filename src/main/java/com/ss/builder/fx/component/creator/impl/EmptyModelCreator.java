package com.ss.builder.fx.component.creator.impl;

import static java.nio.file.StandardOpenOption.*;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Node;
import com.ss.builder.FileExtensions;
import com.ss.builder.Messages;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.FileExtensions;
import com.ss.builder.Messages;
import com.ss.builder.annotation.BackgroundThread;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.fx.component.creator.FileCreatorDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The creator to create an empty model.
 *
 * @author JavaSaBr
 */
public class EmptyModelCreator extends AbstractFileCreator {

    public static final FileCreatorDescriptor DESCRIPTOR = new FileCreatorDescriptor(
            Messages.EMPTY_MODEL_CREATOR_DESCRIPTION,
            EmptyModelCreator::new
    );

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.EMPTY_MODEL_CREATOR_TITLE;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getFileExtension() {
        return FileExtensions.JME_OBJECT;
    }

    @Override
    @BackgroundThread
    protected void writeData(@NotNull Path resultFile) throws IOException {
        super.writeData(resultFile);

        var exporter = BinaryExporter.getInstance();
        var newNode = new Node("Model root");

        try (var out = Files.newOutputStream(resultFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            exporter.save(newNode, out);
        }
    }
}
