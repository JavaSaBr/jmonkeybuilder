package com.ss.extension.scene.app.state.impl;

import com.jme3.util.clone.Cloner;
import com.simsilica.fx.sky.SkyState;
import com.ss.extension.scene.SceneNode;

import org.jetbrains.annotations.NotNull;

/**
 * The editable wrapper of sky state.
 *
 * @author JavaSaBr
 */
public class EditableSkySceneAppState extends AbstractEditableWrappedSceneAppState<SkyState> {

    public EditableSkySceneAppState() {
    }

    @NotNull
    @Override
    protected SkyState createNewStateInstance() {
        return new SkyState();
    }

    @Override
    public void initFor(@NotNull final SceneNode sceneNode) {
        super.initFor(sceneNode);
        appState.setSkyParent(sceneNode);
    }

    @Override
    public void cleanupFor(@NotNull final SceneNode sceneNode) {
        super.cleanupFor(sceneNode);
        appState.setSkyParent(null);
    }

    @Override
    protected void cloneFields(@NotNull final SkyState originalAppState, @NotNull final SkyState newAppState,
                               @NotNull final Cloner cloner) {

        super.cloneFields(originalAppState, newAppState, cloner);

        newAppState.setEnabled(originalAppState.isEnabled());
        newAppState.setFlatShaded(originalAppState.isFlatShaded());
        newAppState.setShowGroundDisc(originalAppState.getShowGroundDisc());
        newAppState.setSkyParent(cloner.clone(originalAppState.getSkyParent()));
    }

    @NotNull
    @Override
    public String getName() {
        return "Sky";
    }
}
