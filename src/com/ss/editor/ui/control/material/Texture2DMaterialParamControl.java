package com.ss.editor.ui.control.material;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.ss.editor.Editor;
import com.ss.editor.manager.ExecutorManager;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Path;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.StringUtils;

import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_ADD;
import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_FLIP;
import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_REMOVE;
import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_REPEAT;
import static com.ss.editor.ui.css.CSSIds.TEXTURE_2D_MATERIAL_PARAM_CONTROL_PREVIEW;


/**
 * Реализация контрола для выбора текстуры.
 *
 * @author Ronn
 */
public class Texture2DMaterialParamControl extends MaterialParamControl {

    public static final Insets ELEMENT_OFFSET = new Insets(0, 0, 0, 3);

    private static final JavaFXImageManager IMAGE_MANAGER = JavaFXImageManager.getInstance();
    private static final ExecutorManager EXECUTOR_MANAGER = ExecutorManager.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    /**
     * Подсказка для описания текущей текстуры.
     */
    private Tooltip textureTooltip;

    /**
     * Превью текстуры.
     */
    private ImageView texturePreview;

    /**
     * Чекбокс для установки параметра Repeat.
     */
    private CheckBox repeatButton;

    /**
     * Чекбокс дляустановки параметра Flip.
     */
    private CheckBox flipButton;

