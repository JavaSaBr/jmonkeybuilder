package com.ss.editor.ui.dialog;

import static com.ss.rlib.util.ObjectUtils.notNull;
import com.jme3.math.Vector3f;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.ToneMapFilter;
import com.jme3x.jfx.injfx.processor.FrameTransferSceneProcessor;
import com.ss.editor.Editor;
import com.ss.editor.JFXApplication;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ClasspathManager;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.ResourceManager;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CssColorTheme;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.ui.util.DynamicIconSupport;
import com.ss.editor.util.OpenGLVersion;
import com.ss.rlib.ui.control.input.IntegerTextField;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.SpinnerValueFactory.DoubleSpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;

/**
 * The dialog with settings.
 *
 * @author JavaSaBr
 */
public class SettingsDialog extends EditorDialog {

    @NotNull
    private static final Point DIALOG_SIZE = new Point(600, -1);

    @NotNull
    private static final Array<Integer> ANISOTROPYCS = ArrayFactory.newArray(Integer.class);

    @NotNull
    private static final Array<CssColorTheme> THEMES = ArrayFactory.newArray(CssColorTheme.class);

    @NotNull
    private static final Array<OpenGLVersion> GL_VERSIONS = ArrayFactory.newArray(OpenGLVersion.class);

    @NotNull
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();

    @NotNull
    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();

    @NotNull
    private static final Editor EDITOR = Editor.getInstance();

    static {
        ANISOTROPYCS.add(0);
        ANISOTROPYCS.add(2);
        ANISOTROPYCS.add(4);
        ANISOTROPYCS.add(8);
        ANISOTROPYCS.add(16);
        THEMES.add(CssColorTheme.LIGHT);
        THEMES.add(CssColorTheme.DARK);
        GL_VERSIONS.addAll(OpenGLVersion.values());
    }

    /**
     * The message label.
     */
    @Nullable
    private Label messageLabel;

    /**
     * The list with anisotropy levels.
     */
    @Nullable
    private ComboBox<Integer> anisotropyComboBox;

    /**
     * The list with themes.
     */
    @Nullable
    private ComboBox<CssColorTheme> themeComboBox;

    /**
     * The list with open GL versions.
     */
    @Nullable
    private ComboBox<OpenGLVersion> openGLVersionComboBox;

    /**
     * The white point X.
     */
    @Nullable
    private Spinner<Double> toneMapFilterWhitePointX;

    /**
     * The white point Y.
     */
    @Nullable
    private Spinner<Double> toneMapFilterWhitePointY;

    /**
     * The white point Z.
     */
    @Nullable
    private Spinner<Double> toneMapFilterWhitePointZ;

    /**
     * The gamma correction checkbox.
     */
    @Nullable
    private CheckBox gammaCorrectionCheckBox;

    /**
     * The tone map filter checkbox.
     */
    @Nullable
    private CheckBox toneMapFilterCheckBox;

    /**
     * The FXAA checkbox.
     */
    @Nullable
    private CheckBox fxaaFilterCheckBox;

    /**
     * The Stop Render On Lost Focus checkbox.
     */
    @Nullable
    private CheckBox stopRenderOnLostFocusCheckBox;

    /**
     * The checkbox for enabling google analytics.
     */
    @Nullable
    private CheckBox googleAnalyticsCheckBox;

    /**
     * The checkbox for enabling auto tangent generating.
     */
    @Nullable
    private CheckBox autoTangentGeneratingCheckBox;

    /**
     * The checkbox for enabling use flip texture by default.
     */
    @Nullable
    private CheckBox defaultUseFlippedTextureCheckBox;

    /**
     * The checkbox for enabling camera lamp by default.
     */
    @Nullable
    private CheckBox defaultCameraLampEnabledCheckBox;

    /**
     * The additional classpath field.
     */
    @Nullable
    private TextField additionalClasspathField;

    /**
     * The additional envs field.
     */
    @Nullable
    private TextField additionalEnvsField;

    /**
     * The frame rate field.
     */
    @Nullable
    private IntegerTextField frameRateField;

    /**
     * The camera angle field.
     */
    @Nullable
    private IntegerTextField cameraAngleField;

    /**
     * The additional classpath folder.
     */
    @Nullable
    private Path additionalClasspathFolder;

    /**
     * The additional envs folder.
     */
    @Nullable
    private Path additionalEnvsFolder;

    /**
     * The flag of ignoring listeners.
     */
    private boolean ignoreListeners;

    public SettingsDialog() {
        FXUtils.addClassTo(getContainer(), CSSClasses.SETTINGS_DIALOG);
    }

    @Override
    public void show(@NotNull final Window owner) {
        super.show(owner);
        setIgnoreListeners(true);
        try {
            load();
        } finally {
            setIgnoreListeners(false);
        }
        EXECUTOR_MANAGER.addFXTask(() -> getDialog().sizeToScene());
    }

