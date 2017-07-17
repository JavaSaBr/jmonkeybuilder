package com.ss.editor.ui.component.creator.impl;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.export.binary.BinaryExporter;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;

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

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.EMPTY_SCENE_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(EmptySceneCreator::new);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.EMPTY_SCENE_CREATOR_TITLE;
    }

    @NotNull
    @Override
    protected String getFileExtension() {
        return FileExtensions.JME_SCENE;
    }

    @Override
    protected void processOk() {
        super.processOk();

        final Path fileToCreate = notNull(getFileToCreate());

        final BinaryExporter exporter = BinaryExporter.getInstance();
        final SceneNode newNode = createScene();

        try (final OutputStream out = Files.newOutputStream(fileToCreate)) {
            exporter.save(newNode, out);
        } catch (final IOException e) {
            LOGGER.warning(this, e);
        }

        notifyFileCreated(fileToCreate, true);
    }

    /**
     * Create scene scene node.
     *
     * @return the scene node
     */
    @NotNull
    protected SceneNode createScene() {
        final SceneNode newNode = new SceneNode();
        newNode.addLayer(new SceneLayer("Default", true));
        newNode.addLayer(new SceneLayer("TransparentFX", true));
        newNode.addLayer(new SceneLayer("Ignore Raycast", true));
        newNode.addLayer(new SceneLayer("Water", true));
        newNode.getLayers().forEach(SceneLayer::show);
        return newNode;
    }
}
