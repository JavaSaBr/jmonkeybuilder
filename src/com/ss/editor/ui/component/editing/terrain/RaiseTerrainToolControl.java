package com.ss.editor.ui.component.editing.terrain;

import com.jme3.math.Vector3f;
import org.jetbrains.annotations.NotNull;

/**
 * The raise implementation of terrain tool.
 *
 * @author JavaSaBr
 */
public class RaiseTerrainToolControl extends TerrainToolControl {

    public RaiseTerrainToolControl(@NotNull final TerrainEditingComponent component) {
        super(component);
    }

    @Override
    public void startEditing(@NotNull final Vector3f contactPoint) {
        super.startEditing(contactPoint);
        System.out.println("Started editing " + contactPoint);
    }

    @Override
    public void updateEditing(@NotNull final Vector3f contactPoint) {
        System.out.println("Updated editing" + contactPoint);
    }

    @Override
    public void finishEditing(@NotNull final Vector3f contactPoint) {
        super.finishEditing(contactPoint);
        System.out.println("Finished editing" + contactPoint);
    }
}
