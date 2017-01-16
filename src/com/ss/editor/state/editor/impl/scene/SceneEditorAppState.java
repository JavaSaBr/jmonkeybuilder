package com.ss.editor.state.editor.impl.scene;

import com.jme3.scene.Spatial;
import com.ss.editor.state.editor.impl.AbstractSceneEditorAppState;
import com.ss.editor.ui.component.editor.impl.scene.SceneFileEditor;

import org.jetbrains.annotations.NotNull;

/**
 * The implementation of the {@link AbstractSceneEditorAppState} for the {@link SceneFileEditor}.
 *
 * @author JavaSaBr
 */
public class SceneEditorAppState extends AbstractSceneEditorAppState<SceneFileEditor> {

    public SceneEditorAppState(@NotNull final SceneFileEditor fileEditor) {
        super(fileEditor);
    }
    

    @Override
    public void notifyTransformed(@NotNull final Spatial spatial) {

    }
}
