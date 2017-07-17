package com.ss.editor.ui.control.model.tree.dialog.sky;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
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
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.nio.file.Path;

/**
 * The dialog to create sky.
 *
 * @author JavaSaBr
 */
public class CreateSkyDialog extends AbstractSimpleEditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(500, -1);

    @NotNull
    private static final ObservableList<SkyType> SKY_TYPES_LIST = observableArrayList(SkyType.VALUES);

    @NotNull
    private static final ObservableList<EnvMapType> ENV_MAP_TYPE_LIST = observableArrayList(EnvMapType.values());

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


    private enum SkyType {
        /**
         * Single texture sky type.
         */
        SINGLE_TEXTURE(Messages.CREATE_SKY_DIALOG_SKY_TYPE_SINGLE),
        /**
         * Multiple texture sky type.
         */
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

    /**
     * Instantiates a new Create sky dialog.
     *
     * @param parentNode the parent node
     * @param nodeTree   the node tree
     */
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
        return notNull(skyTypeComboBox);
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
        skyTypeLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        skyTypeComboBox = new ComboBox<>(SKY_TYPES_LIST);
        skyTypeComboBox.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));
        skyTypeComboBox.getSelectionModel()
                .selectedItemProperty().
                addListener((observable, oldValue, newValue) -> processChange(newValue));

        final Label normalScaleLabel = new Label(Messages.CREATE_SKY_DIALOG_NORMAL_SCALE_LABEL + ":");
        normalScaleLabel.prefWidthProperty().bind(skyTypeLabel.widthProperty());

        normalScaleXField = new FloatTextField();
        normalScaleYField = new FloatTextField();
        normalScaleZField = new FloatTextField();

        final HBox normalScaleContainer = new HBox(normalScaleXField, normalScaleYField, normalScaleZField);
        normalScaleContainer.prefWidthProperty().bind(skyTypeComboBox.widthProperty());

        final GridPane baseSettings = new GridPane();
        baseSettings.add(skyTypeLabel, 0, 0);
        baseSettings.add(skyTypeComboBox, 1, 0);
        baseSettings.add(normalScaleLabel, 0, 1);
        baseSettings.add(normalScaleContainer, 1, 1);

        FXUtils.addToPane(baseSettings, settingsRoot);
        FXUtils.addToPane(settingsRoot, root);

        settingsRoot.prefWidthProperty().bind(root.widthProperty());
        baseSettings.prefWidthProperty().bind(settingsRoot.widthProperty());
        singleTextureSettings.prefWidthProperty().bind(settingsRoot.widthProperty());
        multipleTextureSettings.prefWidthProperty().bind(settingsRoot.widthProperty());

        FXUtils.addClassesTo(normalScaleContainer, CSSClasses.DEF_HBOX, CSSClasses.TEXT_INPUT_CONTAINER);
        FXUtils.addClassTo(settingsRoot, CSSClasses.DEF_VBOX);
        FXUtils.addClassTo(root, CSSClasses.CREATE_SKY_DIALOG);
        FXUtils.addClassTo(singleTextureSettings, multipleTextureSettings, baseSettings, CSSClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(skyTypeLabel, normalScaleLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(skyTypeComboBox, normalScaleXField, normalScaleYField, normalScaleZField,
                CSSClasses.DIALOG_FIELD);
        FXUtils.addClassTo(normalScaleXField, normalScaleYField, normalScaleZField,
                CSSClasses.TRANSPARENT_TEXT_FIELD);
    }

    /**
     * Create multiple textures settings.
     */
    private void createMultipleTextureSettings() {

        multipleTextureSettings = new GridPane();

        final Label northTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_NORTH_LABEL + ":");
        northTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        northTextureControl = new ChooseTextureControl();
        northTextureControl.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));
        northTextureControl.setChangeHandler(this::validate);

        final Label southTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_SOUTH_LABEL + ":");
        southTextureLabel.prefWidthProperty().bind(northTextureLabel.widthProperty());

        southTextureControl = new ChooseTextureControl();
        southTextureControl.prefWidthProperty().bind(northTextureControl.widthProperty());
        southTextureControl.setChangeHandler(this::validate);

        final Label eastTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_EAST_LABEL + ":");
        eastTextureLabel.prefWidthProperty().bind(northTextureLabel.widthProperty());

        eastTextureControl = new ChooseTextureControl();
        eastTextureControl.prefWidthProperty().bind(northTextureControl.widthProperty());
        eastTextureControl.setChangeHandler(this::validate);

        final Label westTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_WEST_LABEL + ":");
        westTextureLabel.prefWidthProperty().bind(northTextureLabel.widthProperty());

        westTextureControl = new ChooseTextureControl();
        westTextureControl.prefWidthProperty().bind(northTextureControl.widthProperty());
        westTextureControl.setChangeHandler(this::validate);

        final Label topTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_TOP_LABEL + ":");
        topTextureLabel.prefWidthProperty().bind(northTextureLabel.widthProperty());

        topTextureControl = new ChooseTextureControl();
        topTextureControl.prefWidthProperty().bind(northTextureControl.widthProperty());
        topTextureControl.setChangeHandler(this::validate);

        final Label bottomTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_BOTTOM_LABEL + ":");
        bottomTextureLabel.prefWidthProperty().bind(northTextureLabel.widthProperty());

        bottomTextureControl = new ChooseTextureControl();
        bottomTextureControl.prefWidthProperty().bind(northTextureControl.widthProperty());
        bottomTextureControl.setChangeHandler(this::validate);

        multipleTextureSettings.add(northTextureLabel, 0, 0);
        multipleTextureSettings.add(northTextureControl, 1, 0);
        multipleTextureSettings.add(southTextureLabel, 0, 1);
        multipleTextureSettings.add(southTextureControl, 1, 1);
        multipleTextureSettings.add(eastTextureLabel, 0, 2);
        multipleTextureSettings.add(eastTextureControl, 1, 2);
        multipleTextureSettings.add(westTextureLabel, 0, 3);
        multipleTextureSettings.add(westTextureControl, 1, 3);
        multipleTextureSettings.add(topTextureLabel, 0, 4);
        multipleTextureSettings.add(topTextureControl, 1, 4);
        multipleTextureSettings.add(bottomTextureLabel, 0, 5);
        multipleTextureSettings.add(bottomTextureControl, 1, 5);

        FXUtils.addClassTo(northTextureLabel, southTextureLabel, eastTextureLabel, westTextureLabel, topTextureLabel,
                bottomTextureLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
    }

    /**
     * Create single texture settings.
     */
    private void createSingleTextureSettings() {

        singleTextureSettings = new GridPane();

        final Label singleTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_TEXTURE_LABEL + ":");
        singleTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        singleTextureControl = new ChooseTextureControl();
        singleTextureControl.setChangeHandler(this::validate);
        singleTextureControl.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final Label envMapTypeLabel = new Label(Messages.CREATE_SKY_DIALOG_TEXTURE_TYPE_LABEL + ":");
        envMapTypeLabel.prefWidthProperty().bind(singleTextureLabel.widthProperty());

        envMapTypeComboBox = new ComboBox<>(ENV_MAP_TYPE_LIST);
        envMapTypeComboBox.prefWidthProperty().bind(singleTextureControl.widthProperty());
        envMapTypeComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> validate());

        final Label flipYLabel = new Label(Messages.CREATE_SKY_DIALOG_FLIP_Y_LABEL + ":");
        flipYLabel.prefWidthProperty().bind(singleTextureLabel.widthProperty());

        flipYCheckBox = new CheckBox();
        flipYCheckBox.prefWidthProperty().bind(singleTextureControl.widthProperty());

        singleTextureSettings.add(singleTextureLabel, 0, 0);
        singleTextureSettings.add(singleTextureControl, 1, 0);
        singleTextureSettings.add(envMapTypeLabel, 0, 1);
        singleTextureSettings.add(envMapTypeComboBox, 1, 1);
        singleTextureSettings.add(flipYLabel, 0, 2);
        singleTextureSettings.add(flipYCheckBox, 1, 2);

        FXUtils.addClassTo(singleTextureLabel, envMapTypeLabel, flipYLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(envMapTypeComboBox, CSSClasses.DIALOG_FIELD);
    }

    /**
     * @return the settings root.
     */
    @NotNull
    private VBox getSettingsRoot() {
        return notNull(settingsRoot);
    }

    /**
     * @return the container of single texture settings.
     */
    @NotNull
    private GridPane getSingleTextureSettings() {
        return notNull(singleTextureSettings);
    }

    /**
     * @return the container of multiply texture settings.
     */
    @NotNull
    private GridPane getMultipleTextureSettings() {
        return notNull(multipleTextureSettings);
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
        getDialog().sizeToScene();
    }

    /**
     * @return the single texture control.
     */
    @NotNull
    private ChooseTextureControl getSingleTextureControl() {
        return notNull(singleTextureControl);
    }

    /**
     * @return the list of env types.
     */
    @NotNull
    private ComboBox<EnvMapType> getEnvMapTypeComboBox() {
        return notNull(envMapTypeComboBox);
    }

    /**
     * @return the top texture control.
     */
    @NotNull
    private ChooseTextureControl getTopTextureControl() {
        return notNull(topTextureControl);
    }

    /**
     * @return the bottom texture control.
     */
    @NotNull
    private ChooseTextureControl getBottomTextureControl() {
        return notNull(bottomTextureControl);
    }

    /**
     * @return the north texture control.
     */
    @NotNull
    private ChooseTextureControl getNorthTextureControl() {
        return notNull(northTextureControl);
    }

    /**
     * @return the south texture control.
     */
    @NotNull
    private ChooseTextureControl getSouthTextureControl() {
        return notNull(southTextureControl);
    }

    /**
     * @return the east texture control.
     */
    @NotNull
    private ChooseTextureControl getEastTextureControl() {
        return notNull(eastTextureControl);
    }

    /**
     * @return the west texture control.
     */
    @NotNull
    private ChooseTextureControl getWestTextureControl() {
        return notNull(westTextureControl);
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
        return notNull(flipYCheckBox);
    }

    /**
     * @return the scale control for X.
     */
    @NotNull
    private FloatTextField getNormalScaleXField() {
        return notNull(normalScaleXField);
    }

    /**
     * @return the scale control for Y.
     */
    @NotNull
    private FloatTextField getNormalScaleYField() {
        return notNull(normalScaleYField);
    }

    /**
     * @return the scale control for Z.
     */
    @NotNull
    private FloatTextField getNormalScaleZField() {
        return notNull(normalScaleZField);
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
        final ChangeConsumer changeConsumer = notNull(nodeTree.getChangeConsumer());

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
        final Path northTextureFile = notNull(northTextureControl.getTextureFile());

        final ChooseTextureControl southTextureControl = getSouthTextureControl();
        final Path southTextureFile = notNull(southTextureControl.getTextureFile());

        final ChooseTextureControl eastTextureControl = getEastTextureControl();
        final Path eastTextureFile = notNull(eastTextureControl.getTextureFile());

        final ChooseTextureControl westTextureControl = getWestTextureControl();
        final Path westTextureFile = notNull(westTextureControl.getTextureFile());

        final ChooseTextureControl topTextureControl = getTopTextureControl();
        final Path topTextureFile = notNull(topTextureControl.getTextureFile());

        final ChooseTextureControl bottomTextureControl = getBottomTextureControl();
        final Path bottomTextureFile = notNull(bottomTextureControl.getTextureFile());

        final Path northTextureAssetFile = notNull(getAssetFile(northTextureFile));
        final Path southTextureAssetFile = notNull(getAssetFile(southTextureFile));
        final Path eastTextureAssetFile = notNull(getAssetFile(eastTextureFile));
        final Path westTextureAssetFile = notNull(getAssetFile(westTextureFile));
        final Path topTextureAssetFile = notNull(getAssetFile(topTextureFile));
        final Path bottomTextureAssetFile = notNull(getAssetFile(bottomTextureFile));

        final Texture northTexture = assetManager.loadTexture(toAssetPath(northTextureAssetFile));
        final Texture southTexture = assetManager.loadTexture(toAssetPath(southTextureAssetFile));
        final Texture eastTexture = assetManager.loadTexture(toAssetPath(eastTextureAssetFile));
        final Texture westTexture = assetManager.loadTexture(toAssetPath(westTextureAssetFile));
        final Texture topTexture = assetManager.loadTexture(toAssetPath(topTextureAssetFile));
        final Texture bottomTexture = assetManager.loadTexture(toAssetPath(bottomTextureAssetFile));

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final Spatial skyModel = SkyFactory.createSky(assetManager, westTexture, eastTexture, northTexture,
                    southTexture, topTexture, bottomTexture, scale);

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
        final Path textureFile = notNull(singleTextureControl.getTextureFile());
        final Path assetFile = notNull(getAssetFile(textureFile));
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

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
