package com.ss.editor.ui.component.editing.terrain.paint;

import static java.util.Objects.requireNonNull;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.terrain.Terrain;
import com.jme3.texture.Texture;
import com.ss.editor.annotation.EditorThread;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.ui.component.editing.terrain.TerrainEditingComponent;
import com.ss.editor.ui.css.CSSClasses;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.util.FXUtils;

import java.util.function.Function;

/**
 * The implementation of texture layer settings.
 *
 * @author JavaSaBr
 */
public class TextureLayerSettings extends VBox {

    @NotNull
    private final TerrainEditingComponent editingComponent;

    /**
     * The function to convert layer index to diffuse texture param name.
     */
    @Nullable
    private Function<Integer, String> layerToDiffuseName;

    /**
     * The function to convert layer index to normal texture param name.
     */
    @Nullable
    private Function<Integer, String> layerToNormalName;

    /**
     * The function to convert layer index to texture scale param name.
     */
    @Nullable
    private Function<Integer, String> layerToScaleName;

    /**
     * The function to convert layer index to alpha texture param name.
     */
    @Nullable
    private Function<Integer, String> layerToAlphaName;

    /**
     * The list of layers.
     */
    @Nullable
    private ListView<TextureLayer> listView;

    /**
     * The max count of texture levels.
     */
    private int maxLevels;

    public TextureLayerSettings(@NotNull final TerrainEditingComponent editingComponent) {
        this.editingComponent = editingComponent;
        createComponents();
    }

    private void createComponents() {

        this.listView = new ListView<>();
        this.listView.setCellFactory(param -> new TextureLayerCell(widthProperty(), widthProperty()));
        this.listView.setEditable(false);

        FXUtils.addToPane(listView, this);
        FXUtils.addClassTo(listView, CSSClasses.TRANSPARENT_LIST_VIEW);
    }

    /**
     * @param layerToAlphaName the function to convert layer index to alpha texture param name.
     */
    @FXThread
    public void setLayerToAlphaName(@NotNull final Function<Integer, String> layerToAlphaName) {
        this.layerToAlphaName = layerToAlphaName;
    }

    /**
     * @return the function to convert layer index to alpha texture param name.
     */
    @Nullable
    @FromAnyThread
    private Function<Integer, String> getLayerToAlphaName() {
        return layerToAlphaName;
    }

    /**
     * @param layerToDiffuseName the function to convert layer index to diffuse texture param name.
     */
    @FXThread
    public void setLayerToDiffuseName(@NotNull final Function<Integer, String> layerToDiffuseName) {
        this.layerToDiffuseName = layerToDiffuseName;
    }

    /**
     * @return the function to convert layer index to diffuse texture param name.
     */
    @Nullable
    @FromAnyThread
    private Function<Integer, String> getLayerToDiffuseName() {
        return layerToDiffuseName;
    }

    /**
     * @param layerToNormalName the function to convert layer index to normal texture param name.
     */
    @FXThread
    public void setLayerToNormalName(@NotNull final Function<Integer, String> layerToNormalName) {
        this.layerToNormalName = layerToNormalName;
    }

    /**
     * @return the function to convert layer index to normal texture param name.
     */
    @Nullable
    @FromAnyThread
    private Function<Integer, String> getLayerToNormalName() {
        return layerToNormalName;
    }

    /**
     * @param layerToScaleName the function to convert layer index to texture scale param name.
     */
    @FXThread
    public void setLayerToScaleName(@NotNull final Function<Integer, String> layerToScaleName) {
        this.layerToScaleName = layerToScaleName;
    }

    /**
     * @return the function to convert layer index to texture scale param name.
     */
    @Nullable
    @FromAnyThread
    private Function<Integer, String> getLayerToScaleName() {
        return layerToScaleName;
    }

    /**
     * @return the list of layers.
     */
    @NotNull
    private ListView<TextureLayer> getListView() {
        return requireNonNull(listView);
    }

    /**
     * Refresh the layers list.
     */
    @FXThread
    public void refresh() {

        final ListView<TextureLayer> listView = getListView();
        final MultipleSelectionModel<TextureLayer> selectionModel = listView.getSelectionModel();
        final TextureLayer selectedItem = selectionModel.getSelectedItem();

        final ObservableList<TextureLayer> items = listView.getItems();
        items.clear();

        final int maxLevels = getMaxLevels() - 1;

        for (int i = 0; i < maxLevels; i++) {

            final float scale = getTextureScale(i);
            if (scale == -1F) continue;

            items.add(new TextureLayer(this, i));
        }

        if (items.contains(selectedItem)) {
            selectionModel.select(selectedItem);
        }
    }

    /**
     * Get current edited terrain.
     *
     * @return the edited terrain.
     */
    @NotNull
    @FXThread
    private Terrain getTerrain() {
        return editingComponent.getEditedObject();
    }

