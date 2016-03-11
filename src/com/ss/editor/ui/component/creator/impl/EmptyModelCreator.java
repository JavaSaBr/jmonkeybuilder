package com.ss.editor.ui.component.creator.impl;

import com.jme3.export.binary.BinaryExporter;
import com.jme3.scene.Node;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.ui.component.creator.FileCreatorDescription;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Реализация создателя новых пустых моделей.
 *
 * @author Ronn
 */
public class EmptyModelCreator extends AbstractFileCreator {

    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.EMPTY_MODEL_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(EmptyModelCreator::new);
    }

    public EmptyModelCreator() {
    }

    @Override
    protected String getTitleText() {
        return Messages.EMPTY_MODEL_CREATOR_TITLE;
    }

    @Override
    protected String getFileExtension() {
        return FileExtensions.JME_OBJECT;
    }

    @Override
    protected void processCreate() {
        super.processCreate();

        final Path fileToCreate = getFileToCreate();

        final BinaryExporter exporter = BinaryExporter.getInstance();
        final Node newNode = new Node("New node");

        try (final OutputStream out = Files.newOutputStream(fileToCreate)) {
            exporter.save(newNode, out);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        notifyFileCreated(fileToCreate, true);
    }
}
