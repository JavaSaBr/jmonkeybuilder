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
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.css.CssColorTheme;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.rlib.ui.control.input.IntegerTextField;
import com.ss.rlib.ui.util.FXUtils;
import com.ss.rlib.util.StringUtils;
import com.ss.rlib.util.array.Array;
import com.ss.rlib.util.array.ArrayFactory;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.*;
import javafx.scene.control.Label;
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

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets MESSAGE_OFFSET = new Insets(5, 0, 10, 0);
    private static final Insets FIELD_OFFSET = new Insets(5, 20, 0, 0);
    private static final Insets ADD_REMOVE_BUTTON_OFFSET = new Insets(0, 0, 0, 2);

    private static final Point DIALOG_SIZE = new Point(600, 400);

    private static final Array<Integer> ANISOTROPYCS = ArrayFactory.newArray(Integer.class);
    private static final Array<CssColorTheme> THEMES = ArrayFactory.newArray(CssColorTheme.class);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    static {
        ANISOTROPYCS.add(0);
        ANISOTROPYCS.add(2);
        ANISOTROPYCS.add(4);
        ANISOTROPYCS.add(8);
        ANISOTROPYCS.add(16);
        THEMES.add(CssColorTheme.LIGHT);
        THEMES.add(CssColorTheme.DARK);
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

    @Override
    public void show(@NotNull final Window owner) {
        super.show(owner);
        setIgnoreListeners(true);
        try {
            load();
        } finally {
            setIgnoreListeners(false);
        }
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
        messageLabel.setId(CSSIds.SETTINGS_DIALOG_MESSAGE_LABEL);

        final VBox graphicsRoot = new VBox();
        graphicsRoot.prefHeightProperty().bind(heightProperty());

        final VBox otherRoot = new VBox();
        otherRoot.prefHeightProperty().bind(heightProperty());

        final Tab graphicsSettings = new Tab(Messages.SETTINGS_DIALOG_TAB_GRAPHICS);
        graphicsSettings.setClosable(false);
        graphicsSettings.setContent(graphicsRoot);

        final Tab otherSettings = new Tab(Messages.SETTINGS_DIALOG_TAB_OTHER);
        otherSettings.setClosable(false);
        otherSettings.setContent(otherRoot);

        final TabPane tabPane = new TabPane();
        tabPane.setId(CSSIds.SETTINGS_DIALOG_TAB_PANE);
        tabPane.getTabs().addAll(graphicsSettings, otherSettings);
        tabPane.prefWidthProperty().bind(widthProperty());
        tabPane.prefHeightProperty().bind(heightProperty());

        createAnisotropyControl(graphicsRoot);
        createGammaCorrectionControl(graphicsRoot);
        createFrameRateControl(graphicsRoot);
        createCameraAngleControl(graphicsRoot);
        createFXAAControl(graphicsRoot);
        createToneMapFilterControl(graphicsRoot);
        createToneMapFilterWhitePointControl(graphicsRoot);

        createAdditionalClasspathControl(otherRoot);
        createAdditionalEnvsControl(otherRoot);
        createThemeControl(otherRoot);
        createGoogleAnalyticsControl(otherRoot);
        createAutoTangentGeneratingControl(otherRoot);
        createUseFlippedTextureDefaultControl(otherRoot);
        createDefaultCameraLampEnabledControl(otherRoot);

        FXUtils.bindFixedWidth(messageLabel, widthProperty().multiply(0.8));

        FXUtils.addClassTo(messageLabel, CSSClasses.SPECIAL_FONT_15);
        FXUtils.addClassTo(graphicsSettings, CSSClasses.SPECIAL_FONT_15);
        FXUtils.addClassTo(otherSettings, CSSClasses.SPECIAL_FONT_15);

        FXUtils.addToPane(tabPane, root);
        FXUtils.addToPane(messageLabel, root);

        VBox.setMargin(messageLabel, MESSAGE_OFFSET);
    }

    /**
     * Create the additional classpath control.
     */
    private void createAdditionalClasspathControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_CLASSPATH_FOLDER_LABEL + ":");
        label.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        additionalClasspathField = new TextField();
        additionalClasspathField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        additionalClasspathField.setEditable(false);
        additionalClasspathField.prefWidthProperty().bind(root.widthProperty());

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> processAddCF());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemoveCF());

        FXUtils.addToPane(label, additionalClasspathField, addButton, removeButton, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, additionalClasspathField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(addButton, removeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(addButton, removeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        HBox.setMargin(addButton, ADD_REMOVE_BUTTON_OFFSET);
        HBox.setMargin(removeButton, ADD_REMOVE_BUTTON_OFFSET);
        VBox.setMargin(container, FIELD_OFFSET);
    }

    /**
     * Create the additional envs control.
     */
    private void createAdditionalEnvsControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_ENVS_FOLDER_LABEL + ":");
        label.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        additionalEnvsField = new TextField();
        additionalEnvsField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        additionalEnvsField.setEditable(false);
        additionalEnvsField.prefWidthProperty().bind(root.widthProperty());

        final Button addButton = new Button();
        addButton.setGraphic(new ImageView(Icons.ADD_12));
        addButton.setOnAction(event -> processAddEF());

        final Button removeButton = new Button();
        removeButton.setGraphic(new ImageView(Icons.REMOVE_12));
        removeButton.setOnAction(event -> processRemoveEF());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(additionalEnvsField, container);
        FXUtils.addToPane(addButton, container);
        FXUtils.addToPane(removeButton, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(additionalEnvsField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(addButton, removeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(addButton, removeButton, CSSClasses.FILE_EDITOR_TOOLBAR_BUTTON);

        HBox.setMargin(addButton, ADD_REMOVE_BUTTON_OFFSET);
        HBox.setMargin(removeButton, ADD_REMOVE_BUTTON_OFFSET);
        VBox.setMargin(container, FIELD_OFFSET);
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

        final HBox gammaCorrectionContainer = new HBox();
        gammaCorrectionContainer.setAlignment(Pos.CENTER_LEFT);

        final Label gammaCorrectionLabel = new Label(Messages.SETTINGS_DIALOG_GAMMA_CORRECTION + ":");
        gammaCorrectionLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        gammaCorrectionCheckBox = new CheckBox();
        gammaCorrectionCheckBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        gammaCorrectionCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(gammaCorrectionLabel, gammaCorrectionContainer);
        FXUtils.addToPane(gammaCorrectionCheckBox, gammaCorrectionContainer);
        FXUtils.addToPane(gammaCorrectionContainer, root);

        FXUtils.addClassTo(gammaCorrectionLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(gammaCorrectionCheckBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(gammaCorrectionContainer, FIELD_OFFSET);
    }

    /**
     * Create tonemap filter control.
     */
    private void createToneMapFilterControl(@NotNull final VBox root) {

        final HBox toneMapFilterContainer = new HBox();
        toneMapFilterContainer.setAlignment(Pos.CENTER_LEFT);

        final Label toneMapFilterLabel = new Label(Messages.SETTINGS_DIALOG_TONEMAP_FILTER + ":");
        toneMapFilterLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        toneMapFilterCheckBox = new CheckBox();
        toneMapFilterCheckBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        toneMapFilterCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(toneMapFilterLabel, toneMapFilterContainer);
        FXUtils.addToPane(toneMapFilterCheckBox, toneMapFilterContainer);
        FXUtils.addToPane(toneMapFilterContainer, root);

        FXUtils.addClassTo(toneMapFilterLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(toneMapFilterCheckBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(toneMapFilterContainer, FIELD_OFFSET);
    }

    /**
     * Create white point control.
     */
    private void createToneMapFilterWhitePointControl(@NotNull final VBox root) {

        final HBox toneMapFilterWhitePointContainer = new HBox();
        toneMapFilterWhitePointContainer.setAlignment(Pos.CENTER_LEFT);
        toneMapFilterWhitePointContainer.disableProperty().bind(toneMapFilterCheckBox.selectedProperty().not());

        final Label toneMapFilterWhitePointLabel = new Label(Messages.SETTINGS_DIALOG_TONEMAP_FILTER_WHITE_POINT + ":");
        toneMapFilterWhitePointLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        final Label xLabel = new Label("x:");
        xLabel.setId(CSSIds.SETTINGS_DIALOG_FIELD);

        final Label yLabel = new Label("y:");
        yLabel.setId(CSSIds.SETTINGS_DIALOG_FIELD);

        final Label zLabel = new Label("z:");
        zLabel.setId(CSSIds.SETTINGS_DIALOG_FIELD);

        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(-30, 30, 0, 0.1);

        toneMapFilterWhitePointX = new Spinner<>();
        toneMapFilterWhitePointX.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        toneMapFilterWhitePointX.setValueFactory(valueFactory);
        toneMapFilterWhitePointX.setEditable(true);
        toneMapFilterWhitePointX.setOnScroll(event -> processScroll(toneMapFilterWhitePointX, event));
        toneMapFilterWhitePointX.valueProperty().addListener((observable, oldValue, newValue) -> validate());

        valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(-30, 30, 0, 0.1);

        toneMapFilterWhitePointY = new Spinner<>();
        toneMapFilterWhitePointY.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        toneMapFilterWhitePointY.setValueFactory(valueFactory);
        toneMapFilterWhitePointY.setEditable(true);
        toneMapFilterWhitePointY.setOnScroll(event -> processScroll(toneMapFilterWhitePointY, event));
        toneMapFilterWhitePointY.valueProperty().addListener((observable, oldValue, newValue) -> validate());

        valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(-30, 30, 0, 0.1);

        toneMapFilterWhitePointZ = new Spinner<>();
        toneMapFilterWhitePointZ.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        toneMapFilterWhitePointZ.setValueFactory(valueFactory);
        toneMapFilterWhitePointZ.setEditable(true);
        toneMapFilterWhitePointZ.setOnScroll(event -> processScroll(toneMapFilterWhitePointZ, event));
        toneMapFilterWhitePointZ.valueProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(toneMapFilterWhitePointLabel, toneMapFilterWhitePointContainer);
        FXUtils.addToPane(xLabel, toneMapFilterWhitePointContainer);
        FXUtils.addToPane(toneMapFilterWhitePointX, toneMapFilterWhitePointContainer);
        FXUtils.addToPane(yLabel, toneMapFilterWhitePointContainer);
        FXUtils.addToPane(toneMapFilterWhitePointY, toneMapFilterWhitePointContainer);
        FXUtils.addToPane(zLabel, toneMapFilterWhitePointContainer);
        FXUtils.addToPane(toneMapFilterWhitePointZ, toneMapFilterWhitePointContainer);
        FXUtils.addToPane(toneMapFilterWhitePointContainer, root);

        FXUtils.addClassTo(toneMapFilterWhitePointLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(xLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(toneMapFilterWhitePointX, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(yLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(toneMapFilterWhitePointY, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(zLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(toneMapFilterWhitePointZ, CSSClasses.SPECIAL_FONT_14);

        HBox.setMargin(xLabel, new Insets(0, 0, 0, 4));
        VBox.setMargin(toneMapFilterWhitePointContainer, FIELD_OFFSET);
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

        final HBox fxaaContainer = new HBox();
        fxaaContainer.setAlignment(Pos.CENTER_LEFT);

        final Label fxaaLabel = new Label(Messages.SETTINGS_DIALOG_FXAA + ":");
        fxaaLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        fxaaFilterCheckBox = new CheckBox();
        fxaaFilterCheckBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        fxaaFilterCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(fxaaLabel, fxaaContainer);
        FXUtils.addToPane(fxaaFilterCheckBox, fxaaContainer);
        FXUtils.addToPane(fxaaContainer, root);

        FXUtils.addClassTo(fxaaLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(fxaaFilterCheckBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(fxaaContainer, FIELD_OFFSET);
    }

    /**
     * Create the checkbox for configuring enabling google analytics.
     */
    private void createGoogleAnalyticsControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_GOOGLE_ANALYTICS + ":");
        label.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        googleAnalyticsCheckBox = new CheckBox();
        googleAnalyticsCheckBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        googleAnalyticsCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(googleAnalyticsCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(googleAnalyticsCheckBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(container, FIELD_OFFSET);
    }

    /**
     * Create the checkbox for configuring auto tangent generating.
     */
    private void createAutoTangentGeneratingControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_AUTO_TANGENT_GENERATING + ":");
        label.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        autoTangentGeneratingCheckBox = new CheckBox();
        autoTangentGeneratingCheckBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        autoTangentGeneratingCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(autoTangentGeneratingCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(autoTangentGeneratingCheckBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(container, FIELD_OFFSET);
    }

    /**
     * Create the checkbox for configuring using flip textures by default.
     */
    private void createUseFlippedTextureDefaultControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_DEFAULT_FLIPPED_TEXTURE + ":");
        label.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        defaultUseFlippedTextureCheckBox = new CheckBox();
        defaultUseFlippedTextureCheckBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        defaultUseFlippedTextureCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(defaultUseFlippedTextureCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(defaultUseFlippedTextureCheckBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(container, FIELD_OFFSET);
    }

    /**
     * Create the checkbox for configuring enabling camera lamp by default.
     */
    private void createDefaultCameraLampEnabledControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_DEFAULT_EDITOR_CAMERA_LAMP_ENABLED + ":");
        label.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        defaultCameraLampEnabledCheckBox = new CheckBox();
        defaultCameraLampEnabledCheckBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        defaultCameraLampEnabledCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(defaultCameraLampEnabledCheckBox, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(defaultCameraLampEnabledCheckBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(container, FIELD_OFFSET);
    }

    /**
     * Create the anisotropy control
     */
    private void createAnisotropyControl(@NotNull final VBox root) {

        final HBox anisotropyContainer = new HBox();
        anisotropyContainer.setAlignment(Pos.CENTER_LEFT);

        final Label anisotropyLabel = new Label(Messages.SETTINGS_DIALOG_ANISOTROPY + ":");
        anisotropyLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        anisotropyComboBox = new ComboBox<>();
        anisotropyComboBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        anisotropyComboBox.prefWidthProperty().bind(root.widthProperty());
        anisotropyComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(anisotropyLabel, anisotropyContainer);
        FXUtils.addToPane(anisotropyComboBox, anisotropyContainer);
        FXUtils.addToPane(anisotropyContainer, root);

        FXUtils.addClassTo(anisotropyLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(anisotropyComboBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(anisotropyContainer, FIELD_OFFSET);

        final ObservableList<Integer> items = anisotropyComboBox.getItems();
        items.addAll(ANISOTROPYCS);
    }

    /**
     * Create the theme control
     */
    private void createThemeControl(@NotNull final VBox root) {

        final HBox anisotropyContainer = new HBox();
        anisotropyContainer.setAlignment(Pos.CENTER_LEFT);

        final Label anisotropyLabel = new Label("Theme" + ":");
        anisotropyLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        themeComboBox = new ComboBox<>();
        themeComboBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        themeComboBox.prefWidthProperty().bind(root.widthProperty());
        themeComboBox.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(anisotropyLabel, anisotropyContainer);
        FXUtils.addToPane(themeComboBox, anisotropyContainer);
        FXUtils.addToPane(anisotropyContainer, root);

        FXUtils.addClassTo(anisotropyLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(themeComboBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(anisotropyContainer, FIELD_OFFSET);

        final ObservableList<CssColorTheme> items = themeComboBox.getItems();
        items.addAll(THEMES);
    }

    /**
     * Create the frame rate control.
     */
    private void createFrameRateControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_FRAME_RATE + ":");
        label.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        frameRateField = new IntegerTextField();
        frameRateField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        frameRateField.prefWidthProperty().bind(root.widthProperty());
        frameRateField.setMinMax(5, 100);
        frameRateField.addChangeListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(frameRateField, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(frameRateField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(container, FIELD_OFFSET);
    }

    /**
     * Create the camera angle control.
     */
    private void createCameraAngleControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.SETTINGS_DIALOG_CAMERA_ANGLE + ":");
        label.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        cameraAngleField = new IntegerTextField();
        cameraAngleField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        cameraAngleField.prefWidthProperty().bind(root.widthProperty());
        cameraAngleField.setMinMax(30, 160);
        cameraAngleField.addChangeListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(cameraAngleField, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(cameraAngleField, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(container, FIELD_OFFSET);
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
        final int currentAnisotropy = editorConfig.getAnisotropy();
        final boolean currentGammaCorrection = editorConfig.isGammaCorrection();

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final Integer anisotropy = anisotropyComboBox.getSelectionModel().getSelectedItem();

        final CheckBox gammaCorrectionCheckBox = getGammaCorrectionCheckBox();
        final boolean gammaCorrection = gammaCorrectionCheckBox.isSelected();

        if (currentAnisotropy != anisotropy) {
            needRestart++;
        } else if (currentGammaCorrection != gammaCorrection) {
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

        final CheckBox fxaaFilterCheckBox = getFXAAFilterCheckBox();
        fxaaFilterCheckBox.setSelected(editorConfig.isFXAA());

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
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        final Button okButton = new Button(Messages.SETTINGS_DIALOG_BUTTON_OK);
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processOk());

        final Button cancelButton = new Button(Messages.SETTINGS_DIALOG_BUTTON_CANCEL);
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addClassTo(okButton, CSSClasses.SPECIAL_FONT_16);
        FXUtils.addClassTo(cancelButton, CSSClasses.SPECIAL_FONT_16);

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
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

        final boolean currentGammaCorrection = editorConfig.isGammaCorrection();

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final Integer anisotropy = anisotropyComboBox.getSelectionModel().getSelectedItem();

        final ComboBox<CssColorTheme> themeComboBox = getThemeComboBox();
        final CssColorTheme theme = themeComboBox.getSelectionModel().getSelectedItem();

        final CheckBox fxaaFilterCheckBox = getFXAAFilterCheckBox();
        final boolean fxaa = fxaaFilterCheckBox.isSelected();

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
        }

        editorConfig.setAnisotropy(anisotropy);
        editorConfig.setFXAA(fxaa);
        editorConfig.setAnalytics(analytics);
        editorConfig.setGammaCorrection(gammaCorrection);
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
            System.exit(2);
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
