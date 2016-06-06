package com.ss.editor.ui.dialog;

import com.ss.editor.Editor;
import com.ss.editor.Messages;
import com.ss.editor.config.EditorConfig;
import com.ss.editor.manager.ClasspathManager;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.scene.EditorFXScene;

import java.awt.*;
import java.io.File;
import java.nio.file.Path;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.stage.Window;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

/**
 * Реализация диалога для всяких настроек.
 *
 * @author Ronn
 */
public class OtherSettingsDialog extends EditorDialog {

    private static final Insets OK_BUTTON_OFFSET = new Insets(0, 4, 0, 0);
    private static final Insets CANCEL_BUTTON_OFFSET = new Insets(0, 15, 0, 0);
    private static final Insets FIELD_OFFSET = new Insets(5, CANCEL_BUTTON_OFFSET.getRight(), 0, 0);
    private static final Insets ADD_REMOVE_BUTTON_OFFSET = new Insets(0, 0, 0, 2);

    private static final Point DIALOG_SIZE = new Point(500, 110);

    private static final Editor EDITOR = Editor.getInstance();

    /**
     * Поле для отображения выбранной папки дополнительного classpath.
     */
    private TextField additionalClasspathField;

    /**
     * Выбранная папка для расширения classpath.
     */
    private Path additionalClasspathFolder;

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

        createAdditionalClasspathControl(root);
    }

    /**
     * Создание настройки для выбора папки для расширения classpath.
     */
    private void createAdditionalClasspathControl(final VBox root) {

        final HBox container = new HBox();
        container.setAlignment(Pos.CENTER_LEFT);

        final Label label = new Label(Messages.OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_LABEL + ":");
        label.setId(CSSIds.GRAPHICS_DIALOG_LABEL);

        additionalClasspathField = new TextField();
        additionalClasspathField.setId(CSSIds.GRAPHICS_DIALOG_FIELD);
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

        FXUtils.addClassTo(label, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(additionalClasspathField, CSSClasses.MAIN_FONT_13);
        FXUtils.addClassTo(addButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(removeButton, CSSClasses.TOOLBAR_BUTTON);

        HBox.setMargin(addButton, ADD_REMOVE_BUTTON_OFFSET);
        HBox.setMargin(removeButton, ADD_REMOVE_BUTTON_OFFSET);
        VBox.setMargin(container, FIELD_OFFSET);
    }

    /**
     * Процесс удаления дополнительного classpath.
     */
    private void processRemoveCF() {
        setAdditionalClasspathFolder(null);

        final TextField textField = getAdditionalClasspathField();
        textField.setText(StringUtils.EMPTY);
    }

    /**
     * @return поле для отображения выбранной папки дополнительного classpath.
     */
    private TextField getAdditionalClasspathField() {
        return additionalClasspathField;
    }

    /**
     * Процесс указания папки для расширения classpath.
     */
    private void processAddCF() {

        final DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle(Messages.OTHER_SETTINGS_DIALOG_CLASSPATH_FOLDER_CHOOSER_TITLE);

        final EditorConfig config = EditorConfig.getInstance();
        final Path currentAdditionalCP = config.getAdditionalClasspath();
        final File currentFolder = currentAdditionalCP == null ? null : currentAdditionalCP.toFile();

        if (currentFolder != null) chooser.setInitialDirectory(currentFolder);

        final EditorFXScene scene = EDITOR.getScene();
        final File folder = chooser.showDialog(scene.getWindow());

        if (folder == null) return;

        setAdditionalClasspathFolder(folder.toPath());

        final TextField textField = getAdditionalClasspathField();
        textField.setText(folder.toString());
    }

    /**
     * Загрузка текущих параметров.
     */
    private void load() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        final Path additionalClasspath = editorConfig.getAdditionalClasspath();

        final TextField additionalClasspathField = getAdditionalClasspathField();
        if (additionalClasspath != null)
            additionalClasspathField.setText(additionalClasspath.toString());

        setAdditionalClasspathFolder(additionalClasspath);
    }

    @Override
    protected boolean isHideOnLostFocus() {
        return false;
    }

    @Override
    protected void createActions(final VBox root) {
        super.createActions(root);

        final HBox container = new HBox();
        container.setId(CSSIds.ASSET_EDITOR_DIALOG_BUTTON_CONTAINER);

        final Button okButton = new Button(Messages.OTHER_SETTINGS_DIALOG_BUTTON_OK);
        okButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_OK);
        okButton.setOnAction(event -> processOk());

        final Button cancelButton = new Button(Messages.OTHER_SETTINGS_DIALOG_BUTTON_CANCEL);
        cancelButton.setId(CSSIds.EDITOR_DIALOG_BUTTON_CANCEL);
        cancelButton.setOnAction(event -> hide());

        FXUtils.addToPane(okButton, container);
        FXUtils.addToPane(cancelButton, container);
        FXUtils.addToPane(container, root);

        HBox.setMargin(okButton, OK_BUTTON_OFFSET);
        HBox.setMargin(cancelButton, CANCEL_BUTTON_OFFSET);
    }

    /**
     * @return выбранная папка для расширения classpath.
     */
    private Path getAdditionalClasspathFolder() {
        return additionalClasspathFolder;
    }

    /**
     * @param additionalClasspathFolder выбранная папка для расширения classpath.
     */
    private void setAdditionalClasspathFolder(Path additionalClasspathFolder) {
        this.additionalClasspathFolder = additionalClasspathFolder;
    }

    /**
     * Процесс сохранения и приминения изменений.
     */
    private void processOk() {

        final EditorConfig editorConfig = EditorConfig.getInstance();
        editorConfig.setAdditionalClasspath(getAdditionalClasspathFolder());
        editorConfig.save();

        final ClasspathManager classpathManager = ClasspathManager.getInstance();
        classpathManager.updateAdditionalCL();

        hide();
    }

    @Override
    protected String getTitleText() {
        return Messages.OTHER_SETTINGS_DIALOG_TITLE;
    }

    @Override
    protected Point getSize() {
        return DIALOG_SIZE;
    }
}
