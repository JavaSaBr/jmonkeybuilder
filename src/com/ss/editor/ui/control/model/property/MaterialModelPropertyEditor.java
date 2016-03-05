package com.ss.editor.ui.control.model.property;

import com.jme3.asset.AssetManager;
import com.jme3.asset.MaterialKey;
import com.jme3.material.Material;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.Messages;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.event.FXEventManager;
import com.ss.editor.ui.event.impl.RequestedOpenFileEvent;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Path;
import java.nio.file.Paths;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

/**
 * Реализация контрола по редактированию материала модели.
 *
 * @author Ronn
 */
public class MaterialModelPropertyEditor extends ModelPropertyControl<Material> {

    public static final String NO_MATERIAL = Messages.MATERIAL_MODEL_PROPERTY_CONTROL_NO_MATERIAL;
    public static final Insets BUTTON_OFFSET = new Insets(0, 0, 0, 3);

    private static final FXEventManager FX_EVENT_MANAGER = FXEventManager.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    private static final Array<String> MATERIAL_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        MATERIAL_EXTENSIONS.add(FileExtensions.JME_MATERIAL);
    }

    /**
     * Надпись с названием материала.
     */
    private Label materialLabel;

    /**
     * Кнопка для выбора материала.
     */
    private Button changeButton;

    /**
     * Кнопка для редактирования материала.
     */
    private Button editButton;

    public MaterialModelPropertyEditor(final Runnable changeHandler, final Material element, final String paramName) {
        super(changeHandler, element, paramName);
    }

    @Override
    protected void createComponents(final HBox container) {
        super.createComponents(container);

        materialLabel = new Label(NO_MATERIAL);
        materialLabel.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_LABEL);

        changeButton = new Button();
        changeButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        changeButton.setGraphic(new ImageView(Icons.ADD_24));
        changeButton.setOnAction(event -> processChange());

        editButton = new Button();
        editButton.setId(CSSIds.MODEL_PARAM_CONTROL_MATERIAL_BUTTON);
        editButton.setGraphic(new ImageView(Icons.EDIT_16));
        editButton.disableProperty().bind(materialLabel.textProperty().isEqualTo(NO_MATERIAL));
        editButton.setOnAction(event -> processEdit());

        FXUtils.addToPane(materialLabel, container);
        FXUtils.addToPane(changeButton, container);
        FXUtils.addToPane(editButton, container);

        HBox.setMargin(changeButton, BUTTON_OFFSET);
        HBox.setMargin(editButton, BUTTON_OFFSET);

        FXUtils.addClassTo(changeButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(editButton, CSSClasses.TOOLBAR_BUTTON);
    }

    /**
     * Процесс смены материала.
     */
    private void processChange() {

        final EditorFXScene scene = EDITOR.getScene();

        final AssetEditorDialog dialog = new AssetEditorDialog(this::addMaterial);
        dialog.setExtensionFilter(MATERIAL_EXTENSIONS);
        dialog.show(scene.getWindow());
    }

    /**
     * Добавление нового материала.
     */
    private void addMaterial(final Path file) {

        final Path assetFile = EditorUtil.getAssetFile(file);
        final MaterialKey materialKey = new MaterialKey(EditorUtil.toClasspath(assetFile));

        final AssetManager assetManager = EDITOR.getAssetManager();
        assetManager.clearCache();

        final Material material = assetManager.loadAsset(materialKey);

        setElement(material);
        changed();

        setIgnoreListener(true);
        try {
            reload();
        } finally {
            setIgnoreListener(false);
        }
    }

    /**
     * Процесс открытия редактирования материала.
     */
    private void processEdit() {

        final Material element = getElement();

        if(element == null) {
            return;
        }

        final Path assetFile = Paths.get(element.getAssetName());
        final Path realFile = EditorUtil.getRealFile(assetFile);

        final RequestedOpenFileEvent event = new RequestedOpenFileEvent();
        event.setFile(realFile);

        FX_EVENT_MANAGER.notify(event);
    }

    /**
     * @return надпись с названием материала.
     */
    private Label getMaterialLabel() {
        return materialLabel;
    }

    @Override
    protected void reload() {

        final Material element = getElement();

        final Label materialLabel = getMaterialLabel();
        materialLabel.setText(element == null || StringUtils.isEmpty(element.getAssetName()) ? NO_MATERIAL : element.getAssetName());
    }
}
