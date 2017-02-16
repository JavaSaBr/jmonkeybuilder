package com.ss.editor.ui.component.editing.terrain;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.ss.editor.Editor;
import com.ss.editor.control.editing.impl.AbstractEditingControl;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The base implementation of terrain tool.
 *
 * @author JavaSaBr
 */
public class TerrainToolControl extends AbstractEditingControl {

    protected static final Editor EDITOR = Editor.getInstance();

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

    public TerrainToolControl(@NotNull final TerrainEditingComponent component) {
        this.component = component;

        final Material material = new Material(EDITOR.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        material.getAdditionalRenderState().setWireframe(true);
        material.setColor("Color", getBrushColor());

        this.brush = new Geometry("Brush", new Sphere(8, 8, 8));
        this.brush.setMaterial(material);
    }

    /**
     * @return the brush color.
     */
    @NotNull
    protected ColorRGBA getBrushColor() {
        return ColorRGBA.Black;
    }

    @Override
    public void setSpatial(final Spatial spatial) {

        final Spatial prev = getSpatial();
        if (prev instanceof Node) {
            ((Node) prev).detachChild(brush);
        }

        super.setSpatial(spatial);

        if (spatial instanceof Node) {
            ((Node) spatial).attachChild(brush);
        }
    }

    @Nullable
    @Override
    public Spatial getEditedModel() {
        return component.getEditedObject();
    }
}
