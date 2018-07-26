package com.ss.editor.ui.component.creator.impl;

import static java.nio.file.StandardOpenOption.*;
import com.jme3.export.binary.BinaryExporter;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.extension.scene.SceneLayer;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.plugin.api.file.creator.GenericFileCreator;
import com.ss.editor.ui.component.creator.FileCreatorDescriptor;
import com.ss.rlib.common.util.VarTable;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The creator to create an empty scene.
 *
 * @author JavaSaBr
 */
public class EmptySceneCreator extends GenericFileCreator {

    public static final FileCreatorDescriptor DESCRIPTOR = new FileCreatorDescriptor(
            Messages.EMPTY_SCENE_CREATOR_DESCRIPTION,
            EmptySceneCreator::new
    );

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
    protected void writeData(@NotNull VarTable vars, @NotNull Path resultFile) throws IOException {
        super.writeData(vars, resultFile);

        var exporter = BinaryExporter.getInstance();
        var newNode = createScene();

        try (var out = Files.newOutputStream(resultFile, WRITE, TRUNCATE_EXISTING, CREATE)) {
            exporter.save(newNode, out);
        }
    }

    /**
     * Create a new scene node.
     *
     * @return the new scene node
     */
    @FxThread
    protected @NotNull SceneNode createScene() {

        var newNode = new SceneNode();
        newNode.addLayer(new SceneLayer("Default", true));
        newNode.addLayer(new SceneLayer("TransparentFX", true));
        newNode.addLayer(new SceneLayer("Ignore Raycast", true));
        newNode.addLayer(new SceneLayer("Water", true));
        newNode.getLayers().forEach(SceneLayer::show);

        return newNode;
    }
}
