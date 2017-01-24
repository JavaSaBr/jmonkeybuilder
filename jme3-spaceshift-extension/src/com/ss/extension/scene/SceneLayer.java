package com.ss.extension.scene;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

/**
 * The implementation of a scene layer.
 *
 * @author JavaSaBr
 */
public class SceneLayer extends Node {

    public static final String KEY = SceneLayer.class.getName();

    /**
     * Get a layer of a spatial.
     *
     * @param spatial the spatial.
     * @return the layer or null.
     */
    @Nullable
    public static SceneLayer getLayer(@NotNull final Spatial spatial) {
        return spatial.getUserData(KEY);
    }

    /**
     * Set a layer to a spatial.
     *
     * @param layer the layer.
     * @param spatial the spatial.
     */
    public static void setLayer(@Nullable final SceneLayer layer, @NotNull final Spatial spatial) {
        spatial.setUserData(KEY, layer);
    }

    /**
     * The flag that layer is builtin.
     */
    private boolean builtIn;

    /**
     * The flag of showing this layer.
     */
    private boolean showed;

    public SceneLayer() {
        super("Empty layer");
    }

    public SceneLayer(@NotNull final String name, final boolean builtIn) {
        super(name);
        this.builtIn = builtIn;
    }

    /**
     * @return true if this layer is built in.
     */
    public boolean isBuiltIn() {
        return builtIn;
    }

    /**
     * @param builtIn true if this layer is built in.
     */
    public void setBuiltIn(final boolean builtIn) {
        this.builtIn = builtIn;
    }

    /**
     * @param showed the flag of showing this layer.
     */
    protected void setShowed(final boolean showed) {
        this.showed = showed;
    }

    /**
     * @return true if this layer is showed.
     */
    public boolean isShowed() {
        return showed;
    }

    /**
     * Hide this layer.
     */
    public void hide() {
        if (!isShowed()) return;
        setShowed(false);
    }

    /**
     * Show this layer.
     */
    public void show() {
        if (isShowed()) return;
        setShowed(true);
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {
        super.write(exporter);

        final OutputCapsule capsule = exporter.getCapsule(this);
        capsule.write(isShowed(), "showed", false);
        capsule.write(isBuiltIn(), "builtIn", false);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {
        super.read(importer);

        final InputCapsule capsule = importer.getCapsule(this);
        setShowed(capsule.readBoolean("showed", false));
        setBuiltIn(capsule.readBoolean("builtIn", false));
    }
}
