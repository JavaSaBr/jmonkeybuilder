package com.ss.builder.jme.control.painting.terrain;

import com.jme3.scene.Spatial;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.JmeThread;
import com.ss.builder.fx.component.painting.terrain.TerrainPaintingComponent;
import com.ss.builder.jme.control.painting.impl.AbstractPaintingControl;
import com.ss.builder.model.undo.editor.ModelChangeConsumer;
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