    public Texture2DMaterialParamControl(final Runnable changeHandler, final Material material, final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        textureTooltip = new Tooltip();

        final VBox previewContainer = new VBox();
        previewContainer.setId(TEXTURE_2D_MATERIAL_PARAM_CONTROL_PREVIEW);

        texturePreview = new ImageView();
        texturePreview.fitHeightProperty().bind(previewContainer.heightProperty().subtract(2));
        texturePreview.fitWidthProperty().bind(previewContainer.widthProperty().subtract(2));

        Tooltip.install(texturePreview, textureTooltip);

        final Button addButton = new Button();
        addButton.setId(CSSIds.MATERIAL_PARAM_CONTROL_BUTTON);
        addButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_ADD));
        addButton.setGraphic(new ImageView(Icons.ADD_18));
        addButton.setOnAction(event -> processAdd());

        repeatButton = new CheckBox();
        repeatButton.setId(CSSIds.MATERIAL_PARAM_CONTROL_CHECKBOX);
        repeatButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_REPEAT));
        repeatButton.selectedProperty().addListener((observable1, oldValue1, newValue) -> processChangeRepeat(newValue));

        flipButton = new CheckBox();
        flipButton.setId(CSSIds.MATERIAL_PARAM_CONTROL_CHECKBOX);
        flipButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_FLIP));
        flipButton.selectedProperty().addListener((observable, oldValue, newValue) -> processChangeFlip(newValue));

        final Button removeButton = new Button();
        removeButton.setId(CSSIds.MATERIAL_PARAM_CONTROL_BUTTON);
        removeButton.setTooltip(new Tooltip(TEXTURE_2D_MATERIAL_PARAM_CONTROL_REMOVE));
        removeButton.setGraphic(new ImageView(Icons.REMOVE_18));
        removeButton.setOnAction(event -> processRemove());

        FXUtils.addToPane(texturePreview, previewContainer);
        FXUtils.addToPane(previewContainer, this);
        FXUtils.addToPane(addButton, this);
        FXUtils.addToPane(repeatButton, this);
        FXUtils.addToPane(flipButton, this);
        FXUtils.addToPane(removeButton, this);

        FXUtils.addClassTo(addButton, CSSClasses.TOOLBAR_BUTTON);
        FXUtils.addClassTo(removeButton, CSSClasses.TOOLBAR_BUTTON);

        HBox.setMargin(previewContainer, ELEMENT_OFFSET);
        HBox.setMargin(addButton, ELEMENT_OFFSET);
        HBox.setMargin(repeatButton, ELEMENT_OFFSET);
        HBox.setMargin(flipButton, ELEMENT_OFFSET);
        HBox.setMargin(removeButton, ELEMENT_OFFSET);

        removeButton.disableProperty().bind(texturePreview.imageProperty().isNull());
        repeatButton.disableProperty().bind(texturePreview.imageProperty().isNull());
        flipButton.disableProperty().bind(texturePreview.imageProperty().isNull());
    }

    /**
     * @return подсказка для описания текущей текстуры.
     */
    public Tooltip getTextureTooltip() {
        return textureTooltip;
    }

    /**
     * Процесс изменения параметра Repeat.
     */
    private void processChangeRepeat(final Boolean newValue) {

        if(isIgnoreListeners()) {
            return;
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() ->  processChangeRepeatImpl(newValue));
    }

    /**
     * Процесс изменения свойтсва текстуры.
     */
    private void processChangeRepeatImpl(Boolean newValue) {

        final Material material = getMaterial();
        final MatParamTexture textureParam = material.getTextureParam(getParameterName());
        final Texture2D texture2D = (Texture2D) textureParam.getValue();

        if(newValue) {
            texture2D.setWrap(Texture.WrapMode.Repeat);
        } else {
            texture2D.setWrap(Texture.WrapMode.EdgeClamp);
        }

        material.setTexture(getParameterName(), texture2D);

        EXECUTOR_MANAGER.addFXTask(this::changed);
    }

    /**
     * Процесс изменения флага Flip.
     */
    private void processChangeFlip(final Boolean newValue) {

        if(isIgnoreListeners()) {
            return;
        }

        EXECUTOR_MANAGER.addEditorThreadTask(() -> processChangeFlipImpl(newValue));
    }

    /**
     * Процесс изменения свойтсва текстуры.
     */
    private void processChangeFlipImpl(final Boolean newValue) {

        final Material material = getMaterial();
        final MatParamTexture textureParam = material.getTextureParam(getParameterName());
        final Texture2D texture2D = (Texture2D) textureParam.getValue();
        final TextureKey textureKey = (TextureKey) texture2D.getKey();
        textureKey.setFlipY(newValue);

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Texture texture = assetManager.loadTexture(textureKey);

        if(texture2D.getWrap(Texture.WrapAxis.S) == Texture.WrapMode.Repeat) {
            texture.setWrap(Texture.WrapMode.Repeat);
        }

        material.setTexture(getParameterName(), texture);

        EXECUTOR_MANAGER.addFXTask(this::changed);
    }

    /**
     * процесс добавление текстуры.
     */
    private void processAdd() {

        final EditorFXScene scene = EDITOR.getScene();

        final AssetEditorDialog dialog = new AssetEditorDialog(this::addTexture);
        dialog.show(scene.getWindow());
    }

    /**
     * Добавление новой текстуры.
     *
     * @param path путь к текстуре.
     */
    private void addTexture(final Path path) {
        EXECUTOR_MANAGER.addEditorThreadTask(() -> addTextureImpl(path));
    }

    /**
     * Процесс изменения текстуры.
     */
    private void addTextureImpl(final Path path) {

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Path assetFile = EditorUtil.getAssetFile(path);

        final CheckBox flipButton = getFlipButton();
        final CheckBox repeatButton = getRepeatButton();

        final TextureKey key = new TextureKey(assetFile.toString());
        key.setFlipY(flipButton.isSelected());

        final Texture texture = assetManager.loadTexture(key);

        if(repeatButton.isSelected()) {
            texture.setWrap(Texture.WrapMode.Repeat);
        }

        final Material material = getMaterial();
        material.setTexture(getParameterName(), texture);

        EXECUTOR_MANAGER.addFXTask(() -> {
            changed();
            setIgnoreListeners(true);
            reload();
            setIgnoreListeners(false);
        });
    }

    /**
     * Удаление текстуры.
     */
    private void processRemove() {
        EXECUTOR_MANAGER.addEditorThreadTask(this::removeTextureImpl);
    }

    /**
     * процесс удаления текстуры.
     */
    private void removeTextureImpl() {

        final Material material = getMaterial();
        material.clearParam(getParameterName());

        EXECUTOR_MANAGER.addFXTask(() -> {
            changed();
            setIgnoreListeners(true);
            reload();
            setIgnoreListeners(false);
        });
    }

    /**
     * @return чекбокс для установки параметра Repeat.
     */
    private CheckBox getRepeatButton() {
        return repeatButton;
    }

    /**
     * @return чекбокс дляустановки параметра Flip.
     */
    private CheckBox getFlipButton() {
        return flipButton;
    }

    /**
     * @return превью текстуры.
     */
    public ImageView getTexturePreview() {
        return texturePreview;
    }

    @Override
    protected void reload() {
        super.reload();

        final Tooltip textureTooltip = getTextureTooltip();
        final Material material = getMaterial();
        final MatParamTexture param = (MatParamTexture) material.getParam(getParameterName());

        if (param == null) {

            final ImageView preview = getTexturePreview();
            preview.setImage(null);

            final CheckBox flipButton = getFlipButton();
            flipButton.setSelected(false);

            final CheckBox repeatButton = getRepeatButton();
            repeatButton.setSelected(false);

            textureTooltip.setText(StringUtils.EMPTY);
            return;
        }

        final Texture2D texture2D = (Texture2D) param.getValue();
        final TextureKey textureKey = (TextureKey) texture2D.getKey();

        final CheckBox flipButton = getFlipButton();
        flipButton.setSelected(textureKey.isFlipY());

        final CheckBox repeatButton = getRepeatButton();
        repeatButton.setSelected(texture2D.getWrap(Texture.WrapAxis.S) == Texture.WrapMode.Repeat);

        final Path realFile = EditorUtil.getRealFile(textureKey.getName());

        final ImageView preview = getTexturePreview();
        preview.setImage(IMAGE_MANAGER.getTexturePreview(realFile, 28, 28));

        textureTooltip.setText(textureKey.getName());
    }
}
