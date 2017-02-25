package com.ss.editor.ui.control.model.tree.dialog.sky;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static java.util.Objects.requireNonNull;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.control.transform.SceneEditorControl;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.ui.control.choose.ChooseTextureControl;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.AbstractNodeTree;
import com.ss.editor.ui.control.tree.node.ModelNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.util.EditorUtil;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rlib.ui.control.input.FloatTextField;
import rlib.ui.util.FXUtils;

import java.awt.*;
import java.nio.file.Path;

/**
 * The dialog to create sky.
 *
 * @author JavaSaBr
 */
public class CreateSkyDialog extends AbstractSimpleEditorDialog {

    private static final Insets CONTAINER_OFFSET = new Insets(6, CANCEL_BUTTON_OFFSET.getRight(), 20, 0);
    private static final Insets SINGLE_TEXTURE_SITTINGS_OFFSET = new Insets(4, 0, 81, 0);
    private static final Insets MULTIPLY_TEXTURE_SETTINGS_OFFSET = new Insets(4, 0, 0, 0);

    private static final Point DIALOG_SIZE = new Point(580, 317);

    @NotNull
    private static final ObservableList<SkyType> SKY_TYPES_LIST = observableArrayList(SkyType.VALUES);

    @NotNull
    private static final ObservableList<EnvMapType> ENV_MAP_TYPE_LIST = observableArrayList(EnvMapType.values());

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
    protected static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    @NotNull
    protected static final Editor EDITOR = Editor.getInstance();


    private enum SkyType {
        SINGLE_TEXTURE(Messages.CREATE_SKY_DIALOG_SKY_TYPE_SINGLE),
        MULTIPLE_TEXTURE(Messages.CREATE_SKY_DIALOG_SKY_TYPE_MULTIPLE);

        private static final SkyType[] VALUES = values();

        /**
         * The sky name.
         */
        @NotNull
        private final String title;

