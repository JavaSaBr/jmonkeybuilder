package com.ss.editor.ui.component.painting.terrain;

import static java.lang.Math.abs;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.editor.state.impl.AbstractEditorState;
import com.ss.editor.ui.component.editor.state.impl.AdditionalEditorState;

/**
 * The state of terrain editing component.
 *
 * @author JavaSaBr
 */
public class TerrainPaintingStateWithEditorTool extends AbstractEditorState implements AdditionalEditorState {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 1;

    private volatile float brushSize;
    private volatile float brushPower;
    private volatile float levelValue;
    private volatile float roughtFrequency;
    private volatile float roughtLacunarity;
    private volatile float roughtOctaves;
    private volatile float roughtRoughness;
    private volatile float roughtScale;

    private volatile boolean levelUseMarker;
    private volatile boolean levelSmoothly;
    private volatile boolean slopeLimited;
    private volatile boolean slopeSmoothly;

    public TerrainPaintingStateWithEditorTool() {
        this.brushSize = 10;
        this.brushPower = 1;
        this.levelValue = 1;
        this.roughtFrequency = 0.2f;
        this.roughtLacunarity = 2.12f;
        this.roughtOctaves = 8;
        this.roughtRoughness = 1.2f;
        this.roughtScale = 1;
        this.levelUseMarker = false;
        this.levelSmoothly = true;
        this.slopeLimited = true;
        this.slopeSmoothly = true;
    }

    @FxThread
    public float getBrushSize() {
        return brushSize;
    }

    @FxThread
    public void setBrushSize(final float brushSize) {
        final boolean changed = abs(getBrushSize() - brushSize) > 0.001f;
        this.brushSize = brushSize;
        if (changed) notifyChange();
    }

    @FxThread
    public float getBrushPower() {
        return brushPower;
    }

    @FxThread
    public void setBrushPower(final float brushPower) {
        final boolean changed = abs(getBrushPower() - brushPower) > 0.001f;
        this.brushPower = brushPower;
        if (changed) notifyChange();
    }

    @FxThread
    public float getLevelValue() {
        return levelValue;
    }

    @FxThread
    public void setLevelValue(final float levelValue) {
        final boolean changed = abs(getLevelValue() - levelValue) > 0.001f;
        this.levelValue = levelValue;
        if (changed) notifyChange();
    }

    @FxThread
    public float getRoughtFrequency() {
        return roughtFrequency;
    }

    @FxThread
    public void setRoughtFrequency(final float roughtFrequency) {
        final boolean changed = abs(getRoughtFrequency() - roughtFrequency) > 0.001f;
        this.roughtFrequency = roughtFrequency;
        if (changed) notifyChange();
    }

    @FxThread
    public float getRoughtLacunarity() {
        return roughtLacunarity;
    }

    @FxThread
    public void setRoughtLacunarity(final float roughtLacunarity) {
        final boolean changed = abs(getRoughtLacunarity() - roughtLacunarity) > 0.001f;
        this.roughtLacunarity = roughtLacunarity;
        if (changed) notifyChange();
    }

    @FxThread
    public float getRoughtOctaves() {
        return roughtOctaves;
    }

    @FxThread
    public void setRoughtOctaves(final float roughtOctaves) {
        final boolean changed = abs(getRoughtOctaves() - roughtOctaves) > 0.001f;
        this.roughtOctaves = roughtOctaves;
        if (changed) notifyChange();
    }

    @FxThread
    public float getRoughtRoughness() {
        return roughtRoughness;
    }

    @FxThread
    public void setRoughtRoughness(final float roughtRoughness) {
        final boolean changed = abs(getRoughtRoughness() - roughtRoughness) > 0.001f;
        this.roughtRoughness = roughtRoughness;
        if (changed) notifyChange();
    }

    @FxThread
    public float getRoughtScale() {
        return roughtScale;
    }

    @FxThread
    public void setRoughtScale(final float roughtScale) {
        final boolean changed = abs(getRoughtScale() - roughtScale) > 0.001f;
        this.roughtScale = roughtScale;
        if (changed) notifyChange();
    }

    @FxThread
    public boolean isLevelUseMarker() {
        return levelUseMarker;
    }

    @FxThread
    public void setLevelUseMarker(final boolean levelUseMarker) {
        final boolean changed = isLevelUseMarker() != levelUseMarker;
        this.levelUseMarker = levelUseMarker;
        if (changed) notifyChange();
    }

    @FxThread
    public boolean isLevelSmoothly() {
        return levelSmoothly;
    }

    @FxThread
    public void setLevelSmoothly(final boolean levelSmoothly) {
        final boolean changed = isLevelSmoothly() != levelSmoothly;
        this.levelSmoothly = levelSmoothly;
        if (changed) notifyChange();
    }

    @FxThread
    public boolean isSlopeLimited() {
        return slopeLimited;
    }

    @FxThread
    public void setSlopeLimited(final boolean slopeLimited) {
        final boolean changed = isSlopeLimited() != slopeLimited;
        this.slopeLimited = slopeLimited;
        if (changed) notifyChange();
    }

    @FxThread
    public boolean isSlopeSmoothly() {
        return slopeSmoothly;
    }

    @FxThread
    public void setSlopeSmoothly(final boolean slopeSmoothly) {
        final boolean changed = isSlopeSmoothly() != slopeSmoothly;
        this.slopeSmoothly = slopeSmoothly;
        if (changed) notifyChange();
    }
}
