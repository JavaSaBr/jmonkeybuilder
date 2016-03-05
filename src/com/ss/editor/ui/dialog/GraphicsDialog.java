package com.ss.editor.ui.dialog;

import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.config.ScreenSize;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static com.ss.editor.ui.css.CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER;
import static com.ss.editor.ui.css.CSSIds.EDITOR_DIALOG_BUTTON_CANCEL;
import static com.ss.editor.ui.css.CSSIds.EDITOR_DIALOG_BUTTON_OK;
import static javafx.geometry.Pos.CENTER_LEFT;

/**
 * Реализация диалога для настроек графики.
 *
 * @author Ronn
 */
public class GraphicsDialog extends EditorDialog {

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);
    private static final Insets MESSAGE_OFFSET = new Insets(5, 0, 5, 0);
    private static final Insets LAST_FIELD_OFFSET = new Insets(10, CANCEL_BUTTON_OFFSET.getRight(), 15, 0);
    private static final Insets FIELD_OFFSET = new Insets(10, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);

    private static final Point DIALOG_SIZE = new Point(500, 250);

    private static final Array<ScreenSize> SCREEN_SIZES = ArrayFactory.newArray(ScreenSize.class);
    private static final Array<Integer> ANISOTROPYCS = ArrayFactory.newArray(Integer.class);

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
        createAnisotropyControl(root);
        createFXAAControl(root);
    }

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

        VBox.setMargin(fxaaContainer, LAST_FIELD_OFFSET);
    }

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
     * @return включение/выключение FXAA.
     */
    private CheckBox getFXAAFilterCheckBox() {
        return fxaaFilterCheckBox;
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

        if (isIgnoreListeners()) {
            return;
        }

        final Label messageLabel = getMessageLabel();

        int needRestart = 0;

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final ScreenSize currentScreenSize = editorConfig.getScreenSize();
        final int currentAnisotropy = editorConfig.getAnisotropy();
        final boolean currentFXAA = editorConfig.isFXAA();

        final ComboBox<ScreenSize> screenSizeComboBox = getScreenSizeComboBox();
        final ScreenSize screenSize = screenSizeComboBox.getSelectionModel().getSelectedItem();

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final Integer anisotropy = anisotropyComboBox.getSelectionModel().getSelectedItem();

        final CheckBox fxaaFilterCheckBox = getFXAAFilterCheckBox();
        final boolean fxaa = fxaaFilterCheckBox.isSelected();

        if (currentScreenSize != screenSize) {
            needRestart++;
        }

        if (currentFXAA != fxaa) {
            needRestart++;
        }

        if (anisotropy != currentAnisotropy) {
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
        final boolean currentFXAA = editorConfig.isFXAA();

        final ComboBox<ScreenSize> screenSizeComboBox = getScreenSizeComboBox();
        final ScreenSize screenSize = screenSizeComboBox.getSelectionModel().getSelectedItem();

        final ComboBox<Integer> anisotropyComboBox = getAnisotropyComboBox();
        final Integer anisotropy = anisotropyComboBox.getSelectionModel().getSelectedItem();

        final CheckBox fxaaFilterCheckBox = getFXAAFilterCheckBox();
        final boolean fxaa = fxaaFilterCheckBox.isSelected();

        if (currentScreenSize != screenSize) {
            needRestart++;
        }

        if (currentFXAA != fxaa) {
            needRestart++;
        }

        if (anisotropy != currentAnisotropy) {
            needRestart++;
        }

        editorConfig.setAnisotropy(anisotropy);
        editorConfig.setFXAA(fxaa);
        editorConfig.setScreenSize(screenSize);
        editorConfig.save();

        if (needRestart > 0) {
            System.exit(2);
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