        SkyType(@NotNull final String title) {
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
     * The list of sky types.
     */
    @Nullable
    private ComboBox<SkyType> skyTypeComboBox;

    /**
     * The scale control for X.
     */
    @Nullable
    private FloatTextField normalScaleXField;

    /**
     * The scale control for Y.
     */
    @Nullable
    private FloatTextField normalScaleYField;

    /**
     * The scale control for Z.
     */
    @Nullable
    private FloatTextField normalScaleZField;

    /**
     * The container of single texture settings.
     */
    @Nullable
    private GridPane singleTextureSettings;

    /**
     * The single texture control.
     */
    @Nullable
    private ChooseTextureControl singleTextureControl;

    /**
     * The list of env types.
     */
    @Nullable
    private ComboBox<EnvMapType> envMapTypeComboBox;

    /**
     * The check box for fliping.
     */
    @Nullable
    private CheckBox flipYCheckBox;

    /**
     * The container of multiply texture settings.
     */
    @Nullable
    private GridPane multipleTextureSettings;

    /**
     * The north texture control.
     */
    @Nullable
    private ChooseTextureControl northTextureControl;

    /**
     * The south texture control.
     */
    @Nullable
    private ChooseTextureControl southTextureControl;

    /**
     * The east texture control.
     */
    @Nullable
    private ChooseTextureControl eastTextureControl;

    /**
     * The west texture control.
     */
    @Nullable
    private ChooseTextureControl westTextureControl;

    /**
     * The top texture control.
     */
    @Nullable
    private ChooseTextureControl topTextureControl;

    /**
     * The bottom texture control.
     */
    @Nullable
    private ChooseTextureControl bottomTextureControl;

    public CreateSkyDialog(@NotNull final ModelNode<?> parentNode,
                           @NotNull final AbstractNodeTree<ModelChangeConsumer> nodeTree) {
        this.parentNode = parentNode;
        this.nodeTree = nodeTree;
        getSkyTypeComboBox().getSelectionModel().select(SkyType.SINGLE_TEXTURE);
        getEnvMapTypeComboBox().getSelectionModel().select(EnvMapType.EquirectMap);
        getNormalScaleXField().setValue(1F);
        getNormalScaleYField().setValue(1F);
        getNormalScaleZField().setValue(1F);
        getFlipYCheckBox().setSelected(true);
    }

    /**
     * @return the list of sky types.
     */
    @NotNull
    private ComboBox<SkyType> getSkyTypeComboBox() {
        return requireNonNull(skyTypeComboBox);
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.CREATE_SKY_DIALOG_TITLE;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        settingsRoot = new VBox();

        createSingleTextureSettings();
        createMultipleTextureSettings();

        final Label skyTypeLabel = new Label(Messages.CREATE_SKY_DIALOG_SKY_TYPE_LABEL + ":");
        skyTypeLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        skyTypeLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        skyTypeComboBox = new ComboBox<>(SKY_TYPES_LIST);
        skyTypeComboBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        skyTypeComboBox.prefWidthProperty().bind(getSingleTextureControl().widthProperty());
        skyTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> processChange(newValue));

        final Label normalScaleLabel = new Label(Messages.CREATE_SKY_DIALOG_NORMAL_SCALE_LABEL + ":");
        normalScaleLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        normalScaleLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        normalScaleXField = new FloatTextField();
        normalScaleXField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        normalScaleXField.prefWidthProperty().bind(singleTextureControl.widthProperty().multiply(0.33));

        normalScaleYField = new FloatTextField();
        normalScaleYField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        normalScaleYField.prefWidthProperty().bind(singleTextureControl.widthProperty().multiply(0.33));

        normalScaleZField = new FloatTextField();
        normalScaleZField.setId(CSSIds.EDITOR_DIALOG_FIELD);
        normalScaleZField.prefWidthProperty().bind(singleTextureControl.widthProperty().multiply(0.33));

        final GridPane baseSettings = new GridPane();
        baseSettings.setId(CSSIds.ABSTRACT_DIALOG_GRID_SETTINGS_CONTAINER);
        baseSettings.add(skyTypeLabel, 0, 0);
        baseSettings.add(skyTypeComboBox, 1, 0, 3, 1);
        baseSettings.add(normalScaleLabel, 0, 1);
        baseSettings.add(normalScaleXField, 1, 1);
        baseSettings.add(normalScaleYField, 2, 1);
        baseSettings.add(normalScaleZField, 3, 1);

        FXUtils.addToPane(baseSettings, settingsRoot);
        FXUtils.addToPane(settingsRoot, root);

        VBox.setMargin(multipleTextureSettings, MULTIPLY_TEXTURE_SETTINGS_OFFSET);
        VBox.setMargin(singleTextureSettings, SINGLE_TEXTURE_SITTINGS_OFFSET);
        VBox.setMargin(settingsRoot, CONTAINER_OFFSET);

        FXUtils.addClassTo(skyTypeLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(skyTypeComboBox, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(normalScaleLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(normalScaleXField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(normalScaleYField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(normalScaleZField, CSSClasses.SPECIAL_FONT_14);
    }

    /**
     * Create multiple textures settings.
     */
    private void createMultipleTextureSettings() {

        multipleTextureSettings = new GridPane();
        multipleTextureSettings.setId(CSSIds.ABSTRACT_DIALOG_GRID_SETTINGS_CONTAINER);

        final Label northTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_NORTH_LABEL + ":");
        northTextureLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        northTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        northTextureControl = new ChooseTextureControl();
        northTextureControl.setChangeHandler(this::validate);

        final Label southTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_SOUTH_LABEL + ":");
        southTextureLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        southTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        southTextureControl = new ChooseTextureControl();
        southTextureControl.setChangeHandler(this::validate);

        final Label eastTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_EAST_LABEL + ":");
        eastTextureLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        eastTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        eastTextureControl = new ChooseTextureControl();
        eastTextureControl.setChangeHandler(this::validate);

        final Label westTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_WEST_LABEL + ":");
        westTextureLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        westTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        westTextureControl = new ChooseTextureControl();
        westTextureControl.setChangeHandler(this::validate);

        final Label topTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_TOP_LABEL + ":");
        topTextureLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        topTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        topTextureControl = new ChooseTextureControl();
        topTextureControl.setChangeHandler(this::validate);

        final Label bottomTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_BOTTOM_LABEL + ":");
        bottomTextureLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        bottomTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        bottomTextureControl = new ChooseTextureControl();
        bottomTextureControl.setChangeHandler(this::validate);

