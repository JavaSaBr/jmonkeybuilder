package com.ss.editor.state.editor.impl.model;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.extension.scene.app.state.impl.bullet.EditableBulletSceneAppState;
import com.ss.editor.state.editor.Editor3DState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of editor 3D state to work with Bullet physics.
 *
 * @author JavaSaBr
 */
public class ModelEditorBulletState extends EditableBulletSceneAppState implements Editor3DState {

    @NotNull
    private final ModelEditor3DState editor3DState;

    public ModelEditorBulletState(@NotNull final ModelEditor3DState editor3DState) {
        this.editor3DState = editor3DState;
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application app) {
        super.initialize(stateManager, app);

        final Spatial currentModel = editor3DState.getCurrentModel();
        if (currentModel != null) {
            updateNode(currentModel, physicsSpace);
        }
    }

    @Override
    protected void rebuildState() {
        super.rebuildState();
        if (!isInitialized()) return;

        final Spatial currentModel = editor3DState.getCurrentModel();
        if (currentModel != null) {
            updateNode(currentModel, physicsSpace);
        }
    }

    /**
     * Update a spatial.
     *
     * @param spatial      the spatial.
     * @param physicsSpace the new physical space or null.
     */
    private void updateNode(@NotNull final Spatial spatial, @Nullable final PhysicsSpace physicsSpace) {
        spatial.depthFirstTraversal(spatial1 -> {

            final int numControls = spatial1.getNumControls();

            for (int i = 0; i < numControls; i++) {
                final Control control = spatial1.getControl(i);
                if (control instanceof PhysicsControl) {
                    ((PhysicsControl) control).setPhysicsSpace(physicsSpace);
                }
            }
        });
    }
}
