package com.ss.editor.ui.component.creator.impl;

import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.nio.file.StandardOpenOption.WRITE;
import com.jme3.export.binary.BinaryExporter;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
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

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.EMPTY_SCENE_CREATOR_TITLE;
    }

    @Override
    @FromAnyThread
    protected @NotNull String getFileExtension() {
        return FileExtensions.JME_SCENE;
    }

    @Override
    @BackgroundThread
    protected void writeData(@NotNull final Path resultFile) throws IOException {
        super.writeData(resultFile);

        final BinaryExporter exporter = BinaryExporter.getInstance();
        final SceneNode newNode = createScene();

        try (final OutputStream out = Files.newOutputStream(resultFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            exporter.save(newNode, out);
        }
    }

    /**
     * Create scene scene node.
     *
     * @return the scene node
     */
    @FXThread
    protected @NotNull SceneNode createScene() {
        final SceneNode newNode = new SceneNode();
        newNode.addLayer(new SceneLayer("Default", true));
        newNode.addLayer(new SceneLayer("TransparentFX", true));
        newNode.addLayer(new SceneLayer("Ignore Raycast", true));
        newNode.addLayer(new SceneLayer("Water", true));
        newNode.getLayers().forEach(SceneLayer::show);
        return newNode;
    }
}
