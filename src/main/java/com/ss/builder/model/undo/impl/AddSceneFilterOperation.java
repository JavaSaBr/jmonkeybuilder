package com.ss.builder.model.undo.impl;

import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import com.ss.builder.annotation.FxThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.filter.SceneFilter;
import com.ss.builder.model.undo.editor.SceneChangeConsumer;
import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractEditorOperation} to add a new {@link SceneFilter} to a {@link SceneNode}.
 *
 * @author JavaSaBr.
 */
public class AddSceneFilterOperation extends AbstractEditorOperation<SceneChangeConsumer> {

    /**
     * The new filter.
     */
    @NotNull
    private final SceneFilter sceneFilter;

    /**
     * The scene node.
     */
    @NotNull
    private final SceneNode sceneNode;

    public AddSceneFilterOperation(@NotNull SceneFilter sceneFilter, @NotNull SceneNode sceneNode) {
        this.sceneFilter = sceneFilter;
        this.sceneNode = sceneNode;
    }

    @Override
    @JmeThread
    protected void redoInJme(@NotNull SceneChangeConsumer editor) {
        super.redoInJme(editor);
        sceneNode.addFilter(sceneFilter);
    }

    @Override
    @FxThread
    protected void endRedoInFx(@NotNull SceneChangeConsumer editor) {
        super.endRedoInFx(editor);
        editor.notifyAddedFilter(sceneFilter);
    }

    @Override
    @JmeThread
    protected void undoInJme(@NotNull SceneChangeConsumer editor) {
        super.undoInJme(editor);
        sceneNode.removeFilter(sceneFilter);
    }

    @Override
    @FxThread
    protected void endUndoInFx(@NotNull SceneChangeConsumer editor) {
        super.endUndoInFx(editor);
        editor.notifyRemovedFilter(sceneFilter);
    }
}
