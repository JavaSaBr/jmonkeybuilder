package com.ss.editor.ui.component.editing.terrain.control;

import static com.ss.editor.util.EditingUtils.isContains;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Objects.requireNonNull;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.noise.Basis;
import com.jme3.terrain.noise.ShaderUtils;
import com.jme3.terrain.noise.basis.FilteredBasis;
import com.jme3.terrain.noise.filter.IterativeFilter;
import com.jme3.terrain.noise.filter.OptimizedErode;
import com.jme3.terrain.noise.filter.PerturbFilter;
import com.jme3.terrain.noise.filter.SmoothFilter;
import com.jme3.terrain.noise.fractal.FractalSum;
import com.jme3.terrain.noise.modulator.NoiseModulator;
import com.ss.editor.control.editing.EditingInput;
import com.ss.editor.ui.component.editing.terrain.TerrainEditingComponent;
import com.ss.editor.util.LocalObjects;
import org.jetbrains.annotations.NotNull;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * The implementation of terrain tool to make rough heights.
 *
 * @author JavaSaBr
 */
public class RoughTerrainToolControl extends ChangeHeightTerrainToolControl {

    private float roughness;
    private float frequency;
    private float lacunarity;
    private float octaves;
    private float scale;

    /**
     * Instantiates a new Rough terrain tool control.
     *
     * @param component the component
     */
    public RoughTerrainToolControl(@NotNull final TerrainEditingComponent component) {
        super(component);
    }

    @NotNull
    @Override
    protected ColorRGBA getBrushColor() {
        return ColorRGBA.Magenta;
    }

    @Override
    public void startEditing(@NotNull final EditingInput editingInput, @NotNull final Vector3f contactPoint) {
        super.startEditing(editingInput, contactPoint);

        switch (editingInput) {
            case MOUSE_PRIMARY: {
                startChange();
                modifyHeight(contactPoint);
            }
        }
    }

    @Override
    public void updateEditing(@NotNull final Vector3f contactPoint) {

        final EditingInput editingInput = requireNonNull(getCurrentInput());

        switch (editingInput) {
            case MOUSE_PRIMARY: {
                modifyHeight(contactPoint);
            }
        }
    }

    @Override
    public void finishEditing(@NotNull final Vector3f contactPoint) {
        super.finishEditing(contactPoint);

        final EditingInput editingInput = requireNonNull(getCurrentInput());

        switch (editingInput) {
            case MOUSE_PRIMARY: {
                modifyHeight(contactPoint);
                commitChanges();
            }
        }
    }

    /**
     * Modify height of terrain points.
     *
     * @param contactPoint the contact point.
     */
    private void modifyHeight(@NotNull final Vector3f contactPoint) {

        final LocalObjects local = LocalObjects.get();
        final Node terrainNode = (Node) requireNonNull(getEditedModel());

        final Vector3f worldTranslation = terrainNode.getWorldTranslation();
        final Vector3f localScale = terrainNode.getLocalScale();
        final Vector3f localPoint = contactPoint.subtract(worldTranslation, local.nextVector());
        final Vector2f terrainLoc = local.nextVector2f();
        final Vector2f effectPoint = local.nextVector2f();

        final Terrain terrain = (Terrain) terrainNode;
        final Geometry brush = getBrush();

        final float brushSize = getBrushSize();
        final int twoBrushSize = (int) (brushSize * 2);

        final Basis fractalFilter = createFractalGenerator();
        final FloatBuffer buffer = fractalFilter.getBuffer(terrainLoc.getX(), terrainLoc.getY(), 0, twoBrushSize);

        final int radiusStepsX = (int) (brushSize / localScale.getX());
        final int radiusStepsZ = (int) (brushSize / localScale.getY());

        final float xStepAmount = localScale.getX();
        final float zStepAmount = localScale.getZ();

        final List<Vector2f> locs = new ArrayList<>();
        final List<Float> heights = new ArrayList<>();

        for (int z = -radiusStepsZ, yfb = 0; z < radiusStepsZ; z++, yfb++) {
            for (int x = -radiusStepsX, xfb = 0; x < radiusStepsX; x++, xfb++) {

                final float locX = localPoint.getX() + (x * xStepAmount);
                final float locZ = localPoint.getZ() + (z * zStepAmount);

                effectPoint.set(locX - localPoint.getX(), locZ - localPoint.getZ());

                if (!isContains(brush, effectPoint.getX(), effectPoint.getX())) {
                    continue;
                }

                final float height = buffer.get(yfb * twoBrushSize + xfb);

                terrainLoc.set(locX, locZ);

                final float currentHeight = terrain.getHeightmapHeight(terrainLoc) * localScale.getY();
                // see if it is in the radius of the tool
                final float newHeight = calculateHeight(brushSize, height, effectPoint);

                locs.add(terrainLoc.clone());
                heights.add(currentHeight + newHeight);
            }
        }

        locs.forEach(this::change);

        // do the actual height adjustment
        terrain.setHeight(locs, heights);
        terrainNode.updateModelBound(); // or else we won't collide with it where we just edited
    }

