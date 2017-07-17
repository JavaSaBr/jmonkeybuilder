package com.ss.editor.ui.component.editing.terrain.paint;

import static com.ss.editor.ui.component.editing.terrain.TerrainEditingComponent.TERRAIN_PARAM;
import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.terrain.Terrain;
import com.jme3.texture.Texture;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.component.editing.terrain.TerrainEditingComponent;
import com.ss.editor.ui.component.editing.terrain.control.PaintTerrainToolControl;
import com.ss.editor.ui.control.model.property.operation.ModelPropertyOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.util.NodeUtils;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.beans.binding.DoubleBinding;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

/**
 * The implementation of texture layer settings.
 *
 * @author JavaSaBr
 */
public class TextureLayerSettings extends VBox {

    private static final double CELL_SIZE = 108;

    /**
     * The list of cells.
     */
    @NotNull
    private final Array<TextureLayerCell> cells;

    /**
     * The editing component.
     */
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
     * The button to add a new layer.
     */
    @Nullable
    private Button addButton;

    /**
     * The max count of texture levels.
     */
    private int maxLevels;

    /**
     * Instantiates a new Texture layer settings.
     *
     * @param editingComponent the editing component
     */
    public TextureLayerSettings(@NotNull final TerrainEditingComponent editingComponent) {
        this.cells = ArrayFactory.newArray(TextureLayerCell.class);
        this.editingComponent = editingComponent;
        createComponents();
        FXUtils.addClassTo(this, CSSClasses.DEF_VBOX);
    }

    private void createComponents() {

        this.listView = new ListView<>();
        this.listView.setCellFactory(param -> newCell());
        this.listView.setEditable(false);
        this.listView.prefWidthProperty().bind(widthProperty());

        final MultipleSelectionModel<TextureLayer> selectionModel = listView.getSelectionModel();
        selectionModel.setSelectionMode(SelectionMode.SINGLE);
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> updateSelectedLayer(newValue));

        addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_16));
        addButton.setOnAction(event -> addLayer());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_16));
        removeButton.setOnAction(event -> removeLayer());
        removeButton.disableProperty().bind(selectionModel.selectedItemProperty().isNull());

        final HBox buttonContainer = new HBox(addButton, removeButton);

        FXUtils.addToPane(listView, this);
        FXUtils.addToPane(buttonContainer, this);

        FXUtils.addClassesTo(listView, CSSClasses.TRANSPARENT_LIST_VIEW, CSSClasses.LIST_VIEW_WITHOUT_SCROLL);
        FXUtils.addClassTo(buttonContainer, CSSClasses.PROCESSING_COMPONENT_TERRAIN_EDITOR_LAYERS_SETTINGS_BUTTONS);
        FXUtils.addClassTo(addButton, CSSClasses.BUTTON_WITHOUT_RIGHT_BORDER);
        FXUtils.addClassTo(removeButton, CSSClasses.BUTTON_WITHOUT_LEFT_BORDER);

        DynamicIconSupport.addSupport(addButton, removeButton);
    }

    /**
     * Update selected layer.
     *
     * @param newValue the selected layer.
     */
    private void updateSelectedLayer(@Nullable final TextureLayer newValue) {

        final int layer = newValue == null ? -1 : newValue.getLayer();
        final Texture alphaTexture = layer == -1 ? null : getAlpha(layer);

        final PaintTerrainToolControl paintToolControl = editingComponent.getPaintToolControl();
        paintToolControl.setAlphaTexture(alphaTexture);
        paintToolControl.setLayer(layer);
    }

    @NotNull
    private ListCell<TextureLayer> newCell() {

        final DoubleBinding width = widthProperty().subtract(4D);
        final TextureLayerCell cell = new TextureLayerCell(width, width);

        cells.add(cell);

        return cell;
    }

    /**
     * Add a new layer.
     */
    private void addLayer() {
        final int maxLevels = getMaxLevels() - 1;

        for (int i = 0; i < maxLevels; i++) {

            final float scale = getTextureScale(i);

            if (scale == -1F) {
                setTextureScale(1F, i);
                return;
            }
        }
    }

    /**
     * Remove selected layer.
     */
    private void removeLayer() {

        final ListView<TextureLayer> listView = getListView();
        final MultipleSelectionModel<TextureLayer> selectionModel = listView.getSelectionModel();
        final TextureLayer textureLayer = selectionModel.getSelectedItem();

        setTextureScale(-1F, textureLayer.getLayer());
    }

    /**
     * Sets layer to alpha name.
     *
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
     * Sets layer to diffuse name.
     *
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
     * Sets layer to normal name.
     *
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
     * Sets layer to scale name.
     *
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
        return notNull(listView);
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
        } else if (!items.isEmpty()) {
            selectionModel.select(items.get(0));
        }

        final ExecutorManager executorManager = ExecutorManager.getInstance();
        executorManager.addFXTask(this::refreshHeight);
        executorManager.addFXTask(this::refreshAddButton);
    }

    /**
     * Refresh the add button.
     */
    private void refreshAddButton() {

        final ListView<TextureLayer> listView = getListView();
        final ObservableList<TextureLayer> items = listView.getItems();

        final Button addButton = getAddButton();
        addButton.setDisable(items.size() >= getMaxLevels());
    }

    /**
     * Refresh height of list view.
     */
    private void refreshHeight() {

        final ListView<TextureLayer> listView = getListView();
        final ObservableList<TextureLayer> items = listView.getItems();

        if (items.isEmpty()) {
            listView.setPrefHeight(0D);
        } else {
            listView.setPrefHeight(items.size() * CELL_SIZE);
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
        return editingComponent.getProcessedObject();
    }

    /**
     * Get current edited terrain.
     *
     * @return the edited terrain.
     */
    @NotNull
    @FXThread
    private Node getTerrainNode() {
        return editingComponent.getProcessedObject();
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
    @FromAnyThread
    public void setDiffuse(@Nullable final Texture texture, final int layer) {

        final Function<Integer, String> layerToDiffuseName = getLayerToDiffuseName();
        if (layerToDiffuseName == null) return;

        final Terrain terrain = getTerrain();
        final Material material = terrain.getMaterial();
        final String paramName = layerToDiffuseName.apply(layer);
        final MatParam matParam = material.getParam(paramName);
        final Texture current = matParam == null ? null : (Texture) matParam.getValue();

        if (texture != null) {
            texture.setWrap(Texture.WrapMode.Repeat);
        }

        final ModelPropertyOperation<Node, Texture> operation =
                new ModelPropertyOperation<>(getTerrainNode(), TERRAIN_PARAM, texture, current);

        operation.setApplyHandler((node, newTexture) ->
                NodeUtils.visitGeometry(node, geometry -> updateTexture(newTexture, paramName, geometry)));

        final ModelChangeConsumer changeConsumer = editingComponent.getChangeConsumer();
        changeConsumer.execute(operation);
    }

    private void updateTexture(@Nullable final Texture texture, @NotNull final String paramName,
                               @NotNull final Geometry geometry) {

        final Material material = geometry.getMaterial();
        final MatParam matParam = material.getParam(paramName);
        if (matParam == null && texture == null) return;

        if (texture == null) {
            material.clearParam(matParam.getName());
        } else {
            material.setTexture(paramName, texture);
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
    @FromAnyThread
    public void setNormal(@Nullable final Texture texture, final int layer) {

        final Function<Integer, String> layerToNormalName = getLayerToNormalName();
        if (layerToNormalName == null) return;

        final Terrain terrain = getTerrain();
        final Material material = terrain.getMaterial();
        final String paramName = layerToNormalName.apply(layer);
        final MatParam matParam = material.getParam(paramName);
        final Texture current = matParam == null ? null : (Texture) matParam.getValue();

        if (texture != null) {
            texture.setWrap(Texture.WrapMode.Repeat);
        }

        final ModelPropertyOperation<Node, Texture> operation =
                new ModelPropertyOperation<>(getTerrainNode(), TERRAIN_PARAM, texture, current);

        operation.setApplyHandler((node, newTexture) ->
                NodeUtils.visitGeometry(node, geometry -> updateTexture(newTexture, paramName, geometry)));

        final ModelChangeConsumer changeConsumer = editingComponent.getChangeConsumer();
        changeConsumer.execute(operation);
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
    @FromAnyThread
    public void setTextureScale(final float scale, final int layer) {

        final Function<Integer, String> layerToScaleName = getLayerToScaleName();
        if (layerToScaleName == null) return;

        final Terrain terrain = getTerrain();
        final Material material = terrain.getMaterial();
        final String paramName = layerToScaleName.apply(layer);
        final MatParam matParam = material.getParam(paramName);
        final Float current = matParam == null ? null : (Float) matParam.getValue();

        final ModelPropertyOperation<Node, Float> operation =
                new ModelPropertyOperation<>(getTerrainNode(), TERRAIN_PARAM, scale, current);

        operation.setApplyHandler((node, newScale) -> {
            NodeUtils.visitGeometry(getTerrainNode(), geometry -> {
                final Material geometryMaterial = geometry.getMaterial();
                final MatParam param = geometryMaterial.getParam(paramName);
                if (param == null && (newScale == null || newScale == -1F)) return;
                if (newScale == null || newScale == -1F) {
                    geometryMaterial.clearParam(paramName);
                } else {
                    geometryMaterial.setFloat(paramName, newScale);
                }
            });
        });

        final ModelChangeConsumer changeConsumer = editingComponent.getChangeConsumer();
        changeConsumer.execute(operation);
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
     * Sets max levels.
     *
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

    /**
     * @return the list of cells.
     */
    @NotNull
    private Array<TextureLayerCell> getCells() {
        return cells;
    }

    /**
     * @return the button to add a new layer.
     */
    @NotNull
    private Button getAddButton() {
        return notNull(addButton);
    }

    /**
     * Notify about changed property.
     */
    @FXThread
    public void notifyChangeProperty() {

        final ListView<TextureLayer> listView = getListView();
        final ObservableList<TextureLayer> items = listView.getItems();

        final int maxLevels = getMaxLevels() - 1;
        int newCount = 0;

        for (int i = 0; i < maxLevels; i++) {
            final float scale = getTextureScale(i);
            if (scale == -1F) continue;
            newCount++;
        }

        if (newCount != items.size()) {
            refresh();
        } else {
            getCells().forEach(TextureLayerCell::refresh);
        }
    }
}
