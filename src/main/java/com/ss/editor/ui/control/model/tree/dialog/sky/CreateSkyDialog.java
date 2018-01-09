package com.ss.editor.ui.control.model.tree.dialog.sky;

import static com.ss.editor.util.EditorUtil.getAssetFile;
import static com.ss.editor.util.EditorUtil.toAssetPath;
import static com.ss.rlib.util.ObjectUtils.notNull;
import static java.nio.file.StandardOpenOption.*;
import static javafx.collections.FXCollections.observableArrayList;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import com.jme3.util.SkyFactory.EnvMapType;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.annotation.BackgroundThread;
import com.ss.editor.annotation.FXThread;
import com.ss.editor.annotation.FromAnyThread;
import com.ss.editor.extension.util.JmbSkyFactory;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.model.undo.editor.ChangeConsumer;
import com.ss.editor.model.undo.editor.ModelChangeConsumer;
import com.ss.editor.serializer.MaterialSerializer;
import com.ss.editor.ui.control.choose.ChooseFolderControl;
import com.ss.editor.ui.control.choose.ChooseTextureControl;
import com.ss.editor.ui.control.model.tree.action.operation.AddChildOperation;
import com.ss.editor.ui.control.tree.NodeTree;
import com.ss.editor.ui.control.tree.node.TreeNode;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.dialog.AbstractSimpleEditorDialog;
import com.ss.editor.ui.util.UIUtils;
import com.ss.editor.util.EditorUtil;
import com.ss.rlib.ui.control.input.FloatTextField;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
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
    private static final ObservableList<EnvMapType> ENV_MAP_TYPE_LIST = observableArrayList(EnvMapType.EquirectMap,
            EnvMapType.SphereMap);

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
    private final TreeNode<?> parentNode;

    /**
     * The node tree.
     */
    @NotNull
    private final NodeTree<?> nodeTree;

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
     * The material folder control.
     */
    @Nullable
    private ChooseFolderControl materialFolderControl;

    /**
     * The material name field.
     */
    @Nullable
    private TextField materialNameField;

    /**
     * The list of env types.
     */
    @Nullable
    private ComboBox<EnvMapType> envMapTypeComboBox;

    /**
     * The check box for flipping.
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

    public CreateSkyDialog(@NotNull final TreeNode<?> parentNode,
                           @NotNull final NodeTree<ModelChangeConsumer> nodeTree) {
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
    @FXThread
    private @NotNull ComboBox<SkyType> getSkyTypeComboBox() {
        return notNull(skyTypeComboBox);
    }

    @Override
    @FromAnyThread
    protected @NotNull String getTitleText() {
        return Messages.CREATE_SKY_DIALOG_TITLE;
    }

    @Override
    @FXThread
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        settingsRoot = new VBox();

        createSingleTextureSettings();
        createMultipleTextureSettings();

        final Label skyTypeLabel = new Label(Messages.CREATE_SKY_DIALOG_SKY_TYPE + ":");
        skyTypeLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        skyTypeComboBox = new ComboBox<>(SKY_TYPES_LIST);
        skyTypeComboBox.setDisable(isEditableSky());
        skyTypeComboBox.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));
        skyTypeComboBox.getSelectionModel()
                .selectedItemProperty().
                addListener((observable, oldValue, newValue) -> processChange(newValue));

        final Label normalScaleLabel = new Label(Messages.CREATE_SKY_DIALOG_NORMAL_SCALE + ":");
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

        if (isEditableSky()) {

            final Label materialFolderLabel = new Label(Messages.CREATE_SKY_DIALOG_MATERIAL_FOLDER + ":");
            materialFolderLabel.prefWidthProperty().bind(skyTypeLabel.widthProperty());

            materialFolderControl = new ChooseFolderControl();
            materialFolderControl.prefWidthProperty().bind(skyTypeComboBox.widthProperty());
            materialFolderControl.setChangeHandler(this::validate);

            final Label materialNameLabel = new Label(Messages.CREATE_SKY_DIALOG_MATERIAL_NAME + ":");
            materialNameLabel.prefWidthProperty().bind(skyTypeLabel.widthProperty());

            materialNameField = new TextField();
            materialNameField.prefWidthProperty().bind(skyTypeComboBox.widthProperty());
            materialNameField.textProperty().addListener((observable, oldValue, newValue) -> validate());

            baseSettings.add(materialFolderLabel, 0, 2);
            baseSettings.add(materialFolderControl, 1, 2);
            baseSettings.add(materialNameLabel, 0, 3);
            baseSettings.add(materialNameField, 1, 3);

            FXUtils.addClassTo(materialFolderLabel, materialNameLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
            FXUtils.addClassTo(materialFolderControl, materialNameField, CSSClasses.DIALOG_FIELD);
        }

        FXUtils.addToPane(baseSettings, settingsRoot);
        FXUtils.addToPane(settingsRoot, root);

        settingsRoot.prefWidthProperty().bind(root.widthProperty());
        baseSettings.prefWidthProperty().bind(settingsRoot.widthProperty());
        singleTextureSettings.prefWidthProperty().bind(settingsRoot.widthProperty());
        multipleTextureSettings.prefWidthProperty().bind(settingsRoot.widthProperty());

        FXUtils.addClassesTo(normalScaleContainer, CSSClasses.DEF_HBOX, CSSClasses.TEXT_INPUT_CONTAINER,
                CSSClasses.DIALOG_FIELD, CSSClasses.ABSTRACT_PARAM_CONTROL_INPUT_CONTAINER);
        FXUtils.addClassTo(settingsRoot, CSSClasses.DEF_VBOX);
        FXUtils.addClassTo(root, CSSClasses.CREATE_SKY_DIALOG);
        FXUtils.addClassTo(singleTextureSettings, multipleTextureSettings, baseSettings, CSSClasses.DEF_GRID_PANE);
        FXUtils.addClassTo(skyTypeLabel, normalScaleLabel, CSSClasses.DIALOG_DYNAMIC_LABEL);
        FXUtils.addClassTo(skyTypeComboBox,CSSClasses.DIALOG_FIELD);
        FXUtils.addClassesTo(normalScaleXField, normalScaleYField, normalScaleZField,
                CSSClasses.ABSTRACT_PARAM_CONTROL_VECTOR3F_FIELD, CSSClasses.TRANSPARENT_TEXT_FIELD);

        UIUtils.addFocusBinding(normalScaleContainer, normalScaleXField, normalScaleYField, normalScaleZField);
    }

    /**
     * Create multiple textures settings.
     */
    @FXThread
    private void createMultipleTextureSettings() {

        multipleTextureSettings = new GridPane();

        final Label northTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_NORTH + ":");
        northTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        northTextureControl = new ChooseTextureControl();
        northTextureControl.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));
        northTextureControl.setChangeHandler(this::validate);

        final Label southTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_SOUTH + ":");
        southTextureLabel.prefWidthProperty().bind(northTextureLabel.widthProperty());

        southTextureControl = new ChooseTextureControl();
        southTextureControl.prefWidthProperty().bind(northTextureControl.widthProperty());
        southTextureControl.setChangeHandler(this::validate);

        final Label eastTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_EAST + ":");
        eastTextureLabel.prefWidthProperty().bind(northTextureLabel.widthProperty());

        eastTextureControl = new ChooseTextureControl();
        eastTextureControl.prefWidthProperty().bind(northTextureControl.widthProperty());
        eastTextureControl.setChangeHandler(this::validate);

        final Label westTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_WEST + ":");
        westTextureLabel.prefWidthProperty().bind(northTextureLabel.widthProperty());

        westTextureControl = new ChooseTextureControl();
        westTextureControl.prefWidthProperty().bind(northTextureControl.widthProperty());
        westTextureControl.setChangeHandler(this::validate);

        final Label topTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_TOP + ":");
        topTextureLabel.prefWidthProperty().bind(northTextureLabel.widthProperty());

        topTextureControl = new ChooseTextureControl();
        topTextureControl.prefWidthProperty().bind(northTextureControl.widthProperty());
        topTextureControl.setChangeHandler(this::validate);

        final Label bottomTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_BOTTOM + ":");
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
     * @return true id need to use SS factory.
     */
    @FXThread
    protected boolean isEditableSky() {
        return false;
    }

    /**
     * Create single texture settings.
     */
    @FXThread
    private void createSingleTextureSettings() {

        singleTextureSettings = new GridPane();

        final Label singleTextureLabel = new Label(Messages.CREATE_SKY_DIALOG_TEXTURE + ":");
        singleTextureLabel.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_LABEL_W_PERCENT));

        singleTextureControl = new ChooseTextureControl();
        singleTextureControl.setChangeHandler(this::validate);
        singleTextureControl.prefWidthProperty().bind(widthProperty().multiply(DEFAULT_FIELD_W_PERCENT));

        final Label envMapTypeLabel = new Label(Messages.CREATE_SKY_DIALOG_TEXTURE_TYPE + ":");
        envMapTypeLabel.prefWidthProperty().bind(singleTextureLabel.widthProperty());

        envMapTypeComboBox = new ComboBox<>(ENV_MAP_TYPE_LIST);
        envMapTypeComboBox.prefWidthProperty().bind(singleTextureControl.widthProperty());
        envMapTypeComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> validate());

        final Label flipYLabel = new Label(Messages.CREATE_SKY_DIALOG_FLIP_Y + ":");
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
    @FXThread
    private @NotNull VBox getSettingsRoot() {
        return notNull(settingsRoot);
    }

    /**
     * @return the container of single texture settings.
     */
    @FXThread
    private @NotNull GridPane getSingleTextureSettings() {
        return notNull(singleTextureSettings);
    }

    /**
     * @return the container of multiply texture settings.
     */
    @FXThread
    private @NotNull GridPane getMultipleTextureSettings() {
        return notNull(multipleTextureSettings);
    }

    /**
     * Handle changing sky type.
     */
    @FXThread
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
    @FXThread
    private @NotNull ChooseTextureControl getSingleTextureControl() {
        return notNull(singleTextureControl);
    }

    /**
     * @return the list of env types.
     */
    @FXThread
    private @NotNull ComboBox<EnvMapType> getEnvMapTypeComboBox() {
        return notNull(envMapTypeComboBox);
    }

    /**
     * @return the material folder control.
     */
    @FXThread
    private @NotNull ChooseFolderControl getMaterialFolderControl() {
        return notNull(materialFolderControl);
    }

    /**
     * @return the material name field.
     */
    @FXThread
    private @NotNull TextField getMaterialNameField() {
        return notNull(materialNameField);
    }

    /**
     * @return the top texture control.
     */
    @FXThread
    private @NotNull ChooseTextureControl getTopTextureControl() {
        return notNull(topTextureControl);
    }

    /**
     * @return the bottom texture control.
     */
    @FXThread
    private @NotNull ChooseTextureControl getBottomTextureControl() {
        return notNull(bottomTextureControl);
    }

    /**
     * @return the north texture control.
     */
    @FXThread
    private @NotNull ChooseTextureControl getNorthTextureControl() {
        return notNull(northTextureControl);
    }

    /**
     * @return the south texture control.
     */
    @FXThread
    private @NotNull ChooseTextureControl getSouthTextureControl() {
        return notNull(southTextureControl);
    }

    /**
     * @return the east texture control.
     */
    @FXThread
    private @NotNull ChooseTextureControl getEastTextureControl() {
        return notNull(eastTextureControl);
    }

    /**
     * @return the west texture control.
     */
    @FXThread
    private @NotNull ChooseTextureControl getWestTextureControl() {
        return notNull(westTextureControl);
    }

    /**
     * Validate the dialog.
     */
    @FXThread
    private void validate() {
        if (!isReady()) return;

        final ComboBox<SkyType> skyTypeComboBox = getSkyTypeComboBox();
        final SingleSelectionModel<SkyType> selectionModel = skyTypeComboBox.getSelectionModel();
        final SkyType selectedItem = selectionModel.getSelectedItem();

        final Button okButton = getOkButton();
        okButton.setDisable(true);

        if (isEditableSky()) {

            final ChooseFolderControl materialFolderControl = getMaterialFolderControl();
            final TextField materialNameField = getMaterialNameField();

            final boolean valid = materialFolderControl.getFolder() != null &&
                    !StringUtils.isEmpty(materialNameField.getText());

            if (!valid) {
                okButton.setDisable(true);
                return;
            }
        }

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
     * @return the check box for flipping.
     */
    @FXThread
    private @NotNull CheckBox getFlipYCheckBox() {
        return notNull(flipYCheckBox);
    }

    /**
     * @return the scale control for X.
     */
    @FXThread
    private @NotNull FloatTextField getNormalScaleXField() {
        return notNull(normalScaleXField);
    }

    /**
     * @return the scale control for Y.
     */
    @FXThread
    private @NotNull FloatTextField getNormalScaleYField() {
        return notNull(normalScaleYField);
    }

    /**
     * @return the scale control for Z.
     */
    @FXThread
    private @NotNull FloatTextField getNormalScaleZField() {
        return notNull(normalScaleZField);
    }

    /**
     * @return the node tree.
     */
    @FXThread
    private @NotNull NodeTree<?> getNodeTree() {
        return nodeTree;
    }

    /**
     * @return the parent node.
     */
    @FXThread
    private @NotNull TreeNode<?> getParentNode() {
        return parentNode;
    }

    @Override
    @FXThread
    protected void processOk() {
        EditorUtil.incrementLoading();

        EXECUTOR_MANAGER.addBackgroundTask(() -> {

            try {
                createSkyInBackground();
            } catch (final Exception e) {
                EditorUtil.handleException(LOGGER, this, e);
            }

            EXECUTOR_MANAGER.addFXTask(EditorUtil::decrementLoading);
        });

        super.processOk();
    }

    /**
     * The process of creating a new sky.
     */
    @FXThread
    private void createSkyInBackground() {

        final AssetManager assetManager = EDITOR.getAssetManager();

        final NodeTree<?> nodeTree = getNodeTree();
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
    }

    /**
     * Create a new sky using multiply textures.
     */
    @FXThread
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

        final Geometry sky;

        if (isEditableSky()) {
            sky = (Geometry) JmbSkyFactory.createSky(assetManager, westTexture, eastTexture, northTexture, southTexture,
                    topTexture, bottomTexture, scale);
        } else {
            sky = (Geometry) SkyFactory.createSky(assetManager, westTexture, eastTexture, northTexture, southTexture,
                    topTexture, bottomTexture, scale);
        }

        final TreeNode<?> parentNode = getParentNode();
        final Node parent = (Node) parentNode.getElement();

        changeConsumer.execute(new AddChildOperation(sky, parent));
    }

    /**
     * Create a new sky using a single texture.
     */
    @FXThread
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
        final Geometry sky;

        if (isEditableSky()) {
            sky = (Geometry) JmbSkyFactory.createSky(assetManager, texture, scale, envMapType);
            sky.setMaterial(createMaterialFileIfNeed(sky));
        } else {
            sky = (Geometry) SkyFactory.createSky(assetManager, texture, scale, envMapType);
        }

        final TreeNode<?> parentNode = getParentNode();
        final Node parent = (Node) parentNode.getElement();

        changeConsumer.execute(new AddChildOperation(sky, parent));
    }

    /**
     * Create a material of the geometry as a file if need.
     *
     * @param geometry the sky geometry.
     */
    @BackgroundThread
    private @NotNull Material createMaterialFileIfNeed(@NotNull final Geometry geometry) {

        final TextField materialNameField = getMaterialNameField();
        final ChooseFolderControl materialFolderControl = getMaterialFolderControl();

        final Material material = geometry.getMaterial();
        final String content = MaterialSerializer.serializeToString(material);

        final Path folder = materialFolderControl.getFolder();
        final Path materialFile = folder.resolve(materialNameField.getText() + "." + FileExtensions.JME_MATERIAL);

        try {
            Files.write(materialFile, content.getBytes("UTF-8"), WRITE, TRUNCATE_EXISTING, CREATE);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }

        final Path assetFile = EditorUtil.getAssetFile(materialFile);
        final String assetPath = EditorUtil.toAssetPath(assetFile);
        final AssetManager assetManager = EDITOR.getAssetManager();

        return assetManager.loadMaterial(assetPath);
    }

    @Override
    @FromAnyThread
    protected @NotNull Point getSize() {
        return DIALOG_SIZE;
    }
}
