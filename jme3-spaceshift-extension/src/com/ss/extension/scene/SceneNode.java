package com.ss.extension.scene;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.scene.Node;

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

    /**
     * The scene layers.
     */
    @NotNull
    private final Array<SceneLayer> layers;

    public SceneNode() {
        super("Empty scene");
        this.layers = ArrayFactory.newArray(SceneLayer.class);
    }

    public void addLayer(@NotNull final SceneLayer layer) {
        layer.setSceneNode(this);
        this.layers.add(layer);
    }

    @NotNull
    public Array<SceneLayer> getLayers() {
        return layers;
    }

    @Override
    public void write(@NotNull final JmeExporter exporter) throws IOException {

        final OutputCapsule capsule = exporter.getCapsule(this);
        final SceneLayer[] layers = getLayers().toArray(SceneLayer.class);

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
    }
}
