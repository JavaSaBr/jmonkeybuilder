package com.ss.editor.part3d.editor.impl.model;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.extension.scene.app.state.impl.bullet.EditableBulletSceneAppState;
import com.ss.editor.part3d.editor.Editor3DPart;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of editor 3D state to work with Bullet physics.
 *
 * @author JavaSaBr
 */
public class ModelEditorBulletPart extends EditableBulletSceneAppState implements Editor3DPart {

    @NotNull
    private final ModelEditor3DPart editor3DPart;

    public ModelEditorBulletPart(@NotNull final ModelEditor3DPart editor3DPart) {
        this.editor3DPart = editor3DPart;
    }

    @Override
    public void initialize(@NotNull final AppStateManager stateManager, @NotNull final Application app) {
        super.initialize(stateManager, app);

        final Spatial currentModel = editor3DPart.getCurrentModel();
        if (currentModel != null) {
            updateNode(currentModel, physicsSpace);
        }
    }

    @Override
    protected void rebuildState() {
        super.rebuildState();

        if (!isInitialized()) {
            return;
        }

        final Spatial currentModel = editor3DPart.getCurrentModel();
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
    @JmeThread
    private void updateNode(@NotNull final Spatial spatial, @Nullable final PhysicsSpace physicsSpace) {
        spatial.depthFirstTraversal(sub -> {

            final int numControls = sub.getNumControls();

            for (int i = 0; i < numControls; i++) {
                final Control control = sub.getControl(i);
                if (control instanceof PhysicsControl) {
                    ((PhysicsControl) control).setPhysicsSpace(physicsSpace);
                }
            }
        });
    }
}