    /**
     * Get a diffuse texture of the level.
     *
     * @param layer the layer.
     * @return the diffuse texture or null.
     */
    @Nullable
    @FromAnyThread
    public Texture getDiffuse(final int layer) {

        final Function<Integer, String> layerToDiffuseName = getLayerToDiffuseName();
        if (layerToDiffuseName == null) return null;

        final Terrain terrain = getTerrain();
        final Material material = terrain.getMaterial();
        final MatParam matParam = material.getParam(layerToDiffuseName.apply(layer));

        if (matParam == null || matParam.getValue() == null) {
            return null;
        }

        return (Texture) matParam.getValue();
    }

    /**
     * Set a new diffuse texture to a level.
     *
     * @param texture the new texture.
     * @param layer   the layer.
     */
    @EditorThread
    public void setDiffuse(@Nullable final Texture texture, final int layer) {

        final Function<Integer, String> layerToDiffuseName = getLayerToDiffuseName();
        if (layerToDiffuseName == null) return;

        if (texture != null) {
            texture.setWrap(Texture.WrapMode.Repeat);
        }

        final Terrain terrain = getTerrain();
        final Material material = terrain.getMaterial();
        final MatParam matParam = material.getParam(layerToDiffuseName.apply(layer));
        if (matParam == null) return;

        if (texture == null) {
            material.clearParam(matParam.getName());
        } else {
            matParam.setValue(texture);
        }
    }

    /**
     * Get a diffuse normal of the level.
     *
     * @param layer the layer.
     * @return the normal texture or null.
     */
    @Nullable
    @FromAnyThread
    public Texture getNormal(final int layer) {

        final Function<Integer, String> layerToNormalName = getLayerToNormalName();
        if (layerToNormalName == null) return null;

        final Terrain terrain = getTerrain();
        final Material material = terrain.getMaterial();
        final MatParam matParam = material.getParam(layerToNormalName.apply(layer));

        if (matParam == null || matParam.getValue() == null) {
            return null;
        }

        return (Texture) matParam.getValue();
    }

    /**
     * Set a new normal texture to a level.
     *
     * @param texture the normal texture.
     * @param layer   the layer.
     */
    @EditorThread
    public void setNormal(@Nullable final Texture texture, final int layer) {

        final Function<Integer, String> layerToNormalName = getLayerToNormalName();
        if (layerToNormalName == null) return;

        if (texture != null) {
            texture.setWrap(Texture.WrapMode.Repeat);
        }

        final Terrain terrain = getTerrain();
        final Material material = terrain.getMaterial();
        final MatParam matParam = material.getParam(layerToNormalName.apply(layer));
        if (matParam == null) return;

        if (texture == null) {
            material.clearParam(matParam.getName());
        } else {
            matParam.setValue(texture);
        }
    }

    /**
     * Get a texture scale of the level.
     *
     * @param layer the layer.
     * @return the texture scale or -1.
     */
    @FromAnyThread
    public float getTextureScale(final int layer) {

        final Function<Integer, String> layerToScaleName = getLayerToScaleName();
        if (layerToScaleName == null) return -1F;

        final Terrain terrain = getTerrain();
        final Material material = terrain.getMaterial();
        final MatParam matParam = material.getParam(layerToScaleName.apply(layer));
        return matParam == null ? -1F : (float) matParam.getValue();
    }

    /**
     * Set a new texture scale to a level.
     *
     * @param scale the texture scale.
     * @param layer the layer.
     */
    @EditorThread
    public void setTextureScale(final float scale, final int layer) {

        final Function<Integer, String> layerToScaleName = getLayerToScaleName();
        if (layerToScaleName == null) return;

        final Terrain terrain = getTerrain();
        final Material material = terrain.getMaterial();
        final MatParam matParam = material.getParam(layerToScaleName.apply(layer));
        if (matParam == null) return;

        matParam.setValue(scale);
    }

    /**
     * Get a alpha texture of the level.
     *
     * @param layer the layer.
     * @return the alpha texture or null.
     */
    @Nullable
    @FromAnyThread
    public Texture getAlpha(final int layer) {

        final Function<Integer, String> layerToAlphaName = getLayerToAlphaName();
        if (layerToAlphaName == null) return null;

        final Terrain terrain = getTerrain();
        final Material material = terrain.getMaterial();
        final MatParam matParam = material.getParam(layerToAlphaName.apply(layer));

        if (matParam == null || matParam.getValue() == null) {
            return null;
        }

        return (Texture) matParam.getValue();
    }

    /**
     * @param maxLevels the max count of texture levels.
     */
    @FXThread
    public void setMaxLevels(final int maxLevels) {
        this.maxLevels = maxLevels;
    }

    /**
     * @return the max count of texture levels.
     */
    private int getMaxLevels() {
        return maxLevels;
    }
}
