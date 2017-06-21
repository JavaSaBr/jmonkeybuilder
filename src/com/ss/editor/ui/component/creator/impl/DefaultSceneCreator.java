package com.ss.editor.ui.component.creator.impl;

import com.jme3.renderer.queue.RenderQueue;
import com.ss.editor.Messages;
import com.ss.editor.extension.scene.SceneNode;
import com.ss.editor.extension.scene.app.state.impl.EditableLightingSceneAppState;
import com.ss.editor.extension.scene.app.state.impl.EditableSkySceneAppState;
import com.ss.editor.extension.scene.filter.impl.EditableFXAAFilter;
import com.ss.editor.extension.scene.filter.impl.EditableLightingStateShadowFilter;
import com.ss.editor.ui.component.creator.FileCreatorDescription;
import org.jetbrains.annotations.NotNull;

/**
 * The creator to create a default scene.
 *
 * @author JavaSaBr
 */
public class DefaultSceneCreator extends EmptySceneCreator {

    /**
     * The constant DESCRIPTION.
     */
    @NotNull
    public static final FileCreatorDescription DESCRIPTION = new FileCreatorDescription();

    static {
        DESCRIPTION.setFileDescription(Messages.DEFAULT_SCENE_CREATOR_DESCRIPTION);
        DESCRIPTION.setConstructor(DefaultSceneCreator::new);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.DEFAULT_SCENE_CREATOR_TITLE;
    }

    @NotNull
    @Override
    protected SceneNode createScene() {

        final SceneNode sceneNode = super.createScene();
        sceneNode.addFilter(new EditableFXAAFilter());
        sceneNode.addFilter(new EditableLightingStateShadowFilter());
        sceneNode.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);

        final EditableLightingSceneAppState lightingState = new EditableLightingSceneAppState();
        lightingState.setTimeOfDay(0.947F);

        final EditableSkySceneAppState skyState = new EditableSkySceneAppState();
        skyState.setFlatShaded(false);
        skyState.init(lightingState.getLightDirRef(), EDITOR.getAssetManager());

        sceneNode.addAppState(lightingState);
        sceneNode.addAppState(skyState);

        return sceneNode;
    }
}
