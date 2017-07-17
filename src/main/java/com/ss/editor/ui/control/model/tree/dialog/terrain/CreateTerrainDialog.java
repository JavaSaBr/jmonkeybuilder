package com.ss.editor.ui.control.model.tree.dialog.terrain;

import static com.ss.editor.control.transform.SceneEditorControl.LOADED_MODEL_KEY;
import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.terrain.FlatHeightmap;
import com.jme3.terrain.Terrain;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.geomipmap.lodcalc.DistanceLodCalculator;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.ui.control.choose.ChooseFolderControl;
import com.ss.editor.ui.control.choose.ChooseTextureControl;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.control.input.IntegerTextField;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.FileUtils;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * The dialog to create terrain.
 *
 * @author JavaSaBr
 */
public class CreateTerrainDialog extends AbstractSimpleEditorDialog {
    
    private static final int NUM_ALPHA_TEXTURES = 3;
    
    @NotNull
    private static final Integer DEFAULT_BLEND_TEXTURE_SIZE = 256;

    @NotNull
    private static final Integer DEFAULT_TOTAL_SIZE = DEFAULT_BLEND_TEXTURE_SIZE;

    @NotNull
    private static final Integer DEFAULT_PATH_SIZE = 64;

    @NotNull
    private static final Point DIALOG_SIZE = new Point(580, 0);

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    /**
     * The constant JFX_APPLICATION.
     */
    @NotNull
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    /**
     * The constant EDITOR.
     */
    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();

    private enum HeightMapType {
        /**
         * Flat height map type.
         */
        FLAT(Messages.CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_FLAT),
        /**
         * Image based height map type.
         */
        IMAGE_BASED(Messages.CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_IMAGE_BASED),
        /**
         * Hill height map type.
         */
        HILL(Messages.CREATE_TERRAIN_DIALOG_TERRAIN_TYPE_HILL);

        private static final HeightMapType[] VALUES = values();

        /**
         * The type name.
         */
        @NotNull
        private final String title;

        HeightMapType(@NotNull final String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    @NotNull
    private static final ObservableList<HeightMapType> HEIGHT_MAP_TYPES = observableArrayList(HeightMapType.VALUES);

    @NotNull
    private static final ObservableList<Integer> SIZE_VARIANTS = observableArrayList();

    @NotNull
    private static final ObservableList<Integer> PATCH_SIZE_VARIANTS = observableArrayList();

    @NotNull
    private static final ObservableList<Integer> TOTAL_SIZE_VARIANTS = observableArrayList();

    static {
        SIZE_VARIANTS.add(16);
        SIZE_VARIANTS.add(32);
        SIZE_VARIANTS.add(DEFAULT_PATH_SIZE);
        SIZE_VARIANTS.add(128);
        SIZE_VARIANTS.add(DEFAULT_BLEND_TEXTURE_SIZE);
        SIZE_VARIANTS.add(512);
        SIZE_VARIANTS.add(1024);
        SIZE_VARIANTS.add(2048);
        SIZE_VARIANTS.add(4096);
        PATCH_SIZE_VARIANTS.addAll(SIZE_VARIANTS);
        TOTAL_SIZE_VARIANTS.addAll(SIZE_VARIANTS);
        TOTAL_SIZE_VARIANTS.remove(0);
        TOTAL_SIZE_VARIANTS.remove(0);
    }

    /**
     * The parent node.
     */
    @NotNull
    private final ModelNode<?> parentNode;

    /**
     * The node tree.
     */
    @NotNull
    private final AbstractNodeTree<?> nodeTree;

    /**
     * The settings root.
     */
    @Nullable
    private VBox settingsRoot;

    /**
     * The flat terrain settings.
     */
    @Nullable
    private GridPane flatSettings;

    /**
     * The heightmap terrain settings.
     */
    @Nullable
    private GridPane heightMapSettings;

    /**
     * The hill terrain settings.
     */
    @Nullable
    private GridPane hillSettings;

    /**
     * The alpha texture folder control.
     */
    @Nullable
    private ChooseFolderControl alphaTextureFolderControl;

    /**
     * The base texture control.
     */
    @Nullable
    private ChooseTextureControl baseTextureControl;

    /**
     * The total size combo box.
     */
    @Nullable
    private ComboBox<Integer> totalSizeComboBox;

    /**
     * The patch size combo box.
     */
    @Nullable
    private ComboBox<Integer> patchSizeComboBox;

    /**
     * The type of height map.
     */
    @Nullable
    private ComboBox<HeightMapType> heightMapTypeComboBox;

    /**
     * The alpha blend texture size combo box.
     */
    @Nullable
    private ComboBox<Integer> alphaBlendTextureSizeComboBox;

    /**
     * The base image control.
     */
    @Nullable
    private ChooseTextureControl heightMapImageControl;

    /**
     * The height map smooth field.
     */
    @Nullable
    private FloatTextField heightMapSmoothField;

    /**
     * The height map scale field.
     */
    @Nullable
    private FloatTextField heightMapScaleField;

    /**
     * The iterations field.
     */
    @Nullable
    private IntegerTextField hillIterationsField;

    /**
     * The flattening field.
     */
    @Nullable
    private IntegerTextField hillFlatteningField;

    /**
     * The min radius field.
     */
    @Nullable
    private FloatTextField hillMinRadiusField;

    /**
     * The max radius field.
     */
    @Nullable
    private FloatTextField hillMaxRadiusField;

    /**
     * Instantiates a new Create terrain dialog.
     *
     * @param parentNode the parent node
     * @param nodeTree   the node tree
     */
    public CreateTerrainDialog(@NotNull final ModelNode<?> parentNode, @NotNull final AbstractNodeTree<?> nodeTree) {
        this.parentNode = parentNode;
        this.nodeTree = nodeTree;

        final ComboBox<Integer> totalSizeComboBox = getTotalSizeComboBox();
        totalSizeComboBox.getSelectionModel().select(DEFAULT_TOTAL_SIZE);

        final ComboBox<Integer> patchSizeComboBox = getPatchSizeComboBox();
        patchSizeComboBox.getSelectionModel().select(DEFAULT_PATH_SIZE);

        final ComboBox<Integer> alphaBlendTextureSizeComboBox = getAlphaBlendTextureSizeComboBox();
        alphaBlendTextureSizeComboBox.getSelectionModel().select(DEFAULT_BLEND_TEXTURE_SIZE);

        final ComboBox<HeightMapType> heightMapTypeComboBox = getHeightMapTypeComboBox();
        heightMapTypeComboBox.getSelectionModel().select(HeightMapType.FLAT);

        updatePathSizeValues();
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        settingsRoot = new VBox();

        final Label baseTextureLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_BASE_TEXTURE + ":");
        baseTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        baseTextureControl = new ChooseTextureControl();
        baseTextureControl.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));
        baseTextureControl.setChangeHandler(this::validate);