    /**
     * @param ignoreListeners the flag of ignoring listeners.
     */
    private void setIgnoreListeners(final boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return true of the listeners are ignored.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    @Override
    protected void createContent(@NotNull final VBox root) {
        super.createContent(root);

        messageLabel = new Label();

        final VBox graphicsRoot = new VBox();
        graphicsRoot.prefHeightProperty().bind(root.heightProperty());

        final VBox otherRoot = new VBox();
        otherRoot.prefHeightProperty().bind(root.heightProperty());

        final Tab graphicsSettings = new Tab(Messages.SETTINGS_DIALOG_TAB_GRAPHICS);
        graphicsSettings.setClosable(false);
        graphicsSettings.setContent(graphicsRoot);

        final Tab otherSettings = new Tab(Messages.SETTINGS_DIALOG_TAB_OTHER);
        otherSettings.setClosable(false);
        otherSettings.setContent(otherRoot);

        final TabPane tabPane = new TabPane();
        tabPane.getTabs().addAll(graphicsSettings, otherSettings);
        tabPane.prefWidthProperty().bind(widthProperty());
        tabPane.prefHeightProperty().bind(heightProperty());

        createOpenGLControl(graphicsRoot);
        createAnisotropyControl(graphicsRoot);
        createGammaCorrectionControl(graphicsRoot);
        createFrameRateControl(graphicsRoot);
        createCameraAngleControl(graphicsRoot);
        createFXAAControl(graphicsRoot);
        createStopRenderControl(graphicsRoot);
        createToneMapFilterControl(graphicsRoot);
        createToneMapFilterWhitePointControl(graphicsRoot);

        createThemeControl(otherRoot);
        createAdditionalClasspathControl(otherRoot);
        createAdditionalEnvsControl(otherRoot);
        createGoogleAnalyticsControl(otherRoot);
        createAutoTangentGeneratingControl(otherRoot);
        createUseFlippedTextureDefaultControl(otherRoot);
        createDefaultCameraLampEnabledControl(otherRoot);

        FXUtils.bindFixedWidth(messageLabel, root.widthProperty());
        FXUtils.addClassTo(messageLabel, CSSClasses.SETTINGS_DIALOG_MESSAGE_LABEL);
        FXUtils.addClassTo(graphicsRoot, otherRoot, CSSClasses.DEF_VBOX);

        FXUtils.addToPane(tabPane, root);
        FXUtils.addToPane(messageLabel, root);
    }

    /**
     * Create the additional classpath control.
     */
    private void createAdditionalClasspathControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_CLASSPATH_FOLDER_LABEL + ":");
        final HBox fieldContainer = new HBox();

