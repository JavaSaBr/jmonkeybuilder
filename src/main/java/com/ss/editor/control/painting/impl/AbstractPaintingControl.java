package com.ss.editor.control.painting.impl;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.JmeThread;
import com.ss.editor.control.painting.PaintingControl;
import com.ss.editor.control.painting.PaintingInput;
import com.ss.editor.ui.component.painting.PaintingComponent;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.LocalObjects;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of {@link PaintingControl}.
 *
 * @author JavaSaBr
 */
public abstract class AbstractPaintingControl<T extends PaintingComponent> extends AbstractControl implements PaintingControl {

    @NotNull
    private static final ThreadLocal<LocalObjects> TOOL_OBJECTS = ThreadLocal.withInitial(LocalObjects::new);

    /**
     * The painting component.
     */
    @NotNull
    protected final T component;

    /**
     * The brush geometry.
     */
    @NotNull
    protected final Geometry brush;

    /**
     * The current painting input.
     */
    @Nullable
    private PaintingInput paintingInput;

    /**
     * The brush size.
     */
    private float brushSize;

    /**
     * The brush power.
     */
    private float brushPower;

    /**
     * The flag of painting state.
     */
    private boolean painting;

    protected AbstractPaintingControl(@NotNull final T component) {
        this.component = component;
        this.brush = new Geometry("Brush", new Sphere(8, 8, 1));
        this.brush.setMaterial(createWireframeMaterial(getBrushColor()));
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

    @Override
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

    @Override
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

    /**
     * Get the brush color.
     *
     * @return the brush color.
     */
    @FromAnyThread
    protected @NotNull ColorRGBA getBrushColor() {
        return ColorRGBA.Black;
    }

    /**
     * Get the local objects.
     *
     * @return the local objects.
     */
    @FromAnyThread
    protected @NotNull LocalObjects getLocalObjects() {
        return TOOL_OBJECTS.get();
    }

    @Override
    @JmeThread
    public void setSpatial(@Nullable final Spatial spatial) {

        final Spatial prev = getSpatial();
        if (prev instanceof Node) {
            onDetached((Node) prev);
        }

        super.setSpatial(spatial);

        if (spatial instanceof Node) {
            onAttached((Node) spatial);
        }
    }

    /**
     * Create a colored wireframe material.
     *
     * @param color the color.
     * @return the colored wireframe material.
     */
    @JmeThread
    protected @NotNull Material createWireframeMaterial(@NotNull final ColorRGBA color) {
        final Material material = createColoredMaterial(color);
        material.getAdditionalRenderState().setWireframe(true);
        return material;
    }

    /**
     * Create a colored material.
     *
     * @param color the color.
     * @return the colored material.
     */
    @JmeThread
    protected @NotNull Material createColoredMaterial(@NotNull final ColorRGBA color) {
        final Material material = new Material(EditorUtil.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", color);
        return material;
    }

    @Override
    @JmeThread
    protected void controlUpdate(final float tpf) {
    }

    @Override
    @JmeThread
    protected void controlRender(@NotNull final RenderManager renderManager, @NotNull final ViewPort viewPort) {
    }

    @Override
    @JmeThread
    public boolean isStartedPainting() {
        return painting;
    }

    /**
     * Set the painting state.
     *
     * @param painting the flag of painting state.
     */
    @JmeThread
    protected void setPainting(final boolean painting) {
        this.painting = painting;
    }

    @Override
    @JmeThread
    public void startPainting(@NotNull final PaintingInput input, @NotNull final Vector3f contactPoint) {
        setPainting(true);
        setPaintingInput(input);
    }

    @Override
    @JmeThread
    public void finishPainting(@NotNull final Vector3f contactPoint) {
        setPainting(false);
    }

    /**
     * Set the painting input,
     *
     * @param input the current painting input.
     */
    @JmeThread
    private void setPaintingInput(@Nullable final PaintingInput input) {
        this.paintingInput = input;
    }

    @Override
    @JmeThread
    public @Nullable PaintingInput getCurrentInput() {
        return paintingInput;
    }

    /**
     * Notify about that this control was attached.
     *
     * @param node the node.
     */
    @JmeThread
    protected void onAttached(@NotNull final Node node) {
        node.attachChild(brush);
    }

    /**
     * Notify about that this control was detached.
     *
     * @param node the node.
     */
    @JmeThread
    protected void onDetached(@NotNull final Node node) {
        node.detachChild(brush);
    }
}
