package com.ss.editor.control.painting.terrain;

import com.jme3.scene.Spatial;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.impl.AbstractPaintingControl;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.painting.terrain.TerrainPaintingComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of terrain tool.
 *
 * @author JavaSaBr
 */
public class TerrainToolControl extends AbstractPaintingControl<TerrainPaintingComponent> {

    public TerrainToolControl(@NotNull final TerrainPaintingComponent component) {
        super(component);
    }

    /**
     * Get the change consumer.
     *
     * @return the change consumer.
     */
    @FromAnyThread
    protected @NotNull ModelChangeConsumer getChangeConsumer() {
        return component.getChangeConsumer();
    }

    @Override
    @JmeThread
    public @Nullable Spatial getPaintedModel() {
        return component.getPaintedObject();
    }
}
