package com.ss.editor.ui.control.material;

import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.material.MatParamTexture;
import com.jme3.material.Material;
import com.jme3.texture.Texture.WrapAxis;
import com.jme3.texture.Texture.WrapMode;
import com.jme3.texture.Texture2D;
import com.ss.editor.Editor;
import com.ss.editor.FileExtensions;
import com.ss.editor.manager.JavaFXImageManager;
import com.ss.editor.model.undo.EditorOperation;
import com.ss.editor.ui.Icons;
import com.ss.editor.ui.control.material.operation.TextureMaterialParamOperation;
import com.ss.editor.ui.css.CSSClasses;
import com.ss.editor.ui.css.CSSIds;
import com.ss.editor.ui.dialog.asset.AssetEditorDialog;
import com.ss.editor.ui.scene.EditorFXScene;
import com.ss.editor.ui.tooltip.ImageChannelPreview;
import com.ss.editor.util.EditorUtil;

import java.nio.file.Path;
import java.util.function.Consumer;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import rlib.ui.util.FXUtils;
import rlib.util.array.Array;
import rlib.util.array.ArrayFactory;

import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_ADD;
import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_FLIP;
import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_REMOVE;
import static com.ss.editor.Messages.TEXTURE_2D_MATERIAL_PARAM_CONTROL_REPEAT;


/**
 * Реализация контрола для выбора текстуры.
 *
 * @author Ronn
 */
public class Texture2DMaterialParamControl extends MaterialParamControl {

    public static final Insets ELEMENT_OFFSET = new Insets(0, 0, 0, 3);

    private static final Array<String> TEXTURE_EXTENSIONS = ArrayFactory.newArray(String.class);

