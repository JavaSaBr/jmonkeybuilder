package com.ss.extension.scene;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.scene.Node;
import com.jme3.util.clone.Cloner;
import com.ss.extension.scene.app.state.SceneAppState;
import com.ss.extension.scene.filter.SceneFilter;

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
    public static final SceneFilter<?>[] EMPTY_FILTERS = new SceneFilter[0];

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

    /**
     * The scene filters.
     */
    @NotNull
    private Array<SceneFilter<?>> filters;

    public SceneNode() {
        super("Empty scene");
        this.layers = ArrayFactory.newArray(SceneLayer.class);
        this.appStates = ArrayFactory.newArray(SceneAppState.class);
        this.filters = ArrayFactory.newArray(SceneFilter.class);
    }

    /**
     * Add a new layer.
     *
     * @param layer the layer.
     */
    public void addLayer(@NotNull final SceneLayer layer) {
        layers.add(layer);
    }

    /**
     * Remove an old layer.
     *
     * @param layer the layer.
     */
    public void removeLayer(@NotNull final SceneLayer layer) {
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

    /**
     * Add a new filter.
     *
     * @param filter the scene filter.
     */
    public void addFilter(@NotNull final SceneFilter filter) {
        filters.add(filter);
    }

    /**
     * Remove an old filter.
     *
     * @param filter the scene filter.
     */
    public void removeFilter(@NotNull final SceneFilter filter) {
        filters.slowRemove(filter);
    }

    /**
     * Get a list of filters.
     *
     * @return the list of filters.
     */
    @NotNull
    public Array<SceneFilter<?>> getFilters() {
        return filters;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {

        final OutputCapsule capsule = exporter.getCapsule(this);
        final SceneLayer[] layers = getLayers().toArray(SceneLayer.class);
        final SceneAppState[] appStates = getAppStates().toArray(SceneAppState.class);
        final SceneFilter<?>[] filters = getFilters().toArray(SceneFilter.class);

        capsule.write(layers, "layers", EMPTY_LAYERS);

        super.write(exporter);

        capsule.write(appStates, "appStates", EMPTY_STATES);
        capsule.write(filters, "filters", EMPTY_FILTERS);
    }

    @Override
    public void read(@NotNull final JmeImporter importer) throws IOException {

        final InputCapsule capsule = importer.getCapsule(this);

        final Savable[] importedLayers = capsule.readSavableArray("layers", EMPTY_LAYERS);

        for (final Savable savable : importedLayers) {
            layers.add((SceneLayer) savable);
        }

        super.read(importer);

        final Savable[] importedAppStates = capsule.readSavableArray("appStates", EMPTY_STATES);

        for (final Savable savable : importedAppStates) {
            appStates.add((SceneAppState) savable);
        }

        final Savable[] importedFilters = capsule.readSavableArray("filters", EMPTY_FILTERS);

        for (final Savable savable : importedFilters) {
            filters.add((SceneFilter) savable);
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

        filters = cloner.clone(filters);

        for (int i = 0; i < filters.size(); i++) {
            filters.set(i, cloner.clone(filters.get(i)));
        }
    }
}
