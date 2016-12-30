package com.ss.editor.ui.control.model.tree.dialog.sky;

import static rlib.util.ClassUtils.unsafeCast;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.model.tree.ModelNodeTree;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.model.tree.dialog.AbstractNodeDialog;
import com.ss.editor.ui.control.model.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;
import com.ss.editor.util.GeomUtils;

import org.jetbrains.annotations.NotNull;

import java.awt.Point;
import java.nio.file.Path;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;

/**
 * The dialog for creating a sky.
 *
 * @author JavaSaBr
 */
public class CreateSkyDialog extends AbstractNodeDialog {

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    private static final Insets SKY_TYPE_OFFSET = new Insets(16, 0, 0, 0);
    private static final Insets FIELD_OFFSET = new Insets(8, 0, 0, 0);
    private static final Insets CHECK_BOX_OFFSET = new Insets(FIELD_OFFSET.getTop(), 0, 0, 200);
    private static final Insets SETTINGS_OFFSET = new Insets(0, 0, 10, 0);

    private static final Point DIALOG_SIZE = new Point(580, 385);

    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    protected static final Editor EDITOR = Editor.getInstance();

    private enum SkyType {
        SINGLE_TEXTURE(Messages.CREATE_SKY_DIALOG_SKY_TYPE_SINGLE),
        MULTIPLE_TEXTURE(Messages.CREATE_SKY_DIALOG_SKY_TYPE_MULTIPLE);

        private static final SkyType[] VALUES = values();

        /**
         * The sky name.
         */
        private final String title;

        SkyType(final String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return title;
        }
    }

    /**
     * The parent node.
     */
    private final ModelNode<?> parentNode;

    /**
     * The model tree.
     */
    private final ModelNodeTree nodeTree;

    /**
     * The list of sky types.
     */
    private ComboBox<SkyType> skyTypeComboBox;

    /**
     * The scale control for X.
     */
    private Spinner<Double> normalScaleXSpinner;

    /**
     * The scale control for Y.
     */
    private Spinner<Double> normalScaleYSpinner;

    /**
     * The scale control for Z.
     */
    private Spinner<Double> normalScaleZSpinner;

    /**
     * The container of single texture settings.
     */
    private VBox singleTextureSettings;

    /**
     * The container of multiply texture settings.
     */
    private VBox multipleTextureSettings;

    /**
     * The single texture control.
     */
    private ChooseTextureControl singleTextureControl;

    /**
     * The list of env types.
     */
    private ComboBox<SkyFactory.EnvMapType> envMapTypeComboBox;

    /**
     * The check box for fliping.
     */
    private CheckBox flipYCheckBox;

    /**
     * The north texture control.
     */
    private ChooseTextureControl northTextureControl;

    /**
     * The south texture control.
     */
    private ChooseTextureControl southTextureControl;

    /**
     * The east texture control.
     */
    private ChooseTextureControl eastTextureControl;

    /**
     * The west texture control.
     */
    private ChooseTextureControl westTextureControl;

    /**
     * The top texture control.
     */
    private ChooseTextureControl topTextureControl;

    /**
     * The bottom texture control.
     */
    private ChooseTextureControl bottomTextureControl;

    public CreateSkyDialog(final ModelNode<?> parentNode, final ModelNodeTree nodeTree) {
        this.parentNode = parentNode;
        this.nodeTree = nodeTree;
        validate();
    }

    /**
     * @return the list of sky types.
     */
    private ComboBox<SkyType> getSkyTypeComboBox() {
        return skyTypeComboBox;
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_SKY_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        createSkyTypeComboBox(root);
        createNormalScaleControls(root);

        final StackPane container = new StackPane();

        singleTextureSettings = new VBox();
        singleTextureSettings.setVisible(false);

        createSingleTextureSettings();

        multipleTextureSettings = new VBox();
        multipleTextureSettings.setVisible(false);

        createMultipleTextureSettings();

        FXUtils.addToPane(singleTextureSettings, container);
        FXUtils.addToPane(multipleTextureSettings, container);
        FXUtils.addToPane(container, root);

        final ComboBox<SkyType> skyTypeComboBox = getSkyTypeComboBox();
        final SingleSelectionModel<SkyType> selectionModel = skyTypeComboBox.getSelectionModel();
        selectionModel.select(SkyType.SINGLE_TEXTURE);

        VBox.setMargin(container, SETTINGS_OFFSET);

        validate();
    }