        final Label alphaTextureFolderLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_FOLDER_ALPHA_TEXTURE + ":");
        alphaTextureFolderLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        alphaTextureFolderControl = new ChooseFolderControl();
        alphaTextureFolderControl.prefWidthProperty().bind(baseTextureControl.widthProperty());
        alphaTextureFolderControl.setChangeHandler(this::validate);

        final Label totalSizeLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_TOTAL_SIZE + ":");
        totalSizeLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        totalSizeComboBox = new ComboBox<>(TOTAL_SIZE_VARIANTS);
        totalSizeComboBox.prefWidthProperty().bind(baseTextureControl.widthProperty());
        totalSizeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updatePathSizeValues());

        final Label pathSizeLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_PATCH_SIZE + ":");
        pathSizeLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        patchSizeComboBox = new ComboBox<>(observableArrayList(PATCH_SIZE_VARIANTS));
        patchSizeComboBox.prefWidthProperty().bind(baseTextureControl.widthProperty());

        final Label alphaBlendTextureSizeLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_ALPHA_BLEND_TEXTURE_SIZE + ":");
        alphaBlendTextureSizeLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        alphaBlendTextureSizeComboBox = new ComboBox<>(SIZE_VARIANTS);
        alphaBlendTextureSizeComboBox.prefWidthProperty().bind(baseTextureControl.widthProperty());

        final Label heightMapTypeLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_TERRAIN_TYPE + ":");
        heightMapTypeLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        heightMapTypeComboBox = new ComboBox<>(HEIGHT_MAP_TYPES);
        heightMapTypeComboBox.prefWidthProperty().bind(baseTextureControl.widthProperty());

        final SingleSelectionModel<HeightMapType> selectionModel = heightMapTypeComboBox.getSelectionModel();
        selectionModel.selectedItemProperty().addListener((observable, oldValue, newValue) -> processChangeType(newValue));

        final GridPane baseSettings = new GridPane();
        baseSettings.add(totalSizeLabel, 0, 0);
        baseSettings.add(totalSizeComboBox, 1, 0);
        baseSettings.add(pathSizeLabel, 0, 1);
        baseSettings.add(patchSizeComboBox, 1, 1);
        baseSettings.add(alphaBlendTextureSizeLabel, 0, 2);
        baseSettings.add(alphaBlendTextureSizeComboBox, 1, 2);
        baseSettings.add(heightMapTypeLabel, 0, 3);
        baseSettings.add(heightMapTypeComboBox, 1, 3);
        baseSettings.add(alphaTextureFolderLabel, 0, 4);
        baseSettings.add(alphaTextureFolderControl, 1, 4);
        baseSettings.add(baseTextureLabel, 0, 5);
        baseSettings.add(baseTextureControl, 1, 5);

        flatSettings = new GridPane();

        final Label heightMapImageControlLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_HEIGHT_MAP_IMAGE + ":");
        heightMapImageControlLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        heightMapImageControl = new ChooseTextureControl();
        heightMapImageControl.prefWidthProperty().bind(baseTextureControl.widthProperty());
        heightMapImageControl.setChangeHandler(this::validate);

        final Label heightMapSmoothLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_HEIGHT_SMOOTH + ":");
        heightMapSmoothLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        heightMapSmoothField = new FloatTextField();
        heightMapSmoothField.prefWidthProperty().bind(baseTextureControl.widthProperty());
        heightMapSmoothField.setMinMax(0F, 1F);
        heightMapSmoothField.setScrollPower(1F);
        heightMapSmoothField.setValue(0.5F);

        final Label heightMapScaleLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_HEIGHT_SCALE + ":");
        heightMapScaleLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        heightMapScaleField = new FloatTextField();
        heightMapScaleField.prefWidthProperty().bind(baseTextureControl.widthProperty());
        heightMapScaleField.setValue(1);

        heightMapSettings = new GridPane();
        heightMapSettings.add(heightMapImageControlLabel, 0, 0);
        heightMapSettings.add(heightMapImageControl, 1, 0);
        heightMapSettings.add(heightMapSmoothLabel, 0, 1);
        heightMapSettings.add(heightMapSmoothField, 1, 1);
        heightMapSettings.add(heightMapScaleLabel, 0, 2);
        heightMapSettings.add(heightMapScaleField, 1, 2);

        final Label hillIterationsLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_ITERATIONS + ":");
        hillIterationsLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        hillIterationsField = new IntegerTextField();
        hillIterationsField.prefWidthProperty().bind(baseTextureControl.widthProperty());
        hillIterationsField.setMinMax(0, 10000);
        hillIterationsField.setValue(2000);

        final Label hillFlatteningLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_FLATTENING + ":");
        hillFlatteningLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        hillFlatteningField = new IntegerTextField();
        hillFlatteningField.prefWidthProperty().bind(baseTextureControl.widthProperty());
        hillFlatteningField.setMinMax(0, 127);
        hillFlatteningField.setValue(4);

        final Label hillMinRadiusLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_MIN_RADIUS + ":");
        hillMinRadiusLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        hillMinRadiusField = new FloatTextField();
        hillMinRadiusField.prefWidthProperty().bind(baseTextureControl.widthProperty());
        hillMinRadiusField.setMinMax(0, 1000);
        hillMinRadiusField.setValue(20);
        hillMinRadiusField.addChangeListener((observable, oldValue, newValue) -> validate());

        final Label hillMaxRadiusLabel = new Label(Messages.CREATE_TERRAIN_DIALOG_MAX_RADIUS + ":");
        hillMaxRadiusLabel.prefWidthProperty().bind(baseTextureLabel.widthProperty());

        hillMaxRadiusField = new FloatTextField();
        hillMaxRadiusField.prefWidthProperty().bind(baseTextureControl.widthProperty());
        hillMaxRadiusField.setMinMax(0, 1000);
        hillMaxRadiusField.setValue(50);
        hillMaxRadiusField.addChangeListener((observable, oldValue, newValue) -> validate());

        hillSettings = new GridPane();
        hillSettings.add(hillIterationsLabel, 0, 0);
        hillSettings.add(hillIterationsField, 1, 0);
        hillSettings.add(hillFlatteningLabel, 0, 1);
        hillSettings.add(hillFlatteningField, 1, 1);
        hillSettings.add(hillMinRadiusLabel, 0, 2);
        hillSettings.add(hillMinRadiusField, 1, 2);
        hillSettings.add(hillMaxRadiusLabel, 0, 3);
        hillSettings.add(hillMaxRadiusField, 1, 3);

        FXUtils.addClassesTo(baseTextureLabel, totalSizeLabel, pathSizeLabel, heightMapTypeLabel,
                alphaBlendTextureSizeLabel, alphaTextureFolderLabel, heightMapImageControlLabel, heightMapSmoothLabel,
                heightMapScaleLabel, hillIterationsLabel, hillFlatteningLabel, hillMinRadiusLabel, hillMaxRadiusLabel,
                CSSClasses.DIALOG_DYNAMIC_LABEL);

        FXUtils.addClassesTo(totalSizeComboBox, patchSizeComboBox, alphaBlendTextureSizeComboBox, heightMapTypeComboBox,
                heightMapSmoothField, heightMapScaleField, hillIterationsField, hillFlatteningField,
                hillMinRadiusField, hillMaxRadiusField, hillFlatteningLabel, hillMinRadiusLabel, hillMaxRadiusLabel,
                CSSClasses.DIALOG_FIELD);

        FXUtils.addToPane(baseSettings, settingsRoot);
        FXUtils.addToPane(settingsRoot, root);

        FXUtils.addClassTo(settingsRoot, CSSClasses.DEF_VBOX);
        FXUtils.addClassTo(baseSettings, flatSettings, heightMapSettings, hillSettings, CSSClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(root, CSSClasses.CREATE_TERRAIN_DIALOG);
    }

    /**
     * Update a list of available path sizes.
     */
    private void updatePathSizeValues() {

        final ComboBox<Integer> pathSizeComboBox = getPatchSizeComboBox();
        final SingleSelectionModel<Integer> selectionModel = pathSizeComboBox.getSelectionModel();
        final Integer current = selectionModel.getSelectedItem();

        final ObservableList<Integer> items = pathSizeComboBox.getItems();
        items.clear();

        final ComboBox<Integer> totalSizeComboBox = getTotalSizeComboBox();
        final Integer naxValue = totalSizeComboBox.getSelectionModel().getSelectedItem();

        for (final Integer value : PATCH_SIZE_VARIANTS) {
            if (value >= naxValue) break;
            items.add(value);
        }

        if (items.contains(current)) {
            selectionModel.select(current);
        } else {
            selectionModel.select(items.get(items.size() - 1));
        }
    }

    /**
     * @return the total size combo box.
     */
    @NotNull
    private ComboBox<Integer> getTotalSizeComboBox() {
        return notNull(totalSizeComboBox);
    }

    /**
     * @return the patch size combo box.
     */
    @NotNull
    private ComboBox<Integer> getPatchSizeComboBox() {
        return notNull(patchSizeComboBox);
    }

    /**
     * @return the alpha blend texture size combo box.
     */
    @NotNull
    private ComboBox<Integer> getAlphaBlendTextureSizeComboBox() {
        return notNull(alphaBlendTextureSizeComboBox);
    }

    /**
     * @return the min radius field.
     */
    @NotNull
    private FloatTextField getHillMinRadiusField() {
        return notNull(hillMinRadiusField);
    }

    /**
     * @return the max radius field.
     */
    @NotNull
    private FloatTextField getHillMaxRadiusField() {
        return notNull(hillMaxRadiusField);
    }

    /**
     * @return the settingsRoot.
     */
    @NotNull
    private VBox getSettingsRoot() {
        return notNull(settingsRoot);
    }

    /**
     * @return the base texture control.
     */
    @NotNull
    private ChooseTextureControl getBaseTextureControl() {
        return notNull(baseTextureControl);
    }

    /**
     * @return the base image control.
     */
    @NotNull
    private ChooseTextureControl getHeightMapImageControl() {
        return notNull(heightMapImageControl);
    }

    /**
     * @return the type of height map.
     */
    @NotNull
    private ComboBox<HeightMapType> getHeightMapTypeComboBox() {
        return notNull(heightMapTypeComboBox);
    }

    /**
     * @return the height map scale field.
     */
    @NotNull
    private FloatTextField getHeightMapScaleField() {
        return notNull(heightMapScaleField);
    }

    /**
     * @return the height map smooth field.
     */
    @NotNull
    private FloatTextField getHeightMapSmoothField() {
        return notNull(heightMapSmoothField);
    }

    /**
     * @return the flattening field.
     */
    @NotNull
    private IntegerTextField getHillFlatteningField() {
        return notNull(hillFlatteningField);
    }

    /**
     * @return the iterations field.
     */
    @NotNull
    private IntegerTextField getHillIterationsField() {
        return notNull(hillIterationsField);
    }

    /**
     * @return the alpha texture folder control.
     */
    @NotNull
    private ChooseFolderControl getAlphaTextureFolderControl() {
        return notNull(alphaTextureFolderControl);
    }

    /**
     * Handle changing type of heightmap.
     */
    private void processChangeType(@NotNull final HeightMapType newValue) {

        final VBox root = getSettingsRoot();
        final ObservableList<Node> children = root.getChildren();
        children.removeAll(flatSettings, heightMapSettings, hillSettings);

        switch (newValue) {
            case FLAT: {
                children.add(flatSettings);
                break;
            }
            case HILL: {
                children.add(hillSettings);
                break;
            }
            case IMAGE_BASED: {
                children.add(heightMapSettings);
                break;
            }
        }

        validate();
        getDialog().sizeToScene();
    }

    /**
     * Validate.
     */
    private void validate() {

        final FloatTextField hillMaxRadiusField = getHillMaxRadiusField();
        final FloatTextField hillMinRadiusField = getHillMinRadiusField();

        final ChooseFolderControl alphaTextureFolderControl = getAlphaTextureFolderControl();
        final ChooseTextureControl baseTextureControl = getBaseTextureControl();
        final ChooseTextureControl heightMapImageControl = getHeightMapImageControl();

        final ComboBox<HeightMapType> heightMapTypeComboBox = getHeightMapTypeComboBox();
        final HeightMapType heightMapType = heightMapTypeComboBox.getSelectionModel().getSelectedItem();

        final Button okButton = getOkButton();
        okButton.setDisable(true);

        final Path baseTexture = baseTextureControl.getTextureFile();
        final Path folder = alphaTextureFolderControl.getFolder();
        if (baseTexture == null || folder == null) return;

        if (heightMapType == HeightMapType.IMAGE_BASED) {
            final Path heightTexture = heightMapImageControl.getTextureFile();
            if (heightTexture == null) return;
        } else if (heightMapType == HeightMapType.HILL) {
            final float minValue = hillMinRadiusField.getValue();
            final float maxValue = hillMaxRadiusField.getValue();
            if (maxValue < minValue) return;
        }

        okButton.setDisable(false);
    }

    @Override
    protected void processOk() {
        super.processOk();
        EditorUtil.incrementLoading();
        EXECUTOR_MANAGER.addBackgroundTask(() -> {

            try {
                createTerrainInBackground();
            } catch (final Exception e) {
                EditorUtil.handleException(LOGGER, this, e);
            }

            EXECUTOR_MANAGER.addFXTask(EditorUtil::decrementLoading);
        });
    }

    /**
     * Create terrain in background.
     */
    @BackgroundThread
    private void createTerrainInBackground() throws Exception {

        final AssetManager assetManager = EDITOR.getAssetManager();
        final ComboBox<HeightMapType> heightMapTypeComboBox = getHeightMapTypeComboBox();
        final HeightMapType heightMapType = heightMapTypeComboBox.getSelectionModel().getSelectedItem();

        final ComboBox<Integer> totalSizeComboBox = getTotalSizeComboBox();
        final int totalSize = totalSizeComboBox.getSelectionModel().getSelectedItem() + 1;

        final ComboBox<Integer> patchSizeComboBox = getPatchSizeComboBox();
        final int patchSize = patchSizeComboBox.getSelectionModel().getSelectedItem();

        final ChooseTextureControl heightMapImageControl = getHeightMapImageControl();
        final FloatTextField heightMapScaleField = getHeightMapScaleField();
        final FloatTextField heightMapSmoothField = getHeightMapSmoothField();

        final FloatTextField hillMinRadiusField = getHillMinRadiusField();
        final FloatTextField hillMaxRadiusField = getHillMaxRadiusField();
        final IntegerTextField hillIterationsField = getHillIterationsField();
        final IntegerTextField hillFlatteningField = getHillFlatteningField();

        AbstractHeightMap heightmap = null;

        switch (heightMapType) {
            case FLAT: {
                heightmap = new FlatHeightmap(totalSize);
                break;
            }
            case IMAGE_BASED: {

                final Path heightMapTextureFile = notNull(heightMapImageControl.getTextureFile());
                final Path assetFile = notNull(getAssetFile(heightMapTextureFile));
                final Texture texture = assetManager.loadTexture(toAssetPath(assetFile));

                heightmap = new ImageBasedHeightMap(texture.getImage(), heightMapScaleField.getValue());
                break;
            }
            case HILL: {

                final int iterations = hillIterationsField.getValue();
                final float minRadius = hillMinRadiusField.getValue();
                final float maxRadius = hillMaxRadiusField.getValue();

                final int flattening = hillFlatteningField.getValue();

                heightmap = new HillHeightMap(totalSize, iterations, minRadius, maxRadius, (byte) flattening);
                break;
            }
        }

        heightmap.load();

        if (heightmap instanceof ImageBasedHeightMap) {
            heightmap.smooth(heightMapSmoothField.getValue());
        }

        final float[] heightMap = heightmap.getHeightMap();

        final Terrain terrain = new TerrainQuad("New terrain", patchSize, totalSize, heightMap);
        final Material terrainMaterial = new Material(assetManager, "Common/MatDefs/Terrain/TerrainLighting.j3md");

        final ComboBox<Integer> alphaBlendTextureSizeComboBox = getAlphaBlendTextureSizeComboBox();
        final int textureAlphaSize = alphaBlendTextureSizeComboBox.getSelectionModel().getSelectedItem();

        final ChooseFolderControl alphaTextureFolderControl = getAlphaTextureFolderControl();
        final Path folder = notNull(alphaTextureFolderControl.getFolder());

        // write out 3 alpha blend images
        for (int i = 0; i < NUM_ALPHA_TEXTURES; i++) {

            final BufferedImage alphaBlend = new BufferedImage(textureAlphaSize, textureAlphaSize,
                    BufferedImage.TYPE_INT_ARGB);

            if (i == 0) {
                // the first alpha level should be opaque so we see the first texture over the whole terrain
                for (int h = 0; h < textureAlphaSize; h++) {
                    for (int w = 0; w < textureAlphaSize; w++) {
                        alphaBlend.setRGB(w, h, 0x00FF0000); //argb
                    }
                }
            }

            final String fileName = FileUtils.getFirstFreeName(folder, Paths.get(
                    "terrain-alpha-blend-" + (i + 1) + ".png"));

            final Path textureFile = folder.resolve(fileName);

            ImageIO.write(alphaBlend, "png", textureFile.toFile());

            final Path assetFile = notNull(getAssetFile(textureFile));
            final Texture texture = assetManager.loadAsset(new TextureKey(toAssetPath(assetFile), false));

            switch (i) {
                case 0: {
                    terrainMaterial.setTexture("AlphaMap", texture);
                    break;
                }
                case 1: {
                    terrainMaterial.setTexture("AlphaMap_1", texture);
                    break;
                }
                case 2: {
                    terrainMaterial.setTexture("AlphaMap_2", texture);
                    break;
                }
                default: {
                    break;
                }
            }

        }

        final ChooseTextureControl baseTextureControl = getBaseTextureControl();
        final Path baseTextureFile = notNull(baseTextureControl.getTextureFile());

        // give the first layer default texture
        final Texture baseTexture = assetManager.loadTexture(toAssetPath(notNull(getAssetFile(baseTextureFile))));
        baseTexture.setWrap(Texture.WrapMode.Repeat);

        terrainMaterial.setTexture("DiffuseMap", baseTexture);
        terrainMaterial.setFloat("DiffuseMap_0_scale", 1);
        terrainMaterial.setFloat("Shininess", 0.01f);
        terrainMaterial.setBoolean("WardIso", true);

        final com.jme3.scene.Node terrainNode = (com.jme3.scene.Node) terrain;
        terrainNode.setUserData(LOADED_MODEL_KEY, true);
        terrainNode.setMaterial(terrainMaterial);
        terrainNode.setModelBound(new BoundingBox());
        terrainNode.updateModelBound();
        terrainNode.setLocalTranslation(0, 0, 0);
        terrainNode.setLocalScale(1f, 1f, 1f);

        // add the lod control
        final TerrainLodControl control = new TerrainLodControl(terrain, EDITOR.getCamera());
        control.setLodCalculator(new DistanceLodCalculator(patchSize, 2.7f));

        terrainNode.addControl(control);

        final ModelNode<?> parentNode = getParentNode();
        final com.jme3.scene.Node parent = (com.jme3.scene.Node) parentNode.getElement();

        final AbstractNodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());
        changeConsumer.execute(new AddChildOperation(terrainNode, parent));
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_TERRAIN_DIALOG_TITLE;
    }

    /**
     * @return the node tree.
     */
    @NotNull
    private AbstractNodeTree<?> getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the parent node.
     */
    @NotNull
    private ModelNode<?> getParentNode() {
        return parentNode;
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
