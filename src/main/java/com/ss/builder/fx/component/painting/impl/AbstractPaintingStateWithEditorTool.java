package com.ss.builder.ui.component.painting.impl;

import static java.lang.Math.abs;
import com.ss.builder.annotation.FromAnyThread;
import com.ss.builder.annotation.FxThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.annotation.FxThread;
import com.ss.editor.ui.component.editor.state.impl.AbstractEditorState;
import com.ss.editor.ui.component.editor.state.impl.AdditionalEditorState;

/**
 * The state of painting component.
 *
 * @author JavaSaBr
 */
public abstract class AbstractPaintingStateWithEditorTool extends AbstractEditorState implements AdditionalEditorState {

    /**
     * The constant serialVersionUID.
     */
    public static final long serialVersionUID = 1;

    /**
     * The brush size.
     */
    private volatile float brushSize;

    /**
     * The brush power.
     */
    private volatile float brushPower;

    public AbstractPaintingStateWithEditorTool() {
        this.brushSize = getDefaultBrushSize();
        this.brushPower = getDefaultBrushPower();
    }

    /**
     * Get the default brush power.
     *
     * @return the default brush power.
     */
    @FromAnyThread
    protected float getDefaultBrushPower() {
        return 1;
    }

    /**
     * Get the default brush size.
     *
     * @return the default brush size.
     */
    @FromAnyThread
    protected float getDefaultBrushSize() {
        return 10;
    }

    @FxThread
    public float getBrushSize() {
        return brushSize;
    }

    @FxThread
    public void setBrushSize(float brushSize) {
        var changed = abs(getBrushSize() - brushSize) > 0.001f;
        this.brushSize = brushSize;
        if (changed) notifyChange();
    }

    @FxThread
    public float getBrushPower() {
        return brushPower;
    }

    @FxThread
    public void setBrushPower(float brushPower) {
        var changed = abs(getBrushPower() - brushPower) > 0.001f;
        this.brushPower = brushPower;
        if (changed) notifyChange();
    }
}
