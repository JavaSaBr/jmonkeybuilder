package com.ss.editor.ui.component.creator.impl;

import com.jme3.export.binary.BinaryExporter;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.extension.scene.SceneLayer;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The creator for creating an empty scene.
 *
 * @author JavaSaBr
 */
public class EmptySceneCreator extends AbstractFileCreator {

    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.EMPTY_SCENE_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(EmptySceneCreator::new);
    }

    public EmptySceneCreator() {
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.EMPTY_SCENE_CREATOR_TITLE;
    }

    @Override
    protected String getFileExtension() {
        return FileExtensions.JME_SCENE;
    }

    @Override
    protected void processCreate() {
        super.processCreate();

        final Path fileToCreate = getFileToCreate();

        final BinaryExporter exporter = BinaryExporter.getInstance();
        final SceneNode newNode = new SceneNode();
        newNode.addLayer(new SceneLayer("Default", true));
        newNode.addLayer(new SceneLayer("TransparentFX", true));
        newNode.addLayer(new SceneLayer("Ignore Raycast", true));
        newNode.addLayer(new SceneLayer("Water", true));
        newNode.getLayers().forEach(SceneLayer::show);

        try (final OutputStream out = Files.newOutputStream(fileToCreate)) {
            exporter.save(newNode, out);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        notifyFileCreated(fileToCreate, true);
    }
}