    private float calculateHeight(final float radius, final float heightFactor, @NotNull final Vector2f point) {

        // find percentage for each 'unit' in radius

        float val = point.length() / radius;
        val = 1 - val;

        if (val <= 0) val = 0;

        return heightFactor * val * 0.1f; // 0.1 scales it down a bit to lower the impact of the tool
    }

    private Basis createFractalGenerator() {

        final FractalSum fractalSum = new FractalSum();
        fractalSum.setRoughness(getRoughness());
        fractalSum.setFrequency(getFrequency());
        fractalSum.setAmplitude(getBrushPower());
        fractalSum.setLacunarity(getLacunarity()); // make it greater than 1.0f
        fractalSum.setOctaves(getOctaves());
        fractalSum.setScale(max(min(getScale(), 1.0F), 0F)); //0.02125f
        fractalSum.addModulator((NoiseModulator) in -> ShaderUtils.clamp(in[0] * 0.5f + 0.5f, 0, 1));

        final FilteredBasis ground = new FilteredBasis(fractalSum);

        final PerturbFilter perturb = new PerturbFilter();
        perturb.setMagnitude(0.2f);//0.119 the higher, the slower it is

        final OptimizedErode therm = new OptimizedErode();
        therm.setRadius(5);
        therm.setTalus(0.011f);

        final SmoothFilter smooth = new SmoothFilter();
        smooth.setRadius(1);
        smooth.setEffect(0.1f); // 0.7

        final IterativeFilter iterate = new IterativeFilter();
        iterate.addPreFilter(perturb);
        iterate.addPostFilter(smooth);
        iterate.setFilter(therm);
        iterate.setIterations(1);

        ground.addPreFilter(iterate);

        return ground;
    }

    /**
     * Sets octaves.
     *
     * @param octaves the octaves
     */
    public void setOctaves(final float octaves) {
        this.octaves = octaves;
    }

    /**
     * Gets octaves.
     *
     * @return the octaves
     */
    public float getOctaves() {
        return octaves;
    }

    /**
     * Sets frequency.
     *
     * @param frequency the frequency
     */
    public void setFrequency(final float frequency) {
        this.frequency = frequency;
    }

    /**
     * Gets frequency.
     *
     * @return the frequency
     */
    public float getFrequency() {
        return frequency;
    }

    /**
     * Sets lacunarity.
     *
     * @param lacunarity the lacunarity
     */
    public void setLacunarity(final float lacunarity) {
        this.lacunarity = lacunarity;
    }

    /**
     * Gets lacunarity.
     *
     * @return the lacunarity
     */
    public float getLacunarity() {
        return lacunarity;
    }

    /**
     * Sets roughness.
     *
     * @param roughness the roughness
     */
    public void setRoughness(final float roughness) {
        this.roughness = roughness;
    }

    /**
     * Gets roughness.
     *
     * @return the roughness
     */
    public float getRoughness() {
        return roughness;
    }

    /**
     * Sets scale.
     *
     * @param scale the scale
     */
    public void setScale(final float scale) {
        this.scale = scale;
    }

    /**
     * Gets scale.
     *
     * @return the scale
     */
    public float getScale() {
        return scale;
    }
}
