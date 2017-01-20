package com.ss.extension.scene;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.scene.Node;
import com.jme3.util.clone.Cloner;
import com.ss.extension.scene.app.state.SceneAppState;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The implementation of a scene node.
 *
 * @author JavaSaBr
 */
public class SceneNode extends Node {

    public static final SceneLayer[] EMPTY_LAYERS = new SceneLayer[0];
    public static final SceneAppState[] EMPTY_STATES = new SceneAppState[0];

    /**
     * The scene layers.
     */
    @NotNull
    private Array<SceneLayer> layers;

    /**
     * The scene app states.
     */
    @NotNull
    private Array<SceneAppState> appStates;

    public SceneNode() {
        super("Empty scene");
        this.layers = ArrayFactory.newArray(SceneLayer.class);
        this.appStates = ArrayFactory.newArray(SceneAppState.class);
    }

    /**
     * Add a new layer.
     *
     * @param layer the layer.
     */
    public void addLayer(@NotNull final SceneLayer layer) {
        layer.setSceneNode(this);
        layers.add(layer);
    }

    /**
     * Remove an old layer.
     *
     * @param layer the layer.
     */
    public void removeLayer(@NotNull final SceneLayer layer) {
        if (layer.getSceneNode() == this) {
            layer.setSceneNode(null);
        }
        layers.slowRemove(layer);
    }

    /**
     * @return the scene layers.
     */
    @NotNull
    public Array<SceneLayer> getLayers() {
        return layers;
    }

    /**
     * Add a new scene app state.
     *
     * @param appState the scene app state.
     */
    public void addAppState(@NotNull final SceneAppState appState) {
        appStates.add(appState);
    }

    /**
     * Remove an old scene app state.
     *
     * @param appState the scene app state.
     */
    public void removeAppState(@NotNull final SceneAppState appState) {
        appStates.slowRemove(appState);
    }

    /**
     * @return the scene app states.
     */
    @NotNull
    public Array<SceneAppState> getAppStates() {
        return appStates;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {

        final OutputCapsule capsule = exporter.getCapsule(this);
        final SceneLayer[] layers = getLayers().toArray(SceneLayer.class);
        final SceneAppState[] appStates = getAppStates().toArray(SceneAppState.class);

        for (final SceneLayer layer : layers) {
            if (!layer.isShowed()) continue;
            detachChild(layer);
        }

        super.write(exporter);

        for (final SceneLayer layer : layers) {
            if (!layer.isShowed()) continue;
            attachChild(layer);
        }

        capsule.write(layers, "layers", EMPTY_LAYERS);
        capsule.write(appStates, "appStates", EMPTY_STATES);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {

        final InputCapsule capsule = importer.getCapsule(this);

        super.read(importer);

        final Savable[] importedLayers = capsule.readSavableArray("layers", EMPTY_LAYERS);

        for (final Savable savable : importedLayers) {
            final SceneLayer layer = (SceneLayer) savable;
            layer.setSceneNode(this);
            layers.add(layer);
            if (layer.isShowed()) attachChild(layer);
        }

        final Savable[] importedAppStates = capsule.readSavableArray("appStates", EMPTY_STATES);

        for (final Savable savable : importedAppStates) {
            appStates.add((SceneAppState) savable);
        }
    }

    @Override
    public void cloneFields(final Cloner cloner, final Object original) {
        super.cloneFields(cloner, original);

        layers = cloner.clone(layers);

        for (int i = 0; i < layers.size(); i++) {
            layers.set(i, cloner.clone(layers.get(i)));
        }

        appStates = cloner.clone(appStates);

        for (int i = 0; i < appStates.size(); i++) {
            appStates.set(i, cloner.clone(appStates.get(i)));
        }
    }
}