    private void createMultipleTextureSettings() {

        final HBox northTextureContainer = new HBox();
        northTextureContainer.setAlignment(Pos.CENTER_LEFT);

        final Label northTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_NORTH_LABEL + ":");
        northTextureLabel.setId(CSSIds.CREATE_SKY_DIALOG_LABEL);

        northTextureControl = new ChooseTextureControl();
        northTextureControl.setChangeHandler(this::validate);

        FXUtils.addToPane(northTextureLabel, northTextureContainer);
        FXUtils.addToPane(northTextureControl, northTextureContainer);
        FXUtils.addToPane(northTextureContainer, multipleTextureSettings);

        final HBox southTextureContainer = new HBox();
        southTextureContainer.setAlignment(Pos.CENTER_LEFT);

        final Label southTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_SOUTH_LABEL + ":");
        southTextureLabel.setId(CSSIds.CREATE_SKY_DIALOG_LABEL);

        southTextureControl = new ChooseTextureControl();
        southTextureControl.setChangeHandler(this::validate);

        FXUtils.addToPane(southTextureLabel, southTextureContainer);
        FXUtils.addToPane(southTextureControl, southTextureContainer);
        FXUtils.addToPane(southTextureContainer, multipleTextureSettings);

        final HBox eastTextureContainer = new HBox();
        eastTextureContainer.setAlignment(Pos.CENTER_LEFT);

        final Label eastTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_EAST_LABEL + ":");
        eastTextureLabel.setId(CSSIds.CREATE_SKY_DIALOG_LABEL);

        eastTextureControl = new ChooseTextureControl();
        eastTextureControl.setChangeHandler(this::validate);

        FXUtils.addToPane(eastTextureLabel, eastTextureContainer);
        FXUtils.addToPane(eastTextureControl, eastTextureContainer);
        FXUtils.addToPane(eastTextureContainer, multipleTextureSettings);

        final HBox westTextureContainer = new HBox();
        westTextureContainer.setAlignment(Pos.CENTER_LEFT);

        final Label westTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_WEST_LABEL + ":");
        westTextureLabel.setId(CSSIds.CREATE_SKY_DIALOG_LABEL);

        westTextureControl = new ChooseTextureControl();
        westTextureControl.setChangeHandler(this::validate);

        FXUtils.addToPane(westTextureLabel, westTextureContainer);
        FXUtils.addToPane(westTextureControl, westTextureContainer);
        FXUtils.addToPane(westTextureContainer, multipleTextureSettings);

        final HBox topTextureContainer = new HBox();
        topTextureContainer.setAlignment(Pos.CENTER_LEFT);

        final Label topTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_TOP_LABEL + ":");
        topTextureLabel.setId(CSSIds.CREATE_SKY_DIALOG_LABEL);

        topTextureControl = new ChooseTextureControl();
        topTextureControl.setChangeHandler(this::validate);

        FXUtils.addToPane(topTextureLabel, topTextureContainer);
        FXUtils.addToPane(topTextureControl, topTextureContainer);
        FXUtils.addToPane(topTextureContainer, multipleTextureSettings);

        final HBox bottomTextureContainer = new HBox();
        bottomTextureContainer.setAlignment(Pos.CENTER_LEFT);

        final Label bottomTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_BOTTOM_LABEL + ":");
        bottomTextureLabel.setId(CSSIds.CREATE_SKY_DIALOG_LABEL);

        bottomTextureControl = new ChooseTextureControl();
        bottomTextureControl.setChangeHandler(this::validate);

        FXUtils.addToPane(bottomTextureLabel, bottomTextureContainer);
        FXUtils.addToPane(bottomTextureControl, bottomTextureContainer);
        FXUtils.addToPane(bottomTextureContainer, multipleTextureSettings);

        FXUtils.addClassTo(northTextureLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(southTextureLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(eastTextureLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(westTextureLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(topTextureContainer, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(bottomTextureLabel, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(bottomTextureContainer, FIELD_OFFSET);
        VBox.setMargin(eastTextureContainer, FIELD_OFFSET);
        VBox.setMargin(northTextureContainer, FIELD_OFFSET);
        VBox.setMargin(southTextureContainer, FIELD_OFFSET);
        VBox.setMargin(topTextureContainer, FIELD_OFFSET);
        VBox.setMargin(westTextureContainer, FIELD_OFFSET);
    }

    private void createSingleTextureSettings() {

        final HBox singleTextureContainer = new HBox();
        singleTextureContainer.setAlignment(Pos.CENTER_LEFT);

        final Label singleTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_TEXTURE_LABEL + ":");
        singleTextureLabel.setId(CSSIds.CREATE_SKY_DIALOG_LABEL);

        singleTextureControl = new ChooseTextureControl();
        singleTextureControl.setChangeHandler(this::validate);

        FXUtils.addToPane(singleTextureLabel, singleTextureContainer);
        FXUtils.addToPane(singleTextureControl, singleTextureContainer);
        FXUtils.addToPane(singleTextureContainer, singleTextureSettings);

        final HBox envMapTypeContainer = new HBox();

        final Label envMapTypeLabel = new Label(Messages.CREATE_SKY_DIALOG_TEXTURE_TYPE_LABEL + ":");
        envMapTypeLabel.setId(CSSIds.CREATE_SKY_DIALOG_LABEL);

        envMapTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(SkyFactory.EnvMapType.values()));
        envMapTypeComboBox.setId(CSSIds.CREATE_SKY_DIALOG_COMBO_BOX);
        envMapTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(envMapTypeLabel, envMapTypeContainer);
        FXUtils.addToPane(envMapTypeComboBox, envMapTypeContainer);
        FXUtils.addToPane(envMapTypeContainer, singleTextureSettings);

        flipYCheckBox = new CheckBox();
        flipYCheckBox.setText(Messages.CREATE_SKY_DIALOG_FLIP_Y_LABEL);

        FXUtils.addToPane(flipYCheckBox, singleTextureSettings);

        FXUtils.addClassTo(singleTextureLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(envMapTypeLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(flipYCheckBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(singleTextureContainer, FIELD_OFFSET);
        VBox.setMargin(envMapTypeContainer, FIELD_OFFSET);
        VBox.setMargin(flipYCheckBox, CHECK_BOX_OFFSET);
    }

    private void createNormalScaleControls(final VBox root) {

        final HBox normalScaleContainer = new HBox();

        final Label normalScaleLabel = new Label(Messages.CREATE_SKY_DIALOG_NORMAL_SCALE_LABEL + ":");
        normalScaleLabel.setId(CSSIds.CREATE_SKY_DIALOG_LABEL);

        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(-50, 50, 0, 0.01);
        valueFactory.setValue(1D);

        normalScaleXSpinner = new Spinner<>();
        normalScaleXSpinner.setId(CSSIds.CREATE_SKY_DIALOG_SPINNER);
        normalScaleXSpinner.setValueFactory(valueFactory);
        normalScaleXSpinner.setEditable(true);
        normalScaleXSpinner.setOnScroll(this::processScroll);

        valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(-50, 50, 0, 0.01);
        valueFactory.setValue(1D);

        normalScaleYSpinner = new Spinner<>();
        normalScaleYSpinner.setId(CSSIds.CREATE_SKY_DIALOG_SPINNER);
        normalScaleYSpinner.setValueFactory(valueFactory);
        normalScaleYSpinner.setEditable(true);
        normalScaleYSpinner.setOnScroll(this::processScroll);

        valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(-50, 50, 0, 0.01);
        valueFactory.setValue(1D);

        normalScaleZSpinner = new Spinner<>();
        normalScaleZSpinner.setId(CSSIds.CREATE_SKY_DIALOG_SPINNER);
        normalScaleZSpinner.setValueFactory(valueFactory);
        normalScaleZSpinner.setEditable(true);
        normalScaleZSpinner.setOnScroll(this::processScroll);

        FXUtils.addToPane(normalScaleLabel, normalScaleContainer);
        FXUtils.addToPane(normalScaleXSpinner, normalScaleContainer);
        FXUtils.addToPane(normalScaleYSpinner, normalScaleContainer);
        FXUtils.addToPane(normalScaleZSpinner, normalScaleContainer);
        FXUtils.addToPane(normalScaleContainer, root);

        FXUtils.addClassTo(normalScaleLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(normalScaleXSpinner, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(normalScaleYSpinner, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(normalScaleZSpinner, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(normalScaleContainer, FIELD_OFFSET);
    }

    private void createSkyTypeComboBox(final VBox root) {

        final HBox skyTypeContainer = new HBox();

        final Label skyTypeLabel = new Label(Messages.CREATE_SKY_DIALOG_SKY_TYPE_LABEL + ":");
        skyTypeLabel.setId(CSSIds.CREATE_SKY_DIALOG_LABEL);

        skyTypeComboBox = new ComboBox<>(FXCollections.observableArrayList(SkyType.VALUES));
        skyTypeComboBox.setId(CSSIds.CREATE_SKY_DIALOG_COMBO_BOX);
        skyTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        FXUtils.addToPane(skyTypeLabel, skyTypeContainer);
        FXUtils.addToPane(skyTypeComboBox, skyTypeContainer);
        FXUtils.addToPane(skyTypeContainer, root);

        FXUtils.addClassTo(skyTypeLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(skyTypeComboBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(skyTypeContainer, SKY_TYPE_OFFSET);
    }

    /**
     * @return the container of single texture settings.
     */
    private VBox getSingleTextureSettings() {
        return singleTextureSettings;
    }

    /**
     * @return the container of multiply texture settings.
     */
    private VBox getMultipleTextureSettings() {
        return multipleTextureSettings;
    }

    /**
     * Handle changing sky type.
     */
    private void processChange(final SkyType newValue) {

        final VBox singleTextureSettings = getSingleTextureSettings();
        singleTextureSettings.setVisible(newValue == SkyType.SINGLE_TEXTURE);

        final VBox multiplyTextureSettings = getMultipleTextureSettings();
        multiplyTextureSettings.setVisible(newValue == SkyType.MULTIPLE_TEXTURE);

        validate();
    }

    /**
     * @return the single texture control.
     */
    private ChooseTextureControl getSingleTextureControl() {
        return singleTextureControl;
    }

    /**
     * @return the list of env types.
     */
    private ComboBox<SkyFactory.EnvMapType> getEnvMapTypeComboBox() {
        return envMapTypeComboBox;
    }

    /**
     * @return the top texture control.
     */
    private ChooseTextureControl getTopTextureControl() {
        return topTextureControl;
    }

    /**
     * @return the bottom texture control.
     */
    private ChooseTextureControl getBottomTextureControl() {
        return bottomTextureControl;
    }

    /**
     * @return the north texture control.
     */
    private ChooseTextureControl getNorthTextureControl() {
        return northTextureControl;
    }

    /**
     * @return the south texture control.
     */
    private ChooseTextureControl getSouthTextureControl() {
        return southTextureControl;
    }

    /**
     * @return the east texture control.
     */
    private ChooseTextureControl getEastTextureControl() {
        return eastTextureControl;
    }

    /**
     * @return the west texture control.
     */
    private ChooseTextureControl getWestTextureControl() {
        return westTextureControl;
    }

    /**
     * Validate the dialog.
     */
    private void validate() {

        final ComboBox<SkyType> skyTypeComboBox = getSkyTypeComboBox();
        final SingleSelectionModel<SkyType> selectionModel = skyTypeComboBox.getSelectionModel();
        final SkyType selectedItem = selectionModel.getSelectedItem();

        final Button okButton = getOkButton();

        if (okButton == null) {
            return;
        }

        okButton.setDisable(true);

        if (selectedItem == SkyType.SINGLE_TEXTURE) {

            final ComboBox<SkyFactory.EnvMapType> envMapTypeComboBox = getEnvMapTypeComboBox();
            final SkyFactory.EnvMapType envMapType = envMapTypeComboBox.getSelectionModel().getSelectedItem();

            final ChooseTextureControl singleTextureControl = getSingleTextureControl();
            final Path textureFile = singleTextureControl.getTextureFile();

            okButton.setDisable(envMapType == null || textureFile == null);

        } else if (selectedItem == SkyType.MULTIPLE_TEXTURE) {

            final ChooseTextureControl northTextureControl = getNorthTextureControl();
            final Path northTextureFile = northTextureControl.getTextureFile();

            final ChooseTextureControl southTextureControl = getSouthTextureControl();
            final Path southTextureFile = southTextureControl.getTextureFile();

            final ChooseTextureControl eastTextureControl = getEastTextureControl();
            final Path eastTextureFile = eastTextureControl.getTextureFile();

            final ChooseTextureControl westTextureControl = getWestTextureControl();
            final Path westTextureFile = westTextureControl.getTextureFile();

            final ChooseTextureControl topTextureControl = getTopTextureControl();
            final Path topTextureFile = topTextureControl.getTextureFile();

            final ChooseTextureControl bottomTextureControl = getBottomTextureControl();
            final Path bottomTextureFile = bottomTextureControl.getTextureFile();

            if (northTextureFile == null || southTextureFile == null) {
                okButton.setDisable(true);
            } else if (eastTextureFile == null || westTextureFile == null) {
                okButton.setDisable(true);
            } else if (topTextureFile == null || bottomTextureFile == null) {
                okButton.setDisable(true);
            } else {
                okButton.setDisable(false);
            }
        }
    }

    /**
     * @return the check box for fliping.
     */
    private CheckBox getFlipYCheckBox() {
        return flipYCheckBox;
    }

    /**
     * @return the scale control for X.
     */
    private Spinner<Double> getNormalScaleXSpinner() {
        return normalScaleXSpinner;
    }

    /**
     * @return the scale control for Y.
     */
    private Spinner<Double> getNormalScaleYSpinner() {
        return normalScaleYSpinner;
    }

    /**
     * @return the scale control for Z.
     */
    private Spinner<Double> getNormalScaleZSpinner() {
        return normalScaleZSpinner;
    }

    /**
     * @return the model tree.
     */
    private ModelNodeTree getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the parent node.
     */
    private ModelNode<?> getParentNode() {
        return parentNode;
    }

    @Override
    protected void processOk() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        scene.incrementLoading();

        EXECUTOR_MANAGER.addBackgroundTask(this::createSkyInBackground);

        hide();
    }

    /**
     * The process of creating a new sky.
     */
    private void createSkyInBackground() {

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final AssetManager assetManager = EDITOR.getAssetManager();

        final ModelNodeTree nodeTree = getNodeTree();
        final ModelChangeConsumer modelChangeConsumer = nodeTree.getModelChangeConsumer();

        final Spinner<Double> normalScaleXSpinner = getNormalScaleXSpinner();
        final Spinner<Double> normalScaleYSpinner = getNormalScaleYSpinner();
        final Spinner<Double> normalScaleZSpinner = getNormalScaleZSpinner();

        final Vector3f scale = new Vector3f();
        scale.setX(normalScaleXSpinner.getValue().floatValue());
        scale.setY(normalScaleYSpinner.getValue().floatValue());
        scale.setZ(normalScaleZSpinner.getValue().floatValue());

        final ComboBox<SkyType> skyTypeComboBox = getSkyTypeComboBox();
        final SingleSelectionModel<SkyType> selectionModel = skyTypeComboBox.getSelectionModel();
        final SkyType selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == SkyType.SINGLE_TEXTURE) {
            createSingleTexture(assetManager, modelChangeConsumer, scale);
        } else if (selectedItem == SkyType.MULTIPLE_TEXTURE) {
            createMultipleTexture(assetManager, modelChangeConsumer, scale);
        }

        EXECUTOR_MANAGER.addFXTask(scene::decrementLoading);
    }

    /**
     * Create a new sky using multiply textures.
     */
    private void createMultipleTexture(final AssetManager assetManager, final ModelChangeConsumer modelChangeConsumer, final Vector3f scale) {

        final ChooseTextureControl northTextureControl = getNorthTextureControl();
        final Path northTextureFile = northTextureControl.getTextureFile();

        final ChooseTextureControl southTextureControl = getSouthTextureControl();
        final Path southTextureFile = southTextureControl.getTextureFile();

        final ChooseTextureControl eastTextureControl = getEastTextureControl();
        final Path eastTextureFile = eastTextureControl.getTextureFile();

        final ChooseTextureControl westTextureControl = getWestTextureControl();
        final Path westTextureFile = westTextureControl.getTextureFile();

        final ChooseTextureControl topTextureControl = getTopTextureControl();
        final Path topTextureFile = topTextureControl.getTextureFile();

        final ChooseTextureControl bottomTextureControl = getBottomTextureControl();
        final Path bottomTextureFile = bottomTextureControl.getTextureFile();

        final Path northTextureAssetFile = EditorUtil.getAssetFile(northTextureFile);
        final Path southTextureAssetFile = EditorUtil.getAssetFile(southTextureFile);
        final Path eastTextureAssetFile = EditorUtil.getAssetFile(eastTextureFile);
        final Path westTextureAssetFile = EditorUtil.getAssetFile(westTextureFile);
        final Path topTextureAssetFile = EditorUtil.getAssetFile(topTextureFile);
        final Path bottomTextureAssetFile = EditorUtil.getAssetFile(bottomTextureFile);

        final Texture northTexture = assetManager.loadTexture(EditorUtil.toAssetPath(northTextureAssetFile));
        final Texture southTexture = assetManager.loadTexture(EditorUtil.toAssetPath(southTextureAssetFile));
        final Texture eastTexture = assetManager.loadTexture(EditorUtil.toAssetPath(eastTextureAssetFile));
        final Texture westTexture = assetManager.loadTexture(EditorUtil.toAssetPath(westTextureAssetFile));
        final Texture topTexture = assetManager.loadTexture(EditorUtil.toAssetPath(topTextureAssetFile));
        final Texture bottomTexture = assetManager.loadTexture(EditorUtil.toAssetPath(bottomTextureAssetFile));

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial sky = SkyFactory.createSky(assetManager, westTexture, eastTexture, northTexture, southTexture, topTexture, bottomTexture, scale);
            sky.setUserData(ModelNodeTree.USER_DATA_IS_SKY, Boolean.TRUE);

            final ModelNode<?> parentNode = getParentNode();
            final Node element = (Node) parentNode.getElement();

            final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), element);

            modelChangeConsumer.execute(new AddChildOperation(sky, index));
        });
    }

    /**
     * Create a new sky using a single texture.
     */
    private void createSingleTexture(AssetManager assetManager, ModelChangeConsumer modelChangeConsumer, Vector3f scale) {

        final CheckBox flipYCheckBox = getFlipYCheckBox();
        final boolean flipY = flipYCheckBox.isSelected();

        final ComboBox<SkyFactory.EnvMapType> envMapTypeComboBox = getEnvMapTypeComboBox();
        final SkyFactory.EnvMapType envMapType = envMapTypeComboBox.getSelectionModel().getSelectedItem();

        final ChooseTextureControl singleTextureControl = getSingleTextureControl();
        final Path textureFile = singleTextureControl.getTextureFile();
        final Path assetFile = EditorUtil.getAssetFile(textureFile);

        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final TextureKey textureKey = new TextureKey(assetPath, flipY);
        textureKey.setGenerateMips(true);

        final Texture texture = assetManager.loadAsset(textureKey);

        if (envMapType == SkyFactory.EnvMapType.CubeMap) {
            textureKey.setTextureTypeHint(Texture.Type.CubeMap);
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial sky = SkyFactory.createSky(assetManager, texture, scale, envMapType);
            sky.setUserData(ModelNodeTree.USER_DATA_IS_SKY, Boolean.TRUE);

            final ModelNode<?> parentNode = getParentNode();
            final Node element = (Node) parentNode.getElement();

            final int index = GeomUtils.getIndex(modelChangeConsumer.getCurrentModel(), element);

            modelChangeConsumer.execute(new AddChildOperation(sky, index));
        });
    }

    /**
     * Scroll a value.
     */
    private void processScroll(final ScrollEvent event) {
        if (!event.isControlDown()) return;

        final Spinner<Double> source = unsafeCast(event.getSource());
        final double deltaY = event.getDeltaY();

        if (deltaY > 0) {
            source.increment(10);
        } else {
            source.decrement(10);
        }
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