    static {
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_PNG);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_JPG);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_JPEG);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_TGA);
        TEXTURE_EXTENSIONS.add(FileExtensions.IMAGE_DDS);
    }

    private static final JavaFXImageManager IMAGE_MANAGER = JavaFXImageManager.getInstance();
    private static final Editor EDITOR = Editor.getInstance();

    /**
     * Подсказка для описания текущей текстуры.
     */
    private ImageChannelPreview textureTooltip;

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

    public Texture2DMaterialParamControl(final Consumer<EditorOperation> changeHandler, final Material material, final String parameterName) {
        super(changeHandler, material, parameterName);
    }

    @Override
    protected void createComponents() {
        super.createComponents();

        textureTooltip = new ImageChannelPreview();

        final VBox previewContainer = new VBox();
        previewContainer.setId(CSSIds.TEXTURE_2D_MATERIAL_PARAM_CONTROL_PREVIEW);

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
    public ImageChannelPreview getTextureTooltip() {
        return textureTooltip;
    }

    /**
     * Процесс изменения параметра Repeat.
     */
    private void processChangeRepeat(final Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParamTexture textureParam = material.getTextureParam(parameterName);
        final Texture2D texture2D = (Texture2D) textureParam.getValue();
        final TextureKey key = (TextureKey) texture2D.getKey();

        WrapMode oldMode;
        WrapMode newMode;

        if (newValue) {
            oldMode = WrapMode.EdgeClamp;
            newMode = WrapMode.Repeat;
            texture2D.setWrap(WrapMode.Repeat);
        } else {
            oldMode = WrapMode.Repeat;
            newMode = WrapMode.EdgeClamp;
            texture2D.setWrap(WrapMode.EdgeClamp);
        }

        execute(new TextureMaterialParamOperation(parameterName, key, newMode, key, oldMode));
    }

    /**
     * Процесс изменения флага Flip.
     */
    private void processChangeFlip(final Boolean newValue) {

        if (isIgnoreListeners()) {
            return;
        }

        final String parameterName = getParameterName();

        final Material material = getMaterial();
        final MatParamTexture textureParam = material.getTextureParam(parameterName);
        final Texture2D texture2D = (Texture2D) textureParam.getValue();
        final TextureKey oldKey = (TextureKey) texture2D.getKey();
        final TextureKey newKey = (TextureKey) oldKey.clone();
        newKey.setFlipY(newValue);

        WrapMode mode = WrapMode.EdgeClamp;

        if (texture2D.getWrap(WrapAxis.S) == WrapMode.Repeat) {
            mode = WrapMode.Repeat;
        }

        execute(new TextureMaterialParamOperation(parameterName, newKey, mode, oldKey, mode));
    }

    /**
     * процесс добавление текстуры.
     */
    private void processAdd() {

        final EditorFXScene scene = EDITOR.getScene();

        final AssetEditorDialog dialog = new AssetEditorDialog(this::addTexture);
        dialog.setExtensionFilter(TEXTURE_EXTENSIONS);
        dialog.show(scene.getWindow());
    }

    /**
     * Добавление новой текстуры.
     *
     * @param path путь к текстуре.
     */
    private void addTexture(final Path path) {

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParamTexture textureParam = material.getTextureParam(parameterName);
        final Texture2D oldTexture = textureParam == null ? null : (Texture2D) textureParam.getValue();
        final TextureKey oldKey = oldTexture == null ? null : (TextureKey) oldTexture.getKey();

        WrapMode oldMode = WrapMode.EdgeClamp;

        if (oldTexture != null && oldTexture.getWrap(WrapAxis.S) == WrapMode.Repeat) {
            oldMode = WrapMode.Repeat;
        }

        final AssetManager assetManager = EDITOR.getAssetManager();
        final Path assetFile = EditorUtil.getAssetFile(path);
        final String assetPath = EditorUtil.toAssetPath(assetFile);

        final CheckBox flipButton = getFlipButton();
        final CheckBox repeatButton = getRepeatButton();

        final TextureKey newKey = new TextureKey(assetPath);
        newKey.setFlipY(flipButton.isSelected());

        try {
            assetManager.loadTexture(newKey);
        } catch (final Exception e) {
            EditorUtil.handleException(LOGGER, this, e);
            return;
        }

        final WrapMode newMode = repeatButton.isSelected() ? WrapMode.Repeat : WrapMode.EdgeClamp;
        execute(new TextureMaterialParamOperation(parameterName, newKey, newMode, oldKey, oldMode));
    }

    /**
     * Удаление текстуры.
     */
    private void processRemove() {

        final String parameterName = getParameterName();
        final Material material = getMaterial();
        final MatParamTexture textureParam = material.getTextureParam(parameterName);
        final Texture2D texture2D = (Texture2D) textureParam.getValue();
        final TextureKey oldKey = (TextureKey) texture2D.getKey();

        WrapMode mode = WrapMode.EdgeClamp;

        if (texture2D.getWrap(WrapAxis.S) == WrapMode.Repeat) {
            mode = WrapMode.Repeat;
        }

        execute(new TextureMaterialParamOperation(parameterName, null, null, oldKey, mode));
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
    public void reload() {
        super.reload();

        final ImageChannelPreview textureTooltip = getTextureTooltip();
        final Material material = getMaterial();
        final MatParamTexture param = (MatParamTexture) material.getParam(getParameterName());

        if (param == null) {

            final ImageView preview = getTexturePreview();
            preview.setImage(null);

            final CheckBox flipButton = getFlipButton();
            flipButton.setSelected(false);

            final CheckBox repeatButton = getRepeatButton();
            repeatButton.setSelected(false);

            textureTooltip.showImage(null);
            return;
        }

        final Texture2D texture2D = (Texture2D) param.getValue();
        final TextureKey textureKey = (TextureKey) texture2D.getKey();

        final CheckBox flipButton = getFlipButton();
        flipButton.setSelected(textureKey.isFlipY());

        final CheckBox repeatButton = getRepeatButton();
        repeatButton.setSelected(texture2D.getWrap(WrapAxis.S) == WrapMode.Repeat);

        final Path realFile = EditorUtil.getRealFile(textureKey.getName());

        final ImageView preview = getTexturePreview();
        preview.setImage(IMAGE_MANAGER.getTexturePreview(realFile, 28, 28));

        textureTooltip.showImage(realFile);
    }
}