        additionalClasspathField = new TextField();
        additionalClasspathField.setEditable(false);
        additionalClasspathField.prefWidthProperty().bind(root.widthProperty());

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> processAddCF());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemoveCF());
        removeButton.disableProperty().bind(additionalClasspathField.textProperty().isEmpty());

        FXUtils.addToPane(label, fieldContainer, container);
        FXUtils.addToPane(additionalClasspathField, addButton, removeButton, fieldContainer);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(additionalClasspathField, CSSClasses.TRANSPARENT_TEXT_FIELD);
        FXUtils.addClassTo(fieldContainer, CSSClasses.TEXT_INPUT_CONTAINER);
        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(additionalClasspathField, fieldContainer, CSSClasses.SETTINGS_DIALOG_FIELD);
        FXUtils.addClassesTo(addButton, removeButton, CSSClasses.FLAT_BUTTON,
                CSSClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(addButton, removeButton);
    }

    /**
     * Create the additional envs control.
     */
    private void createAdditionalEnvsControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_ENVS_FOLDER_LABEL + ":");
        final HBox fieldContainer = new HBox();

        additionalEnvsField = new TextField();
        additionalEnvsField.setEditable(false);
        additionalEnvsField.prefWidthProperty().bind(root.widthProperty());

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> processAddEF());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemoveEF());
        removeButton.disableProperty().bind(additionalEnvsField.textProperty().isEmpty());

        FXUtils.addToPane(label, fieldContainer, container);
        FXUtils.addToPane(additionalEnvsField, addButton, removeButton, fieldContainer);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(additionalEnvsField, CSSClasses.TRANSPARENT_TEXT_FIELD);
        FXUtils.addClassTo(fieldContainer, CSSClasses.TEXT_INPUT_CONTAINER);
        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(additionalEnvsField, fieldContainer, CSSClasses.SETTINGS_DIALOG_FIELD);
        FXUtils.addClassesTo(addButton, removeButton, CSSClasses.FLAT_BUTTON,
                CSSClasses.INPUT_CONTROL_TOOLBAR_BUTTON);

        DynamicIconSupport.addSupport(addButton, removeButton);
    }

    /**
     * Process of removing the additional classpath.
     */
    private void processRemoveCF() {
        setAdditionalClasspathFolder(null);

        final TextField textField = getAdditionalClasspathField();
        textField.setText(StringUtils.EMPTY);
    }

    /**
     * Process of removing the additional envs.
     */
    private void processRemoveEF() {
        setAdditionalEnvsFolder(null);

        final TextField textField = getAdditionalEnvsField();
        textField.setText(StringUtils.EMPTY);
    }

    /**
     * @return the additional classpath field.
     */
    @NotNull
    private TextField getAdditionalClasspathField() {
        return notNull(additionalClasspathField);
    }

    /**
     * @return the additional envs field.
     */
    @NotNull
    private TextField getAdditionalEnvsField() {
        return notNull(additionalEnvsField);
    }

    /**
     * Process of adding the additional classpath.
     */
    private void processAddCF() {

        final DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(Messages.SETTINGS_DIALOG_CLASSPATH_FOLDER_CHOOSER_TITLE);

        final EditorConfig config = EditorConfig.getInstance();
        final Path currentAdditionalCP = config.getAdditionalClasspath();
        final File currentFolder = currentAdditionalCP == null ? null : currentAdditionalCP.toFile();
        if (currentFolder != null) chooser.setInitialDirectory(currentFolder);

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final File folder = chooser.showDialog(scene.getWindow());
        if (folder == null) return;

        setAdditionalClasspathFolder(folder.toPath());

        final TextField textField = getAdditionalClasspathField();
        textField.setText(folder.toString());
    }

    /**
     * Process of adding the additional envs.
     */
    private void processAddEF() {

        final DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(Messages.SETTINGS_DIALOG_ENVS_FOLDER_CHOOSER_TITLE);

        final EditorConfig config = EditorConfig.getInstance();
        final Path currentAdditionalEnvs = config.getAdditionalEnvs();
        final File currentFolder = currentAdditionalEnvs == null ? null : currentAdditionalEnvs.toFile();
        if (currentFolder != null) chooser.setInitialDirectory(currentFolder);

        final EditorFXScene scene = JFX_APPLICATION.getScene();
        final File folder = chooser.showDialog(scene.getWindow());
        if (folder == null) return;

        setAdditionalEnvsFolder(folder.toPath());

        final TextField textField = getAdditionalEnvsField();
        textField.setText(folder.toString());
    }

    /**
     * Create gamma correction control.
     */
    private void createGammaCorrectionControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label gammaCorrectionLabel = new Label(Messages.SETTINGS_DIALOG_GAMMA_CORRECTION + ":");

        gammaCorrectionCheckBox = new CheckBox();
        gammaCorrectionCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(gammaCorrectionLabel, container);
        FXUtils.addToPane(gammaCorrectionCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(gammaCorrectionLabel, gammaCorrectionCheckBox, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(gammaCorrectionLabel, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(gammaCorrectionCheckBox, CSSClasses.SETTINGS_DIALOG_FIELD);
    }

    /**
     * Create tonemap filter control.
     */
    private void createToneMapFilterControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label toneMapFilterLabel = new Label(Messages.SETTINGS_DIALOG_TONEMAP_FILTER + ":");

        toneMapFilterCheckBox = new CheckBox();
        toneMapFilterCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(toneMapFilterLabel, container);
        FXUtils.addToPane(toneMapFilterCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(toneMapFilterLabel, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(toneMapFilterCheckBox, CSSClasses.SETTINGS_DIALOG_FIELD);
    }

    /**
     * Create white point control.
     */
    private void createToneMapFilterWhitePointControl(@NotNull final VBox root) {

        final CheckBox filterCheckBox = getToneMapFilterCheckBox();

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);
        container.disableProperty().bind(filterCheckBox.selectedProperty().not());

        final Label label = new Label(Messages.SETTINGS_DIALOG_TONEMAP_FILTER_WHITE_POINT + ":");

        final HBox fieldContainer = new HBox();
        fieldContainer.prefWidthProperty().bind(cameraAngleField.widthProperty());

        final Label xLabel = new Label("X:");
        final Label yLabel = new Label("Y:");
        final Label zLabel = new Label("Z:");

        SpinnerValueFactory<Double> valueFactory = new DoubleSpinnerValueFactory(-30, 30, 0, 0.1);

        toneMapFilterWhitePointX = new Spinner<>();
        toneMapFilterWhitePointX.setValueFactory(valueFactory);
        toneMapFilterWhitePointX.setEditable(true);
        toneMapFilterWhitePointX.setOnScroll(event -> processScroll(toneMapFilterWhitePointX, event));
        toneMapFilterWhitePointX.valueProperty().addListener((observable, oldValue, newValue) -> validate());
        toneMapFilterWhitePointX.prefWidthProperty().bind(fieldContainer.widthProperty().multiply(0.3));

        valueFactory = new DoubleSpinnerValueFactory(-30, 30, 0, 0.1);

        toneMapFilterWhitePointY = new Spinner<>();
        toneMapFilterWhitePointY.setValueFactory(valueFactory);
        toneMapFilterWhitePointY.setEditable(true);
        toneMapFilterWhitePointY.setOnScroll(event -> processScroll(toneMapFilterWhitePointY, event));
        toneMapFilterWhitePointY.valueProperty().addListener((observable, oldValue, newValue) -> validate());
        toneMapFilterWhitePointY.prefWidthProperty().bind(fieldContainer.widthProperty().multiply(0.3));

        valueFactory = new DoubleSpinnerValueFactory(-30, 30, 0, 0.1);

        toneMapFilterWhitePointZ = new Spinner<>();
        toneMapFilterWhitePointZ.setValueFactory(valueFactory);
        toneMapFilterWhitePointZ.setEditable(true);
        toneMapFilterWhitePointZ.setOnScroll(event -> processScroll(toneMapFilterWhitePointZ, event));
        toneMapFilterWhitePointZ.valueProperty().addListener((observable, oldValue, newValue) -> validate());
        toneMapFilterWhitePointZ.prefWidthProperty().bind(fieldContainer.widthProperty().multiply(0.3));

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(fieldContainer, container);
        FXUtils.addToPane(xLabel, fieldContainer);
        FXUtils.addToPane(toneMapFilterWhitePointX, fieldContainer);
        FXUtils.addToPane(yLabel, fieldContainer);
        FXUtils.addToPane(toneMapFilterWhitePointY, fieldContainer);
        FXUtils.addToPane(zLabel, fieldContainer);
        FXUtils.addToPane(toneMapFilterWhitePointZ, fieldContainer);
        FXUtils.addToPane(container, root);

        FXUtils.addClassesTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(xLabel, yLabel, zLabel, CSSClasses.SETTINGS_DIALOG_SHORT_LABEL);
        FXUtils.addClassTo(fieldContainer, toneMapFilterWhitePointX, toneMapFilterWhitePointY, toneMapFilterWhitePointZ,
                CSSClasses.SETTINGS_DIALOG_FIELD);

        FXUtils.addClassTo(toneMapFilterWhitePointX, toneMapFilterWhitePointY,
                toneMapFilterWhitePointZ, CSSClasses.TRANSPARENT_SPINNER);

        FXUtils.addClassTo(fieldContainer, CSSClasses.TEXT_INPUT_CONTAINER);
    }

    /**
     * The process of scrolling.
     */
    private void processScroll(@NotNull final Spinner<Double> spinner, @NotNull final ScrollEvent event) {
        if (!event.isControlDown()) return;

        final double deltaY = event.getDeltaY();

        if (deltaY > 0) {
            spinner.increment(1);
        } else {
            spinner.decrement(1);
        }
    }

    /**
     * Create FXAA control.
     */
    private void createFXAAControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_FXAA + ":");

        fxaaFilterCheckBox = new CheckBox();
        fxaaFilterCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(fxaaFilterCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(fxaaFilterCheckBox, CSSClasses.SETTINGS_DIALOG_FIELD);
    }

    /**
     * Create stop render control.
     */
    private void createStopRenderControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_STOP_RENDER_ON_LOST_FOCUS + ":");

        stopRenderOnLostFocusCheckBox = new CheckBox();
        stopRenderOnLostFocusCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(stopRenderOnLostFocusCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(stopRenderOnLostFocusCheckBox, CSSClasses.SETTINGS_DIALOG_FIELD);
    }

    /**
     * Create the checkbox for configuring enabling google analytics.
     */
    private void createGoogleAnalyticsControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_GOOGLE_ANALYTICS + ":");

        googleAnalyticsCheckBox = new CheckBox();
        googleAnalyticsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(googleAnalyticsCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(googleAnalyticsCheckBox, CSSClasses.SETTINGS_DIALOG_FIELD);
    }

    /**
     * Create the checkbox for configuring auto tangent generating.
     */
    private void createAutoTangentGeneratingControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_AUTO_TANGENT_GENERATING + ":");

        autoTangentGeneratingCheckBox = new CheckBox();
        autoTangentGeneratingCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(autoTangentGeneratingCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(autoTangentGeneratingCheckBox, CSSClasses.SETTINGS_DIALOG_FIELD);
    }

    /**
     * Create the checkbox for configuring using flip textures by default.
     */
    private void createUseFlippedTextureDefaultControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_DEFAULT_FLIPPED_TEXTURE + ":");

        defaultUseFlippedTextureCheckBox = new CheckBox();
        defaultUseFlippedTextureCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(defaultUseFlippedTextureCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(defaultUseFlippedTextureCheckBox, CSSClasses.SETTINGS_DIALOG_FIELD);
    }

    /**
     * Create the checkbox for configuring enabling camera lamp by default.
     */
    private void createDefaultCameraLampEnabledControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_DEFAULT_EDITOR_CAMERA_LAMP_ENABLED + ":");

        defaultCameraLampEnabledCheckBox = new CheckBox();
        defaultCameraLampEnabledCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(defaultCameraLampEnabledCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(defaultCameraLampEnabledCheckBox, CSSClasses.SETTINGS_DIALOG_FIELD);
    }

    /**
     * Create the anisotropy control
     */
    private void createAnisotropyControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_ANISOTROPY + ":");

        anisotropyComboBox = new ComboBox<>();
        anisotropyComboBox.prefWidthProperty().bind(root.widthProperty());
        anisotropyComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(anisotropyComboBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(anisotropyComboBox, CSSClasses.SETTINGS_DIALOG_FIELD);

        final ObservableList<Integer> items = anisotropyComboBox.getItems();
        items.addAll(ANISOTROPYCS);
    }

    /**
     * Create the theme control
     */
    private void createThemeControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_THEME_LABEL + ":");

        themeComboBox = new ComboBox<>();
        themeComboBox.prefWidthProperty().bind(root.widthProperty());
        themeComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(themeComboBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(themeComboBox, CSSClasses.SETTINGS_DIALOG_FIELD);

        final ObservableList<CssColorTheme> items = themeComboBox.getItems();
        items.addAll(THEMES);
    }

    /**
     * Create the open GL control
     */
    private void createOpenGLControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_OPEN_GL_LABEL + ":");

        openGLVersionComboBox = new ComboBox<>();
        openGLVersionComboBox.prefWidthProperty().bind(root.widthProperty());
        openGLVersionComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(openGLVersionComboBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(openGLVersionComboBox, CSSClasses.SETTINGS_DIALOG_FIELD);

        final ObservableList<OpenGLVersion> items = openGLVersionComboBox.getItems();
        items.addAll(GL_VERSIONS);
    }

    /**
     * Create the frame rate control.
     */
    private void createFrameRateControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_FRAME_RATE + ":");

        frameRateField = new IntegerTextField();
        frameRateField.prefWidthProperty().bind(root.widthProperty());
        frameRateField.setMinMax(5, 100);
        frameRateField.addChangeListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(frameRateField, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(frameRateField, CSSClasses.SETTINGS_DIALOG_FIELD);
    }

    /**
     * Create the camera angle control.
     */
    private void createCameraAngleControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_CAMERA_ANGLE + ":");

        cameraAngleField = new IntegerTextField();
        cameraAngleField.prefWidthProperty().bind(root.widthProperty());
        cameraAngleField.setMinMax(30, 160);
        cameraAngleField.addChangeListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(cameraAngleField, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SETTINGS_DIALOG_LABEL);
        FXUtils.addClassTo(cameraAngleField, CSSClasses.SETTINGS_DIALOG_FIELD);
    }

    /**
     * @return the gamma correction checkbox.
     */
    @NotNull
    private CheckBox getGammaCorrectionCheckBox() {
        return notNull(gammaCorrectionCheckBox);
    }

    /**
     * @return the tone map filter checkbox.
     */
    @NotNull
    private CheckBox getToneMapFilterCheckBox() {
        return notNull(toneMapFilterCheckBox);
    }

    /**
     * @return the white point X.
     */
    @NotNull
    private Spinner<Double> getToneMapFilterWhitePointX() {
        return notNull(toneMapFilterWhitePointX);
    }

    /**
     * @return the white point Y.
     */
    @NotNull
    private Spinner<Double> getToneMapFilterWhitePointY() {
        return notNull(toneMapFilterWhitePointY);
    }

    /**
     * @return the white point Z.
     */
    @NotNull
    private Spinner<Double> getToneMapFilterWhitePointZ() {
        return notNull(toneMapFilterWhitePointZ);
    }

    /**
     * @return the FXAA checkbox.
     */
    @NotNull
    private CheckBox getFXAAFilterCheckBox() {
        return notNull(fxaaFilterCheckBox);
    }

    /**
     * @return the Stop Render On Lost Focus checkbox.
     */
    @NotNull
    private CheckBox getStopRenderOnLostFocusCheckBox() {
        return notNull(stopRenderOnLostFocusCheckBox);
    }

    /**
     * @return the list with anisotropy levels.
     */
    @NotNull
    private ComboBox<Integer> getAnisotropyComboBox() {
        return notNull(anisotropyComboBox);
    }

    /**
     * @return The frame rate field.
     */
    private IntegerTextField getFrameRateField() {
        return notNull(frameRateField);
    }

    /**
     * @return the camera angle field.
     */
    private IntegerTextField getCameraAngleField() {
        return notNull(cameraAngleField);
    }

    /**
     * @return the checkbox for enabling auto tangent generating.
     */
    @NotNull
    private CheckBox getAutoTangentGeneratingCheckBox() {
        return notNull(autoTangentGeneratingCheckBox);
    }

    /**
     * @return the checkbox for enabling camera lamp by default.
     */
    @NotNull
    private CheckBox getDefaultCameraLampEnabledCheckBox() {
        return notNull(defaultCameraLampEnabledCheckBox);
    }

    /**
     * @return the checkbox for enabling use flip texture by default.
     */
    @NotNull
    private CheckBox getDefaultUseFlippedTextureCheckBox() {
        return notNull(defaultUseFlippedTextureCheckBox);
    }

    /**
     * @return the checkbox for enabling google analytics.
     */
    @NotNull
    private CheckBox getGoogleAnalyticsCheckBox() {
        return notNull(googleAnalyticsCheckBox);
    }

    /**
     * @return the message label.
     */
    @NotNull
    private Label getMessageLabel() {
        return notNull(messageLabel);
    }

    /**
     * Validate changes.
     */
    private void validate() {
        if (isIgnoreListeners()) return;

        final Label messageLabel = getMessageLabel();

        int needRestart = 0;

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final CssColorTheme currentTheme = editorConfig.getTheme();
        final OpenGLVersion currentOpenGLVersion = editorConfig.getOpenGLVersion();
        final int currentAnisotropy = editorConfig.getAnisotropy();
        final boolean currentGammaCorrection = editorConfig.isGammaCorrection();

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final Integer anisotropy = anisotropyComboBox.getSelectionModel().getSelectedItem();

        final CheckBox gammaCorrectionCheckBox = getGammaCorrectionCheckBox();
        final boolean gammaCorrection = gammaCorrectionCheckBox.isSelected();

        final ComboBox<CssColorTheme> themeComboBox = getThemeComboBox();
        final CssColorTheme theme = themeComboBox.getSelectionModel().getSelectedItem();

        final ComboBox<OpenGLVersion> openGLVersionComboBox = getOpenGLVersionComboBox();
        final OpenGLVersion glVersion = openGLVersionComboBox.getSelectionModel().getSelectedItem();

        if (currentAnisotropy != anisotropy) {
            needRestart++;
        } else if (currentGammaCorrection != gammaCorrection) {
            needRestart++;
        } else if (currentTheme != theme) {
            needRestart++;
        } else if (currentOpenGLVersion != glVersion) {
            needRestart++;
        }

        if (needRestart > 0) {
            messageLabel.setText(Messages.SETTINGS_DIALOG_MESSAGE);
        } else {
            messageLabel.setText(StringUtils.EMPTY);
        }
    }

    /**
     * Load current settings.
     */
    private void load() {

        final EditorConfig editorConfig = EditorConfig.getInstance();

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final SingleSelectionModel<Integer> selectedAnisotropy = anisotropyComboBox.getSelectionModel();
        selectedAnisotropy.select(Integer.valueOf(editorConfig.getAnisotropy()));

        final ComboBox<CssColorTheme> themeComboBox = getThemeComboBox();
        final SingleSelectionModel<CssColorTheme> selectedTheme = themeComboBox.getSelectionModel();
        selectedTheme.select(editorConfig.getTheme());

        final ComboBox<OpenGLVersion> openGLVersionComboBox = getOpenGLVersionComboBox();
        final SingleSelectionModel<OpenGLVersion> selectionGLVersion = openGLVersionComboBox.getSelectionModel();
        selectionGLVersion.select(editorConfig.getOpenGLVersion());

        final CheckBox fxaaFilterCheckBox = getFXAAFilterCheckBox();
        fxaaFilterCheckBox.setSelected(editorConfig.isFXAA());

        final CheckBox stopRenderOnLostFocusCheckBox = getStopRenderOnLostFocusCheckBox();
        stopRenderOnLostFocusCheckBox.setSelected(editorConfig.isStopRenderOnLostFocus());

        final CheckBox gammaCorrectionCheckBox = getGammaCorrectionCheckBox();
        gammaCorrectionCheckBox.setSelected(editorConfig.isGammaCorrection());

        final CheckBox toneMapFilterCheckBox = getToneMapFilterCheckBox();
        toneMapFilterCheckBox.setSelected(editorConfig.isToneMapFilter());

        final CheckBox googleAnalyticsCheckBox = getGoogleAnalyticsCheckBox();
        googleAnalyticsCheckBox.setSelected(editorConfig.isAnalytics());

        final CheckBox autoTangentGeneratingCheckBox = getAutoTangentGeneratingCheckBox();
        autoTangentGeneratingCheckBox.setSelected(editorConfig.isAutoTangentGenerating());

        final CheckBox defaultCameraLampEnabledCheckBox = getDefaultCameraLampEnabledCheckBox();
        defaultCameraLampEnabledCheckBox.setSelected(editorConfig.isDefaultEditorCameraEnabled());

        final CheckBox defaultUseFlippedTextureCheckBox = getDefaultUseFlippedTextureCheckBox();
        defaultUseFlippedTextureCheckBox.setSelected(editorConfig.isDefaultUseFlippedTexture());

        final Vector3f toneMapFilterWhitePoint = editorConfig.getToneMapFilterWhitePoint();

        final Spinner<Double> toneMapFilterWhitePointX = getToneMapFilterWhitePointX();
        toneMapFilterWhitePointX.getValueFactory().setValue((double) toneMapFilterWhitePoint.getX());

        final Spinner<Double> toneMapFilterWhitePointY = getToneMapFilterWhitePointY();
        toneMapFilterWhitePointY.getValueFactory().setValue((double) toneMapFilterWhitePoint.getY());

        final Spinner<Double> toneMapFilterWhitePointZ = getToneMapFilterWhitePointZ();
        toneMapFilterWhitePointZ.getValueFactory().setValue((double) toneMapFilterWhitePoint.getZ());

        final IntegerTextField frameRateTextField = getFrameRateField();
        frameRateTextField.setValue(editorConfig.getFrameRate());

        final IntegerTextField cameraAngleField = getCameraAngleField();
        cameraAngleField.setValue(editorConfig.getCameraAngle());

        final Path additionalClasspath = editorConfig.getAdditionalClasspath();
        final Path additionalEnvs = editorConfig.getAdditionalEnvs();

        final TextField additionalClasspathField = getAdditionalClasspathField();
        final TextField additionalEnvsField = getAdditionalEnvsField();

        if (additionalClasspath != null) {
            additionalClasspathField.setText(additionalClasspath.toString());
        }

        if (additionalEnvs != null) {
            additionalEnvsField.setText(additionalEnvs.toString());
        }

        setAdditionalClasspathFolder(additionalClasspath);
        setAdditionalEnvsFolder(additionalEnvs);
    }

    /**
     * @return the list with themes.
     */
    @NotNull
    private ComboBox<CssColorTheme> getThemeComboBox() {
        return notNull(themeComboBox);
    }

    /**
     * @return the list with open GL versions.
     */
    @NotNull
    private ComboBox<OpenGLVersion> getOpenGLVersionComboBox() {
        return notNull(openGLVersionComboBox);
    }

    /**
     * @return the additional classpath folder.
     */
    @Nullable
    private Path getAdditionalClasspathFolder() {
        return additionalClasspathFolder;
    }

    /**
     * @param additionalClasspathFolder the additional classpath folder.
     */
    private void setAdditionalClasspathFolder(@Nullable final Path additionalClasspathFolder) {
        this.additionalClasspathFolder = additionalClasspathFolder;
    }

    /**
     * @param additionalEnvsFolder the additional envs folder.
     */
    private void setAdditionalEnvsFolder(@Nullable final Path additionalEnvsFolder) {
        this.additionalEnvsFolder = additionalEnvsFolder;
    }

    /**
     * @return the additional envs folder.
     */
    @Nullable
    private Path getAdditionalEnvsFolder() {
        return additionalEnvsFolder;
    }

    @Override
    protected void createActions(@NotNull final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();

        final Button okButton = new Button(Messages.SETTINGS_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processOk());

        final Button cancelButton = new Button(Messages.SETTINGS_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addClassTo(okButton, cancelButton, CSSClasses.DIALOG_BUTTON);
        FXUtils.addClassTo(container, CSSClasses.DEF_HBOX);

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);
    }

    /**
     * Save new settings.
     */
    private void processOk() {

        int needRestart = 0;

        final EditorConfig editorConfig = EditorConfig.getInstance();

        final int currentAnisotropy = editorConfig.getAnisotropy();
        final int currentFrameRate = editorConfig.getFrameRate();
        final int currentCameraAngle = editorConfig.getCameraAngle();

        final CssColorTheme currentTheme = editorConfig.getTheme();
        final OpenGLVersion currentOpenGLVersion = editorConfig.getOpenGLVersion();

        final boolean currentGammaCorrection = editorConfig.isGammaCorrection();

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final Integer anisotropy = anisotropyComboBox.getSelectionModel().getSelectedItem();

        final ComboBox<CssColorTheme> themeComboBox = getThemeComboBox();
        final CssColorTheme theme = themeComboBox.getSelectionModel().getSelectedItem();

        final ComboBox<OpenGLVersion> openGLVersionComboBox = getOpenGLVersionComboBox();
        final OpenGLVersion glVersion = openGLVersionComboBox.getSelectionModel().getSelectedItem();

        final CheckBox fxaaFilterCheckBox = getFXAAFilterCheckBox();
        final boolean fxaa = fxaaFilterCheckBox.isSelected();

        final CheckBox stopRenderOnLostFocusCheckBox = getStopRenderOnLostFocusCheckBox();
        final boolean isStopRenderOnLostFocus = stopRenderOnLostFocusCheckBox.isSelected();

        final CheckBox gammaCorrectionCheckBox = getGammaCorrectionCheckBox();
        final boolean gammaCorrection = gammaCorrectionCheckBox.isSelected();

        final CheckBox toneMapFilterCheckBox = getToneMapFilterCheckBox();
        final boolean toneMapFilter = toneMapFilterCheckBox.isSelected();

        final CheckBox googleAnalyticsCheckBox = getGoogleAnalyticsCheckBox();
        final boolean analytics = googleAnalyticsCheckBox.isSelected();

        final CheckBox autoTangentGeneratingCheckBox = getAutoTangentGeneratingCheckBox();
        final boolean autoTangentGenerating = autoTangentGeneratingCheckBox.isSelected();

        final CheckBox defaultCameraLampEnabledCheckBox = getDefaultCameraLampEnabledCheckBox();
        final boolean cameraLampEnabled = defaultCameraLampEnabledCheckBox.isSelected();

        final CheckBox defaultUseFlippedTextureCheckBox = getDefaultUseFlippedTextureCheckBox();
        final boolean useFlippedTextures = defaultUseFlippedTextureCheckBox.isSelected();

        final float toneMapFilterWhitePointX = getToneMapFilterWhitePointX().getValue().floatValue();
        final float toneMapFilterWhitePointY = getToneMapFilterWhitePointY().getValue().floatValue();
        final float toneMapFilterWhitePointZ = getToneMapFilterWhitePointZ().getValue().floatValue();

        final IntegerTextField frameRateTextField = getFrameRateField();
        final int frameRate = frameRateTextField.getValue();

        final IntegerTextField cameraAngleField = getCameraAngleField();
        final int cameraAngle = cameraAngleField.getValue();

        final Vector3f toneMapFilterWhitePoint = new Vector3f(toneMapFilterWhitePointX, toneMapFilterWhitePointY, toneMapFilterWhitePointZ);

        if (currentAnisotropy != anisotropy) {
            needRestart++;
        } else if (currentGammaCorrection != gammaCorrection) {
            needRestart++;
        } else if (frameRate != currentFrameRate) {
            needRestart++;
        } else if (theme != currentTheme) {
            needRestart++;
        } else if (currentOpenGLVersion != glVersion) {
            needRestart++;
        }

        editorConfig.setAnisotropy(anisotropy);
        editorConfig.setFXAA(fxaa);
        editorConfig.setAnalytics(analytics);
        editorConfig.setGammaCorrection(gammaCorrection);
        editorConfig.setStopRenderOnLostFocus(isStopRenderOnLostFocus);
        editorConfig.setToneMapFilter(toneMapFilter);
        editorConfig.setToneMapFilterWhitePoint(toneMapFilterWhitePoint);
        editorConfig.setAdditionalClasspath(getAdditionalClasspathFolder());
        editorConfig.setAdditionalEnvs(getAdditionalEnvsFolder());
        editorConfig.setFrameRate(frameRate);
        editorConfig.setCameraAngle(cameraAngle);
        editorConfig.setAutoTangentGenerating(autoTangentGenerating);
        editorConfig.setDefaultUseFlippedTexture(useFlippedTextures);
        editorConfig.setDefaultEditorCameraEnabled(cameraLampEnabled);
        editorConfig.setTheme(theme);
        editorConfig.setOpenGLVersion(glVersion);
        editorConfig.save();

        if (cameraAngle != currentCameraAngle) {
            final FrameTransferSceneProcessor sceneProcessor = JFX_APPLICATION.getSceneProcessor();
            sceneProcessor.reshape();
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> {

            final FXAAFilter fxaaFilter = EDITOR.getFXAAFilter();
            fxaaFilter.setEnabled(editorConfig.isFXAA());

            final ToneMapFilter filter = EDITOR.getToneMapFilter();
            filter.setEnabled(editorConfig.isToneMapFilter());
            filter.setWhitePoint(editorConfig.getToneMapFilterWhitePoint());
        });

        final ClasspathManager classpathManager = ClasspathManager.getInstance();
        classpathManager.updateAdditionalCL();

        final ResourceManager resourceManager = ResourceManager.getInstance();
        resourceManager.updateAdditionalEnvs();

        if (needRestart > 0) {
            Platform.exit();
        } else {
            hide();
        }
    }

    @NotNull
    @Override
    protected String getTitleText() {
        return Messages.SETTINGS_DIALOG_TITLE;
    }

    @NotNull
    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
