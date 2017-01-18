package com.ss.editor.state.editor.impl.scene;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Grid;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.editor.impl.scene.SceneFileEditor;
import com.ss.extension.scene.SceneNode;
import com.ss.extension.scene.app.state.SceneAppState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of the {@link AbstractSceneEditorAppState} for the {@link SceneFileEditor}.
 *
 * @author JavaSaBr
 */
public class SceneEditorAppState extends AbstractSceneEditorAppState<SceneFileEditor, SceneNode> {

    public SceneEditorAppState(@NotNull final SceneFileEditor fileEditor) {
        super(fileEditor);

        final Node stateNode = getStateNode();
        stateNode.attachChild(getModelNode());
        stateNode.attachChild(getToolNode());
    }

    @Override
    public void notifyTransformed(@NotNull final Spatial spatial) {
        getFileEditor().notifyTransformed(spatial);
    }

    @NotNull
    @Override
    protected Geometry createGrid() {
        final Geometry grid = new Geometry("grid", new Grid(2000, 2000, 1.0f));
        grid.setMaterial(createColorMaterial(ColorRGBA.Gray));
        grid.setLocalTranslation(-1000, 0, -1000);
        return grid;
    }

    @Override
    protected void notifySelected(@Nullable final Object object) {
        getFileEditor().notifySelected(object);
    }

    @Override
    protected void undo() {
        final SceneFileEditor fileEditor = getFileEditor();
        fileEditor.undo();
    }

    @Override
    protected void redo() {
        final SceneFileEditor fileEditor = getFileEditor();
        fileEditor.redo();
    }

    @FromAnyThread
    public void notifyAddedAppState(@NotNull final SceneAppState appState) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> notifyAddedAppStateImpl(appState));
    }

    private void notifyAddedAppStateImpl(@NotNull final SceneAppState appState) {
        EDITOR.getStateManager().attach(appState);
    }
}
