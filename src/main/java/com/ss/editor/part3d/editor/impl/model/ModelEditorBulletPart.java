package com.ss.editor.part3d.editor.impl.model;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.PhysicsControl;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.extension.scene.app.state.impl.bullet.EditableBulletSceneAppState;
import com.ss.editor.part3d.editor.Editor3dPart;
import com.ss.editor.part3d.editor.control.Editor3dPartControl;
import com.ss.editor.ui.component.editor.FileEditor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The implementation of editor 3D state to work with Bullet physics.
 *
 * @author JavaSaBr
 */
public class ModelEditorBulletPart extends EditableBulletSceneAppState implements Editor3dPart {

    @NotNull
    private final ModelEditor3dPart editor3dPart;

    public ModelEditorBulletPart(@NotNull ModelEditor3dPart editor3dPart) {
        this.editor3dPart = editor3dPart;
    }

    @Override
    public void initialize(@NotNull AppStateManager stateManager, @NotNull Application app) {
        super.initialize(stateManager, app);

        var currentModel = editor3dPart.getCurrentModel();

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

        var currentModel = editor3dPart.getCurrentModel();

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
    private void updateNode(@NotNull Spatial spatial, @Nullable PhysicsSpace physicsSpace) {
        spatial.depthFirstTraversal(sub -> {
            var numControls = sub.getNumControls();
            for (int i = 0; i < numControls; i++) {
                var control = sub.getControl(i);
                if (control instanceof PhysicsControl) {
                    ((PhysicsControl) control).setPhysicsSpace(physicsSpace);
                }
            }
        });
    }
}
