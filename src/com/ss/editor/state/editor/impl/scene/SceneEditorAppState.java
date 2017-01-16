package com.ss.editor.state.editor.impl.scene;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.ui.component.editor.impl.scene.SceneFileEditor;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;

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
}
