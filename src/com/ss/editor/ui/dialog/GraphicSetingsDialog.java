package com.ss.editor.ui.dialog;

import com.jme3.math.Vector3f;
import com.jme3.post.filters.FXAAFilter;
import com.jme3.post.filters.ToneMapFilter;
import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.config.ScreenSize;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;

import java.awt.*;

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
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация диалога для настроек графики.
 *
 * @author Ronn
 */
public class GraphicSetingsDialog extends EditorDialog {

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);
    private static final Insets MESSAGE_OFFSET = new Insets(5, 0, 5, 0);
    private static final Insets LAST_FIELD_OFFSET = new Insets(5, CANCEL_BUTTON_OFFSET.getRight(), 29, 0);
    private static final Insets FIELD_OFFSET = new Insets(5, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);

    private static final Point DIALOG_SIZE = new Point(500, 365);

    private static final Array<ScreenSize> SCREEN_SIZES = ArrayFactory.newArray(ScreenSize.class);
    private static final Array<Integer> ANISOTROPYCS = ArrayFactory.newArray(Integer.class);

    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    static {
        SCREEN_SIZES.addAll(ScreenSize.values());
        ANISOTROPYCS.add(0);
        ANISOTROPYCS.add(2);
        ANISOTROPYCS.add(4);
        ANISOTROPYCS.add(8);
        ANISOTROPYCS.add(16);
    }

    /**
     * Надпись с сообщением.
     */
    private Label messageLabel;

    /**
     * Комбобокс с выбором разрешения.
     */
    private ComboBox<ScreenSize> screenSizeComboBox;

    /**
     * Комбобокс с выбором анизатропного фильтра.
     */
    private ComboBox<Integer> anisotropyComboBox;

    /**
     * Координата белой точки экспозиции.
     */
    private Spinner<Double> toneMapFilterWhitePointX;

    /**
     * Координата белой точки экспозиции.
     */
    private Spinner<Double> toneMapFilterWhitePointY;

    /**
     * Координата белой точки экспозиции.
     */
    private Spinner<Double> toneMapFilterWhitePointZ;

    /**
     * Включение/выключение полноэкранного режима.
     */
    private CheckBox fullscreenCheckBox;

    /**
     * Включение/выключение режима гамма коррекции.
     */
    private CheckBox gammaCorrectionCheckBox;

    /**
     * Включение/выключение фиьтра для коррекции экспозиции.
     */
    private CheckBox toneMapFilterCheckBox;

    /**
     * Включение/выключение FXAA.
     */
    private CheckBox fxaaFilterCheckBox;

    /**
     * Игнорировать ли слушателей.
     */
    private boolean ignoreListeners;

    @Override
    public void show(final Window owner) {
        super.show(owner);
        setIgnoreListeners(true);
        try {
            load();
        } finally {
            setIgnoreListeners(false);
        }
    }

    /**
     * @param ignoreListeners Игнорировать ли слушателей.
     */
    private void setIgnoreListeners(boolean ignoreListeners) {
        this.ignoreListeners = ignoreListeners;
    }

    /**
     * @return Игнорировать ли слушателей.
     */
    private boolean isIgnoreListeners() {
        return ignoreListeners;
    }

    @Override
    protected void createContent(final VBox root) {
        super.createContent(root);

        messageLabel = new Label();
        messageLabel.setId(CSSIds.GRAPHICS_DIALOG_MESSAGE_LABEL);

        FXUtils.bindFixedWidth(messageLabel, root.widthProperty().multiply(0.8));
        FXUtils.addClassTo(messageLabel, CSSClasses.MAIN_FONT_15);
        FXUtils.addToPane(messageLabel, root);

        VBox.setMargin(messageLabel, MESSAGE_OFFSET);

        createScreenSizeControl(root);
        createFullscreenControl(root);
        createAnisotropyControl(root);
        createGammaCorrectionControl(root);
        createFXAAControl(root);
        createToneMapFilterControl(root);
        createToneMapFilterWhitePointControl(root);
    }

    /**
     * Создание контрола для активации гамма коррекции.
     */
    private void createGammaCorrectionControl(final VBox root) {

        final HBox gammaCorrectionContainer = new HBox();
        gammaCorrectionContainer.setAlignment(Pos.CENTER_LEFT);

        final Label gammaCorrectionLabel = new Label(Messages.GRAPHICS_DIALOG_GAMMA_CORRECTION + ":");
        gammaCorrectionLabel.setId(CSSIds.GRAPHICS_DIALOG_LABEL);

        gammaCorrectionCheckBox = new CheckBox();
        gammaCorrectionCheckBox.setId(CSSIds.GRAPHICS_DIALOG_FIELD);
        gammaCorrectionCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(gammaCorrectionLabel, gammaCorrectionContainer);
        FXUtils.addToPane(gammaCorrectionCheckBox, gammaCorrectionContainer);
        FXUtils.addToPane(gammaCorrectionContainer, root);

        FXUtils.addClassTo(gammaCorrectionLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(gammaCorrectionCheckBox, CSSClasses.MAIN_FONT_13);

        VBox.setMargin(gammaCorrectionContainer, FIELD_OFFSET);
    }

    /**
     * Создание настройки для активации фильтра экспозиции.
     */
    private void createToneMapFilterControl(final VBox root) {

        final HBox toneMapFilterContainer = new HBox();
        toneMapFilterContainer.setAlignment(Pos.CENTER_LEFT);

        final Label toneMapFilterLabel = new Label(Messages.GRAPHICS_DIALOG_TONEMAP_FILTER + ":");
        toneMapFilterLabel.setId(CSSIds.GRAPHICS_DIALOG_LABEL);

        toneMapFilterCheckBox = new CheckBox();
        toneMapFilterCheckBox.setId(CSSIds.GRAPHICS_DIALOG_FIELD);
        toneMapFilterCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(toneMapFilterLabel, toneMapFilterContainer);
        FXUtils.addToPane(toneMapFilterCheckBox, toneMapFilterContainer);
        FXUtils.addToPane(toneMapFilterContainer, root);

        FXUtils.addClassTo(toneMapFilterLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(toneMapFilterCheckBox, CSSClasses.MAIN_FONT_13);

        VBox.setMargin(toneMapFilterContainer, FIELD_OFFSET);
    }

    /**
     * Создание настройки белой точки фильтра экспозиции.
     */
    private void createToneMapFilterWhitePointControl(final VBox root) {

        final HBox toneMapFilterWhitePointContainer = new HBox();
        toneMapFilterWhitePointContainer.setAlignment(Pos.CENTER_LEFT);
        toneMapFilterWhitePointContainer.disableProperty().bind(toneMapFilterCheckBox.selectedProperty().not());

        final Label toneMapFilterWhitePointLabel = new Label(Messages.GRAPHICS_DIALOG_TONEMAP_FILTER_WHITE_POINT + ":");
        toneMapFilterWhitePointLabel.setId(CSSIds.GRAPHICS_DIALOG_LABEL);

        final Label xLabel = new Label("x:");
        xLabel.setId(CSSIds.GRAPHICS_DIALOG_FIELD);

        final Label yLabel = new Label("y:");
        yLabel.setId(CSSIds.GRAPHICS_DIALOG_FIELD);

        final Label zLabel = new Label("z:");
        zLabel.setId(CSSIds.GRAPHICS_DIALOG_FIELD);

        SpinnerValueFactory<Double> valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(-30, 30, 0, 0.1);

        toneMapFilterWhitePointX = new Spinner<>();
        toneMapFilterWhitePointX.setId(CSSIds.GRAPHICS_DIALOG_FIELD);
        toneMapFilterWhitePointX.setValueFactory(valueFactory);
        toneMapFilterWhitePointX.setEditable(true);
        toneMapFilterWhitePointX.setOnScroll(event -> processScroll(toneMapFilterWhitePointX, event));
        toneMapFilterWhitePointX.valueProperty().addListener((observable, oldValue, newValue) -> validate());

        valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(-30, 30, 0, 0.1);

        toneMapFilterWhitePointY = new Spinner<>();
        toneMapFilterWhitePointY.setId(CSSIds.GRAPHICS_DIALOG_FIELD);
        toneMapFilterWhitePointY.setValueFactory(valueFactory);
        toneMapFilterWhitePointY.setEditable(true);
        toneMapFilterWhitePointY.setOnScroll(event -> processScroll(toneMapFilterWhitePointY, event));
        toneMapFilterWhitePointY.valueProperty().addListener((observable, oldValue, newValue) -> validate());

        valueFactory = new SpinnerValueFactory.DoubleSpinnerValueFactory(-30, 30, 0, 0.1);

        toneMapFilterWhitePointZ = new Spinner<>();
        toneMapFilterWhitePointZ.setId(CSSIds.GRAPHICS_DIALOG_FIELD);
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

        FXUtils.addClassTo(toneMapFilterWhitePointLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(xLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(toneMapFilterWhitePointX, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(yLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(toneMapFilterWhitePointY, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(zLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(toneMapFilterWhitePointZ, CSSClasses.MAIN_FONT_13);

        HBox.setMargin(xLabel, new Insets(0, 0, 0, 4));
        VBox.setMargin(toneMapFilterWhitePointContainer, LAST_FIELD_OFFSET);
    }

    /**
     * Процесс скролирования значения.
     */
    private void processScroll(final Spinner<Double> spinner, final ScrollEvent event) {
        if (!event.isControlDown()) return;

        final double deltaY = event.getDeltaY();

        if (deltaY > 0) {
            spinner.increment(1);
        } else {
            spinner.decrement(1);
        }
    }

    /**
     * Создание настройки для активации полноэкранного режима.
     */
    private void createFullscreenControl(final VBox root) {

        final HBox fullscreenContainer = new HBox();
        fullscreenContainer.setAlignment(Pos.CENTER_LEFT);

        final Label fullscreenLabel = new Label(Messages.GRAPHICS_DIALOG_FULLSCREEN + ":");
        fullscreenLabel.setId(CSSIds.GRAPHICS_DIALOG_LABEL);

        fullscreenCheckBox = new CheckBox();
        fullscreenCheckBox.setId(CSSIds.GRAPHICS_DIALOG_FIELD);
        fullscreenCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(fullscreenLabel, fullscreenContainer);
        FXUtils.addToPane(fullscreenCheckBox, fullscreenContainer);
        FXUtils.addToPane(fullscreenContainer, root);

        FXUtils.addClassTo(fullscreenLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(fullscreenCheckBox, CSSClasses.MAIN_FONT_13);

        VBox.setMargin(fullscreenContainer, FIELD_OFFSET);
    }

    /**
     * Создание настройки для активации FXAA.
     */
    private void createFXAAControl(final VBox root) {

        final HBox fxaaContainer = new HBox();
        fxaaContainer.setAlignment(Pos.CENTER_LEFT);

        final Label fxaaLabel = new Label(Messages.GRAPHICS_DIALOG_FXAA + ":");
        fxaaLabel.setId(CSSIds.GRAPHICS_DIALOG_LABEL);

        fxaaFilterCheckBox = new CheckBox();
        fxaaFilterCheckBox.setId(CSSIds.GRAPHICS_DIALOG_FIELD);
        fxaaFilterCheckBox.selectedProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(fxaaLabel, fxaaContainer);
        FXUtils.addToPane(fxaaFilterCheckBox, fxaaContainer);
        FXUtils.addToPane(fxaaContainer, root);

        FXUtils.addClassTo(fxaaLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(fxaaFilterCheckBox, CSSClasses.MAIN_FONT_13);

        VBox.setMargin(fxaaContainer, FIELD_OFFSET);
    }

    /**
     * Создание настройкид ля выбора анизатроной фильтрации.
     */
    private void createAnisotropyControl(final VBox root) {

        final HBox anisotropyContainer = new HBox();
        anisotropyContainer.setAlignment(Pos.CENTER_LEFT);

        final Label anisotropyLabel = new Label(Messages.GRAPHICS_DIALOG_ANISOTROPY + ":");
        anisotropyLabel.setId(CSSIds.GRAPHICS_DIALOG_LABEL);

        anisotropyComboBox = new ComboBox<>();
        anisotropyComboBox.setId(CSSIds.GRAPHICS_DIALOG_FIELD);
        anisotropyComboBox.prefWidthProperty().bind(root.widthProperty());
        anisotropyComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(anisotropyLabel, anisotropyContainer);
        FXUtils.addToPane(anisotropyComboBox, anisotropyContainer);
        FXUtils.addToPane(anisotropyContainer, root);

        FXUtils.addClassTo(anisotropyLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(anisotropyComboBox, CSSClasses.MAIN_FONT_13);

        VBox.setMargin(anisotropyContainer, FIELD_OFFSET);

        final ObservableList<Integer> items = anisotropyComboBox.getItems();

        ANISOTROPYCS.forEach(items::add);
    }

    /**
     * Создание настройки для выбора разрешения экрана.
     */
    private void createScreenSizeControl(final VBox root) {

        final HBox screenSizeContainer = new HBox();
        screenSizeContainer.setAlignment(Pos.CENTER_LEFT);

        final Label screenSizeLabel = new Label(Messages.GRAPHICS_DIALOG_SCREEN_SIZE + ":");
        screenSizeLabel.setId(CSSIds.GRAPHICS_DIALOG_LABEL);

        screenSizeComboBox = new ComboBox<>();
        screenSizeComboBox.setId(CSSIds.GRAPHICS_DIALOG_FIELD);
        screenSizeComboBox.prefWidthProperty().bind(root.widthProperty());
        screenSizeComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> validate());

        FXUtils.addToPane(screenSizeLabel, screenSizeContainer);
        FXUtils.addToPane(screenSizeComboBox, screenSizeContainer);
        FXUtils.addToPane(screenSizeContainer, root);

        FXUtils.addClassTo(screenSizeLabel, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(screenSizeComboBox, CSSClasses.MAIN_FONT_13);

        VBox.setMargin(screenSizeContainer, FIELD_OFFSET);

        final ObservableList<ScreenSize> items = screenSizeComboBox.getItems();

        SCREEN_SIZES.forEach(items::add);
    }

    /**
     * @return включение/выключение режима гамма коррекции.
     */
    private CheckBox getGammaCorrectionCheckBox() {
        return gammaCorrectionCheckBox;
    }

    /**
     * @return включение/выключение фиьтра для коррекции экспозиции.
     */
    private CheckBox getToneMapFilterCheckBox() {
        return toneMapFilterCheckBox;
    }

    /**
     * @return координата белой точки экспозиции.
     */
    private Spinner<Double> getToneMapFilterWhitePointX() {
        return toneMapFilterWhitePointX;
    }

    /**
     * @return координата белой точки экспозиции.
     */
    private Spinner<Double> getToneMapFilterWhitePointY() {
        return toneMapFilterWhitePointY;
    }

    /**
     * @return координата белой точки экспозиции.
     */
    private Spinner<Double> getToneMapFilterWhitePointZ() {
        return toneMapFilterWhitePointZ;
    }

    /**
     * @return включение/выключение FXAA.
     */
    private CheckBox getFXAAFilterCheckBox() {
        return fxaaFilterCheckBox;
    }

    /**
     * @return включение/выключение полноэкранного режима.
     */
    private CheckBox getFullscreenCheckBox() {
        return fullscreenCheckBox;
    }

    /**
     * @return комбобокс с выбором анизатропного фильтра.
     */
    private ComboBox<Integer> getAnisotropyComboBox() {
        return anisotropyComboBox;
    }

    /**
     * @return комбобокс с выбором разрешения.
     */
    private ComboBox<ScreenSize> getScreenSizeComboBox() {
        return screenSizeComboBox;
    }

    /**
     * @return надпись с сообщением.
     */
    public Label getMessageLabel() {
        return messageLabel;
    }

    /**
     * Валидация изменений.
     */
    private void validate() {
        if (isIgnoreListeners()) return;

        final Label messageLabel = getMessageLabel();

        int needRestart = 0;

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final ScreenSize currentScreenSize = editorConfig.getScreenSize();
        final int currentAnisotropy = editorConfig.getAnisotropy();
        final boolean currentFullscreen = editorConfig.isFullscreen();
        final boolean currentGammaCorrection = editorConfig.isGammaCorrection();

        final ComboBox<ScreenSize> screenSizeComboBox = getScreenSizeComboBox();
        final ScreenSize screenSize = screenSizeComboBox.getSelectionModel().getSelectedItem();

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final Integer anisotropy = anisotropyComboBox.getSelectionModel().getSelectedItem();

        final CheckBox fullscreenCheckBox = getFullscreenCheckBox();
        final boolean fullscreen = fullscreenCheckBox.isSelected();

        final CheckBox fxaaFilterCheckBox = getFXAAFilterCheckBox();
        final boolean fxaa = fxaaFilterCheckBox.isSelected();

        final CheckBox gammaCorrectionCheckBox = getGammaCorrectionCheckBox();
        final boolean gammaCorrection = gammaCorrectionCheckBox.isSelected();

        if (currentScreenSize != screenSize) {
            needRestart++;
        } else if (currentAnisotropy != anisotropy) {
            needRestart++;
        } else if (currentFullscreen != fullscreen) {
            needRestart++;
        } else if (currentGammaCorrection != gammaCorrection) {
            needRestart++;
        }

        if (needRestart > 0) {
            messageLabel.setText(Messages.GRAPHICS_DIALOG_MESSAGE);
        } else {
            messageLabel.setText(StringUtils.EMPTY);
        }
    }

    /**
     * Загрузка текущих параметров.
     */
    private void load() {

        final EditorConfig editorConfig = EditorConfig.getInstance();

        final ComboBox<ScreenSize> screenSizeComboBox = getScreenSizeComboBox();
        final SingleSelectionModel<ScreenSize> selectedScreenSize = screenSizeComboBox.getSelectionModel();
        selectedScreenSize.select(editorConfig.getScreenSize());

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final SingleSelectionModel<Integer> selectedAnisotropy = anisotropyComboBox.getSelectionModel();
        selectedAnisotropy.select(Integer.valueOf(editorConfig.getAnisotropy()));

        final CheckBox fxaaFilterCheckBox = getFXAAFilterCheckBox();
        fxaaFilterCheckBox.setSelected(editorConfig.isFXAA());

        final CheckBox fullscreenCheckBox = getFullscreenCheckBox();
        fullscreenCheckBox.setSelected(editorConfig.isFullscreen());

        final CheckBox gammaCorrectionCheckBox = getGammaCorrectionCheckBox();
        gammaCorrectionCheckBox.setSelected(editorConfig.isGammaCorrection());

        final CheckBox toneMapFilterCheckBox = getToneMapFilterCheckBox();
        toneMapFilterCheckBox.setSelected(editorConfig.isToneMapFilter());

        final Vector3f toneMapFilterWhitePoint = editorConfig.getToneMapFilterWhitePoint();

        final Spinner<Double> toneMapFilterWhitePointX = getToneMapFilterWhitePointX();
        toneMapFilterWhitePointX.getValueFactory().setValue((double) toneMapFilterWhitePoint.getX());

        final Spinner<Double> toneMapFilterWhitePointY = getToneMapFilterWhitePointY();
        toneMapFilterWhitePointY.getValueFactory().setValue((double) toneMapFilterWhitePoint.getY());

        final Spinner<Double> toneMapFilterWhitePointZ = getToneMapFilterWhitePointZ();
        toneMapFilterWhitePointZ.getValueFactory().setValue((double) toneMapFilterWhitePoint.getZ());
    }

    @Override
    protected void createActions(final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        final Button okButton = new Button(Messages.GRAPHICS_DIALOG_BUTTON_OK);
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processOk());

        final Button cancelButton = new Button(Messages.GRAPHICS_DIALOG_BUTTON_CANCEL);
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    /**
     * Процесс сохранения и приминения изменений.
     */
    private void processOk() {

        int needRestart = 0;

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final ScreenSize currentScreenSize = editorConfig.getScreenSize();
        final int currentAnisotropy = editorConfig.getAnisotropy();
        final boolean currentFullscreen = editorConfig.isFullscreen();
        final boolean currentGammaCorrection = editorConfig.isGammaCorrection();

        final ComboBox<ScreenSize> screenSizeComboBox = getScreenSizeComboBox();
        final ScreenSize screenSize = screenSizeComboBox.getSelectionModel().getSelectedItem();

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final Integer anisotropy = anisotropyComboBox.getSelectionModel().getSelectedItem();

        final CheckBox fxaaFilterCheckBox = getFXAAFilterCheckBox();
        final boolean fxaa = fxaaFilterCheckBox.isSelected();

        final CheckBox fullscreenCheckBox = getFullscreenCheckBox();
        final boolean fullscreen = fullscreenCheckBox.isSelected();

        final CheckBox gammaCorrectionCheckBox = getGammaCorrectionCheckBox();
        final boolean gammaCorrection = gammaCorrectionCheckBox.isSelected();

        final CheckBox toneMapFilterCheckBox = getToneMapFilterCheckBox();
        final boolean toneMapFilter = toneMapFilterCheckBox.isSelected();

        final float toneMapFilterWhitePointX = getToneMapFilterWhitePointX().getValue().floatValue();
        final float toneMapFilterWhitePointY = getToneMapFilterWhitePointY().getValue().floatValue();
        final float toneMapFilterWhitePointZ = getToneMapFilterWhitePointZ().getValue().floatValue();

        final Vector3f toneMapFilterWhitePoint = new Vector3f(toneMapFilterWhitePointX, toneMapFilterWhitePointY, toneMapFilterWhitePointZ);

        if (currentScreenSize != screenSize) {
            needRestart++;
        } else if (currentAnisotropy != anisotropy) {
            needRestart++;
        } else if (currentFullscreen != fullscreen) {
            needRestart++;
        } else if (currentGammaCorrection != gammaCorrection) {
            needRestart++;
        }

        editorConfig.setAnisotropy(anisotropy);
        editorConfig.setFXAA(fxaa);
        editorConfig.setScreenSize(screenSize);
        editorConfig.setFullscreen(fullscreen);
        editorConfig.setGammaCorrection(gammaCorrection);
        editorConfig.setToneMapFilter(toneMapFilter);
        editorConfig.setToneMapFilterWhitePoint(toneMapFilterWhitePoint);
        editorConfig.save();

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

    @Override
    protected String getTitleText() {
        return Messages.GRAPHICS_DIALOG_TITLE;
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
