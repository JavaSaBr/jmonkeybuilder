package com.ss.editor.ui.component.editing.terrain.control;

import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.control.editing.impl.AbstractEditingControl;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.component.editing.terrain.TerrainEditingComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of terrain tool.
 *
 * @author JavaSaBr
 */
public class TerrainToolControl extends AbstractEditingControl {

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

    /**
     * Instantiates a new Terrain tool control.
     *
     * @param component the component
     */
    public TerrainToolControl(@NotNull final TerrainEditingComponent component) {
        this.component = component;
        this.brush = new Geometry("Brush", new Sphere(8, 8, 1));
        this.brush.setMaterial(createWireframeMaterial(getBrushColor()));
    }

    /**
     * Gets brush color.
     *
     * @return the brush color.
     */
    @NotNull
    protected ColorRGBA getBrushColor() {
        return ColorRGBA.Black;
    }

    @Override
    protected void onAttached(@NotNull final Node node) {
        super.onAttached(node);
        node.attachChild(brush);
    }

    @Override
    protected void onDetached(@NotNull final Node node) {
        super.onDetached(node);
        node.detachChild(brush);
    }

    @Nullable
    @Override
    public Spatial getEditedModel() {
        return component.getProcessedObject();
    }

    /**
     * Gets change consumer.
     *
     * @return the change consumer.
     */
    @NotNull
    protected ModelChangeConsumer getChangeConsumer() {
        return component.getChangeConsumer();
    }

    /**
     * Gets brush.
     *
     * @return the brush geometry.
     */
    @NotNull
    protected Geometry getBrush() {
        return brush;
    }

    /**
     * Sets brush size.
     *
     * @param brushSize the brush size.
     */
    public void setBrushSize(final float brushSize) {
        this.brushSize = brushSize;
        getBrush().setLocalScale(brushSize);
    }

    /**
     * Gets brush size.
     *
     * @return the brush size.
     */
    public float getBrushSize() {
        return brushSize;
    }

    /**
     * Sets brush power.
     *
     * @param brushPower the brush power.
     */
    public void setBrushPower(final float brushPower) {
        this.brushPower = brushPower;
    }

    /**
     * Gets brush power.
     *
     * @return the brush power.
     */
    public float getBrushPower() {
        return brushPower;
    }
}