        multipleTextureSettings.add(northTextureLabel, 0, 0, 1, 1);
        multipleTextureSettings.add(northTextureControl, 1, 0, 3, 1);
        multipleTextureSettings.add(southTextureLabel, 0, 1, 1, 1);
        multipleTextureSettings.add(southTextureControl, 1, 1, 3, 1);
        multipleTextureSettings.add(eastTextureLabel, 0, 2, 1, 1);
        multipleTextureSettings.add(eastTextureControl, 1, 2, 3, 1);
        multipleTextureSettings.add(westTextureLabel, 0, 3, 1, 1);
        multipleTextureSettings.add(westTextureControl, 1, 3, 3, 1);
        multipleTextureSettings.add(topTextureLabel, 0, 4, 1, 1);
        multipleTextureSettings.add(topTextureControl, 1, 4, 3, 1);
        multipleTextureSettings.add(bottomTextureLabel, 0, 5, 1, 1);
        multipleTextureSettings.add(bottomTextureControl, 1, 5, 3, 1);

        FXUtils.addClassTo(northTextureLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(southTextureLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(eastTextureLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(westTextureLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(topTextureLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(bottomTextureLabel, CSSClasses.SPECIAL_FONT_14);
    }

    /**
     * Create single texture settings.
     */
    private void createSingleTextureSettings() {

        singleTextureSettings = new GridPane();
        singleTextureSettings.setId(CSSIds.ABSTRACT_DIALOG_GRID_SETTINGS_CONTAINER);

        final Label singleTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_TEXTURE_LABEL + ":");
        singleTextureLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        singleTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        singleTextureControl = new ChooseTextureControl();
        singleTextureControl.setChangeHandler(this::validate);

        final Label envMapTypeLabel = new Label(Messages.CREATE_SKY_DIALOG_TEXTURE_TYPE_LABEL + ":");
        envMapTypeLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        envMapTypeLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        envMapTypeComboBox = new ComboBox<>(ENV_MAP_TYPE_LIST);
        envMapTypeComboBox.setId(CSSIds.EDITOR_DIALOG_FIELD);
        envMapTypeComboBox.prefWidthProperty().bind(singleTextureControl.widthProperty());
        envMapTypeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validate());

        final Label flipYLabel = new Label(Messages.CREATE_SKY_DIALOG_FLIP_Y_LABEL + ":");
        flipYLabel.setId(CSSIds.EDITOR_DIALOG_DYNAMIC_LABEL);
        flipYLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        flipYCheckBox = new CheckBox();
        flipYCheckBox.prefWidthProperty().bind(singleTextureControl.widthProperty());

        singleTextureSettings.add(singleTextureLabel, 0, 0, 1, 1);
        singleTextureSettings.add(singleTextureControl, 1, 0, 3, 1);
        singleTextureSettings.add(envMapTypeLabel, 0, 1, 1, 1);
        singleTextureSettings.add(envMapTypeComboBox, 1, 1, 3, 1);
        singleTextureSettings.add(flipYLabel, 0, 2, 1, 1);
        singleTextureSettings.add(flipYCheckBox, 1, 2, 3, 1);

        FXUtils.addClassTo(singleTextureLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(envMapTypeLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(envMapTypeComboBox, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(flipYLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(flipYCheckBox, CSSClasses.SPECIAL_FONT_14);
    }

    /**
     * @return the settings root.
     */
    @NotNull
    private VBox getSettingsRoot() {
        return requireNonNull(settingsRoot);
    }

    /**
     * @return the container of single texture settings.
     */
    @NotNull
    private GridPane getSingleTextureSettings() {
        return requireNonNull(singleTextureSettings);
    }

    /**
     * @return the container of multiply texture settings.
     */
    @NotNull
    private GridPane getMultipleTextureSettings() {
        return requireNonNull(multipleTextureSettings);
    }

    /**
     * Handle changing sky type.
     */
    private void processChange(@NotNull final SkyType newValue) {

        final VBox settingsRoot = getSettingsRoot();
        final GridPane singleTextureSettings = getSingleTextureSettings();
        final GridPane multiplyTextureSettings = getMultipleTextureSettings();

        final ObservableList<javafx.scene.Node> children = settingsRoot.getChildren();
        children.removeAll(singleTextureSettings, getMultipleTextureSettings());

        switch (newValue) {
            case SINGLE_TEXTURE: {
                children.add(singleTextureSettings);
                break;
            }
            case MULTIPLE_TEXTURE: {
                children.add(multiplyTextureSettings);
            }
        }

        validate();
    }

    /**
     * @return the single texture control.
     */
    @NotNull
    private ChooseTextureControl getSingleTextureControl() {
        return requireNonNull(singleTextureControl);
    }

    /**
     * @return the list of env types.
     */
    @NotNull
    private ComboBox<EnvMapType> getEnvMapTypeComboBox() {
        return requireNonNull(envMapTypeComboBox);
    }

    /**
     * @return the top texture control.
     */
    @NotNull
    private ChooseTextureControl getTopTextureControl() {
        return requireNonNull(topTextureControl);
    }

    /**
     * @return the bottom texture control.
     */
    @NotNull
    private ChooseTextureControl getBottomTextureControl() {
        return requireNonNull(bottomTextureControl);
    }

    /**
     * @return the north texture control.
     */
    @NotNull
    private ChooseTextureControl getNorthTextureControl() {
        return requireNonNull(northTextureControl);
    }

    /**
     * @return the south texture control.
     */
    @NotNull
    private ChooseTextureControl getSouthTextureControl() {
        return requireNonNull(southTextureControl);
    }

    /**
     * @return the east texture control.
     */
    @NotNull
    private ChooseTextureControl getEastTextureControl() {
        return requireNonNull(eastTextureControl);
    }

    /**
     * @return the west texture control.
     */
    @NotNull
    private ChooseTextureControl getWestTextureControl() {
        return requireNonNull(westTextureControl);
    }

    /**
     * Validate the dialog.
     */
    private void validate() {
        if (!isReady()) return;

        final ComboBox<SkyType> skyTypeComboBox = getSkyTypeComboBox();
        final SingleSelectionModel<SkyType> selectionModel = skyTypeComboBox.getSelectionModel();
        final SkyType selectedItem = selectionModel.getSelectedItem();

        final Button okButton = getOkButton();
        okButton.setDisable(true);

        if (selectedItem == SkyType.SINGLE_TEXTURE) {

            final ComboBox<EnvMapType> envMapTypeComboBox = getEnvMapTypeComboBox();
            final EnvMapType envMapType = envMapTypeComboBox.getSelectionModel().getSelectedItem();

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
    @NotNull
    private CheckBox getFlipYCheckBox() {
        return requireNonNull(flipYCheckBox);
    }

    /**
     * @return the scale control for X.
     */
    @NotNull
    private FloatTextField getNormalScaleXField() {
        return requireNonNull(normalScaleXField);
    }

    /**
     * @return the scale control for Y.
     */
    @NotNull
    private FloatTextField getNormalScaleYField() {
        return requireNonNull(normalScaleYField);
    }

    /**
     * @return the scale control for Z.
     */
    @NotNull
    private FloatTextField getNormalScaleZField() {
        return requireNonNull(normalScaleZField);
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

    @Override
    protected void processOk() {
        EditorUtil.incrementLoading();
        EXECUTOR_MANAGER.addBackgroundTask(this::createSkyInBackground);
        super.processOk();
    }

    /**
     * The process of creating a new sky.
     */
    private void createSkyInBackground() {

        final AssetManager assetManager = EDITOR.getAssetManager();

        final AbstractNodeTree<?> nodeTree = getNodeTree();
        final ChangeConsumer changeConsumer = requireNonNull(nodeTree.getChangeConsumer());

        final FloatTextField normalScaleXSpinner = getNormalScaleXField();
        final FloatTextField normalScaleYSpinner = getNormalScaleYField();
        final FloatTextField normalScaleZSpinner = getNormalScaleZField();

        final Vector3f scale = new Vector3f();
        scale.setX(normalScaleXSpinner.getValue());
        scale.setY(normalScaleYSpinner.getValue());
        scale.setZ(normalScaleZSpinner.getValue());

        final ComboBox<SkyType> skyTypeComboBox = getSkyTypeComboBox();
        final SingleSelectionModel<SkyType> selectionModel = skyTypeComboBox.getSelectionModel();
        final SkyType selectedItem = selectionModel.getSelectedItem();

        if (selectedItem == SkyType.SINGLE_TEXTURE) {
            createSingleTexture(assetManager, changeConsumer, scale);
        } else if (selectedItem == SkyType.MULTIPLE_TEXTURE) {
            createMultipleTexture(assetManager, changeConsumer, scale);
        }

        EXECUTOR_MANAGER.addFXTask(EditorUtil::decrementLoading);
    }

    /**
     * Create a new sky using multiply textures.
     */
    private void createMultipleTexture(@NotNull final AssetManager assetManager,
                                       @NotNull final ChangeConsumer changeConsumer, @NotNull final Vector3f scale) {

        final ChooseTextureControl northTextureControl = getNorthTextureControl();
        final Path northTextureFile = requireNonNull(northTextureControl.getTextureFile());

        final ChooseTextureControl southTextureControl = getSouthTextureControl();
        final Path southTextureFile = requireNonNull(southTextureControl.getTextureFile());

        final ChooseTextureControl eastTextureControl = getEastTextureControl();
        final Path eastTextureFile = requireNonNull(eastTextureControl.getTextureFile());

        final ChooseTextureControl westTextureControl = getWestTextureControl();
        final Path westTextureFile = requireNonNull(westTextureControl.getTextureFile());

        final ChooseTextureControl topTextureControl = getTopTextureControl();
        final Path topTextureFile = requireNonNull(topTextureControl.getTextureFile());

        final ChooseTextureControl bottomTextureControl = getBottomTextureControl();
        final Path bottomTextureFile = requireNonNull(bottomTextureControl.getTextureFile());

        final Path northTextureAssetFile = requireNonNull(getAssetFile(northTextureFile));
        final Path southTextureAssetFile = requireNonNull(getAssetFile(southTextureFile));
        final Path eastTextureAssetFile = requireNonNull(getAssetFile(eastTextureFile));
        final Path westTextureAssetFile = requireNonNull(getAssetFile(westTextureFile));
        final Path topTextureAssetFile = requireNonNull(getAssetFile(topTextureFile));
        final Path bottomTextureAssetFile = requireNonNull(getAssetFile(bottomTextureFile));

        final Texture northTexture = assetManager.loadTexture(toAssetPath(northTextureAssetFile));
        final Texture southTexture = assetManager.loadTexture(toAssetPath(southTextureAssetFile));
        final Texture eastTexture = assetManager.loadTexture(toAssetPath(eastTextureAssetFile));
        final Texture westTexture = assetManager.loadTexture(toAssetPath(westTextureAssetFile));
        final Texture topTexture = assetManager.loadTexture(toAssetPath(topTextureAssetFile));
        final Texture bottomTexture = assetManager.loadTexture(toAssetPath(bottomTextureAssetFile));

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial skyModel = SkyFactory.createSky(assetManager, westTexture, eastTexture, northTexture, southTexture, topTexture, bottomTexture, scale);
            skyModel.setUserData(SceneEditorControl.SKY_NODE_KEY, Boolean.TRUE);

            final ModelNode<?> parentNode = getParentNode();
            final Node parent = (Node) parentNode.getElement();

            changeConsumer.execute(new AddChildOperation(skyModel, parent));
        });
    }

    /**
     * Create a new sky using a single texture.
     */
    private void createSingleTexture(@NotNull final AssetManager assetManager,
                                     @NotNull final ChangeConsumer changeConsumer, @NotNull final Vector3f scale) {

        final CheckBox flipYCheckBox = getFlipYCheckBox();
        final boolean flipY = flipYCheckBox.isSelected();

        final ComboBox<EnvMapType> envMapTypeComboBox = getEnvMapTypeComboBox();
        final EnvMapType envMapType = envMapTypeComboBox.getSelectionModel().getSelectedItem();

        final ChooseTextureControl singleTextureControl = getSingleTextureControl();
        final Path textureFile = requireNonNull(singleTextureControl.getTextureFile());
        final Path assetFile = requireNonNull(getAssetFile(textureFile));
        final String assetPath = toAssetPath(assetFile);

        final TextureKey textureKey = new TextureKey(assetPath, flipY);
        textureKey.setGenerateMips(true);

        final Texture texture = assetManager.loadAsset(textureKey);

        if (envMapType == EnvMapType.CubeMap) {
            textureKey.setTextureTypeHint(Texture.Type.CubeMap);
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial sky = SkyFactory.createSky(assetManager, texture, scale, envMapType);
            sky.setUserData(SceneEditorControl.SKY_NODE_KEY, Boolean.TRUE);

            final ModelNode<?> parentNode = getParentNode();
            final Node parent = (Node) parentNode.getElement();

            changeConsumer.execute(new AddChildOperation(sky, parent));
        });
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
