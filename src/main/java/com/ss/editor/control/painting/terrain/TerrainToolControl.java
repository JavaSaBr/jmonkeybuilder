package com.ss.editor.control.painting.terrain;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.impl.AbstractPaintingControl;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.editing.terrain.TerrainEditingComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of terrain tool.
 *
 * @author JavaSaBr
 */
public class TerrainToolControl extends AbstractPaintingControl {

    /**
     * The editing component.
     */
    @NotNull
    protected final TerrainEditingComponent component;

    /**
     * The brush geometry.
     */
    @NotNull
    protected final Geometry brush;

    /**
     * The brush size.
     */
    private float brushSize;

    /**
     * The brush power.
     */
    private float brushPower;

    public TerrainToolControl(@NotNull final TerrainEditingComponent component) {
        this.component = component;
        this.brush = new Geometry("Brush", new Sphere(8, 8, 1));
        this.brush.setMaterial(createWireframeMaterial(getBrushColor()));
    }

    /**
     * Get the brush color.
     *
     * @return the brush color.
     */
    @FromAnyThread
    protected @NotNull ColorRGBA getBrushColor() {
        return ColorRGBA.Black;
    }

    @Override
    @JmeThread
    protected void onAttached(@NotNull final Node node) {
        super.onAttached(node);
        node.attachChild(brush);
    }

    @Override
    @JmeThread
    protected void onDetached(@NotNull final Node node) {
        super.onDetached(node);
        node.detachChild(brush);
    }

    @Override
    @JmeThread
    public @Nullable Spatial getPaintedModel() {
        return component.getProcessedObject();
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

    /**
     * Get the brush.
     *
     * @return the brush geometry.
     */
    @JmeThread
    protected @NotNull Geometry getBrush() {
        return brush;
    }

    /**
     * Set the brush size.
     *
     * @param brushSize the brush size.
     */
    @JmeThread
    public void setBrushSize(final float brushSize) {
        this.brushSize = brushSize;
        getBrush().setLocalScale(brushSize);
    }

    /**
     * Get the brush size.
     *
     * @return the brush size.
     */
    @JmeThread
    public float getBrushSize() {
        return brushSize;
    }

    /**
     * Set the brush power.
     *
     * @param brushPower the brush power.
     */
    @JmeThread
    public void setBrushPower(final float brushPower) {
        this.brushPower = brushPower;
    }

    /**
     * Get the brush power.
     *
     * @return the brush power.
     */
    @JmeThread
    public float getBrushPower() {
        return brushPower;
    }
}
