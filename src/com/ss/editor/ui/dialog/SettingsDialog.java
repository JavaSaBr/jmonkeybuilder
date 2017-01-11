package com.ss.editor.ui.dialog;

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
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.scene.EditorFXScene;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.Point;
import java.io.File;
import java.nio.file.Path;

import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import rlib.ui.control.input.IntegerTextField;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * The dialog with settings.
 *
 * @author JavaSaBr
 */
public class SettingsDialog extends EditorDialog {

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);
    private static final Insets MESSAGE_OFFSET = new Insets(5, 0, 5, 0);
    private static final Insets LAST_FIELD_OFFSET = new Insets(5, 20, 10, 0);
    private static final Insets FIELD_OFFSET = new Insets(5, 20, 0, 0);
    private static final Insets ADD_REMOVE_BUTTON_OFFSET = new Insets(0, 0, 0, 2);

    private static final Point DIALOG_SIZE = new Point(600, 426);

    private static final Array<Integer> ANISOTROPYCS = ArrayFactory.newArray(Integer.class);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final JFXApplication JFX_APPLICATION = JFXApplication.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    static {
        ANISOTROPYCS.add(0);
        ANISOTROPYCS.add(2);
        ANISOTROPYCS.add(4);
        ANISOTROPYCS.add(8);
        ANISOTROPYCS.add(16);
    }

    /**
     * The message label.
     */
    private Label messageLabel;

    /**
     * The list with anisotropy levels.
     */
    private ComboBox<Integer> anisotropyComboBox;

    /**
     * The white point X.
     */
    private Spinner<Double> toneMapFilterWhitePointX;

    /**
     * The white point Y.
     */
    private Spinner<Double> toneMapFilterWhitePointY;

    /**
     * The white point Z.
     */
    private Spinner<Double> toneMapFilterWhitePointZ;

    /**
     * The gamma correction checkbox.
     */
    private CheckBox gammaCorrectionCheckBox;

    /**
     * The tone map filter checkbox.
     */
    private CheckBox toneMapFilterCheckBox;

    /**
     * The FXAA checkbox.
     */
    private CheckBox fxaaFilterCheckBox;

    /**
     * The checkbox for enabling decorating.
     */
    private CheckBox decoratedCheckBox;

    /**
     * The checkbox for enabling google analytics.
     */
    private CheckBox googleAnalyticsCheckBox;

    /**
     * The additional classpath field.
     */
    private TextField additionalClasspathField;

    /**
     * The frame rate field.
     */
    private IntegerTextField frameRateField;

    /**
     * The camera angle field.
     */
    private IntegerTextField cameraAngleField;

    /**
     * The additional classpath folder.
     */
    private Path additionalClasspathFolder;

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

        FXUtils.bindFixedWidth(messageLabel, root.widthProperty().multiply(0.8));
        FXUtils.addClassTo(messageLabel, CSSClasses.SPECIAL_FONT_15);
        FXUtils.addToPane(messageLabel, root);

        VBox.setMargin(messageLabel, MESSAGE_OFFSET);

        createAnisotropyControl(root);
        createGammaCorrectionControl(root);
        createFrameRateControl(root);
        createCameraAngleControl(root);
        createFXAAControl(root);
        createGoogleAnalyticsControl(root);
        createDecoratedControl(root);
        createToneMapFilterControl(root);
        createToneMapFilterWhitePointControl(root);
        createAdditionalClasspathControl(root);
    }

    /**
     * Create the additional classpath control.
     */
    private void createAdditionalClasspathControl(@NotNull final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_LABEL + ":");
        label.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        additionalClasspathField = new TextField();
        additionalClasspathField.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        additionalClasspathField.setEditable(false);
        additionalClasspathField.prefWidthProperty().bind(root.widthProperty());

        final Button addButton = new Button();
        addButton.setId(CSSIds.CREATE_SKY_DIALOG_BUTTON);
        addButton.setGraphic(new ImageView(Icons.ADD_18));
        addButton.setOnAction(event -> processAddCF());

        final Button removeButton = new Button();
        removeButton.setId(CSSIds.CREATE_SKY_DIALOG_BUTTON);
        removeButton.setGraphic(new ImageView(Icons.REMOVE_18));
        removeButton.setOnAction(event -> processRemoveCF());

        FXUtils.addToPane(label, container);
        FXUtils.addToPane(additionalClasspathField, container);
        FXUtils.addToPane(addButton, container);
        FXUtils.addToPane(removeButton, container);
        FXUtils.addToPane(container, root);

        FXUtils.addClassTo(label, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(additionalClasspathField, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(addButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(removeButton, CSSClasses.TOOLBAR_BUTTON);

        HBox.setMargin(addButton, ADD_REMOVE_BUTTON_OFFSET);
        HBox.setMargin(removeButton, ADD_REMOVE_BUTTON_OFFSET);
        VBox.setMargin(container, LAST_FIELD_OFFSET);
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
     * @return the additional classpath field.
     */
    private TextField getAdditionalClasspathField() {
        return additionalClasspathField;
    }

    /**
     * Process of adding the additional classpath.
     */
    private void processAddCF() {

        final DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(Messages.OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_CHOOSER_TITLE);

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
     * Create the checkbox for configuring decorated windows.
     */
    private void createDecoratedControl(@NotNull final VBox root) {

        final HBox decoratedContainer = new HBox();
        decoratedContainer.setAlignment(Pos.CENTER_LEFT);

        final Label decoratedLabel = new Label(Messages.SETTINGS_DIALOG_DECORATED + ":");
        decoratedLabel.setId(CSSIds.SETTINGS_DIALOG_LABEL);

        decoratedCheckBox = new CheckBox();
        decoratedCheckBox.setId(CSSIds.SETTINGS_DIALOG_FIELD);
        decoratedCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(decoratedLabel, decoratedContainer);
        FXUtils.addToPane(decoratedCheckBox, decoratedContainer);
        FXUtils.addToPane(decoratedContainer, root);

        FXUtils.addClassTo(decoratedLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(decoratedCheckBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(decoratedContainer, FIELD_OFFSET);
    }

    /**
     * Create the checkbox for configuring decorated windows.
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
        anisotropyComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(anisotropyLabel, anisotropyContainer);
        FXUtils.addToPane(anisotropyComboBox, anisotropyContainer);
        FXUtils.addToPane(anisotropyContainer, root);

        FXUtils.addClassTo(anisotropyLabel, CSSClasses.SPECIAL_FONT_14);
        FXUtils.addClassTo(anisotropyComboBox, CSSClasses.SPECIAL_FONT_14);

        VBox.setMargin(anisotropyContainer, FIELD_OFFSET);

        final ObservableList<Integer> items = anisotropyComboBox.getItems();

        ANISOTROPYCS.forEach(items::add);
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
        return gammaCorrectionCheckBox;
    }

    /**
     * @return the tone map filter checkbox.
     */
    @NotNull
    private CheckBox getToneMapFilterCheckBox() {
        return toneMapFilterCheckBox;
    }

    /**
     * @return the white point X.
     */
    @NotNull
    private Spinner<Double> getToneMapFilterWhitePointX() {
        return toneMapFilterWhitePointX;
    }

    /**
     * @return the white point Y.
     */
    @NotNull
    private Spinner<Double> getToneMapFilterWhitePointY() {
        return toneMapFilterWhitePointY;
    }

    /**
     * @return the white point Z.
     */
    @NotNull
    private Spinner<Double> getToneMapFilterWhitePointZ() {
        return toneMapFilterWhitePointZ;
    }

    /**
     * @return the FXAA checkbox.
     */
    @NotNull
    private CheckBox getFXAAFilterCheckBox() {
        return fxaaFilterCheckBox;
    }

    /**
     * @return the list with anisotropy levels.
     */
    @NotNull
    private ComboBox<Integer> getAnisotropyComboBox() {
        return anisotropyComboBox;
    }

    /**
     * @return The frame rate field.
     */
    private IntegerTextField getFrameRateField() {
        return frameRateField;
    }

    /**
     * @return the camera angle field.
     */
    private IntegerTextField getCameraAngleField() {
        return cameraAngleField;
    }

    /**
     * @return the checkbox for enabling decorating.
     */
    @NotNull
    private CheckBox getDecoratedCheckBox() {
        return decoratedCheckBox;
    }

    /**
     * @return the checkbox for enabling google analytics.
     */
    @NotNull
    private CheckBox getGoogleAnalyticsCheckBox() {
        return googleAnalyticsCheckBox;
    }

    /**
     * @return the message label.
     */
    @NotNull
    public Label getMessageLabel() {
        return messageLabel;
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
        final boolean currentDecorated = editorConfig.isDecorated();

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final Integer anisotropy = anisotropyComboBox.getSelectionModel().getSelectedItem();

        final CheckBox gammaCorrectionCheckBox = getGammaCorrectionCheckBox();
        final boolean gammaCorrection = gammaCorrectionCheckBox.isSelected();

        final CheckBox decoratedCheckBox = getDecoratedCheckBox();
        final boolean decorated = decoratedCheckBox.isSelected();

        if (currentAnisotropy != anisotropy) {
            needRestart++;
        } else if (currentGammaCorrection != gammaCorrection) {
            needRestart++;
        } else if (decorated != currentDecorated) {
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

        final CheckBox fxaaFilterCheckBox = getFXAAFilterCheckBox();
        fxaaFilterCheckBox.setSelected(editorConfig.isFXAA());

        final CheckBox gammaCorrectionCheckBox = getGammaCorrectionCheckBox();
        gammaCorrectionCheckBox.setSelected(editorConfig.isGammaCorrection());

        final CheckBox toneMapFilterCheckBox = getToneMapFilterCheckBox();
        toneMapFilterCheckBox.setSelected(editorConfig.isToneMapFilter());

        final CheckBox decoratedCheckBox = getDecoratedCheckBox();
        decoratedCheckBox.setSelected(editorConfig.isDecorated());

        final CheckBox googleAnalyticsCheckBox = getGoogleAnalyticsCheckBox();
        googleAnalyticsCheckBox.setSelected(editorConfig.isAnalytics());

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

        final TextField additionalClasspathField = getAdditionalClasspathField();

        if (additionalClasspath != null) {
            additionalClasspathField.setText(additionalClasspath.toString());
        }

        setAdditionalClasspathFolder(additionalClasspath);
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
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
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

        final boolean currentGammaCorrection = editorConfig.isGammaCorrection();
        final boolean currentDecorated = editorConfig.isDecorated();

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final Integer anisotropy = anisotropyComboBox.getSelectionModel().getSelectedItem();

        final CheckBox fxaaFilterCheckBox = getFXAAFilterCheckBox();
        final boolean fxaa = fxaaFilterCheckBox.isSelected();

        final CheckBox gammaCorrectionCheckBox = getGammaCorrectionCheckBox();
        final boolean gammaCorrection = gammaCorrectionCheckBox.isSelected();

        final CheckBox toneMapFilterCheckBox = getToneMapFilterCheckBox();
        final boolean toneMapFilter = toneMapFilterCheckBox.isSelected();

        final CheckBox decoratedCheckBox = getDecoratedCheckBox();
        final boolean decorated = decoratedCheckBox.isSelected();

        final CheckBox googleAnalyticsCheckBox = getGoogleAnalyticsCheckBox();
        final boolean analytics = googleAnalyticsCheckBox.isSelected();

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
        } else if (currentDecorated != decorated) {
            needRestart++;
        } else if (frameRate != currentFrameRate) {
            needRestart++;
        }

        final ClasspathManager classpathManager = ClasspathManager.getInstance();
        classpathManager.updateAdditionalCL();

        editorConfig.setAnisotropy(anisotropy);
        editorConfig.setFXAA(fxaa);
        editorConfig.setDecorated(decorated);
        editorConfig.setAnalytics(analytics);
        editorConfig.setGammaCorrection(gammaCorrection);
        editorConfig.setToneMapFilter(toneMapFilter);
        editorConfig.setToneMapFilterWhitePoint(toneMapFilterWhitePoint);
        editorConfig.setAdditionalClasspath(getAdditionalClasspathFolder());
        editorConfig.setFrameRate(frameRate);
        editorConfig.setCameraAngle(cameraAngle);
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

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
